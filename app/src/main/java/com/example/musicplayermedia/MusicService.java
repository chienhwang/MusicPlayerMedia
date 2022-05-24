package com.example.musicplayermedia;

import static com.example.musicplayermedia.ApplicationClass.ACTION_NEXT;
import static com.example.musicplayermedia.ApplicationClass.ACTION_PLAY;
import static com.example.musicplayermedia.ApplicationClass.ACTION_PREVIOUS;
import static com.example.musicplayermedia.ApplicationClass.CHANNEL_ID_2;
import static com.example.musicplayermedia.MusicPlayerActivity.listSongs;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    Uri uri;
    int position = -1;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    MediaSessionCompat mediaSessionCompat;
    ActionPlaying actionPlaying;
    public static final String MUSIC_LAST_FLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_FILE";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String SONG_NAME = "SONG_NAME";

    @Override
    public void onCreate() {
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public class MyBinder extends Binder{
        MusicService getService() {
                return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if(myPosition != -1)
        {
            playMedia(myPosition);
        }
        if(actionName != null)
        {
            switch (actionName)
            {
                case "playPause":
                    Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
                    playPauseBtnClicked();

                    break;
                case "next":
                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                    nextBtnClicked();
                    break;
                case "previous":
                    Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
                    previousBtnClicked();
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else
        {
            createMediaPlayer(position);
            mediaPlayer.start();
        }

    }

    void start()
    {
        mediaPlayer.start();
    }
    Boolean isPlaying()
    {
       return mediaPlayer.isPlaying();
    }
    void stop()
    {
        mediaPlayer.stop();
    }
    void release()
    {
        mediaPlayer.release();
    }
    int getDuration()
    {
        return mediaPlayer.getDuration();
    }
    void seekTo(int position)
    {
        mediaPlayer.seekTo(position);
    }
    int getCurrentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }
    void createMediaPlayer(int positionInner)
    {
        position = positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_FLAYED, MODE_PRIVATE)
                .edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST_NAME, musicFiles.get(position).getArtist());
        editor.putString(SONG_NAME, musicFiles.get(position).getTitle());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }
    void pause()
    {
        mediaPlayer.pause();
    }
    void onCompleted()
    {
        mediaPlayer.setOnCompletionListener(MusicService.this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(actionPlaying != null )
        {

            actionPlaying.nextThreadBtnClicked();
            if(mediaPlayer != null) {
                createMediaPlayer(position);
                onCompleted();
                mediaPlayer.start();
            }
        }

    }


    int getAudioSessionId()
    {
       return mediaPlayer.getAudioSessionId();
    }

    void setCallBack(ActionPlaying actionPlaying)
    {
        this.actionPlaying = actionPlaying;
    }

    void showNotification(int playPauseBtn)
    {
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0 ,
                intent,
                0);
        Intent preIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0 ,
                preIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0 ,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0 ,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;
        picture = getAlbum(listSongs.get(position).getPath());
        Bitmap thumb = null;
        if(picture != null)
        {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        }
        Notification notification = new NotificationCompat.Builder(MusicService.this,
                CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn).setLargeIcon(thumb)
                .setContentTitle(listSongs.get(position).getTitle())
                .setContentText(listSongs.get(position).getArtist())
                .addAction(R.drawable.ic_previous, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_next, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .build();
        startForeground(2, notification);
    }

    private byte[] getAlbum(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    void nextBtnClicked()
    {
        if(actionPlaying != null)
        {
            actionPlaying.nextThreadBtnClicked();
        }
    }
    void previousBtnClicked()
    {
        if(actionPlaying != null)
        {
            actionPlaying.preThreadBtnClicked();
        }
    }
    void playPauseBtnClicked()
    {
        if(actionPlaying != null)
        {
            actionPlaying.playThreadBtnClicked();
        }
    }
}
