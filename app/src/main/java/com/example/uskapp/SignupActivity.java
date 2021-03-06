package com.example.uskapp;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignupActivity";
    private EditText editTextEmail, editTextConfirmPw, editTextPw, editTextName;
    private Button btnSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        editTextName = (EditText) findViewById(R.id.name);
        editTextPw = (EditText) findViewById(R.id.pw);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextConfirmPw = (EditText) findViewById(R.id.confirmpw);

        btnSignUp = (Button) findViewById(R.id.login);
        btnSignUp.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        signUp();
    }

    private void signUp(){
        final String name_signup = editTextName.getText().toString().trim();
        final String email_signup = editTextEmail.getText().toString().trim();
        String password = editTextPw.getText().toString().trim();
        String confirmPassword = editTextConfirmPw.getText().toString().trim();

        if(name_signup.isEmpty()){
            editTextName.setError("Full name required");
            editTextName.requestFocus();
            return;
        }
        if(email_signup.isEmpty()){
            editTextEmail.setError("Email required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email_signup).matches()){
            editTextEmail.setError("Email invalid!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPw.setError("Password required");
            editTextPw.requestFocus();
            return;
        }

        if(password.length()<8){
            editTextPw.setError("Password too short!");
            editTextPw.requestFocus();
            return;
        }

        if(confirmPassword.isEmpty()){
            editTextConfirmPw.setError("Password required");
            editTextConfirmPw.requestFocus();
            return;
        }

        if(!confirmPassword.equals(password)){
            editTextConfirmPw.setError("Passwords does not match!");
            editTextConfirmPw.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email_signup,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            User user = new User(name_signup,email_signup);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {


                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this,"Success!",Toast.LENGTH_LONG).show();
                                    }else{
                                        Log.d(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                        Toast.makeText(SignupActivity.this,"Opps, something went wrong...",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Log.d(TAG, "onComplete: Failed=" + task.getException().getMessage());
                        }
                    }
                });


    }
}
