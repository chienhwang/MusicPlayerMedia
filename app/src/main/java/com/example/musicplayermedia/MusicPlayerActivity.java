package com.example.musicplayermedia;

import static com.example.musicplayermedia.AlbumMenuAdapter.albumFiles;
import static com.example.musicplayermedia.HomeActivity.musicFiles;
import static com.example.musicplayermedia.HomeActivity.repeatBoolean;
import static com.example.musicplayermedia.HomeActivity.shuffleBoolean;

import androidx.appcompat.app.AppCompatActivity;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;

import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.util.ArrayList;
import java.util.Random;

public class MusicPlayerActivity extends AppCompatActivity
        implements ActionPlaying, ServiceConnection {

    ImageView image_expand, image_music, image_shuffle, image_play, image_pre, image_next, image_repeat;
    TextView textTimeStart, textEndTime, song_name, song_art;
    SeekBar simpleSeekBar;
    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    private Handler handler = new Handler();
    private Thread playThread, preThread, nextThread;
    public BarVisualizer barVisualizer;
    MusicService mService;


    @Override
    protected void onDestroy() {
        if (barVisualizer != null)
            barVisualizer.release();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        intentView();
        getIntentMenthod();
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mService != null && b)
                {
                    mService.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mService != null)
                {
                    int mCurrentPosition = mService.getCurrentPosition()/1000;
                    simpleSeekBar.setProgress(mCurrentPosition);
                    textTimeStart.setText(formattedTime(mCurrentPosition));

                }
                handler.postDelayed(this, 1000);

            }
        });

        image_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean)
                {
                    shuffleBoolean = false;
                    image_shuffle.setImageResource(R.drawable.ic_shuffle);
                }
                else
                {
                    shuffleBoolean = true;
                    image_shuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        image_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean)
                {
                    repeatBoolean = false;
                    image_repeat.setImageResource(R.drawable.ic_repeat);
                }
                else
                {
                    repeatBoolean = true;
                    image_repeat.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });

        image_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void onResume() {
        Intent intent = new Intent(MusicPlayerActivity.this, MusicService.class);
        bindService(intent, MusicPlayerActivity.this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        preThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(MusicPlayerActivity.this);
    }

    private void preThreadBtn() {
        preThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                image_pre.setOnClickListener(view -> preThreadBtnClicked());
            }
        };
        preThread.start();
    }

    public void preThreadBtnClicked() {
        if(mService.isPlaying())
        {
            mService.stop();
            mService.release();
            if (shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size()-1) : (position - 1));
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            song_name.setText(listSongs.get(position).getTitle());
            song_art.setText(listSongs.get(position).getArtist());
            mService.createMediaPlayer(position);
            metaData(uri);
            simpleSeekBar.setMax(mService.getDuration()/1000);
            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mService != null)
                    {
                        int mCurrentPosition = mService.getCurrentPosition()/1000;
                        simpleSeekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mService.onCompleted();
            mService.showNotification(R.drawable.ic_pause);
            image_play.setImageResource(R.drawable.ic_pause);
            startAnimation(image_music);
            int audioSessionId = mService.getAudioSessionId();
            if (audioSessionId != -1) {
                barVisualizer.setAudioSessionId(audioSessionId);
            }
            mService.start();
        }
        else
        {
            mService.stop();
            mService.release();
            if (shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size()-1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            song_name.setText(listSongs.get(position).getTitle());
            song_art.setText(listSongs.get(position).getArtist());
            mService.createMediaPlayer(position);
            metaData(uri);
            simpleSeekBar.setMax(mService.getDuration()/1000);
            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mService != null)
                    {
                        int mCurrentPosition = mService.getCurrentPosition()/1000;
                        simpleSeekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mService.onCompleted();
            int audioSessionId = mService.getAudioSessionId();
            if (audioSessionId != -1) {
                barVisualizer.setAudioSessionId(audioSessionId);
            }
            mService.showNotification(R.drawable.ic_play);
            image_play.setImageResource(R.drawable.ic_play);
            startAnimation(image_music);

        }

    }

    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                image_next.setOnClickListener(view -> nextThreadBtnClicked());
            }
        };
        nextThread.start();
    }

    public void nextThreadBtnClicked() {
        if(mService.isPlaying())
        {
            mService.stop();
            mService.release();
           if (shuffleBoolean && !repeatBoolean)
           {
               position = getRandom(listSongs.size() - 1);
           }
            else if(!shuffleBoolean && !repeatBoolean)
           {
               position = ((position + 1) % listSongs.size());
           }

           uri = Uri.parse(listSongs.get(position).getPath());
           song_name.setText(listSongs.get(position).getTitle());
           song_art.setText(listSongs.get(position).getArtist());
           mService.createMediaPlayer(position);
           metaData(uri);
            simpleSeekBar.setMax(mService.getDuration()/1000);
            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mService != null)
                    {
                        int mCurrentPosition = mService.getCurrentPosition()/1000;
                        simpleSeekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mService.showNotification(R.drawable.ic_pause);
            image_play.setImageResource(R.drawable.ic_pause);
            mService.onCompleted();
            startAnimation(image_music);
            int audioSessionId = mService.getAudioSessionId();
            if (audioSessionId != -1) {
                barVisualizer.setAudioSessionId(audioSessionId);
            }
            mService.start();
        }
        else
        {
            mService.stop();
            mService.release();
            if (shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            song_name.setText(listSongs.get(position).getTitle());
            song_art.setText(listSongs.get(position).getArtist());
            mService.createMediaPlayer(position);
            metaData(uri);
            simpleSeekBar.setMax(mService.getDuration()/1000);
            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mService != null)
                    {
                        int mCurrentPosition = mService.getCurrentPosition()/1000;
                        simpleSeekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mService.showNotification(R.drawable.ic_play);
            image_play.setImageResource(R.drawable.ic_play);
            mService.onCompleted();
            int audioSessionId = mService.getAudioSessionId();
            if (audioSessionId != -1) {
                barVisualizer.setAudioSessionId(audioSessionId);
            }
            startAnimation(image_music);
        }

    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                image_play.setOnClickListener(view -> playThreadBtnClicked());
            }
        };
        playThread.start();
    }

    public void playThreadBtnClicked() {
        if(mService.isPlaying())
        {
            image_play.setImageResource(R.drawable.ic_play);
            mService.showNotification(R.drawable.ic_play);
            mService.pause();
            simpleSeekBar.setMax(mService.getDuration() / 1000);

            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mService != null)
                    {
                        int mCurrentPosition = mService.getCurrentPosition()/1000;
                        simpleSeekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

        }
        else
        {
            image_play.setImageResource(R.drawable.ic_pause);
            mService.showNotification(R.drawable.ic_pause);
            mService.start();
            simpleSeekBar.setMax(mService.getDuration() / 1000);

            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mService != null)
                    {
                        int mCurrentPosition = mService.getCurrentPosition()/1000;
                        simpleSeekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

        }
        startAnimation(image_music);
    }

    private String formattedTime(int mCurrentPosition)
    {
        String totalout = "";
        String totalNew = "";
        String seconds= String.valueOf(mCurrentPosition % 60);
        String minutes= String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if(seconds.length() == 1)
        {
            return totalNew;
        }
        else
        {
            return totalout;
        }
    }

    private void getIntentMenthod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if(sender != null && sender.equals("albumMenu"))
        {
            listSongs = albumFiles;
        }
        else {
            listSongs = musicFiles;
        }

        if(listSongs != null)
        {
            image_play.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);

    }

    private void intentView() {
        image_expand = findViewById(R.id.image_expand);
        song_name = findViewById(R.id.song_name);
        song_art = findViewById(R.id.song_art);
        image_music = findViewById(R.id.image_music);
        image_shuffle = findViewById(R.id.image_shuffle);
        image_play = findViewById(R.id.image_play);
        image_pre = findViewById(R.id.image_pre);
        image_next = findViewById(R.id.image_next);
        image_repeat = findViewById(R.id.image_repeat);
        textTimeStart = findViewById(R.id.textTimeStart);
        textEndTime = findViewById(R.id.textEndTime);
        simpleSeekBar = findViewById(R.id.simpleSeekBar);
        barVisualizer = findViewById(R.id.blast);
    }

    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration())/1000;
        textEndTime.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        if(art != null)
        {

            Glide.with(getApplicationContext()).asBitmap().placeholder(R.drawable.img).load(art).into(image_music);

        }
        else
        {
            Glide.with(getApplicationContext()).asBitmap().load(R.drawable.music).into(image_music);

        }

    }

    private void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(image_music,"rotation",
                0f,60f,120f,180f,240f,300f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder myBinder= (MusicService.MyBinder) service;
        mService = myBinder.getService();
        mService.setCallBack(this);
        Toast.makeText(this, "Connected " + mService, Toast.LENGTH_SHORT).show();
        simpleSeekBar.setMax(mService.getDuration()/1000);
        metaData(uri);
        int audioSessionId = mService.getAudioSessionId();
        if (audioSessionId != -1) {
            barVisualizer.setAudioSessionId(audioSessionId);
        }
        song_name.setText(listSongs.get(position).getTitle());
        song_art.setText(listSongs.get(position).getArtist());
        mService.onCompleted();
        mService.showNotification(R.drawable.ic_pause);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }


}