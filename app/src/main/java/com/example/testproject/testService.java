package com.example.testproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

public class testService extends Service {


    private DatabaseReference databaseReferenceTime;
    private FirebaseDatabase database;
    int sensor;
    int onoff;
    String text="센서 작동중!!";
    String text1="센서 작동중!!";
    long now;
    private Date date;
    private Intent testIntent;
    int a =2;
    int opc;
    int hmc;
    int fmc;
    int start;
    SimpleDateFormat Format = new SimpleDateFormat("hh:mm:ss");
    public static testService testService;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        database = FirebaseDatabase.getInstance();
        databaseReferenceTime = database.getReference("timeline");
        testService=this;


        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
        testIntent = new Intent(getApplicationContext(), MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addCategory(Intent.CATEGORY_LAUNCHER);

        start=intent.getIntExtra("start",0);
        sensor=intent.getIntExtra("sensor",0);
        onoff=intent.getIntExtra("onoff",100);


        PendingIntent pendingIntent
                = PendingIntent
                .getActivity(this, 0, testIntent, FLAG_CANCEL_CURRENT );

        if(onoff==1)
            text1="작동중";
        else
            text1="작동 멈춤";

        if(sensor==1&&opc==1&&onoff==1)
            text = "문열림이 감지 되었습니다.";

        else if(sensor==2&&hmc==1&&onoff==1)
            text = "인체가 감지 되었습니다";

        else if(sensor==3&&fmc==1&&onoff==1)
            text = "화염이 감지 되었습니다";



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("channel", "play!!",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel channel1 = new NotificationChannel("channel1", "play!!1",
                    NotificationManager.IMPORTANCE_LOW);

            // Notification과 채널 연걸
            NotificationManager mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            // Notification 세팅
            NotificationCompat.Builder notification
                    = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(R.drawable.home_ic)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager nNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            nNotificationManager.createNotificationChannel(channel1);

            // Notification 세팅
            NotificationCompat.Builder notification1
                    = new NotificationCompat.Builder(getApplicationContext(), "channel1")
                    .setSmallIcon(R.drawable.home_ic)
                    .setContentIntent(pendingIntent)
                    .setContentText(text1);

            if(sensor==1&&opc==1&&onoff==1) {
                databaseReferenceTime.push().setValue(getTime() + " 문열림이 감지 되었습니다.");
                opc=0;
                mNotificationManager.notify(a, notification.build());
                a++;
                if(a==50){
                    a=2;
                }
            }

            else if(sensor==2&&hmc==1&&onoff==1) {
                databaseReferenceTime.push().setValue(getTime() + " 인체가 감지 되었습니다.");
                hmc=0;
                mNotificationManager.notify(a, notification.build());
                //
                a++;
                if(a==50){
                    a=2;
                }
            }

            else if(sensor==3&&fmc==1&&onoff==1) {
                databaseReferenceTime.push().setValue(getTime() + " 화염이 감지 되었습니다.");
                fmc=0;
                mNotificationManager.notify(a, notification.build());
                //
                a++;
                if(a==50){
                    a=2;
                }
            }

            else if(sensor==0) {
                opc=1;
                hmc=1;
                fmc=1;
            }
            // foreground에서 시작
            if(start==1){
            startForeground(1, notification1.build());
            }

            Toast.makeText(getApplicationContext(),"s"+start,Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    private String getTime(){
        now = System.currentTimeMillis();
        date = new Date(now);
        return Format.format(date);

    }

}
