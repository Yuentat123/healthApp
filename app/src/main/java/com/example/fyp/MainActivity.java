package com.example.fyp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "error";
    RecyclerView recyclerView;
    ArrayList<UserItem> userItemArrayList;
    ImageView empty_image;
    TextView empty_record;
    Adapter adapter;
    private FirebaseAuth mAuth;
    private boolean hasInputData = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        empty_image = findViewById(R.id.empty_imageview);
        empty_record = findViewById(R.id.empty_record_text);

        userItemArrayList = new ArrayList<>();
        adapter = new Adapter(MainActivity.this, userItemArrayList);
        adapter.setContext(MainActivity.this); // set the activity context
        recyclerView.setAdapter(adapter);

        Button addButton=findViewById(R.id.addButton);
        addButton.setEnabled(true);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.alert_dialog_add_new_record);

                EditText blood_pressure = dialog.findViewById(R.id.bloodpressure);
                EditText textdate = dialog.findViewById(R.id.date);
                EditText heart_rate = dialog.findViewById(R.id.heartrate);
                EditText body_temperature = dialog.findViewById(R.id.bodytemperature);

                Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
                Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                dialog.show();

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String bloodpressure = blood_pressure.getText().toString();
                        String totaldate = textdate.getText().toString();
                        String heartrate = heart_rate.getText().toString();
                        String bodytemperature = body_temperature.getText().toString();
                        String status = null;


                        if (bloodpressure.isEmpty() || totaldate.isEmpty()|| heartrate.isEmpty()|| bodytemperature.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please Enter All The Data", Toast.LENGTH_SHORT).show();
                        }else if (!isValidDate(totaldate)) {
                            Toast.makeText(MainActivity.this, "Please Enter Valid Date", Toast.LENGTH_SHORT).show();
                        } else if (!isValidBloodPressure(bloodpressure)) {
                            Toast.makeText(MainActivity.this, "Please Enter Valid Blood Pressure", Toast.LENGTH_SHORT).show();
                        }else if (!isValidHeartRate(heartrate)) {
                            Toast.makeText(MainActivity.this, "Please Enter Valid Heart Rate", Toast.LENGTH_SHORT).show();
                        } else if (!isValidBodyTemperature(bodytemperature)) {
                            Toast.makeText(MainActivity.this, "Please Enter Valid Body Temperature", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mAuth = FirebaseAuth.getInstance();
                            String userId = mAuth.getCurrentUser().getUid();
                            int hr = Integer.parseInt(heartrate);
                            double bt = Double.parseDouble(bodytemperature);
                            if(hr>=60 && hr<=70 && bt>=36 && bt<=37){
                                status="Relax";
                            } else if(hr>=70 && hr<=90 && bt>=35 && bt<=36){
                                status="Calm";
                            }else if(hr>=90 && hr<=100 && bt>=33 && bt<=35){
                                status="Anxious";
                                notification();
                            }else if(hr>=100 && bt<=33){
                                status="Stress";
                                notification();
                            }else{
                                status="Cannot Detect !";
                            }

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Records");

                            String key = databaseReference.push().getKey();

                            UserItem userItem = new UserItem(bloodpressure, totaldate,heartrate,bodytemperature,status,System.currentTimeMillis());
                            userItem.setKey(key);
                            databaseReference.child(key).setValue(userItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.this, "Record insert successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.home:
                        return true;
                    case R.id.qr:
                        Intent intent1 = new Intent(getApplicationContext(),Smartwatch.class);
                        startActivity(intent1);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        Intent intent2 = new Intent(getApplicationContext(),Profile.class);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        readData();
    }
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean isValidBloodPressure(String bpStr) {
        String[] bpValues = bpStr.split("/");
        if (bpValues.length != 2) {
            return false;
        }
        try {
            int systolic = Integer.parseInt(bpValues[0]);
            int diastolic = Integer.parseInt(bpValues[1]);
            if (systolic < 60 || systolic > 200 || diastolic < 40 || diastolic > 120) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean isValidHeartRate(String hrStr) {
        try {
            int hr = Integer.parseInt(hrStr);
            if (hr < 30 || hr > 200) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean isValidBodyTemperature(String btStr) {
        try {
            double bt = Double.parseDouble(btStr);
            if (bt < 30 || bt > 45) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


//    private void sendEmailReminder() {
//        mAuth = FirebaseAuth.getInstance();
//        String userId = mAuth.getCurrentUser().getUid();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Records");
//        databaseReference.equalTo(getTodayTimestamp()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                } else {
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (user != null) {
//                        String userEmail=snapshot.child("emailTxt").getValue(String.class);
//                        if (userEmail != null) {
//                            String subject = "Reminder: Add data for today";
//                            String message = "Dear user,\n\nThis is a friendly reminder to add data for today in the app.\n\nThank you,\nYour App Team";
//                            sendEmail(userEmail, subject, message);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // handle error
//            }
//        });
//    }

    private long getTodayTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

//    private void sendEmail(String emailAddress, String subject, String message) {
//        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.setData(Uri.parse("mailto:" + emailAddress));
//        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        intent.putExtra(Intent.EXTRA_TEXT, message);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(MainActivity.this, "Error: No email app found on this device.", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void readData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth =FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference recordsRef = database.getReference("Users").child(userId).child("Records");
        recordsRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userItemArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserItem user=snapshot.getValue(UserItem.class);

                    userItemArrayList.add(user);
                }
                if(userItemArrayList.size()==0){
                    empty_image.setVisibility(View.VISIBLE);
                    empty_record.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }else{
                    empty_image.setVisibility(View.GONE);
                    empty_record.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new Adapter(MainActivity.this,userItemArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void notification(){
        NotificationChannel channel =
                new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager= getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentText("GoHealthy")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setAutoCancel(true)
                .setContentText("Make sure you go to nearby hospital find an doctor to have some consultation !");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }
}