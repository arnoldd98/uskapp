package com.example.uskapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SubjectAdapter extends RecyclerView.Adapter {

    private ArrayList<String> subjectArrayList;
    private Context context;
    private boolean is_tag = false;

    public SubjectAdapter(Context context, ArrayList<String> subjectArrayList, boolean is_tag) {
        this.context = context;
        this.subjectArrayList = subjectArrayList;
        this.is_tag = is_tag;
    }

    public SubjectAdapter(Context context, ArrayList<String> subjectArrayList) {
        this.subjectArrayList = subjectArrayList;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (!is_tag) {
            View myOwnView = layoutInflater.inflate(R.layout.recyclerlayout, parent, false);
            ViewHolder viewHolder = new ViewHolder(myOwnView);

            return viewHolder;
        } else {
            View tag_view = layoutInflater.inflate(R.layout.tag_selector_view, parent, false);
            SmolTagViewHolder tag_view_holder = new SmolTagViewHolder(tag_view);

            return tag_view_holder;
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (is_tag) {
            SmolTagViewHolder tag_view_holder = (SmolTagViewHolder) holder;
            tag_view_holder.tag_name_textview.setText(subjectArrayList.get(position));
            return;
        }
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.textView.setText(subjectArrayList.get(position));
        viewHolder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    private class SmolTagViewHolder extends SubjectAdapter.ViewHolder {
        TextView tag_name_textview;

        public SmolTagViewHolder(@NonNull View itemView) {
            super(itemView);
            tag_name_textview = itemView.findViewById(R.id.tag_name_textview);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.subjectView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

}

