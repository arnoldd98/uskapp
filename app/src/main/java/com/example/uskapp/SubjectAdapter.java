package com.example.uskapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Random;

public class SubjectAdapter extends RecyclerView.Adapter {

    private ArrayList<String> subjectArrayList;
    private Context context;
    private ArrayList<Integer> androidcolors;

    public SubjectAdapter(Context context, ArrayList<String> subjectArrayList, ArrayList<Integer> androidcolors ) {
        this.androidcolors = androidcolors;
        this.subjectArrayList = subjectArrayList;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View myOwnView = layoutInflater.inflate(R.layout.recyclerlayout, parent, false);

        ViewHolder viewHolder = new ViewHolder(myOwnView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.textView.setText(subjectArrayList.get(position));
        viewHolder.Button.setBackgroundColor(androidcolors.get(position));
        //viewHolder.imageButton.setImageResource(R.drawable.ic_launcher_foreground);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        Button Button;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Button = itemView.findViewById(R.id.subjectView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

}

