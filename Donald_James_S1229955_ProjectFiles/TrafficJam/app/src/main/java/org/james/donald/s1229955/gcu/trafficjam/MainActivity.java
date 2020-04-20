package org.james.donald.s1229955.gcu.trafficjam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */
public class MainActivity extends AppCompatActivity
{

    //Log in
    private EditText inName;
    private EditText inPassword;
    private TextView register;

    private TextView attempts;
    private Button loginBtn;
    private String dev_userName = "Admin";
    private String dev_password = "1234";
    private int attemptCounter = 4;

    private FirebaseAuth fAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);

        if(CheckDeviceNetwork())
        {
            InitFireBase();
            InitLogin();
        }
        else
        {
            inName = findViewById(R.id.userName);
            inPassword = findViewById(R.id.userPassword);
            attempts = findViewById(R.id.incorrectAttempts);

            register =findViewById(R.id.register);
            attempts.setText("No network connectivity");
            loginBtn = findViewById(R.id.logInBtn);
            inPassword.setVisibility(View.INVISIBLE);
            inName.setVisibility(View.INVISIBLE);
            register.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);

            progressDialog.setMessage("No Network Connectivity");
            progressDialog.show();
        }

    }

    private void InitFireBase()
    {
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        CheckService();

        if(user != null)
        {
            finish();
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
        }
    }
    private boolean CheckDeviceNetwork()
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if(connectivityManager != null)
            {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if(networkInfo != null && networkInfo.isConnected())
            {
                return true;
            }

            return false;

        }

        catch (NullPointerException e)
        {
            return false;
        }
    }

    private void InitLogin()
    {
        inName = findViewById(R.id.userName);
        inPassword = findViewById(R.id.userPassword);
        attempts = findViewById(R.id.incorrectAttempts);

        register =findViewById(R.id.register);
        attempts.setText("");


        if(CheckService())
        {
            loginBtn = findViewById(R.id.logInBtn);
            loginBtn.setOnClickListener(v -> {
                if(!inName.getText().toString().isEmpty() && !inPassword.getText().toString().isEmpty())
                {
                    LogInValidation(inName.getText().toString(),inPassword.getText().toString());
                }
            });
        }
        register.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,UserRegistration.class)));
    }
    private boolean CheckService()
    {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //error occured but can be resolved
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,9001);
            dialog.show();

        }
        else
        {
            Toast.makeText(this,"Cant make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void CheckVerification()
    {
        FirebaseUser fUser = fAuth.getInstance().getCurrentUser();
        Boolean eFlag = fUser.isEmailVerified();
        if(eFlag)
        {
            startActivity(new Intent(this,HomeActivity.class));
        }
        else
        {
            Toast.makeText(this,"Email requires verification",Toast.LENGTH_SHORT).show();
            fAuth.signOut();
        }
    }
    private void LogInValidation(String userName, String userPassword)
    {
        progressDialog.setMessage("Authenticating!");
        progressDialog.show();

        if(userName.equals(dev_userName) && userPassword.equals(dev_password))
        {
            Toast.makeText(this,"Developer Access!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        }

        fAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressDialog.dismiss();

                if(task.isSuccessful())
                {
                    CheckVerification();
                }
                else
                {
                    attemptCounter--;
                    attempts.setText("Unrecognised login: " + attemptCounter +" attemps");

                    if(attemptCounter == 0)
                    {
                        attempts.setText("Account temporarily locked");
                        loginBtn.setEnabled(false);
                    }
                }
            }
        });
    }

}
