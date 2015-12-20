package itsjustaaron.movietogether;

import android.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import static itsjustaaron.movietogether.R.menu.menu_main;
import static itsjustaaron.movietogether.R.menu.menu_schedule;

public class Schedule extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView)findViewById(R.id.listView2);

        try {
            File calendar = Schedule.this.getFileStreamPath("entries.in");
                new sync().execute();
            BufferedReader br = new BufferedReader(new FileReader(calendar));
            String entry = br.readLine();
            if(entry == null) {
                listView.setVisibility(View.GONE);
            }else {
                findViewById(R.id.textView13).setVisibility(View.GONE);
                ArrayList<String> list = new ArrayList<String>();
                while(entry != null) {
                    list.add("reference #" + entry);
                    entry = br.readLine();
                }
                findViewById(R.id.button6).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.textView12)).setText("Here Are The Movies You Are Intereted In");
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = (String) parent.getItemAtPosition(position);
                        int reference = Integer.parseInt(item);
                        Intent form = new Intent(Schedule.this, Detail.class);
                        form.putExtra("itsjustaaron.movietogether.ref", reference);
                        form.putExtra("itsjustaaron.movietogether.source", "schedule");
                        startActivity(form);

                    }
                });
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

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
            File calendar = Schedule.this.getFileStreamPath("entries.in");
            try {
                if (!calendar.exists()) {
                    calendar.createNewFile();
                }
                Main.transferUtility.download(Main.bucket, Main.Email + "/entries.in", calendar);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return "uhuh";
        }
    }
}
