package com.example.fyp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context context;
    private ArrayList<UserItem> UserItemArrayList;
    private FirebaseAuth mAuth;

    public Adapter(Context context, ArrayList<UserItem> userItemArrayList) {
        this.context = context;
        if (userItemArrayList == null) {
            UserItemArrayList = new ArrayList<>();
        } else {
            UserItemArrayList = userItemArrayList;
        }
        mAuth = FirebaseAuth.getInstance();
    }
    public void setContext(Context context) {
        this.context = context;
    }
    /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Records");
                databaseReference.child(UserItemArrayList.get(position).getKey()).removeValue();
                UserItemArrayList.remove(position);
    notifyDataSetChanged();
                Toast.makeText(Context, "Record deleted successfully", Toast.LENGTH_SHORT).show();*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserItem user = UserItemArrayList.get(position);
        System.out.println(user+"+++++++++++++++++++++++++++");

        holder.bloodpressure.setText("Blood Pressure Value:"+user.getBloodpressure());
        System.out.println(user.getBloodpressure()+"+++++++++++++++++++++++++++");
        holder.date.setText("Date:"+user.getDate());
        holder.heartrate.setText("Heart Rate :"+user.getHeartrate());
        holder.bodytemperature.setText("Body Temperature :"+user.getBodytemperature());
        holder.status.setText("Status :"+user.getStatus());

        if (user.getStatus().equals("Relax")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (user.getStatus().equals("Calm")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        }else if (user.getStatus().equals("Anxious")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.yellow));
        }else if (user.getStatus().equals("Stress")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
        }

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.delete_dialog);

                Button buttonConDelete = dialog.findViewById(R.id.buttonConDelete);
                Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                dialog.show();

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                buttonConDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Records").child(user.getKey());
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!UserItemArrayList.isEmpty() && position < UserItemArrayList.size()) {
                                    UserItemArrayList.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Record deleted unsuccessful", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
        });

        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.alert_dialog_add_new_record);

                EditText blood_pressure = dialog.findViewById(R.id.bloodpressure);
                EditText textdate = dialog.findViewById(R.id.date);
                EditText heart_rate = dialog.findViewById(R.id.heartrate);
                EditText body_temperature = dialog.findViewById(R.id.bodytemperature);
                blood_pressure.setText(UserItemArrayList.get(position).getBloodpressure());
                textdate.setText(UserItemArrayList.get(position).getDate());
                heart_rate.setText(UserItemArrayList.get(position).getHeartrate());
                body_temperature.setText(UserItemArrayList.get(position).getBodytemperature());

                Button buttonUpdate = dialog.findViewById(R.id.buttonAdd);
                Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

                dialog.show();

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                buttonUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!UserItemArrayList.isEmpty()){
                            String bloodpressure = blood_pressure.getText().toString();
                            String totaldate = textdate.getText().toString();
                            String heartrate = heart_rate.getText().toString();
                            String bodytemperature = body_temperature.getText().toString();
                            String status=user.getStatus();

                            if (bloodpressure.isEmpty() || totaldate.isEmpty()|| heartrate.isEmpty()|| bodytemperature.isEmpty()) {
                                Toast.makeText(context, "Please Enter All The Data", Toast.LENGTH_SHORT).show();
                            }else if (!isValidDate(totaldate)) {
                                Toast.makeText(context, "Please Enter Valid Date", Toast.LENGTH_SHORT).show();
                            } else if (!isValidBloodPressure(bloodpressure)) {
                                Toast.makeText(context, "Please Enter Valid Blood Pressure", Toast.LENGTH_SHORT).show();
                            }else if (!isValidHeartRate(heartrate)) {
                                Toast.makeText(context, "Please Enter Valid Heart Rate", Toast.LENGTH_SHORT).show();
                            } else if (!isValidBodyTemperature(bodytemperature)) {
                                Toast.makeText(context, "Please Enter Valid Body Temperature", Toast.LENGTH_SHORT).show();
                            }
                            else if (userId == null) {
                                Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show();
                            }else {
                                int hr = Integer.parseInt(heartrate);
                                double bt = Double.parseDouble(bodytemperature);
                                if(hr>=60 && hr<=70 && bt>=36 && bt<=37){
                                    status="Relax";
                                } else if(hr>=70 && hr<=90 && bt>=35 && bt<=36){
                                    status="Calm";
                                }else if(hr>=90 && hr<=100 && bt>=33 && bt<=35){
                                    status="Anxious";
                                }else if(hr>=100 && bt<=33){
                                    status="Stress";
                                }else{
                                    status="Cannot Detect!";
                                }


                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Records");
                                UserItem userItem = new UserItem(bloodpressure, totaldate, heartrate,bodytemperature,status,System.currentTimeMillis());
                                databaseReference.child(UserItemArrayList.get(position).getKey()).setValue(userItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Update item in RecyclerView
                                            UserItemArrayList.set(position, userItem);

                                            notifyDataSetChanged();
                                            Toast.makeText(context, "Record updated successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(context, "Record update unsuccessful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

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

    @Override
    public int getItemCount() {

        return UserItemArrayList.size() ;
    }

    public void setUserItemList(ArrayList<UserItem> userItemArrayList) {
    }

    public void setContext(MainActivity mainActivity) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bloodpressure;
        TextView date;
        TextView heartrate;
        TextView bodytemperature;
        TextView status;

        Button buttonDelete;
        Button buttonUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bloodpressure = itemView.findViewById(R.id.bloodpressure);
            date = itemView.findViewById(R.id.date);
            heartrate = itemView.findViewById(R.id.heartrate);
            bodytemperature = itemView.findViewById(R.id.bodytemperature);
            status = itemView.findViewById(R.id.status);

            buttonDelete=itemView.findViewById(R.id.buttonDelete);
            buttonUpdate=itemView.findViewById(R.id.updateButton);
        }

    }

}
