package org.kllbff.mygallery.photos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kllbff.mygallery.MyGalleryApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PhotoDownloader {
    private static PhotoDownloader instance;

    public static PhotoDownloader getInstance() {
        if(instance == null) {
            instance = new PhotoDownloader();
        }

        return instance;
    }

    private HashMap<String, Album> knownAlbums = new HashMap<>();
    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    private PhotoDownloader() {

    }

    public Future<Bitmap> downloadPhoto(final String surl) {
        return threadPool.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                URL url = new URL(surl);
                File file = new File(MyGalleryApplication.currentActivity.getCacheDir(), url.getFile());

                BufferedInputStream in = null;
                BufferedOutputStream out = null;
                try {
                    if(file.exists() && file.lastModified() > System.currentTimeMillis() - 1000 * 60 * 60 * 10) {
                        Log.i("PhotoDownloader", "Loaded from cache: " + url.getFile());

                        return BitmapFactory.decodeFile(file.getPath());
                    }

                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    in = new BufferedInputStream(url.openStream());
                    out = new BufferedOutputStream(new FileOutputStream(file));

                    byte[] buffer = new byte[1024];
                    int count;
                    while((count = in.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                    Log.i("PhotoDownloader", "Loaded from VK: " + url.getFile());
                } catch(IOException ioe) {
                    Log.e("PhotoDownloader", "Failed download photo", ioe);
                }

                out.close();
                in.close();

                return BitmapFactory.decodeFile(file.getPath());
            }
        });
    }

    public Future<Bitmap> downloadPhoto(String albumId, int id) throws Exception {
        VKRequest request = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, albumId, VKApiConst.PHOTO_IDS, id));
        request.executeSyncWithListener(null);
        VKResponse response = request.response.get();

        JSONArray items = response.json.getJSONObject("response").getJSONArray("items");

        JSONObject item = items.getJSONObject(0);
        String qualityKey;

        if(item.has("photo_1280")) {
            qualityKey = "photo_1280";
        } else if(item.has("photo_807")) {
            qualityKey = "photo_807";
        } else if(item.has("photo_604")) {
            qualityKey = "photo_604";
        } else {
            qualityKey = "photo_130";
        }

        return downloadPhoto(item.getString(qualityKey));
    }

    public Album[] loadAlbums() throws Exception {
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from("need_system", 1));
        request.executeSyncWithListener(null);
        JSONObject response = request.response.get().json.getJSONObject("response");
        JSONArray items = response.getJSONArray("items");

        Album[] albums = new Album[response.getInt("count")];

        JSONObject albumInfo;
        for(int i = 0; i < albums.length; i++) {
            albumInfo = items.getJSONObject(i);

            String title = albumInfo.getString("title");
            String id = albumInfo.getString("id");
            int size = albumInfo.getInt("size");
            Bitmap thumb =  downloadPhoto(id, albumInfo.getInt("thumb_id")).get();
            albums[i] = new Album(title, id, size,thumb);
            knownAlbums.put(id, albums[i]);
        }

        return albums;
    }

    public void downloadAlbum(Album album, int count) throws Exception {
        if(count < 0) {
            count = 33;
        }

        ArrayList<Future<Bitmap>> array = new ArrayList<>();

        VKRequest request = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, album.getId(), VKApiConst.OFFSET, album.getOffset(), VKApiConst.COUNT, count));
        request.executeSyncWithListener(null);
        VKResponse response = request.response.get();
        JSONArray items = response.json.getJSONObject("response").getJSONArray("items");

        JSONObject item;
        String qualityKey;
        for(int i = 0; i < items.length(); i++) {
            item = items.getJSONObject(i);
            if(item.has("photo_807")) {
                qualityKey = "photo_807";
            } else if(item.has("photo_604")) {
                qualityKey = "photo_604";
            } else {
                qualityKey = "photo_130";
            }

            array.add(downloadPhoto(item.getString(qualityKey)));
        }

        for (Future<Bitmap> future : array) {
            album.addPhoto(future.get());
        }
    }

    public Album getAlbum(String id) {
        return knownAlbums.get(id);
    }
}
