package com.myapplication.adapter;

import com.myapplication.model.Song;

import java.util.List;

public interface OnSelectSongListener {
    public void onPlaylist(List<Song> list);
}
