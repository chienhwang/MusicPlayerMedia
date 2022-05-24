package com.example.musicplayermedia;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayermedia.HomeActivity.ARTIST_TO_FRAG;
import static com.example.musicplayermedia.HomeActivity.PATH_TO_FRAG;
import static com.example.musicplayermedia.HomeActivity.SHOW_MINI_PLAYER;
import static com.example.musicplayermedia.HomeActivity.SONG_NAME;
import static com.example.musicplayermedia.HomeActivity.SONG_NAME_TO_FRAG;
import static com.example.musicplayermedia.HomeActivity.musicFiles;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class Fragment_layout_player extends Fragment implements ServiceConnection {

    ImageView art_music, next_bottom, play_bottom;
    TextView text_song_name_bottom, text_art_name_bottom;
    MusicService musicService;
    public static final String MUSIC_LAST_FLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_FILE";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String SONG_NAME = "SONG_NAME";

    public Fragment_layout_player() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_layout_player, container, false);
        art_music = view.findViewById(R.id.art_music);
        next_bottom = view.findViewById(R.id.next_bottom);
        play_bottom = view.findViewById(R.id.play_bottom);
        text_song_name_bottom = view.findViewById(R.id.text_song_name_bottom);
        text_art_name_bottom = view.findViewById(R.id.text_art_name_bottom);
         view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
              intent.putExtra("position", musicService.position);
              getContext().startActivity(intent);
          }
         });
        next_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(musicService != null)
                    {
                        musicService.nextBtnClicked();
                        if(getActivity() != null)
                        {
                            SharedPreferences.Editor editor = getActivity().
                                    getSharedPreferences(MUSIC_LAST_FLAYED, MODE_PRIVATE)
                                    .edit();
                            editor.putString(MUSIC_FILE, musicService.musicFiles
                                    .get(musicService.position).getPath());
                            editor.putString(ARTIST_NAME, musicService.musicFiles
                                    .get(musicService.position).getArtist());
                            editor.putString(SONG_NAME, musicService.musicFiles
                                    .get(musicService.position).getTitle());
                            editor.apply();
                            SharedPreferences sharedPreferences = getActivity().
                                    getSharedPreferences(MUSIC_LAST_FLAYED, MODE_PRIVATE);
                            String path = sharedPreferences.getString(MUSIC_FILE, null);
                            String artist = sharedPreferences.getString(ARTIST_NAME,null);
                            String song_name = sharedPreferences.getString(SONG_NAME,null);
                            if(path != null)
                            {
                                SHOW_MINI_PLAYER = true;
                                PATH_TO_FRAG = path;
                                ARTIST_TO_FRAG = artist;
                                SONG_NAME_TO_FRAG = song_name;
                            }else
                            {
                                SHOW_MINI_PLAYER = false;
                                PATH_TO_FRAG = null;
                                ARTIST_TO_FRAG = null;
                                SONG_NAME_TO_FRAG = null;
                            }
                            if(SHOW_MINI_PLAYER)
                            {
                                if(PATH_TO_FRAG != null) {
                                    byte[] art = getAlbum(PATH_TO_FRAG);
                                    if(art != null)
                                    {
                                        Glide.with(getContext()).load(art).into(art_music);
                                    }else
                                    {
                                        Glide.with(getContext()).load(R.drawable.timthumb).into(art_music);
                                    }
                                    text_song_name_bottom.setText(SONG_NAME_TO_FRAG);
                                    text_art_name_bottom.setText(ARTIST_TO_FRAG);
                                }
                            }
                        }
                }
                }catch (Exception e)
                {
                    Toast.makeText(getContext(), "Hãy chọn mở bài hát !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        play_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try {
                   if(musicService != null)
                   {

                       musicService.playPauseBtnClicked();
                       if(musicService.isPlaying())
                       {
                           play_bottom.setImageResource(R.drawable.ic_pause);
                       }
                       else
                       {
                           play_bottom.setImageResource(R.drawable.ic_play);

                       }
                   }
               }catch (Exception e)
               {
                   Toast.makeText(getContext(), "Hãy chọn mở bài hát !", Toast.LENGTH_SHORT).show();
               }


            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SHOW_MINI_PLAYER)
        {
            if(PATH_TO_FRAG != null) {
                byte[] art = getAlbum(PATH_TO_FRAG);
                if(art != null)
                {
                    Glide.with(getContext()).load(art).into(art_music);
                }else
                {
                    Glide.with(getContext()).load(R.drawable.timthumb).into(art_music);
                }
                text_song_name_bottom.setText(SONG_NAME_TO_FRAG);
                text_art_name_bottom.setText(ARTIST_TO_FRAG);
                Intent intent = new Intent(getContext(), MusicService.class);
                if(getContext() != null)
                {
                    getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getContext() != null)
        {
            getContext().unbindService(this);
        }
    }

    private byte[] getAlbum(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
}