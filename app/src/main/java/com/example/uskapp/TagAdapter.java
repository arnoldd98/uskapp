package com.example.uskapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter {

    private ArrayList<Tag> tag_list;
    private Activity activity;
    private boolean is_clickable;

    public TagAdapter(Activity activity, ArrayList<Tag> tag_list) {
        this.tag_list = tag_list;
        this.activity = activity;
        is_clickable = false;
    }

    public TagAdapter(Activity activity, ArrayList<Tag> tag_list, boolean is_clickable) {
        this.tag_list = tag_list;
        this.activity = activity;
        this.is_clickable = is_clickable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.tag_selector_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String current_tag_name = tag_list.get(position).getTagName();
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tag_name_textview.setText(current_tag_name);
        if (is_clickable) {
            viewHolder.tag_name_textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity instanceof HomeActivity) {
                        ((HomeActivity) activity).indicateCurrentSearchTerm(current_tag_name, true);
                    } else {
                        Intent search_tag_intent = new Intent(activity, HomeActivity.class);
                        search_tag_intent.putExtra("searchtag", current_tag_name);
                        activity.startActivity(search_tag_intent);
                    }
                }
            });
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tag_name_textview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag_name_textview = itemView.findViewById(R.id.tag_name_textview);
        }
    }

    @Override
    public int getItemCount() {
        return tag_list.size();
    }
}
