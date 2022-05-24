package com.example.musicplayermedia;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles)
    {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());

        byte[] image = getAlbum(mFiles.get(position).getPath());
        if(image != null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.timthumb).into(holder.album_art);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MusicPlayerActivity.class);
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });

        holder.image_more_vert.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            popupMenu.getMenuInflater().inflate(R.menu.select_delete, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.love:
                        Toast.makeText(mContext, "Bạn chọn yêu thích bài hát này !", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.show_album:
                        Toast.makeText(mContext, "Album Click!!", Toast.LENGTH_SHORT)
                                .show();

                    case R.id.show_nghe_si:
                        Toast.makeText(mContext, "Artits Click!!", Toast.LENGTH_SHORT)
                                .show();
                        break;

                    case R.id.delete:
                        Toast.makeText(mContext, "Delete Click!!", Toast.LENGTH_SHORT)
                                .show();
                        deleteFiles(position, v);
                        break;
                }
                return true;
            });
           /* MenuPopupHelper menuPopupHelper = new MenuPopupHelper(mContext, (MenuBuilder) popupMenu.getMenu(), v);
            menuPopupHelper.setForceShowIcon(true);*/

            popupMenu.show();
        });

    }


    @SuppressLint("NotifyDataSetChanged")
    private void deleteFiles(int position, View v)
    {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Long.parseLong(mFiles.get(position).getId()));
        File file = new File(mFiles.get(position).getPath());
        boolean delete = file.delete();
            if (delete) {
                mContext.getContentResolver().delete(contentUri, null, null);
                mFiles.remove(position);
                notifyItemRemoved(position);
                notifyItemMoved(position, mFiles.size());
                notifyDataSetChanged();
                Snackbar.make(v, "1 File Deleted", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(v, "Can't be Deleted", Snackbar.LENGTH_LONG).show();
            }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView album_art, image_more_vert;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.nameSongs);
            album_art = itemView.findViewById(R.id.musicImage);
            image_more_vert = itemView.findViewById(R.id.image_more_vert);
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
