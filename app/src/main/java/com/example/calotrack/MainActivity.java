package com.example.calotrack;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,ExampleDialog.ExampleDialogListener {

    TextView tvDate;
    Button btPickDate;

    EditText hello;
    Boolean flag = Boolean.FALSE;
    FirebaseFirestore db;
    TextView Bname, Lname, Sname, Dname;
    TextView Bcal, Lcal, Scal, Dcal;
    Button edit1, edit2, edit3, edit4, save;
    TextView Total, Remaining, Consumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvDate = findViewById(R.id.tvDate);
        btPickDate = findViewById(R.id.btPickDate);
        db = FirebaseFirestore.getInstance();
        btPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("tag_value","hello");
                // Please note that use your package name here
                com.example.calotrack.DatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new com.example.calotrack.DatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");

            }
        });
        Bname = (TextView) findViewById(R.id.Bname);
        Lname = (TextView) findViewById(R.id.Lname);
        Sname = (TextView) findViewById(R.id.Sname);
        Dname = (TextView) findViewById(R.id.Dname);

        Bcal = (TextView) findViewById(R.id.Bcal);
        Lcal = (TextView) findViewById(R.id.Lcal);
        Scal = (TextView) findViewById(R.id.Scal);
        Dcal = (TextView) findViewById(R.id.Dcal);

        edit1 = (Button) findViewById(R.id.edit1);
        edit2 = (Button) findViewById(R.id.edit2);
        edit3 = (Button) findViewById(R.id.edit3);
        edit4 = (Button) findViewById(R.id.edit4);

        Total = (TextView) findViewById(R.id.total);
        Consumed = (TextView) findViewById(R.id.consumed);
        Remaining = (TextView) findViewById(R.id.remaining);
        save = (Button) findViewById(R.id.save);


        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("edit1");
            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("edit2");
            }
        });
        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("edit3");
            }
        });
        edit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("edit4");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    public void calculate() {
        int bcal = Integer.parseInt(Bcal.getText().toString());
        int lcal = Integer.parseInt(Lcal.getText().toString());
        int scal = Integer.parseInt(Scal.getText().toString());
        int dcal = Integer.parseInt(Dcal.getText().toString());

        int total = Integer.parseInt(Total.getText().toString());
        int consumed = Integer.parseInt(Consumed.getText().toString());
        int remaining = Integer.parseInt(Remaining.getText().toString());

        total = 2400;
        consumed = bcal + lcal + scal + dcal;
        remaining = total - consumed;

        Total.setText(String.valueOf(total));
        Consumed.setText(String.valueOf(consumed));


        if (consumed > 2400) {
            remaining = 0;
            Consumed.setTextColor(Color.RED);
        } else {
            Consumed.setTextColor(Color.BLACK);
        }
        Remaining.setText(String.valueOf(remaining));

        isdateavailable(tvDate.getText().toString(),bcal,lcal,scal,dcal,total,consumed,remaining);
    }


    public void openDialog(String st) {
        ExampleDialog exampleDialog = new ExampleDialog(st);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String username, String password, String btn) {
        switch (btn) {
            case "edit1":
                Bname.setText(username);
                Bcal.setText(password);
                break;

            case "edit2":
                Lname.setText(username);
                Lcal.setText(password);
                break;
            case "edit3":
                Sname.setText(username);
                Scal.setText(password);
                break;
            case "edit4":
                Dname.setText(username);
                Dcal.setText(password);
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalender.getTime());
        String selectedDate = simpleDateFormat.format(mCalender.getTime()).toString();
        tvDate.setText(selectedDate);
        finddata(tvDate.getText().toString());
    }

    public void finddata(String date) {
//        Log.d("tag_value",date);
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("date").toString().equals(date)) {
                                    Bname.setText(document.get("bitem").toString());
                                    Bcal.setText(document.get("bcal").toString());
                                    Lname.setText(document.get("litem").toString());
                                    Lcal.setText(document.get("lcal").toString());
                                    Sname.setText(document.get("sitem").toString());
                                    Scal.setText(document.get("scal").toString());
                                    Dname.setText(document.get("ditem").toString());
                                    Dcal.setText(document.get("dcal").toString());
                                    Consumed.setText(document.get("consumed").toString());
                                    Remaining.setText(document.get("remaining").toString());
                                    return;
                                }
                            }
                        } else {
                            Log.w("tag_value", "Error getting documents.", task.getException());
                        }
                        Bname.setText("Name");
                        Bcal.setText("0");
                        Lname.setText("Name");
                        Lcal.setText("0");
                        Sname.setText("Name");
                        Scal.setText("0");
                        Dname.setText("Name");
                        Dcal.setText("0");
                        Consumed.setText("0");
                        Remaining.setText("2400");

                    }
                });
    }


    public void isdateavailable(String date,int bcal,int lcal,int scal,int dcal,
                                int total,int consumed,int remaining) {
        flag = Boolean.FALSE;
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("date").toString().equals(date)) {
                                        Log.d("tag_value",date);
                                        flag = Boolean.TRUE;
                                        DocumentReference details = db.collection("users").document(document.getId());
                                        details.update("bcal",bcal);
                                        details.update("bitem",Bname.getText().toString());
                                        details.update("lcal",lcal);
                                        details.update("litem",Lname.getText().toString());
                                        details.update("scal",scal);
                                        details.update("sitem",Sname.getText().toString());
                                        details.update("dcal",dcal);
                                        details.update("ditem",Dname.getText().toString());
                                        details.update("consumed",consumed);
                                        details.update("remaining",remaining);
                                        Toast.makeText(getApplicationContext(), "Data Saved Successfully", Toast.LENGTH_LONG).show();

                                }
                            }
                        }
                        else {
                            Log.w("tag_value", "Error getting documents.", task.getException());
                        }
                        Log.d("tag_value",flag.toString());
                        if(!flag) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("date", tvDate.getText().toString());
                            user.put("bitem", Bname.getText().toString());
                            user.put("bcal", bcal);
                            user.put("litem", Lname.getText().toString());
                            user.put("lcal", lcal);
                            user.put("sitem", Sname.getText().toString());
                            user.put("scal", scal);
                            user.put("ditem", Dname.getText().toString());
                            user.put("dcal", dcal);
                            user.put("consumed", consumed);
                            user.put("remaining", remaining);

                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(), "Data Saved Successfully", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                });
    }
}
