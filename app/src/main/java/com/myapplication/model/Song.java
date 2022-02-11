package com.myapplication.model;

import android.graphics.Bitmap;

public class Song {
    public String title, path, album, artist, author,composer;
    private Bitmap icon;
    private boolean isSelected;

    public Song(String title, String path, String album, String artist, String author, String composer, Bitmap icon) {
        this.title = title;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.author = author;
        this.composer = composer;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", author='" + author + '\'' +
                ", composer='" + composer + '\'' +
                ", icon=" + icon +
                '}';
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getAuthor() {
        return author;
    }

    public String getPath() {
        return path;
    }

    public String getComposer() {
        return composer;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
