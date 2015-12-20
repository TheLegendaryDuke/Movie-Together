package itsjustaaron.movietogether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Detail extends AppCompatActivity {
    AlertDialog ad;

    private void updateSchedule(String em1,String em2) {
        try {
            File calendar = Detail.this.getFileStreamPath("calendar.in");
            FileOutputStream cfos = openFileOutput("calendar.in", MODE_PRIVATE);
            cfos.write((new DecimalFormat("000000").format(entry.getRef()) + " " + entry.getTime() + " " + entry.getMovie() + "\n").getBytes());
            cfos.close();
            Main.transferUtility.upload(Main.bucket, em1 + "/calendar.in", calendar);
            File temp = Detail.this.getFileStreamPath("temp.in");
            Main.transferUtility.download(Main.bucket, em2 + "/calendar.in", temp);
            cfos = openFileOutput("temp.in", MODE_PRIVATE);
            cfos.write((new DecimalFormat("000000").format(entry.getRef()) + " " + entry.getTime() + " " + entry.getMovie() + "\n").getBytes());
            cfos.close();
            Main.transferUtility.upload(Main.bucket, em2 + "/calendar.in", temp);
            Main.transferUtility.download(Main.bucket, em2 + "/entries.in", temp);
            cfos = openFileOutput("temp.in", MODE_PRIVATE);
            cfos.write((ref + "\n").getBytes());
            cfos.close();
            Main.transferUtility.upload(Main.bucket, em2 + "/entries.in", temp);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Entry entry;
    private String source;
    private int ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        source = getIntent().getExtras().getString("itsjustaaron.movietogether.source");
        ref = getIntent().getExtras().getInt("itsjustaaron.movietogether.ref");



        if(source.equals("lobby")) {
            ((Button)findViewById(R.id.detailPCR)).setText("Propose");
            ((Button)findViewById(R.id.detailDC)).setVisibility(View.GONE);
        }else if(source.equals("schedule")) {
            if(entry.getEmail().equals(Main.Email)) {
                Button pcr = (Button)findViewById(R.id.detailPCR);
                Button dc = (Button)findViewById(R.id.detailDC);
                dc.setText("Delete");
                if(entry.getClosed()) {
                    pcr.setText("Reopen");
                }else {
                    pcr.setText("Close");
                }
            }else {
                ((Button)findViewById(R.id.detailPCR)).setVisibility(View.GONE);
                ((Button)findViewById(R.id.detailDC)).setText("Cancel");
            }
        }
        Button b = (Button)findViewById(R.id.detailPCR);
        if(b.getText().equals("Close")) {
            new load().execute();
        }
        ad = new AlertDialog.Builder(this).setTitle("Please Wait").show();
        new fetch().execute();
    }

    private class fetch extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            entry = Main.mapper.load(Entry.class, ref);
            return "OK";
        }

        protected void onPostExecute(String result) {
            File img = Detail.this.getFileStreamPath(entry.getMovie() + ".jpg");
            if(!img.exists()) {
                Main.transferUtility.download(Main.bucket,entry.getMovie() + ".jpg", img);
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
            }
            else {

                ((ImageView) findViewById(R.id.imageView)).setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
            }
            if(!img.exists()) {
                ((ImageView)findViewById(R.id.imageView)).setImageResource(R.drawable.blank);
            }

            ((TextView)findViewById(R.id.detailRef)).setText(String.valueOf(ref));
            ((TextView)findViewById(R.id.detailMovie)).setText(entry.getMovie());
            ((TextView)findViewById(R.id.detailLoc)).setText(Main.convert(entry.getLocation()));
            ((TextView)findViewById(R.id.detailTime)).setText(entry.getTime());
            ((TextView)findViewById(R.id.detailName)).setText(entry.getName());
            ((TextView)findViewById(R.id.detailMsg)).setText(entry.getMessage());
            ad.dismiss();
        }
    }

    public void detailOC(View view) {
        switch (view.getId()) {
            case R.id.detailPCR:
                final Button pcr = (Button)view;
                if(pcr.getText().equals("Propose")) {
                    new AlertDialog.Builder(Detail.this).setTitle(entry.getName() + " Has Beem Notified.").setMessage("Please Contact" + entry.getName() + " Directly:\nEmail: " + entry.getEmail() + "\nPhone: " + entry.getPhone()).setNeutralButton("Contact Information Noted", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (!entry.getPropose().contains(Main.Email)) {
                                entry.addPropose(Main.Email);
                                new update().execute();
                            }
                        }
                    }).show();
                } else if(pcr.getText().equals("Close")) {
                    if(entry.getPropose().size() == 0) {
                        new AlertDialog.Builder(Detail.this).setTitle("Confirmation").setMessage("Are You Sure You Want To Close This Entry? It Will No Longer Be Displayed In The Lobby").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                entry.setClosed(true);
                                new update().execute();
                                dialog.dismiss();
                                pcr.setText("Reopen");
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }else {
                        final Spinner select = (Spinner)findViewById(R.id.spinner2);
                        select.setVisibility(View.VISIBLE);
                        findViewById(R.id.selector).setVisibility(View.VISIBLE);
                        select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                                if(parent.getItemAtPosition(position).equals("     ")) {
                                }
                                else {
                                    new AlertDialog.Builder(Detail.this).setTitle("Message").setMessage("Are You Sure You Are Going With " + parent.getItemAtPosition(position) + " to see " + entry.getMovie() + " at " + entry.getTime() + " ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            entry.setClosed(true);
                                            new update().execute();
                                            updateSchedule(entry.getEmail(), parent.getItemAtPosition(position).toString());
                                            dialog.dismiss();
                                            findViewById(R.id.selector).setVisibility(View.GONE);
                                            select.setVisibility(View.GONE);
                                            pcr.setText("Reopen");
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                                }
                            }


                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }

                        });

                    }
                }else {
                    entry.setClosed(false);
                    new update().execute();
                    pcr.setText("Close");
                    new AlertDialog.Builder(Detail.this).setTitle("Message").setMessage("Your Entry Has Been Reopened").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                break;
            case R.id.detailDC:
                final Button dc = (Button)view;
                if(dc.getText().equals("Delete")) {
                    new AlertDialog.Builder(Detail.this).setTitle("Confirmation").setMessage("Are You Sure You Want To Delete This Entry? It Will Be Gone Forever").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            entry.setClosed(true);
                            new delete().execute();
                            dialog.dismiss();
                            new AlertDialog.Builder(Detail.this).setTitle("Message").setMessage("Entry Deleted").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                            Intent back = new Intent(Detail.this, Schedule.class);
                            startActivity(back);
                            new change().execute();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }else {
                    new AlertDialog.Builder(Detail.this).setTitle("Warning").setMessage("It Is Highly Discouraged To Cancel A Mutually Agreed On Event! Do You Want To Proceed?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            entry.setClosed(false);
                            new update().execute();
                            dialog.dismiss();
                            new AlertDialog.Builder(Detail.this).setTitle("Message").setMessage("You Have Cancelled This Event.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                            new change().execute();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
        }
    }

    private class delete extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            Main.mapper.delete(entry);
            return "ahhhhhh";
        }
    }
    private class update extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            Main.mapper.save(entry);
            return "this sucks";
        }
    }

    private class change extends AsyncTask<Void, Void, String > {
        protected String doInBackground(Void... voids) {
            File calendar = Detail.this.getFileStreamPath("entries.in");
            File entries = Detail.this.getFileStreamPath("calendar.in");

            try {
                BufferedReader br = new BufferedReader(new FileReader(calendar));
                String entry = br.readLine();
                FileOutputStream fos = openFileOutput("entries.in", MODE_PRIVATE);
                if(Integer.parseInt(entry) != (ref)) {
                    fos.write(entry.getBytes());
                    fos.write("\n".getBytes());
                }
                fos.close();
                    BufferedReader br2 = new BufferedReader(new FileReader(entries));
                    String cal = br2.readLine();
                    FileOutputStream fos2 = openFileOutput("calendar.in", MODE_PRIVATE);
                    if (!cal.substring(0, 6).equals(ref)) {
                        fos.write(cal.getBytes());
                        fos.write("\n".getBytes());
                    }
                    fos2.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        if(!Main.Email.equals("guest")) {
            Main.transferUtility.upload(Main.bucket, Main.Email + "/" + "calendar.in", entries);
            Main.transferUtility.upload(Main.bucket, Main.Email + "/entries.in", entries);
        }
            return "duh";
        }
    }

    private class load extends AsyncTask<Void, Void, String> {

        ArrayList<String> emails;

        protected String doInBackground(Void... voids) {

            emails = new ArrayList<String>();
            emails.add("     ");
            emails.addAll(entry.getPropose());
            return "I hate the rules";
        }
        protected void onPostExecute(String s) {

            final Spinner select = (Spinner)findViewById(R.id.spinner2);
            ArrayAdapter<String> adapter = new ArrayAdapter(Detail.this, android.R.layout.simple_spinner_dropdown_item, emails);
            select.setAdapter(adapter);
        }
    }
}
