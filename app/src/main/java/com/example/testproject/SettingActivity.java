package com.example.testproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String string="s";
    private int openonoff;
    private int humanonoff,temp_humionoff;
    private Switch openSwitch;
    private Switch humanSwitch;
    private Switch flameSwitch,temp_humiSwich;
    private int flameonoff;
    private Button btninfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // alert dialog
        final LinearLayout cctv = findViewById(R.id.cctv);
        final TextView tv = findViewById(R.id.ip);

        btninfo = findViewById(R.id.btninfo);
        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"CCTV ip 포트포워딩:\n" +
                        " 공유기 관리자 모드로 들어간 후\n"+
                        " 내부포트->81\n" +
                        " 외부포트->8001",Toast.LENGTH_LONG).show();
            }
        });

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("User");

        Intent intentM = getIntent();
         string = intentM.getStringExtra("cctvip");
         openonoff = intentM.getIntExtra("openonoff",0);
         humanonoff = intentM.getIntExtra("humanonoff",0);
         flameonoff = intentM.getIntExtra("flameonoff",0);
        temp_humionoff = intentM.getIntExtra("temp_humionoff",0);

         humanSwitch = findViewById(R.id.switch_human);
         openSwitch = findViewById(R.id.switch_open);
        flameSwitch = findViewById(R.id.switch_flame);
        temp_humiSwich = findViewById(R.id.switch_temp_humi);

         if(openonoff==1)
            openSwitch.setChecked(true);
         else
             openSwitch.setChecked(false);

         if(humanonoff==1)
            humanSwitch.setChecked(true);
         else
            humanSwitch.setChecked(false);

        if(flameonoff==1)
            flameSwitch.setChecked(true);
        else
            flameSwitch.setChecked(false);

        if(temp_humionoff==1)
            temp_humiSwich.setChecked(true);
        else
            temp_humiSwich.setChecked(false);

         openSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    databaseReference.child("User_01").child("open_onoff").setValue(1);
                }else{
                    databaseReference.child("User_01").child("open_onoff").setValue(0);
                }
            }
        });

        humanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    databaseReference.child("User_01").child("human_onoff").setValue(1);
                }else{
                    databaseReference.child("User_01").child("human_onoff").setValue(0);
                }
            }
        });

        flameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    databaseReference.child("User_01").child("flame_onoff").setValue(1);
                }else{
                    databaseReference.child("User_01").child("flame_onoff").setValue(0);
                }
            }
        });
        temp_humiSwich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    databaseReference.child("User_01").child("temp_humi_onoff").setValue(1);
                }else{
                    databaseReference.child("User_01").child("temp_humi_onoff").setValue(0);
                }
            }
        });



        tv.setText(string);

        cctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(SettingActivity.this);
                ad.setView(R.layout.alert_dialog);

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog f = (Dialog) dialog;
                        EditText input = (EditText) f.findViewById(R.id.address);
                        String result = input.getText().toString();
                        tv.setText(result);
                        dialog.dismiss();
                        databaseReference.child("User_01").child("cctv_ip").setValue(result);
                    }
                });

                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                ad.show();
            }
        });


        // alert dialog

    }


}