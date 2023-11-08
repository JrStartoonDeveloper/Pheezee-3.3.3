package com.start.apps.pheezee.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.start.apps.pheezee.activities.Record_affected_GetData;

import java.util.ArrayList;

import start.apps.pheezee.R;

public class Affectedside_adapter extends RecyclerView.Adapter<Affectedside_adapter.MyViewHolder> {
    Context context;
    ArrayList<Record_affected_GetData> patientData;
    public Affectedside_adapter(Context context, ArrayList<Record_affected_GetData> affectedsidedata){
        this.context=context;
        this.patientData=affectedsidedata;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        final String[] case_description = {""};
//        PopupWindow pw;
//        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        assert inflater != null;
//        @SuppressLint("InflateParams") View layout;
//        Log.e("Widthabhbd",String.valueOf(width));
//        Log.e("Heightabhbd",String.valueOf(height));
//        if(width>1100){
//            layout= inflater.inflate(R.layout.patient_records_recycle_tablet, null);
//        }
//        else {
//            layout= inflater.inflate(R.layout.patient_records_recycle, null);
//        }
//        pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
        LayoutInflater inflater=LayoutInflater.from(context);
        Configuration config = ((Activity)context).getResources().getConfiguration();
        View layout;
        if (config.screenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.patient_records_recycle_tablet, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.patient_records_recycle, null);
        }
        View view=inflater.inflate(R.layout.patient_records_recycle_tablet,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.ExerciseName.setText(patientData.get(position).getBodyPart()+" "+patientData.get(position).getExerciseType()+"|"+patientData.get(position).getMuscleName());
        holder.helonDate.setText(patientData.get(position).getHeldOnDate());
    }

    @Override
    public int getItemCount() {
        return patientData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView helonDate,ExerciseName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.exer_done);
            ExerciseName=itemView.findViewById(R.id.exer_name);
            helonDate=itemView.findViewById(R.id.heldon_date);
        }
    }
    }
