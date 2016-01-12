package itsjustaaron.movietogether;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

import static itsjustaaron.movietogether.R.menu.menu_main;
import static itsjustaaron.movietogether.R.menu.menu_schedule;

public class Schedule extends AppCompatActivity {

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pd = ProgressDialog.show(this, "", "Please Wait", true);

            new sync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lobby:
                Intent intent3 = new Intent(this, Lobby.class);
                startActivity(intent3);
                return true;
            case R.id.profile:
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void redirect(View view) {
        Intent lobby = new Intent(this, Lobby.class);
        startActivity(lobby);
        finish();
    }

    private class sync extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            final File calendar = Schedule.this.getFileStreamPath("entries.in");
            try {
                if (!calendar.exists()) {
                    calendar.createNewFile();
                }
                if(!Main.Email.equals("guest")) {
                    Main.transferUtility.download(Main.bucket, Main.Email + "/entries.in", calendar).setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int i, TransferState transferState) {
                            if (transferState.equals(TransferState.COMPLETED)) {
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(calendar));
                                    String ref = br.readLine();
                                    FileOutputStream fileOutputStream = openFileOutput("tempEntries.in", MODE_PRIVATE);
                                    while (ref != null) {
                                        Entry thisE = Main.mapper.load(Entry.class, Integer.parseInt(ref));
                                        if (thisE != null) {
                                            fileOutputStream.write(ref.getBytes());
                                        }
                                    }
                                    fileOutputStream.close();
                                    getFileStreamPath("tempEntries.in").renameTo(calendar);
                                    Main.transferUtility.upload(Main.bucket, Main.Email + "/entries.in", calendar);
                                } catch (Exception e) {

                                }
                                loadTable();
                            }
                        }

                        @Override
                        public void onProgressChanged(int i, long l, long l1) {

                        }

                        @Override
                        public void onError(int i, Exception e) {

                        }
                    });
                }else {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(calendar));
                        String ref = br.readLine();
                        FileOutputStream fileOutputStream = openFileOutput("tempEntries.in", MODE_PRIVATE);
                        while (ref != null) {
                            Entry thisE = Main.mapper.load(Entry.class, Integer.parseInt(ref));
                            if (thisE != null) {
                                fileOutputStream.write(ref.getBytes());
                            }
                        }
                        fileOutputStream.close();
                        getFileStreamPath("tempEntries.in").renameTo(calendar);
                    }catch (Exception e) {

                    }
                    loadTable();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return "uhuh";
        }
    }

    protected void loadTable() {
        try {
            File calendar = Schedule.this.getFileStreamPath("entries.in");
            ListView listView = (ListView) findViewById(R.id.listView2);
            BufferedReader br = new BufferedReader(new FileReader(calendar));
            String entry = br.readLine();
            if (entry == null) {
                listView.setVisibility(View.GONE);
            } else {
                findViewById(R.id.textView13).setVisibility(View.GONE);
                ArrayList<String> list = new ArrayList<String>();
                while (entry != null) {
                    list.add("reference #" + entry);
                    entry = br.readLine();
                }
                findViewById(R.id.button6).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.textView12)).setText("Here Are The Movies You Are Intereted In");
                ArrayAdapter adapter = new ArrayAdapter(Schedule.this, android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = (String) parent.getItemAtPosition(position);
                        int reference = Integer.parseInt(item.substring(11));
                        Intent form = new Intent(Schedule.this, Detail.class);
                        form.putExtra("itsjustaaron.movietogether.ref", reference);
                        form.putExtra("itsjustaaron.movietogether.source", "schedule");
                        startActivity(form);

                    }
                });
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        pd.dismiss();
    }
}
