package com.example.uskapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView register,forgot;
    private EditText editTextEmail,editTextPassword;
    private Button login;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.pw);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register:
                startActivity(new Intent(this,SignupActivity.class));
                break;

            case R.id.login:
                userLogin();
                break;

            case R.id.forgot:
                startActivity(new Intent(this,ForgotPassword.class));
                break;
        }

    }


    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        System.out.println(email);
        System.out.println(password);
        if(email.isEmpty()){
            editTextEmail.setError("Email required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Invalid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password required!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length()<8){
            editTextPassword.setError("Invalid password!");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //go to homepage
                    startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Failed to Login!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signInWithGoogle(){

    }

}
