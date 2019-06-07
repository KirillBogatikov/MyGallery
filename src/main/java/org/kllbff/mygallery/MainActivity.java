package org.kllbff.mygallery;

import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.SubMenu;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Menu;
import android.widget.TextView;

import org.json.JSONObject;
import org.kllbff.mygallery.photos.Album;
import org.kllbff.mygallery.photos.VkPhotos;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private VkPhotos photos;
    private Album[] albums;
    private Album album;
    private GalleryAdapter adapter;
    private boolean firstLaunch = false;
    private MenuItem exit, exitVk, clearCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photos = VkPhotos.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        SubMenu subMenu = menu.addSubMenu(R.string.label_albums);

        albums = photos.getAlbums();
        for(Album album : albums) {
            subMenu.add(album.getName());
        }

        clearCache = menu.add("Очистить кэш");
        exitVk = menu.add("Выход из аккаунта");
        exit = menu.add(R.string.label_exit);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        loadUserName();

        final Handler uiHandler = new Handler();

        if(getIntent().getBooleanExtra("AlbumLoaded", false)) {
            RecyclerView recycler = findViewById(R.id.galleryView);
            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(firstLaunch || (dy < recyclerView.getHeight() * 2 / 3 && album.getCount() == album.getSize())) {
                        firstLaunch = false;
                        return;
                    }

                    try {
                        photos.downloadAlbumPhotos();
                        uiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTitle();
                                adapter.notifyDataSetChanged();
                            }
                        }, 500);

                    } catch (Exception e) {
                        Log.e("Gallery", "Failed to load a pert of album", e);
                    }
                }
            });
            recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

            album = VkPhotos.getInstance().getCurrentAlbum();
            adapter = new GalleryAdapter(album, 450);
            recycler.setAdapter(adapter);
            updateTitle();
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void updateTitle() {
        setTitle(album.getName() + " " + album.getCount() + "/" + album.getSize());
    }

    private void loadUserName() {
        NetworkThread.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                VKRequest request = VKApi.users().get();
                request.executeSyncWithListener(null);
                try {
                    JSONObject response = request.response.get().json.getJSONArray("response").getJSONObject(0);
                    final String name = response.getString("first_name");
                    final String surname = response.getString("last_name");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = findViewById(R.id.userName);
                            textView.setText(name + " " + surname);
                        }
                    });

                } catch(Exception e) {
                    Log.e("Gallery", "Failed to fetch user name", e);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setResult(-2);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Album target = null;
        for(Album album : albums) {
            if(album.getName().contentEquals(item.getTitle())) {
                target = album;
            }
        }

        if(target == null) {
            if(exit.equals(item)) {
                Log.i("Gallery", "Exit");
                setResult(-2);
            } else if(exitVk.equals(item)) {
                Log.i("Gallery", "Exit account");
                VKSdk.logout();
            } else {
                Log.i("Gallery", "Clean cache");
                photos.clearCache();
            }
            finish();
        } else if(!target.equals(album)) {
            Log.i("Gallery", "Show " + target);
            if(album != null) {
                album.recycle();
            }
            photos.setCurrentAlbum(target);
            setResult(-3);
            finish();
        }

        return true;
    }
}
