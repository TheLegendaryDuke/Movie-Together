package itsjustaaron.movietogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    public static Activity reg;

    String email, name, password, password2;

    private ArrayList<String> emails;

    private class download extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<Person> result = Welcome.mapper.scan(Person.class, scanExpression);
            for(int i = 0; i < result.size(); i++) {
                emails.add(result.get(i).getEmail());
            }
            return "Download Completed";
        }
    }
    private class upload extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... voids) {
            Person thisPerson = new Person();
            thisPerson.setEmail(email);
            thisPerson.setName(name);
            thisPerson.setPassword(password);
            Welcome.mapper.save(thisPerson);
            return "Save Successful";
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        reg = this;

        emails = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new download().execute();
        Button confirm = (Button) findViewById(R.id.button5);
        confirm.setOnClickListener(new View.OnClickListener() {
            private boolean notOK(String email) {
                if (email.equals("AdminZhang")) return false;
                if (email.isEmpty()) {
                    return true;
                } else {
                    return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
                }
            }

            @Override
            public void onClick(View v) {

                email = ((EditText) findViewById(R.id.editText3)).getText().toString();
                password = ((EditText) findViewById(R.id.editText4)).getText().toString();
                password2 = ((EditText) findViewById(R.id.editText6)).getText().toString();
                name = ((EditText) findViewById(R.id.editText5)).getText().toString();

                if (notOK(email)) {
                    new AlertDialog.Builder(Register.this).setTitle("Error").setMessage("Please Enter a Valid E-mail Address").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else if (password.equals("")) {
                    new AlertDialog.Builder(Register.this).setTitle("Error").setMessage("Please Enter a Password").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else if (name.equals("")) {
                    new AlertDialog.Builder(Register.this).setTitle("Error").setMessage("Please Enter Your Name").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else if (name.equals("guest")) {
                    new AlertDialog.Builder(Register.this).setTitle("Error").setMessage("That Name Has Been Called Dibs By Aaron, Please Enter a Different Name.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else if (!password.equals(password2)) {
                    new AlertDialog.Builder(Register.this).setTitle("Error").setMessage("The Two Passwords Entered Were Different.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else if (emails.contains(email)) {
                    new AlertDialog.Builder(Register.this).setTitle("Error").setMessage("This E-mail Address Has Already Been Used.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    new upload().execute();
                    final Intent back = new Intent(Register.this, Welcome.class);
                    new AlertDialog.Builder(Register.this).setTitle("Success").setMessage("Your New Account Has Been Created, You Will Now Be Redirected to Welcome Screen").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(back);
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}


