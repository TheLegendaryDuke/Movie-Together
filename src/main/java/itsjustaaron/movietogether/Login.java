package itsjustaaron.movietogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Login extends AppCompatActivity {
    public final static  String eName = "itsjustaaron.movietogether.name";
    public final static String eEmail = "itsjustaaron.movietogether.email";

    private String name;

    public String email, password;
    protected boolean remember;
    private ArrayList<String> emails;
    private ArrayList<String> passwords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emails = new ArrayList<String>();
        passwords = new ArrayList<String>();


        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText username = (EditText)findViewById(R.id.editText);
                EditText passcode = (EditText)findViewById(R.id.editText2);

                email = username.getText().toString();
                password = passcode.getText().toString();
                remember = ((CheckBox)findViewById(R.id.checkBox)).isChecked();

                new verify().execute();

            }
        });
    }


    private class save extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            try {
                name = Welcome.mapper.load(Person.class, email).getName();
                FileOutputStream outputStream;
                File profile = getFileStreamPath("profile.in");
                profile.deleteOnExit();
                profile.createNewFile();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("profile.in", Context.MODE_PRIVATE));
                Person thisPerson = Welcome.mapper.load(Person.class, email);
                outputStreamWriter.write(thisPerson.getName());
                outputStreamWriter.write("\n");
                outputStreamWriter.write(thisPerson.getEmail());
                outputStreamWriter.write("\n");
                if(remember) {
                    outputStreamWriter.write("1\n");
                }else {
                    outputStreamWriter.write("0\n");
                }
                outputStreamWriter.close();
                AmazonS3 s3 = new AmazonS3Client(Welcome.credentialsProvider);
                TransferUtility transferUtility = new TransferUtility(s3, Login.this);
                transferUtility.upload("movietogetherbyaaron", "AdminZhang/profile.in", profile);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return "Success";
        }
        protected void onPostExecute(String result) {

            Intent guest = new Intent(Login.this, Main.class);
            startActivity(guest);
            Welcome.wel.finish();
            finish();
        }
    }

    private boolean fail(String email, String password) {
        Person thisPerson = Welcome.mapper.load(Person.class, email);
        if(thisPerson == null) {
            return true;
        }else if(thisPerson.getPassword().equals(password)) {
            return false;
        }else {
            return true;
        }
    }
    private class verify extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            if(fail(email, password)) {
                return "OK";
            }else {
                return "NOOK";
            }
        }
        protected void onPostExecute(String s) {
            if(email.equals("")) {
                new AlertDialog.Builder(Login.this).setTitle("Error").setMessage("Please Enter a Valid E-mail Address").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }else if(s.equals("OK")) {
                new AlertDialog.Builder(Login.this).setTitle("Error").setMessage("Wrong Password or Email Entered").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }else {
                    new save().execute();

            }
        }
    }

}
