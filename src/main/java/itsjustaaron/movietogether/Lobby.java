package itsjustaaron.movietogether;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;

import java.util.ArrayList;

import static itsjustaaron.movietogether.R.menu.menu_lobby;
import static itsjustaaron.movietogether.R.menu.menu_main;

public class Lobby extends AppCompatActivity {
    static public String param;
    private int noofitems;
    private ArrayList<Entry> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        new sync().execute();
/*
        ListView listView = (ListView)findViewById(R.id.listView3);
        Button btnLoadMore = (Button)findViewById(R.id.button10);
        listView.addFooterView(btnLoadMore);

*/
        if(Main.Email.equals("guest")) {
            ((ImageButton)findViewById(R.id.button8)).setVisibility(View.GONE);
        }



    }

    private class sync extends AsyncTask<Void, Void, Integer> {
        protected Integer doInBackground(Void... voids) {
            arrayList = new ArrayList<Entry>();
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<Entry> result = Main.mapper.scan(Entry.class, scanExpression);
            int i;
            int j = 0;
            noofitems = 0;
            for(i = 0; i < 20; i++) {
                try{
                    if(param == null) {
                        for (; result.get(j).getClosed() || result.get(j).getEmail().equals(Main.Email); j++) {
                            noofitems++;
                        }
                    }else {
                        for (; result.get(j).getClosed() || result.get(j).getEmail().equals(Main.Email) || !result.get(j).getMovie().equals(param); j++) {
                            noofitems++;
                        }
                    }
                    arrayList.add(result.get(j));
                    noofitems++;
                    j++;
                }catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                    break;
                }
            }
            return i;
        }
        protected void onPostExecute(Integer result) {
            if(result == 0) {
                findViewById(R.id.listView3).setVisibility(View.GONE);
                findViewById(R.id.textView15).setVisibility(View.VISIBLE);
                if(param != null) {
                    ((TextView)findViewById(R.id.textView15)).setText("No Entry Matches Your Search Criteria. Sometimes A Slightly Different Wording Might Help.");
                }
            }
            if(result < 20) {
                findViewById(R.id.button10).setVisibility(View.GONE);
                MyArrayAdapter adapter = new MyArrayAdapter(Lobby.this, arrayList);
                ListView listView = (ListView)findViewById(R.id.listView3);

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent detail = new Intent(Lobby.this, Detail.class);
                        detail.putExtra("itsjustaaron.movietogether.source", "lobby");
                        detail.putExtra("itsjustaaron.movietogether.ref", ((Entry) parent.getItemAtPosition(position)).getRef());
                        startActivity(detail);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(menu_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public class MyArrayAdapter extends ArrayAdapter<Entry> {
        private final Context context;
        private ArrayList<Entry> entries;

        public ArrayList<Entry> getEntries() {
            return entries;
        }

        public void setEntries(ArrayList<Entry> entries) {
            this.entries = entries;
        }

        public MyArrayAdapter(Context context, ArrayList<Entry> entries) {
            super(context, -1, entries);
            this.context = context;
            this.entries = entries;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflator.inflate(R.layout.rowlayout, parent, false);
            Entry data = entries.get(position);
            TextView ref = (TextView)rowView.findViewById(R.id.ref);
            TextView movie = (TextView)rowView.findViewById(R.id.movie);
            TextView name = (TextView)rowView.findViewById(R.id.name);
            TextView time = (TextView)rowView.findViewById(R.id.time);
            TextView loc = (TextView)rowView.findViewById(R.id.loc);
            ref.setText(String.valueOf(data.getRef()));
            movie.setText(data.getMovie());
            name.setText(data.getName());
            String timeS = data.getTime();
            time.setText(timeS.substring(0,2) + ":" + timeS.substring(2,4) + " " + timeS.substring(5,7) + "/" + timeS.substring(7,9) + "/" + timeS.substring(9,13));
            loc.setText(Main.convert(data.getLocation()));
            return rowView;
        }
    }

    public void lobbyOC(View view) {
        switch (view.getId()) {
            case R.id.button7:
                param = null;
                new sync().execute();
                return;
            case R.id.button8:
                Intent create = new Intent(Lobby.this, Create.class);
                startActivity(create);
                return;
            case R.id.button9:
                Intent search = new Intent(Lobby.this, Search.class);
                startActivity(search);
                return;
            case R.id.button10:
                DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();
                PaginatedScanList<Entry> result = Main.mapper.scan(Entry.class, dynamoDBScanExpression);
                ArrayList<Entry> newL = new ArrayList<Entry>();
                int j = noofitems;
                for(int i = 0; i < 20; i++) {
                    try{
                        if(param == null) {
                            for (; result.get(j).getClosed() || result.get(j).getEmail().equals(Main.Email); j++) {
                                noofitems++;
                            }
                        }else {
                            for (; result.get(j).getClosed() || result.get(j).getEmail().equals(Main.Email) || !result.get(j).getMovie().equals(param); j++) {
                                noofitems++;
                            }
                        }
                        newL.add(result.get(j));
                        noofitems++;
                        j++;
                    }catch (IndexOutOfBoundsException ex) {
                        view.setVisibility(View.GONE);
                        break;
                    }
                }
                arrayList.addAll(newL);
                ListView listView = (ListView)findViewById(R.id.listView3);
                MyArrayAdapter adapter = (MyArrayAdapter)listView.getAdapter();
                ArrayList temp = adapter.getEntries();
                temp.addAll(newL);
                adapter.setEntries(temp);
                listView.setAdapter(adapter);
                return;
        }
    }
}
