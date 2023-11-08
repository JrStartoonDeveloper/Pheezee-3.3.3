package com.start.apps.pheezee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;

import start.apps.pheezee.R;

public class ReportDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        TextView textView = findViewById(R.id.text_view);
        Intent intent1 = getIntent();
        String str2 = intent1.getStringExtra("report_collection");
        String kranthi = String.valueOf(str2.split(","+System.lineSeparator()));
        textView.setText(kranthi);
        textView.setMovementMethod(new ScrollingMovementMethod());

//        String[] res = str2.replaceAll("(.)\\1{1,}", "$1").split("[,]");
//
//        String[] res2 = str2.split("," + System.lineSeparator());
//        textView.setText(res2.toString());
//        textView.setMovementMethod(new ScrollingMovementMethod());
//        for(String myStr: res2) {
//            System.out.println(myStr);
//            Log.e("myStr",myStr);
//
//        }


    }
}