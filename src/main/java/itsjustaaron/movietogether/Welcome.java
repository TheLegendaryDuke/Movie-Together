package itsjustaaron.movietogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;

public class Welcome extends AppCompatActivity  {
    public final static  String eName = "itsjustaaron.movietogether.name";
    public final static  String eEmail = "itsjustaaron.movietogether.email";

    public static Activity wel;

    public static CognitoCachingCredentialsProvider credentialsProvider;
    public static AmazonDynamoDBClient ddbClient;
    public static DynamoDBMapper mapper;

    private boolean isNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNet = connectivityManager.getActiveNetworkInfo();
        return activeNet != null && activeNet.isConnected();
    }

    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isNetwork()) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage("Please Ensure that You Are Connected to the Internet and Try Again.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        }

        File file = Welcome.this.getFileStreamPath("profile.in");
        if(file.exists()) {
            try {
                String name;
                BufferedReader br = new BufferedReader(new FileReader(file));
                name = br.readLine();
                String email = br.readLine();
                String yes = br.readLine();
                if(yes.equals("1")) {
                    Intent guest = new Intent(Welcome.this, Main.class);
                    guest.putExtra(eName, name);
                    guest.putExtra(eEmail, email);
                    startActivity(guest);
                    finish();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        wel = this;

        credentialsProvider = new CognitoCachingCredentialsProvider(getBaseContext(), "us-east-1:1d2749df-22eb-422a-b7f6-d6e979e11b43", Regions.US_EAST_1);
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.button).setOnClickListener(welcomeClick);
        findViewById(R.id.button2).setOnClickListener(welcomeClick);
        findViewById(R.id.button3).setOnClickListener(welcomeClick);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener welcomeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button:
                    Intent register = new Intent(Welcome.this, Register.class);
                    startActivity(register);
                    break;
                case R.id.button2:
                    Intent login = new Intent(Welcome.this, Login.class);
                    startActivity(login);
                    break;
                case R.id.button3:
                    Intent guest = new Intent(Welcome.this, Main.class);
                    try {
                        FileOutputStream outputStream;
                        outputStream = openFileOutput("profile.in", Context.MODE_PRIVATE);
                        outputStream.write("guest\nguest\n0\n".getBytes());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(guest);
                    finish();
                    break;
            }
        }
    };

}
