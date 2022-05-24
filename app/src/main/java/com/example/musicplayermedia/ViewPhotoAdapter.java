package com.example.musicplayermedia;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ViewPhotoAdapter extends PagerAdapter {

    private Context mContext;
    static ArrayList<MusicFiles> photoFiles;
    View view;

    public ViewPhotoAdapter(Context mContext, ArrayList<MusicFiles> photoFiles) {
        this.mContext = mContext;
        this.photoFiles = photoFiles;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        view = LayoutInflater.from(mContext).inflate(R.layout.image_view_item, container, false);
        ImageView image_view_photo = view.findViewById(R.id.image_view_photo);
        byte[] image = getAlbum(photoFiles.get(position).getPath());
        if(image != null)
        {
            Glide.with(mContext).load(image).into(image_view_photo);
        }
        else
        {
            Glide.with(mContext).load(R.drawable.timthumb).into(image_view_photo);
        }
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MusicPlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return photoFiles.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
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
