package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    //DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://gohealthy-b0ed7-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;
    private EditText username,password,confirmpassword,email;
    private Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.fullname);
        password = (EditText) findViewById(R.id.password);
        confirmpassword = (EditText) findViewById(R.id.password1);
        email = (EditText) findViewById(R.id.email);

        registerBtn = (Button) findViewById(R.id.Register);
        final TextView loginNow = findViewById(R.id.LoginNow);
        final ImageView viewPassword =findViewById(R.id.view_password);
        final ImageView viewPassword1 =findViewById(R.id.view_password1);


        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        viewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    viewPassword.setImageResource(R.drawable.view);
                }
                else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    viewPassword.setImageResource(R.drawable.hidden);
                }
            }
        });

        viewPassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmpassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    confirmpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    viewPassword1.setImageResource(R.drawable.hidden);
                }
                else {
                    confirmpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    viewPassword1.setImageResource(R.drawable.view);
                }
            }
        });

        registerBtn.setOnClickListener(v -> createAcc());


        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void createAcc() {
        String usernameTxt=username.getText().toString();
        String passwordTxt = password.getText().toString();
        String confirmPasswordTxt = confirmpassword.getText().toString();
        String emailTxt = email.getText().toString();

        boolean isValidated = validateData(usernameTxt,passwordTxt,confirmPasswordTxt,emailTxt);
        if(!isValidated){
            return;
        }

        createAccountIntFirebase(usernameTxt,emailTxt,passwordTxt);


    }

    boolean validateData(String usernameTxt,String passwordTxt, String confirmPasswordTxt, String emailTxt) {
        if(usernameTxt.isEmpty()){
            username.setError("Please fill in the username.");
            return false;
        }
        if (passwordTxt.length() < 6) {
            password.setError("Passowrd length is invalid");
            return false;
        }
        if (!passwordTxt.equals(confirmPasswordTxt)) {
            confirmpassword.setError("Password is not matched");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            email.setError("Email is invalid");
            email.requestFocus();
            return false;
        }

        return true;
    }

    void createAccountIntFirebase(String usernameTxt,String emailTxt,String passwordTxt){
        mAuth =FirebaseAuth.getInstance();

        try {
            String firstEncrypted =null;
            firstEncrypted=Encryption.encrypt(passwordTxt);
            System.out.println(firstEncrypted);
            String secondEncrypted = null;
            secondEncrypted = Encryption1.encrypt(firstEncrypted);
            System.out.println(secondEncrypted);
            passwordTxt=secondEncrypted;
            mAuth.createUserWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        Users users=new Users(usernameTxt,emailTxt);

                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    firebaseUser.sendEmailVerification();

                                    Intent intent = new Intent(Register.this,Login.class);
                                    intent.putExtra("username",usernameTxt);
                                    intent.putExtra("email",emailTxt);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            |Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(Register.this, "Failed create account,Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(Register.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}