package com.example.uskapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter {
    private ArrayList<String> subjectArrayList;
    private int[] androidcolors;
    private Activity activity;

    public SubjectAdapter(Activity activity, ArrayList<String> subjectArrayList, int[] androidcolors) {
        this.activity = activity;
        this.androidcolors = androidcolors;
        this.subjectArrayList = subjectArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View myOwnView = layoutInflater.inflate(R.layout.subject_recyclerlayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(myOwnView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.subject_button.setText(subjectArrayList.get(position));
        viewHolder.subject_button.setBackgroundColor(androidcolors[position]);

        viewHolder.subject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                Intent intent = new Intent(activity,HomeActivity.class);
                intent.putExtra("indsubject", subjectArrayList.get(position));
                activity.startActivity(intent);

            }
        });
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        Button subject_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subject_button = itemView.findViewById(R.id.subject_button);
        }
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }
}
