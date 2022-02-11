package com.myapplication.utils;

import com.myapplication.model.Song;
import com.myapplication.service.MusicService;

import java.util.List;

public class Storage {
    private List<Song> songList;
    private List<Song> shuffleList;
    private MusicService musicService;

    public List<Song> getSongList() {
        return songList;
    }

    public List<Song> getShuffle() {
        return shuffleList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public void setShuffle(List<Song> songList) {
        this.shuffleList = songList;
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }
}
