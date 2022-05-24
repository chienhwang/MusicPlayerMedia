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

public class MoveSongAdapter extends RecyclerView.Adapter<MoveSongAdapter.MoveViewHolder>{

    private Context mContext;
    private ArrayList<MusicFiles> moveMusic;
    View view;
    public MoveSongAdapter(Context mContext, ArrayList<MusicFiles> moveMusic) {
        this.mContext = mContext;
        this.moveMusic = moveMusic;
    }


    @NonNull
    @Override
    public MoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.item_move, parent, false);
        return new MoveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoveViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.text_name_song.setText(moveMusic.get(position).getTitle());
        byte[] image = getAlbum(moveMusic.get(position).getPath());
        if(image != null)
        {
            Glide.with(mContext).load(image).into(holder.round_image_view);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.timthumb).into(holder.round_image_view);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MusicPlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moveMusic.size();
    }

    public static class MoveViewHolder extends RecyclerView.ViewHolder{

        ImageView round_image_view;
        TextView text_name_song;

        public MoveViewHolder(@NonNull View itemView) {
            super(itemView);
            round_image_view = itemView.findViewById(R.id.round_image_view);
            text_name_song = itemView.findViewById(R.id.text_name_song);
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
