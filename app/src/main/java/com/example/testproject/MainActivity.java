package com.example.testproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;
    public static testService testService;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReferenceTime;
    private int openS,humanS,flameS;
    private int openonoff,humanonoff,flameonoff,temp_humionoff;
    private int temp,humi;
    private User user;
    private TextView opentext,temp_humitext,flametext,humantext;
    private TextView appname;
    private int openC,humanC,flameC;
    private Intent serviceIntent;
    private int nf,start;
    private ImageView image;
    private String cctvip;
    private LinearLayout humanL,flameL,openL;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    //파이어베이스에서 읽은 변화에 맞게 텍스트와 레이아웃에 변화를 주고 서비스로 데이터를 넘겨주는 메소드
    private int send_Service(int onoff, int S, TextView text, LinearLayout L, int C, Intent intent,int Sv){
        if(onoff==1) {
            if (S == 1) {
                text.setText("감지 됨");
                L.setBackgroundResource(R.drawable.detected);
                if (C == 1) {

                    intent.putExtra("sensor", Sv);

                    C = 0;
                }
            } else {
                text.setText("감지되지 않음");
                L.setBackgroundResource(R.drawable.not_detected);
                C = 1;

            }
        }
        else{
            text.setText("센서 꺼짐");
            L.setBackgroundResource(R.drawable.not_detected);

        }
        return C;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testService = new testService();

        mainActivity =this;

        layoutManager = new LinearLayoutManager(this);
        arrayList = new ArrayList<>();//User 객체를 담을 어레이 리스트(어댑터쪽으로)

        opentext=(TextView)findViewById(R.id.opentext);
        humantext=(TextView)findViewById(R.id.humantext);
        flametext=(TextView)findViewById(R.id.flametext);
        temp_humitext=(TextView)findViewById(R.id.temp_humitext);
        appname=(TextView)findViewById(R.id.appname);
        openL=(LinearLayout) findViewById(R.id.openL);
        humanL=(LinearLayout)findViewById(R.id.humanL);
        flameL=(LinearLayout)findViewById(R.id.flameL);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);//특정 상황에만 포그라운드를 실행할 수 있게 쉐어드프리퍼런스 사용.
        editor = pref.edit();




        database = FirebaseDatabase.getInstance();       //파이어베이스 데이터베이스 연동
        databaseReferenceTime = database.getReference("timeline");
        databaseReference = database.getReference("User");//DB 테이블 연결

        final Button btnon = findViewById(R.id.btnon);
        final Button btnoff = findViewById(R.id.btnoff);
        final Button btncctv = findViewById(R.id.cctvButton);
        final Button btnsetting = findViewById(R.id.btnsetting);

        btnon.setOnClickListener(new View.OnClickListener() {//Off버튼
            @Override
            public void onClick(View v) {
                btnon.setVisibility(View.INVISIBLE);
                btnoff.setVisibility(View.VISIBLE);
                appname.setText("OFF");
                image.setImageResource(R.drawable.disconnect);
                databaseReference.child("User_01").child("nf").setValue(0);
                editor.putInt("start", 1);//쉐어드 프리퍼런스에 1값 저장
                editor.apply();

            }
        });

        btnoff.setOnClickListener(new View.OnClickListener() {//on버튼
            @Override
            public void onClick(View v) {
                btnon.setVisibility(View.VISIBLE);
                btnoff.setVisibility(View.INVISIBLE);
                image = (ImageView) findViewById(R.id.connect);
                Glide.with(getApplicationContext()).load(R.drawable.connect2).into(image);
                appname.setText("ON");
                databaseReference.child("User_01").child("nf").setValue(1);
                editor.putInt("start", 1);//쉐어드 프리퍼런스
                editor.apply();

            }
        });

        btncctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cctvip.isEmpty()){
                    Toast.makeText(getApplicationContext(),"설정에서 CCTV ip를 입력하세요",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+cctvip+"/stream"));

                    //Intent intent = new Intent(getApplicationContext(), CctvAcrivity.class);
                    // intent.putExtra("ip",cctvip);
                    startActivity(intent);
                }
            }
        });

        ImageView timestamp = findViewById(R.id.iv_menu);

        timestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), timeline.class);
                startActivity(intent);
            }
        });

        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.putExtra("cctvip",cctvip)
                        .putExtra("openonoff",openonoff)
                        .putExtra("humanonoff",humanonoff)
                        .putExtra("flameonoff",flameonoff)
                        .putExtra("temp_humionoff",temp_humionoff);
                startActivity(intent);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear();  // 기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {  // 반복문으로 데이터 List를 추출해 냄
                    user = snapshot.getValue(User.class);  //만들어뒀던 User 객체에 데이터를 담는다.
                    arrayList.add(user);
                    // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비

                }

                serviceIntent = new Intent(MainActivity.this, testService.class)
                        .setAction(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_LAUNCHER)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //파이어베이스에서 각종 데이터를 읽어 온다
                nf=user.getNf();
                openS=user.getOpen();
                humanS=user.getHuman();
                cctvip=user.getCctv_ip();
                openonoff=user.getOpen_onoff();
                humanonoff=user.getHuman_onoff();
                flameS=user.getFlame();
                flameonoff=user.getFlame_onoff();
                temp_humionoff=user.getTemp_humi_onoff();
                temp = user.getTemp();
                humi = user.getHumi();
                //쉐어드 프리퍼런스에 값을 가져오고 값이 1이면 서비스에 1값을 보내 포그라운드가 실행
                start=pref.getInt("start",1);
                if(start==1){
                    serviceIntent.putExtra("start",1);
                    editor.putInt("start", 0);
                    editor.apply();
                }
                else if(start==0){
                    serviceIntent.putExtra("start",0);
                }




                if(nf==0){
                    btnon.setVisibility(View.INVISIBLE);
                    btnoff.setVisibility(View.VISIBLE);
                    appname.setText("OFF");
                }
                else {
                    btnon.setVisibility(View.VISIBLE);
                    btnoff.setVisibility(View.INVISIBLE);
                    image = (ImageView) findViewById(R.id.connect);
                    Glide.with(getApplicationContext()).load(R.drawable.connect2).into(image);
                    appname.setText("ON");
                }

                if(nf==1){
                    serviceIntent.putExtra("onoff",1);

                }
                else {
                    serviceIntent.putExtra("onoff",0);

                }
                //----------------------------------------------


                openC=send_Service(openonoff,openS,opentext,openL,openC,serviceIntent,1);//만들어 놓은 메소드를 각 센서에 맞게 적용


                //----------------------------------------------

                humanC=send_Service(humanonoff,humanS,humantext,humanL,humanC,serviceIntent,2);
                //----------------------------------------------


                flameC=send_Service(flameonoff,flameS,flametext,flameL,flameC,serviceIntent,3);
                //----------------------------------------------
                if(temp_humionoff==1) {

                    temp_humitext.setText(temp+"ºC / "+humi+"%");
                }
                else
                    temp_humitext.setText("센서 꺼짐");
                //----------------------------------------------

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    startForegroundService(serviceIntent);

                }
                else {

                    startService(serviceIntent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB를 가져오던중 엘러 발생 시
                Log.e("MainActivity", String.valueOf(databaseError.toException()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //앱이 종료되면 쉐어드 프리퍼런스에 1값을 넣어 다시 실행하였을때 포그라운드가 실행될 수 있도록
        editor.putInt("start", 1);
        editor.apply();
    }
}