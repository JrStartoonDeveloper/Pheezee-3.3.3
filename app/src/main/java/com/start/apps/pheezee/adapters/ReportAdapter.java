package com.start.apps.pheezee.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.start.apps.pheezee.classes.SessionListClass;

import java.util.ArrayList;

import start.apps.pheezee.R;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private ArrayList<SessionListClass> reports;

    public ReportAdapter(ArrayList<SessionListClass> reports) {
        Log.e("1111111111111111111111111111122", reports.toString());
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        SessionListClass report = reports.get(position);
        Log.e("3333333333333333332213233232", String.valueOf(report));
        holder.textDate.setText(report.getBodypart());
//        SessionAdapter sessionAdapter = new SessionAdapter(reports);
//        holder.sessionRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
//        holder.sessionRecyclerView.setAdapter(sessionAdapter);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        RecyclerView sessionRecyclerView;

        ReportViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            sessionRecyclerView = itemView.findViewById(R.id.sessionRecyclerView);
        }
    }
}
