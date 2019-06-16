package org.kllbff.mygallery.photos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.kllbff.mygallery.MyGalleryApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Позволяет выполнить загрузку фотографии с сервера в папку кэша приложения
 * <p>Использование интерфейса {@link Callable<Bitmap>} необходимо для запуска одновременной загрузки
 *    нескольких фото в пуле потоков</p>
 *
 * @see Callable
 * @see VkPhotos
 */
public class PhotoDownloader implements Callable<Bitmap> {
    private String album, photo;
    private URL url;

    /**
     * @param url URL-адрес изображения
     * @param album идентификатор альбома
     * @param photo идентификатор фото
     * @throws MalformedURLException в случае некорректного значения <code>url</code>
     */
    public PhotoDownloader(String url, String album, String photo) throws MalformedURLException {
        this.url = new URL(url);
        this.album = album;
        this.photo = photo;
    }

    @Override
    public Bitmap call() throws Exception {
        File file = new File(MyGalleryApplication.currentActivity.getCacheDir() + "/" + album, photo + ".jpg");

        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            if(file.exists() && file.lastModified() > System.currentTimeMillis() - 1000 * 60 * 60 * 10) {
                Log.i("VkPhotos", "Loaded from cache: " + file.getName());
                return BitmapFactory.decodeFile(file.getPath());
            }

            file.getParentFile().mkdirs();
            file.createNewFile();

            Log.i("VkPhotos", "Saving to " + file);
            in = new BufferedInputStream(url.openStream());
            out = new BufferedOutputStream(new FileOutputStream(file));

            Log.i("VkPhotos", "Resources allocated");

            byte[] buffer = new byte[1024];
            int count;
            while((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            Log.i("VkPhotos", "Loaded from VK: " + file);
        } catch(IOException ioe) {
            Log.e("VkPhotos", "Failed download photo", ioe);
        }

        try {
            out.close();
            in.close();
        } catch(IOException | NullPointerException e) {
            Log.e("VkPhotos", "Failed to close resources", e);
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        options.inSampleSize = options.outWidth / 450;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }
}
