package com.example.uskapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter {

    private ArrayList<Bitmap> subjectBitmaps;
    private ArrayList<String> subjectArrayList;
    private Context context;

    public Adapter(Context context, ArrayList<String> subjectArrayList, ArrayList<Bitmap> subjectBitmaps) {
        this.context = context;
        this.subjectBitmaps =subjectBitmaps;
        this.subjectArrayList = subjectArrayList;
    }

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
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.textView.setText(subjectArrayList.get(position));
        try{
            viewHolder.imageView.setImageBitmap(subjectBitmaps.get(position));
        } catch (Exception e){
            viewHolder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }
        //viewHolder.imageView.setImageResource(R.drawable.ic_launcher_foreground);


        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, viewHolder.textView.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,TestSubjectActivity.class);
                intent.putExtra("indsubject", viewHolder.textView.getText().toString());
                context.startActivity(intent);
            }
        });
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
