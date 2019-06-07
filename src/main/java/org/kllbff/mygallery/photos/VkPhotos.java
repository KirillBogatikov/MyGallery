package org.kllbff.mygallery.photos;

import android.graphics.Bitmap;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kllbff.mygallery.MyGalleryApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VkPhotos {
    private static VkPhotos instance;

    public static VkPhotos getInstance() {
        if(instance == null) {
            instance = new VkPhotos();
        }

        return instance;
    }

    private HashMap<String, Album> knownAlbums = new HashMap<>();
    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private Album[] albums;
    private Album current;
    private boolean alreadyLoading;

    private VkPhotos() {}

    public void loadAlbumsList() throws Exception {
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from("need_system", 1));
        request.executeSyncWithListener(null);
        JSONObject response = request.response.get().json.getJSONObject("response");
        JSONArray items = response.getJSONArray("items");

        albums = new Album[response.getInt("count")];

        JSONObject albumInfo;
        for(int i = 0; i < albums.length; i++) {
            albumInfo = items.getJSONObject(i);

            String title = albumInfo.getString("title");
            String id = albumInfo.getString("id");
            int size = albumInfo.getInt("size");
            albums[i] = new Album(title, id, size);
            knownAlbums.put(id, albums[i]);
        }
    }

    public void setCurrentAlbum(Album album) {
        current = album;
    }

    public Album getCurrentAlbum() {
        return current;
    }

    public Album[] getAlbums() {
        return albums;
    }

    public boolean isAlbumsLoaded() {
        return albums != null;
    }

    private void deleteDir(File dir) {
        File[] content = dir.listFiles();
        for(File file : content) {
            if(file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }
    }

    public void clearCache() {
        try {
            File dir = MyGalleryApplication.currentActivity.getCacheDir();
            deleteDir(dir);
        } catch(Exception ioe) {
            Log.e("VkPhotos", "Failed to clear cache", ioe);
        }
    }

    public void downloadAlbumPhotos() throws Exception {
        if(alreadyLoading) {
            return;
        }

        alreadyLoading = true;
        Log.i("VkPhotos", "Loading album");
        ArrayList<Future<Bitmap>> array = new ArrayList<>();

        VKRequest request = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, current.getId(), VKApiConst.OFFSET, current.getCount(), VKApiConst.COUNT, 25));
        request.executeSyncWithListener(null);
        VKResponse response = request.response.get();
        JSONArray items = response.json.getJSONObject("response").getJSONArray("items");

        Log.i("VkPhotos", "Found " + items.length() + " images");

        JSONObject item;
        String qualityKey;
        for(int i = 0; i < items.length(); i++) {
            item = items.getJSONObject(i);
            /*if(item.has("photo_807")) {
                qualityKey = "photo_807";
            } else */if(item.has("photo_604")) {
                qualityKey = "photo_604";
            } else {
                qualityKey = "photo_130";
            }

            String url = item.getString(qualityKey);
            Log.i("VkPhotos", "Loading " + i + " image with " + url);

            array.add(threadPool.submit(new PhotoDownloader(url, current.getId(), item.getString("id"))));
        }

        for (Future<Bitmap> future : array) {
            current.addPhoto(future.get());
        }

        alreadyLoading = false;
    }
}
