package org.kllbff.mygallery.photos;

import android.graphics.Bitmap;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kllbff.mygallery.MyGalleryApplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 */
public class VkPhotos {
    /**
     * Количество фотографий, загружаемых за один вызов метода {@link #downloadAlbumPhotos()}
     */
    public static final int DOWNLOADING_PHOTO_COUNT = 25;
    private static VkPhotos instance;

    /**
     * Вовзвращает текущий экземпляр класса
     *
     * @return текущий экземпляр класса
     */
    public static VkPhotos getInstance() {
        if(instance == null) {
            instance = new VkPhotos();
        }

        return instance;
    }

    private ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private Album[] albums;
    private Album current;
    private boolean alreadyLoading;

    private VkPhotos() {}

    /**
     * Позволяет загрузить информацию об альбомах пользователя с сервера вконтакте
     * <p>VkApi SDK позволяет выполнять запросы асинхронно, однако для упрощения работы (отпадает
     *    необходимость реализации слушателей) используется синхронная обработка запроса</p>
     * <p>В качестве результата поступает JSON-объект, содержащий одно едиственное свойство
     *    &quot;response&quot;, хранящее JSON-объект ответа. Ответ содержит множество информации об
     *    альбомах, однако в данном приложении используются только два:
     *    <ul>
     *        <li>Количество альбомов,</li>
     *        <li>Список альбомов.</li>
     *    </ul></p>
     * @see <a href="https://vk.com/dev/android_sdk">VK API Android SDK</a>
     *
     * @throws JSONException ошибка при разборе ответа сервера
     */
    public void loadAlbumsList() throws JSONException {
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
        }
    }

    /**
     * Устанавливает текущий альбом используемый для подкачки файлов
     *
     * @param album новый текущий альбом
     */
    public void setCurrentAlbum(Album album) {
        current = album;
    }

    /**
     * Возвращает текущий альбом используемый для подкачки файлов
     *
     * @return текущий альбом
     */
    public Album getCurrentAlbum() {
        return current;
    }

    /**
     * Возвращает массив альбомов, полученный ранее методом {@link #loadAlbumsList()}
     *
     * @return массив альбомов
     */
    public Album[] getAlbums() {
        return albums;
    }

    /**
     * Позволяет узнать были ли загружены альбомы с сервера
     *
     * @return true, если загрузка альбомов выполнена, false в остальных случаях
     */
    public boolean isAlbumsLoaded() {
        return albums != null;
    }

    /**
     * Позволяет удалить папку со всеми файлами внутри
     *
     * @param dir папка, которую необходимо удалить
     */
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

    /**
     * Очищает кэш приложения, удаляя все загруженные ранее файлы
     */
    public void clearCache() {
        try {
            File dir = MyGalleryApplication.currentActivity.getCacheDir();
            deleteDir(dir);
        } catch(Exception ioe) {
            Log.e("VkPhotos", "Failed to clear cache", ioe);
        }
    }

    /**
     * Позволяет выполнить загрузку новой &quot;порции&quot; фотографий
     *
     * @throws JSONException ошибка разбора ответа сервера
     * @throws IOException ошибка создания URL на основе ответа сервера
     */
    public void downloadAlbumPhotos() throws JSONException, IOException {
        if(alreadyLoading) {
            return;
        }

        alreadyLoading = true;
        Log.i("VkPhotos", "Loading album");
        ArrayList<Future<Bitmap>> array = new ArrayList<>();

        VKRequest request = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, current.getId(), VKApiConst.OFFSET, current.getCount(), VKApiConst.COUNT, DOWNLOADING_PHOTO_COUNT));
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
            try {
                current.addPhoto(future.get());
            } catch(ExecutionException | InterruptedException e) {
                Log.e("VkPhotos", "Failed to get Bitmap from PhotoDownloader", e);
            }
        }

        alreadyLoading = false;
    }
}
