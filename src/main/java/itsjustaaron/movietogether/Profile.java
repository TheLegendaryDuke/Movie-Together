package itsjustaaron.movietogether;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.text.DecimalFormat;

import static itsjustaaron.movietogether.R.menu.menu_profile;

public class Profile extends AppCompatActivity {

    String name, phone, sex, dob;

    private int month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if(Main.Email.equals("guest")) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage("You Are Not Logged In").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent login = new Intent(Profile.this, Welcome.class);
                    startActivity(login);
                    finish();
                }
            }).show();
        }else {
            new update().execute();

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                month = pos;
            }

            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(month);
            }
        });

    }

    public void check(View view) {
        RadioButton rb = (RadioButton) view;
        switch (view.getId()) {
            case R.id.radioButton:
                if(rb.isChecked()) {

                    ((RadioButton)findViewById(R.id.radioButton2)).setChecked(false);
                    return;
                }
                else{
                    rb.setChecked(true);
                    ((RadioButton)findViewById(R.id.radioButton2)).setChecked(false);
                    return;
                }
            case R.id.radioButton2:
                if(rb.isChecked()) {

                    ((RadioButton)findViewById(R.id.radioButton)).setChecked(false);
                    return;
                }
                else{
                    rb.setChecked(true);
                    ((RadioButton)findViewById(R.id.radioButton)).setChecked(false);
                    return;
                }
        }
    }

    public void save(View view) {
        name = ((EditText)findViewById(R.id.editText8)).getText().toString();
        phone = ((EditText)findViewById(R.id.editText7)).getText().toString();
        RadioButton male = (RadioButton)findViewById(R.id.radioButton);
        RadioButton female = (RadioButton)findViewById(R.id.radioButton2);
        if(male.isChecked()) {
            sex = "male";
        }else if(female.isChecked()) {
            sex = "female";
        }else {
            sex = null;
        }
        dob = ((EditText)findViewById(R.id.ETYear)).getText().toString() + new DecimalFormat("00").format(month) + ((EditText)findViewById(R.id.ETDay)).getText().toString();

        new saveInfo().execute();

    }

    private class saveInfo extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            Person me = Main.mapper.load(Person.class, Main.Email);
            me.setName(name);
            me.setPhone(phone);
            me.setSex(sex);
            me.setDob(dob);
            Main.mapper.save(me);
            return "Success";
        }
        protected void onPostExecute(String result) {
            new AlertDialog.Builder(Profile.this).setTitle("Success").setMessage("Your Information Was Saved Successfully.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    private class update extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            Person me = Main.mapper.load(Person.class, Main.Email);
            name = me.getName();
            phone = me.getPhone();
            sex = me.getSex();
            dob = me.getDob();
            month = 0;
            if(dob != null) {
                month = Integer.parseInt(dob.substring(4, 6));
            }
            return "Sigh";
        }
        protected void onPostExecute(String result) {
            if(result.equals("Sigh"))
            updateViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(menu_profile, menu);
        return true;
    }

    private void updateViews() {
        ((EditText) findViewById(R.id.editText8)).setText(name);
        ((EditText) findViewById(R.id.editText7)).setText(phone);
        if (sex == null) {
            ((RadioButton) findViewById(R.id.radioButton)).setChecked(false);
            ((RadioButton) findViewById(R.id.radioButton2)).setChecked(false);
        }
        else if (sex.equals("male")) {
            ((RadioButton) findViewById(R.id.radioButton)).setChecked(true);
            ((RadioButton) findViewById(R.id.radioButton2)).setChecked(false);
        } else {
            ((RadioButton) findViewById(R.id.radioButton)).setChecked(false);
            ((RadioButton) findViewById(R.id.radioButton2)).setChecked(true);
        }
        if(dob != null) {
            ((EditText) findViewById(R.id.ETYear)).setText(dob.substring(0, 4));
            ((EditText) findViewById(R.id.ETDay)).setText(dob.substring(6, 8));
        }
        ((Spinner)findViewById(R.id.spinner)).setSelection(month);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
