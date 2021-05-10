package com.example.assignment_300cem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login_btn;
    private TextView createAccount_txt;

    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    private Spinner change_language;
    public static final String[] languages = {"Language", "English", "中文"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_btn);
        createAccount_txt = (TextView) findViewById(R.id.createAccount_txt);
        change_language = (Spinner) findViewById(R.id.change_language);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        change_language.setAdapter(adapter);
        change_language.setSelection(0);
        change_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLang = adapterView.getItemAtPosition(i).toString();

                System.out.println(java.util.Locale.getDefault().getDisplayName());
                if(selectedLang.equals("中文")){
                    setLocal(LoginActivity.this,"zh");
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else if(selectedLang.equals("English")){
                    setLocal(LoginActivity.this,"en");
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createAccount_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private boolean checkError(String str_email, String str_password){
        if(str_email.isEmpty()){ //check email is empty show error message
            email.setError(getResources().getString(R.string.email_require));
            email.requestFocus();
            return true;
        }

        if(str_password.isEmpty()){ // check password is empty show error message
            password.setError(getResources().getString(R.string.password_require));
            password.requestFocus();
            return true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()){ //check email pattern is not matched show error message
            email.setError(getResources().getString(R.string.valid_email));
            email.requestFocus();
            return true;
        }

        if(str_password.length() < 6){ // check password length is less then 6 character show error message
            password.setError(getResources().getString(R.string.password_length));
            password.requestFocus();
            return true;
        }

        return false;
    }

    private void login() {
        String str_email = email.getText().toString(); // get email String
        String str_password = password.getText().toString(); // get password String

        // check error message
        if(checkError(str_email,str_password)) return;

        //display Loading message
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage(getResources().getString(R.string.loading));
        pd.show();

        // login user in firebase
        mAuth.signInWithEmailAndPassword(str_email, str_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            pd.dismiss(); // close Loading message
                            startActivity(new Intent(LoginActivity.this, MainActivity.class)); // go to main page
                        }else{
                            pd.dismiss(); // close Loading message
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.fail_login), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public  void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
    }
}