package com.z_waligorski.przyrodnik;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// Adapter for RecyclerView wit onClickListener interface
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public interface ClickListener {
        void onItemClicked(int position, View view);
    }

    private static ClickListener clickListener;
    private ArrayList<Observation> observationList;
    public RecyclerAdapter(ArrayList<Observation> list) {
        this.observationList = list;
    }

    // Inflate single item for RecyclerView and create ViewHolder object
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Load data to proper views
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Observation observation = observationList.get(position);

        holder.noteTitle.setText(observation.getNoteTitle().toString());
        holder.date.setText(observation.getDate().toString());
        holder.noteText.setText(observation.getNote().toString());
        holder.noteId.setText(Integer.toString(observation.getId()));

        // Create thumbnail and load it to ImageView
        Uri photoUri = Uri.parse(observation.getPhoto());
        Bitmap photo = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photoUri.getPath()), 75, 75);
        holder.thumbnail.setImageBitmap(photo);
    }

    @Override
    public int getItemCount() {
        return observationList.size();
    }

    // Initialize views that will be needed for displaying data
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView noteTitle;
        public TextView date;
        public TextView noteText;
        public ImageView thumbnail;
        public TextView noteId;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            noteTitle = (TextView) itemView.findViewById(R.id.recycler_note_title);
            date = (TextView) itemView.findViewById(R.id.recycler_note_date);
            noteText = (TextView) itemView.findViewById(R.id.recycler_note_text);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            noteId = (TextView) itemView.findViewById(R.id.recycler_note_id);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClicked(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerAdapter.clickListener = clickListener;
    }
}
