package com.start.apps.pheezee.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.start.apps.pheezee.classes.SessionListClass;

import java.util.ArrayList;

import start.apps.pheezee.R;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private ArrayList<SessionListClass> sessions;

    public SessionAdapter(ArrayList<SessionListClass> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sessionsreport_listview_model, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionListClass session = sessions.get(position);
        // Bind session data to the view holder
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView textExerciseName;

        SessionViewHolder(View itemView) {
            super(itemView);
            textExerciseName = itemView.findViewById(R.id.tv_exercise_no);
        }
    }
}
