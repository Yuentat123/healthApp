package com.example.fyp;

import static com.google.common.collect.ComparisonChain.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://gohealthy-b0ed7-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        final EditText username= findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText confirmpassword = findViewById(R.id.password1);
        final EditText email = findViewById(R.id.email);
        final Button reset = findViewById(R.id.Reset);

        final ImageView viewPassword =findViewById(R.id.view_password);
        final ImageView viewPassword1 =findViewById(R.id.view_password1);
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

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameTxt = username.getText().toString();
                String emailTxt = email.getText().toString();
                String newpasswordTxt = password.getText().toString();
                String password1Txt = confirmpassword.getText().toString();
                if(usernameTxt.isEmpty() || emailTxt.isEmpty() || newpasswordTxt.isEmpty()|| password1Txt.isEmpty()){
                    Toast.makeText(ResetPassword.this,"Please fill in all the field.",Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(usernameTxt)){
                                 String getEmail = snapshot.child(usernameTxt).child("email").getValue(String.class);
                                if(getEmail.equals(emailTxt)){
                                    if(newpasswordTxt.length()<6){
                                        Toast.makeText(ResetPassword.this,"Please make sure the password length is valid.(6)",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(newpasswordTxt.equals(password1Txt)){
                                        try {
                                            final String firstEncrypted = Encryption.encrypt(newpasswordTxt);
                                            System.out.println(firstEncrypted);
                                            final String secondEncrypted = Encryption1.encrypt(firstEncrypted);
                                            System.out.println(secondEncrypted);
                                            databaseReference.child("users").child(usernameTxt).child("password").setValue(secondEncrypted);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(ResetPassword.this,"Reset Password Successfully",Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else{
                                        Toast.makeText(ResetPassword.this,"Password is not matching.",Toast.LENGTH_SHORT).show();}
                                }

                                else{
                                    Toast.makeText(ResetPassword.this,"Wrong Email",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(ResetPassword.this,"Wrong Username",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }

}