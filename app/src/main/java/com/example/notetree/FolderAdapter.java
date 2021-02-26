package com.example.notetree;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    File[] folders;
    Context context;

    public FolderAdapter(Context context, File[] folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater lF = LayoutInflater.from(context);
        View view = lF.inflate(R.layout.folder_row, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, final int position) {
        if(folders[position].isDirectory()) {
            holder.b.setText(folders[position].getName());
            holder.del.setImageResource(R.drawable.foldersmall);
            MainActivity.currentFile = folders[position];
            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.currentDirectory = new File(folders[position].getPath());
                    refresh();
                }
            });
        }
        else {
            holder.b.setText(folders[position].getName());
            holder.del.setImageResource(R.drawable.textsmall);
            MainActivity.currentFile = folders[position];
            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.currentFile = new File(folders[position].getPath());
                    context.startActivity(new Intent(context, TextEditActivity.class));
                }
            });
        }
        holder.name = folders[position].getName();
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.selectedFile = folders[position];
                MainActivity.main.hidePopups();
                MainActivity.main.settingsVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.length;
    }

    public void refresh() {
        folders = MainActivity.currentDirectory.listFiles();
        notifyDataSetChanged();
        MainActivity.main.hidePopups();
        ((TextView) MainActivity.main.findViewById(R.id.locationText)).setText(MainActivity.currentDirectory.getName());
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {

        Button b;
        String name;
        ImageButton del;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            b = itemView.findViewById(R.id.folderButton);
            del = itemView.findViewById(R.id.folderEdit);
        }
    }
}