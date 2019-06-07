package org.kllbff.mygallery.photos;

import android.graphics.Bitmap;

import org.json.JSONException;

import java.io.Serializable;

public class Album implements Serializable {
    private Bitmap[] photos;
    private String id;
    private int offset;
    private String name;

    public Album(String name, String id, int size) {
        this.name = name;
        this.id = id;
        this.photos = new Bitmap[size];
        this.offset = 0;
    }

    public void recycle() {
        for(Bitmap bmp : photos) {
            if(bmp != null) {
                bmp.recycle();
            }
        }
        int size = getSize();
        photos = null;
        System.gc();
        photos = new Bitmap[size];
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

    public synchronized void addPhoto(Bitmap photo) {
        photos[offset++] = photo;
    }
}
