package org.kllbff.mygallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.kllbff.mygallery.photos.Album;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView view;

        public MyViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            this.view = itemView;
        }

        private void setPhoto(Bitmap bmp, int width) {
            this.view.setImageBitmap(bmp);
        }
    }

    private Album album;
    private int width;
    public GalleryAdapter(Album album, int width) {
        this.album = album;
        this.width = width;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ImageView view = (ImageView)LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_view, null, false);
        ImageView view = new ImageView(parent.getContext());
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return album.getOffset();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setPhoto(album.getPhoto(position), width);
    }
}
