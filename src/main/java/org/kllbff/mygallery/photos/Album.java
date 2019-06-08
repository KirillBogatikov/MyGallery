package org.kllbff.mygallery.photos;

import android.graphics.Bitmap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Упрощенная модель альбома фотографий пользователя Вконтакте
 *
 * Содержит необходимые для использования в данном приложении характеристики:
 * <ul>
 *     <li>id - уникальный идентификатор альбома;</li>
 *     <li>size - кол-во фотографий в альбоме на сервере;</li>
 *     <li>count - кол-во загруженных на устройство фотографий;</li>
 *     <li>name - имя альбома.</li>
 * </ul>
 * Также класс содержит коллекцию экземпляров {@link Bitmap}, представляющих загруженные изображения
 */
public class Album implements Serializable {
    private List<Bitmap> photos;
    private String id;
    private int size;
    private String name;

    /**
     * Инициализирует объект полученными данными
     *
     * @param name имя альбома
     * @param id уникальный идентификатор альбома
     * @param size количество фотографий в альбоме на сервере
     */
    public Album(String name, String id, int size) {
        this.name = name;
        this.id = id;
        this.size = size;
        this.photos = new ArrayList<Bitmap>();
    }

    /**
     * Позволяет высвободить оперативную память, занимаемую данными альбома
     */
    public void recycle() {
        for(Bitmap bmp : photos) {
            if(bmp != null) {
                bmp.recycle();
            }
        }
        photos.clear();
        System.gc();
    }

    /**
     * Возвращает уникальный идентификатор альбома на сервере
     * @return уникальный идентификатор
     */
    public String getId() {
        return id;
    }

    /**
     * Возвращает имя альбома
     * @return имя альбома
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает количество фото в альбоме на сервере
     *
     * @return количество фото в альбоме на сервере
     */
    public int getSize() {
        return size;
    }

    /**
     * Возвращает количество загруженных на устройство фото из этого альбома
     *
     * @return количество загруженных на устройство фото из этого альбома
     */
    public int getCount() {
        return photos.size();
    }

    /**
     * Вовзращает фото по заданному индексу
     *
     * @param index индекс фото в списке, начиная с 0 и до значения {@link #getSize()}
     * @return фото по заданному индексу
     */
    public Bitmap getPhoto(int index) {
        return photos.get(index);
    }

    /**
     * Добавляет в список новое загруженное фото
     *
     * @param photo загруженное на устройство фото
     */
    public synchronized void addPhoto(Bitmap photo) {
        photos.add(photo);
    }
}
