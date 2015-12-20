package itsjustaaron.movietogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Create extends AppCompatActivity {
    String message,movie,time,date;
    int loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Please Select A Theater");
        String cinemas[] = {"Cineplex Yonge-Dundas", "Scotiabank Theartre Toronto", "Cineplex Cinemas Varsity", "The Beach Cinemas", "Famous Players Canada Square", "Cineplex Yonge-Eglinton", "Cineplex Don Mills", "Cineplex Yorkdale", "Cineplex Cinemas Scarborough"};
        for(int i = 0; i < cinemas.length; i++) {
            arrayList.add(cinemas[i]);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Create.this, android.R.layout.simple_list_item_1, arrayList);
        Spinner spinner = ((Spinner) findViewById(R.id.spinner3));
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loc = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ((Button) findViewById(R.id.button12)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                movie = ((EditText) findViewById(R.id.editText10)).getText().toString();
                time = ((EditText) findViewById(R.id.editText11)).getText().toString();
                date = ((EditText) findViewById(R.id.editText12)).getText().toString();
                message = ((EditText) findViewById(R.id.editText13)).getText().toString();
                if (movie.equals("") || movie.equals("Please Enter The Movie You Want To See")) {
                    new AlertDialog.Builder(Create.this).setTitle("Error").setMessage("Please Enter A Valid Movie Name!").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else if (time.equals("") || time.equals("(HHMM)")) {
                    new AlertDialog.Builder(Create.this).setTitle("Error").setMessage("Please Enter A Valid Time!").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else if (date.equals("") || time.equals("(DDMMYYYY)")) {
                    new AlertDialog.Builder(Create.this).setTitle("Error").setMessage("Please Enter A Valid Date!").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    new create().execute();
                    new AlertDialog.Builder(Create.this).setTitle("Message").setMessage("Your Entry Was Successfully Created.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });
    }
    private class create extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            Entry newE = new Entry();
            Person GOD = Main.mapper.load(Person.class, "AdminZhang");
            int ref = Integer.parseInt(GOD.getPassword()) + 1;
            GOD.setPassword(String.valueOf(ref));
            newE.setRef(ref);
            newE.setEmail(Main.Email);
            newE.setName(Main.Name);
            newE.setClosed(false);
            newE.setLocation(loc);
            newE.setMessage(message);
            newE.setPhone(Main.mapper.load(Person.class, Main.Email).getPhone());
            newE.setMovie(movie);
            newE.setTime(time + " " + date);
            newE.setPropose(new ArrayList<String>());
            Main.mapper.save(newE);
            Main.mapper.save(GOD);
            File entries = Create.this.getFileStreamPath("entries.in");
            try {
                if (!entries.exists()) {
                    entries.createNewFile();
                }
                    FileOutputStream fos = openFileOutput("entries.in", MODE_PRIVATE);
                    fos.write((String.valueOf(ref) + "\n").getBytes());
                    fos.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
            Main.transferUtility.upload(Main.bucket, Main.Email + "/entries.in", entries);
            return "OK";
        }
    }

}
