package com.example.fyp;

import android.Manifest;
import com.github.barteksc.pdfviewer.PDFView;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.internal.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Smartwatch extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ArrayList<UserItem> paymentUsersList;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };
    public static int PERMISSION_ALL = 12;
    public static File pFile;
    private File payfile;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartwatch);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.qr);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.home:
                        Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);

                        startActivity(intent1);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.qr:
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
    }
    public void BtnSetPDFGenerator_onClick(View view)  {
        Intent intent = new Intent(Smartwatch.this, SearchWeb.class);
        startActivity(intent);
    }


    public void BtnSetEmergency_onClick(View view) {
        String number = "0165295741";
        Intent intent = new Intent (Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"Permission Not Granted.Please go to Settings -->" +
                    "Apps-->Permissions and allowed phone",Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(intent);
    }

    public void BtnSetScan_onClick(View view) {
        Intent intent = new Intent(Smartwatch.this, BarcodeScanner.class);
        startActivity(intent);
    }

    public void BtnNearbyHospital_onClick(View view) {
        Intent intent = new Intent(Smartwatch.this, MapsActivity
                .class);
        startActivity(intent);
    }

    private void fetchPaymentUsers(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth =FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference recordsRef = database.getReference("Users").child(userId).child("Records");
        recordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                paymentUsersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Constants records = new Constants();
                    UserItem user=snapshot.getValue(UserItem.class);

                    paymentUsersList.add(user);

                }
                try {
                    createPaymentReport(paymentUsersList);
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void createPaymentReport(ArrayList<UserItem> paymentUsersList) throws DocumentException, FileNotFoundException {
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");
        FileOutputStream output = null;
        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        try{output = new FileOutputStream(pFile);
            System.out.println("+++++===================================");
        }catch(IOException e){
            System.out.println("+++++===================================");
            e.printStackTrace();
        }
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{6, 25, 20, 20});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Chunk noText = new Chunk("Date.", white);
        PdfPCell noCell = new PdfPCell(new Phrase(noText));
        noCell.setFixedHeight(50);
        noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        noCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk nameText = new Chunk("Blood Pressure", white);
        PdfPCell nameCell = new PdfPCell(new Phrase(nameText));
        nameCell.setFixedHeight(50);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_CENTER);

        /*Chunk phoneText = new Chunk("Other Names", white);
        PdfPCell phoneCell = new PdfPCell(new Phrase(phoneText));
        phoneCell.setFixedHeight(50);
        phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        phoneCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk amountText = new Chunk("Phone Number", white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);*/


        Chunk footerText = new Chunk("Moses Njoroge - Copyright @ 2020");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(4);


        table.addCell(noCell);
        table.addCell(nameCell);

        table.setHeaderRows(1);

        PdfPCell[] cells = table.getRow(0).getCells();


        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(grayColor);
        }
        for (int i = 0; i < paymentUsersList.size(); i++) {
            UserItem user = paymentUsersList.get(i);

            String id = String.valueOf(i + 1);
            String date = user.getDate();
            String bloodpressure = user.getBloodpressure();

            table.addCell(id + ". ");
            table.addCell(date);
            table.addCell(bloodpressure);

        }

        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20});
        footTable.setTotalWidth(PageSize.A4.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph(" How to generate real-time reports using Firebase\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void previewDisabledUsersReport() throws IOException {
        if (hasPermissions(this, PERMISSIONS)) {
            System.out.println("++++++++++++++++++++++++++++++");

            paymentUsersList = new ArrayList<>();

            //create files in charity care folder

//            payfile= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//            payfile = new File("/storage/emulated/0/Report/");
//            pFile = new File(payfile, "PaymentUsers.pdf");
//            File test = new File("score.pdf");
//            if(!test.exists()){
//                System.out.println("111111111111111111111111111111111111111111");
//                test.createNewFile(); // if file already exists will do nothing
//                System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
//            }else{
//                System.out.println("ppppppppppppppppppppppppppppppppppppp");
//            }


            //check if they exist, if not create them(directory)
//            if ( !payfile.exists()) {
//                System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
//                payfile.mkdirs();
//                System.out.println("+1111111111111111111111111111111111111111111");
//            }

            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/EmailClient/");
            folder.mkdirs();
            File file = new File(folder,"PaymentUsers.pdf");
            file.createNewFile();

//            payfile.mkdirs();

            //fetch payment and disabled users details;
            fetchPaymentUsers();

        } else {
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
}

