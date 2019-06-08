package org.kllbff.mygallery;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.kllbff.mygallery.photos.Album;

/**
 * Реализует адаптер для обеспечения {@link RecyclerView} доступом к данным альбома
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    /**
     * Предоставляет доступ к ImageView одного фото альбома
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView view;

        /**
         * Инициализирует объект созданным ранее экземпляром {@link ImageView}
         *
         * @param itemView Экземпляр элемента графического интерфейса
         */
        public MyViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            this.view = itemView;
        }

        /**
         * Устанавливает ранее заданному экземпляру {@link ImageView} картинку
         *
         * @param bmp изображение в виде экземпляра {@link Bitmap}
         */
        private void setPhoto(Bitmap bmp) {
            this.view.setImageBitmap(bmp);
        }
    }

    private Album album;

    /**
     * Инициализирует адаптер, &quot;привязавая&quot; его к указанному альбому
     *
     * @param album альбом
     */
    public GalleryAdapter(Album album) {
        this.album = album;
    }

    /**
     * Создает для каждого загруженного фото из альбома экземпляр ImageView и MyViewHolder для него
     *
     * @param parent контейнер для ImageView, используется для получения контекста при создании ImageView
     * @param viewType тип элемента, по умолчанию для всех элементов = 0
     * @return экземпляр MyViewHolder связанный с созданным ImageView
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView view = new ImageView(parent.getContext());
        return new MyViewHolder(view);
    }

    /**
     * Возвращает количество загруженных на устройство фото, полученное с помощью {@link Album#getCount()}
     *
     * @return количество загруженных на устройство фото
     */
    @Override
    public int getItemCount() {
        return album.getCount();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setPhoto(album.getPhoto(position));
    }
}
