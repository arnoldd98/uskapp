package com.example.uskapp;

import android.content.Context;
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
    private Context context;

    public TagAdapter(Context context, ArrayList<Tag> tag_list) {
        Toast.makeText(context, "Tag adapter initialized", Toast.LENGTH_SHORT);
        this.tag_list = tag_list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tag_selector_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tag_name_textview.setText(tag_list.get(position).getTagName());
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
