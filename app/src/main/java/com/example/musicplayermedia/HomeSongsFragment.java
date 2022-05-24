package com.example.musicplayermedia;


import static com.example.musicplayermedia.HomeActivity.albums;
import static com.example.musicplayermedia.HomeActivity.musicFiles;
import static com.example.musicplayermedia.ViewPhotoAdapter.photoFiles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.relex.circleindicator.CircleIndicator;


public class HomeSongsFragment extends Fragment {


    RecyclerView recyclerView, recycle_dash_board;
    AlbumAdapter albumAdapter;
    ViewPager view_pager;
    ViewPhotoAdapter viewPhotoAdapter;
    MoveSongAdapter moveSongAdapter;
    CircleIndicator circle_indicator;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(view_pager.getCurrentItem() == (photoFiles.size() - 1))
            {
                view_pager.setCurrentItem(0);
            }
            else {
                view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
            }

        }
    };

    public HomeSongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_songs, container, false);
        view_pager = view.findViewById(R.id.view_pager);
        circle_indicator = view.findViewById(R.id.circle_indicator);
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        if(!(musicFiles.size() < 1)){
            viewPhotoAdapter = new ViewPhotoAdapter(getContext(), albums);
            view_pager.setAdapter(viewPhotoAdapter);
            circle_indicator.setViewPager(view_pager);
            handler.postDelayed(runnable, 3000);
            view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 3000);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            recycle_dash_board = view.findViewById(R.id.recycle_dash_board);
            moveSongAdapter = new MoveSongAdapter(getContext(), musicFiles);
            recycle_dash_board.setAdapter(moveSongAdapter);
            recycle_dash_board.setHasFixedSize(true);
            recycle_dash_board.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false) );

            albumAdapter = new AlbumAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false) );
        }

        return view;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onResume() {
        handler.postDelayed(runnable,3000);
        super.onResume();
    }
}