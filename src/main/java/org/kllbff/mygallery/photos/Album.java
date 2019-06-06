package org.kllbff.mygallery.photos;

import android.graphics.Bitmap;

import org.json.JSONException;

import java.io.Serializable;

public class Album implements Serializable {
    private Bitmap[] photos;
    private Bitmap cover;
    private String id;
    private int offset;
    private String name;

    public Album(String name, String id, int size, Bitmap cover) throws JSONException {
        this.name = name;
        this.id = id;
        this.photos = new Bitmap[size];
        this.cover = cover;
        this.offset = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return photos.length;
    }

    public int getOffset() {
        return offset;
    }

    public Bitmap getPhoto(int index) {
        return photos[index];
    }

    public void addPhoto(Bitmap photo) {
        photos[offset++] = photo;
    }

    public Bitmap getCover() {
        return cover;
    }

    public String toString() {
        return name + " (" + photos.length + " фото)";
    }
}
