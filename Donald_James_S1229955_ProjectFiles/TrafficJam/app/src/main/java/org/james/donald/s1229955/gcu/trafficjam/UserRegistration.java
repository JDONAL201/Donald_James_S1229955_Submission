package org.james.donald.s1229955.gcu.trafficjam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */
public class UserRegistration extends AppCompatActivity
{
    private EditText regUsername;
    private EditText regPassword;
    private EditText regEmail;
    private TextView regSignup;
    private Button regButton;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        fAuth =FirebaseAuth.getInstance();
        Init();
    }
    private void Init()
    {
        regUsername = findViewById(R.id.userName);
        regEmail =findViewById(R.id.userEmail);
        regPassword =findViewById(R.id.userPassword);
        regButton = findViewById(R.id.signupBtn);
        regSignup = findViewById(R.id.registeredUser);

        regButton.setOnClickListener(v -> {
            if(IsValid())
            {
                String userEmail = regEmail.getText().toString().trim();
                String userPassword =regPassword.getText().toString().trim();

                fAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            VerificationEmail();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(UserRegistration.this,"Already Registered!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        regSignup.setOnClickListener(v -> startActivity(new Intent(UserRegistration.this,MainActivity.class)));

    }
    private void VerificationEmail()
    {
        FirebaseUser fUser = fAuth.getCurrentUser();
        if(fUser != null)
        {
            fUser.sendEmailVerification().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Toast.makeText(UserRegistration.this,"Verification email sent!", Toast.LENGTH_SHORT).show();
                    fAuth.signOut();
                    finish();
                    startActivity(new Intent(UserRegistration.this,MainActivity.class));
                }
                else
                {
                    Toast.makeText(UserRegistration.this,"Failed to send verification email!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private boolean IsValid()
    {
        boolean valid = false;

        String name = regUsername.getText().toString();
        String email = regEmail.getText().toString();
        String password = regPassword.getText().toString();

        if(name.isEmpty() || password.isEmpty() || email.isEmpty())
        {
            Toast.makeText(this,"Missing details in required fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            valid = true;
        }

        return valid;
    }
}
