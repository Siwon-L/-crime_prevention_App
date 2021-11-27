package com.example.testproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class timeline extends AppCompatActivity {
    private ArrayAdapter<String> adapterA;
    private ListView listView;
    private DatabaseReference databaseReferenceTime;
    private FirebaseDatabase database;
    private Intent serviceIntent;
    List<Object> Array = new ArrayList<Object>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        database = FirebaseDatabase.getInstance();       //파이어베이스 데이터베이스 연동
        databaseReferenceTime = database.getReference("timeline");

        listView = (ListView) findViewById(R.id.list);
        adapterA = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapterA);

        databaseReferenceTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                serviceIntent = new Intent(timeline.this, testService.class)
                        .setAction(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_LAUNCHER)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                adapterA.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String msg = snapshot.getValue().toString();
                    Array.add(msg);
                    adapterA.add(msg);


                }
                adapterA.notifyDataSetChanged();
                listView.setSelection(adapterA.getCount() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException()));

            }

        });

    }
    public void onClickDel(View v){
        databaseReferenceTime.removeValue();
        //serviceIntent.putExtra("sensor", 4);

    }
}




