package itsjustaaron.movietogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Region;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import static itsjustaaron.movietogether.R.menu.menu_main;

public class Main extends AppCompatActivity {
    public static final String bucket = "movietogetherbyaaron";
    AlertDialog ad;

    public static String Name;
    public static String Email;

    public static CognitoCachingCredentialsProvider credentialsProvider;
    public static AmazonDynamoDBClient ddbClient;
    public static DynamoDBMapper mapper;
    public static AmazonS3 s3;
    public static TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        credentialsProvider = new CognitoCachingCredentialsProvider(getBaseContext(), "us-east-1:1d2749df-22eb-422a-b7f6-d6e979e11b43", Regions.US_EAST_1);
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
        s3.setEndpoint("s3.amazonaws.com");
        transferUtility = new TransferUtility(s3, getApplicationContext());

        try {
            File file = Main.this.getFileStreamPath("profile.in");
            BufferedReader br = new BufferedReader(new FileReader(file));
            Name = br.readLine();
            Email = br.readLine();
        }catch (Exception e) {
            e.printStackTrace();
        }




        if(!isNetwork()) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage("Please Ensure that You Are Connected to the Internet and Try Again.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        }



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Movie Together");


        new download().execute();

        ((TextView)findViewById(R.id.TV1)).setText("Hello, " + Name);

        ad = new AlertDialog.Builder(Main.this).setTitle("Please Wait...").show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lobby:
                Intent intent3 = new Intent(this, Lobby.class);
                startActivity(intent3);
                return true;
            case R.id.calendar:
                Intent intent2 = new Intent(this, Schedule.class);
                startActivity(intent2);
                return true;
            case R.id.profile:
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class upload extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            File calendar = Main.this.getFileStreamPath("calendar.in");
            if(!calendar.exists()) {
                try {
                    calendar.createNewFile();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            TransferObserver observer1 = transferUtility.upload(bucket, Email + "/calendar.in", calendar);

            return "success";
        }
    }

    private boolean isNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNet = connectivityManager.getActiveNetworkInfo();
        return activeNet != null && activeNet.isConnected();
    }

    private class download extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            File file = Main.this.getFileStreamPath("calendar.in");
            File schedule = Main.this.getFileStreamPath("schedule.in");
            try {
                schedule.createNewFile();
            }catch (Exception e) {
                e.printStackTrace();
            }
            TransferObserver observer = transferUtility.download(bucket, "Schedule.in", schedule);
            TransferObserver observer1 = transferUtility.download(bucket, Email + "/calendar.in", file);
            return "success";
        }
        protected void onPostExecute(String s) {
            File file = Main.this.getFileStreamPath("calendar.in");
            if(file.exists()) {
                try {
                    String event;
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    boolean finished = false;
                    while(true) {
                        event = br.readLine();
                        if(event == null)break;
                        String hour = event.substring(7, 9);
                        String min = event.substring(9, 11);
                        String day = event.substring(12, 14);
                        String month = event.substring(14, 16);
                        String year = event.substring(16, 20);
                        Calendar current = Calendar.getInstance();
                        if(current.YEAR > Integer.parseInt(year)) continue;
                        else if(current.MONTH > Integer.parseInt(month) && current.YEAR == Integer.parseInt(year)) continue;
                        else if(current.DAY_OF_MONTH > Integer.parseInt(day) && current.MONTH == Integer.parseInt(month))continue;
                        else if(current.HOUR_OF_DAY > Integer.parseInt(hour) && current.DAY_OF_MONTH == Integer.parseInt(day))continue;
                        else if (current.MINUTE > Integer.parseInt(min) && current.HOUR_OF_DAY == Integer.parseInt(hour)) {
                            continue;
                        }else {
                            if(!finished) {
                                ((TextView) findViewById(R.id.TV2)).setText("ref #" + event.substring(0,6) + " " + hour + ":" + min + " " + day + "/" + month + "/" + year + " ");
                                finished = true;
                            }
                            FileOutputStream outputStream;
                            outputStream = openFileOutput("calendarTemp.in", Context.MODE_PRIVATE);
                            outputStream.write(event.getBytes());
                            outputStream.write('\n');
                        }
                    }
                    file.delete();
                    File oldFile = Main.this.getFileStreamPath("calendarTemp.in");
                    oldFile.renameTo(file);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(!Name.equals("guest")) {
                new upload().execute();
            }

            ListView listView = (ListView)findViewById(R.id.listView);
            ArrayList<String> movies = new ArrayList<String>();
            try{
                BufferedReader br = new BufferedReader(new FileReader(Main.this.getFileStreamPath("schedule.in")));
                String movie = br.readLine();
                while (movie!= null) {
                    movies.add(movie);
                    movie=br.readLine();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            MyAdapter adapter = new MyAdapter(Main.this, movies);
            listView.setAdapter(adapter);
            ad.dismiss();
        }
    }

    private class MyAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public MyAdapter(Context context, ArrayList<String> values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView2 = inflater.inflate(R.layout.rowlayout2, parent, false);
            String movie = values.get(position);
            AlertDialog alertDialog = new AlertDialog.Builder(Main.this).setTitle("Please Wait").show();
            File temp = Main.this.getFileStreamPath(movie + ".jpg");
            transferUtility.download(bucket, movie + ".JPG", temp);
            ((TextView)rowView2.findViewById(R.id.movieTitle)).setText(movie);
            ((ImageView)rowView2.findViewById(R.id.movieIcon)).setImageBitmap(BitmapFactory.decodeFile(temp.getAbsolutePath()));
            alertDialog.dismiss();
            return rowView2;
        }

    }
    public static String convert(int loc) {
        switch (loc) {
            case 1:
                return "Cineplex Yonge-Dundas";
            case 2:
                return "Scotiabank Theartre Toronto";
            case 3:
                return "Cineplex Cinemas Varsity";
            case 4:
                return "The Beach Cinemas";
            case 5:
                return "Famous Players Canada Square";
            case 6:
                return "Cineplex Yonge-Eglinton";
            case 7:
                return "Cineplex Don Mills";
            case 8:
                return "Cineplex Yorkdale";
            case 9:
                return "Cineplex Cinemas Scarborough";
            default:return "";
        }
    }
}
