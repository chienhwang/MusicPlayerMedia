package com.example.musicplayermedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumMenuAdapter extends RecyclerView.Adapter<AlbumMenuAdapter.MyHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;
    public AlbumMenuAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_album_items, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nameSongs.setText(albumFiles.get(position).getTitle());

        byte[] image = getAlbum(albumFiles.get(position).getPath());
        if(image != null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.musicImage);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.timthumb).into(holder.musicImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MusicPlayerActivity.class);
                intent.putExtra("sender", "albumMenu");
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView musicImage;
        TextView nameSongs;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            musicImage = itemView.findViewById(R.id.musicImage);
            nameSongs = itemView.findViewById(R.id.nameSongs);

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
