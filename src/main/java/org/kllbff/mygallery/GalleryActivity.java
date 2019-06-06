package org.kllbff.mygallery;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.kllbff.mygallery.photos.Album;
import org.kllbff.mygallery.photos.LoadingThread;
import org.kllbff.mygallery.photos.PhotoDownloader;

public class GalleryActivity extends AppCompatActivity {
    private boolean loading = false;
    private GalleryAdapter adapter;
    private RecyclerView galleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryView = findViewById(R.id.galleryView);
        StaggeredGridLayoutManager layout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        galleryView.setLayoutManager(layout);

        PhotoDownloader dl = PhotoDownloader.getInstance();
        final Album album = dl.getAlbum(getIntent().getStringExtra("targetAlbumId"));

        adapter = new GalleryAdapter(album, 450);
        galleryView.setAdapter(adapter);
        galleryView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!loading && album.getOffset() < album.getSize() - 1) {
                    loading = true;
                    LoadingThread.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PhotoDownloader dl = PhotoDownloader.getInstance();
                                dl.downloadAlbum(album, -1);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        loading = false;
                                        Log.i("GalleryActivity", "Part loaded");
                                    }
                                });
                            } catch(Exception e) {
                                Log.e("GalleryActivity", "Failed to load on scrolling event", e);
                            }
                        }
                    });
                }
            }
        });
    }

    public void onBackPressed() {
        finish();
    }
}
