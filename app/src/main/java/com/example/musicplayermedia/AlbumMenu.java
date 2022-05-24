package com.example.musicplayermedia;

import static com.example.musicplayermedia.HomeActivity.musicFiles;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumMenu extends AppCompatActivity {
    RecyclerView recycle_album_image;
    ImageView album_image;
    String albumName;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumMenuAdapter adapterAlbum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_menu);
        recycle_album_image = findViewById(R.id.recycle_album_image);
        album_image = findViewById(R.id.album_image);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0;
        for(int i = 0; i < musicFiles.size(); i++ )
        {
            if(albumName.equals(musicFiles.get(i).getAlbum()))
            {
                albumSongs.add(j, musicFiles.get(i));
                j++;
            }
        }
        byte[] image = getAlbum(albumSongs.get(0).getPath());
        if(image != null)
        {
            Glide.with(getApplicationContext()).load(image).into(album_image);

        }else {
            Glide.with(getApplicationContext()).load(R.drawable.timthumb).into(album_image);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumSongs.size() < 1))
        {
            adapterAlbum = new AlbumMenuAdapter(this, albumSongs);
            recycle_album_image.setAdapter(adapterAlbum);
            recycle_album_image.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false));
        }
    }

    private byte[] getAlbum(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return  art;
    }
}