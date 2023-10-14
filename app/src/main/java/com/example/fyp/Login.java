package com.example.fyp;

import static android.content.ContentValues.TAG;
import static com.google.common.collect.ComparisonChain.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {
    //DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://gohealthy-b0ed7-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;
    private EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email= (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.Login);
        final TextView resetBtn = findViewById(R.id.resetPass);
        final TextView registerNowBtn = findViewById(R.id.RegisterNow);
        final ImageView viewPassword = findViewById(R.id.view_password);

        Intent intent = getIntent();
        String usernameTxt = intent.getStringExtra("username");
        String emailTxt = intent.getStringExtra("email");


        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth= FirebaseAuth.getInstance();

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

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail  = email.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(mail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Login.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mail  = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                System.out.println(pass+"++++++++++++++++++++++++++++++");

                if(mail.isEmpty()){
                    email.setError("Email cannot be empty...");
                }
                else if(pass.isEmpty()){
                    password.setError("Email cannot be empty...");
                }

                else{
                    try {
                        String firstDecrypted =null;
                        firstDecrypted=Encryption.encrypt(pass);
                        System.out.println(firstDecrypted);
                        String secondDecrypted = null;
                        secondDecrypted = Encryption1.encrypt(firstDecrypted);
                        System.out.println(secondDecrypted);
                        mAuth.signInWithEmailAndPassword(mail,secondDecrypted).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful() ){
                                    Toast.makeText(Login.this,"Login Successfully",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, MainActivity.class).putExtra("email", mail));
                                    finish();
                                }
                                else{
                                    Toast.makeText(Login.this,"Login Failed" +task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        registerNowBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(Login.this, Register.class));
            }

        });
        //resetBtn.setOnClickListener(new View.OnClickListener(){
        // @Override
        //public void onClick(View v){
        //startActivity(new Intent(Login.this, ResetPassword.class));
        //}
        //});

    }
}