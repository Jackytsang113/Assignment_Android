package com.example.assignment_300cem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment_300cem.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password, password_confirm;
    private Button register_btn;
    private TextView login_txt;
    private ImageView backpage;

    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password_confirm = (EditText) findViewById(R.id.password_confirm);
        register_btn = (Button) findViewById(R.id.register_btn);

        login_txt = (TextView) findViewById(R.id.login_txt);
        login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        backpage = (ImageView) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(); // run createAccount
            }
        });
    }

    private boolean checkError(String str_username, String str_email, String str_password, String str_password_confirm){
        if(str_username.isEmpty()){ //check username is empty show error message
            username.setError(getResources().getString(R.string.username_require));
            username.requestFocus();
            return true;
        }

        if(str_email.isEmpty()){ //check email is empty show error message
            email.setError(getResources().getString(R.string.email_require));
            email.requestFocus();
            return true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()){ //check email pattern is not matched show error message
            email.setError(getResources().getString(R.string.valid_email));
            email.requestFocus();
            return true;
        }

        if(str_password.isEmpty()){ // check password is empty show error message
            password.setError(getResources().getString(R.string.password_require));
            password.requestFocus();
            return true;
        }

        if(str_password.length() < 6){ // check password length is less then 6 character show error message
            password.setError(getResources().getString(R.string.password_length));
            password.requestFocus();
            return true;
        }

        if(str_password_confirm.isEmpty()){ // check confirm password is empty
            password_confirm.setError(getResources().getString(R.string.repeat_passwor_require));
            password_confirm.requestFocus();
            return true;
        }

        if(!str_password_confirm.equals(str_password)){ // check confirm password and password is not matched show error message
            password_confirm.setError(getResources().getString(R.string.password_match));
            password_confirm.requestFocus();
            return true;
        }

        return false;
    }

    private void createAccount(){
        String str_username = username.getText().toString(); // get username String
        String str_email = email.getText().toString(); // get email String
        String str_password = password.getText().toString(); // get password String
        String str_password_confirm = password_confirm.getText().toString(); // get password confirm String

        // check error message
        if(checkError(str_username, str_email, str_password, str_password_confirm)) return;

        //display Loading message
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage(getResources().getString(R.string.loading));
        pd.show();

        //firebase create account
        mAuth.createUserWithEmailAndPassword(str_email, str_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){ // successfully created an account in firebase
                            FirebaseUser firebaseUser = mAuth.getCurrentUser(); // get current account user data
                            String userId = firebaseUser.getUid(); // get current user id

                            Users users = new Users(userId, str_username, str_email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId)
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){ //successfully added new user data to Realtime Database
                                        pd.dismiss(); // close Loading message
                                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registered_success), Toast.LENGTH_LONG).show(); // print message
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // go to Login page
                                    }else{
                                        pd.dismiss(); // close Loading message
                                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.fail_register), Toast.LENGTH_LONG).show(); // print fail message
                                    }

                                }
                            });
                        }else{
                            pd.dismiss(); // close Loading message
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.fail_register), Toast.LENGTH_LONG).show(); // print fail message
                        }
                    }
                });
    }
}