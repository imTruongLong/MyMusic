package com.myapplication.view.playlistfavorite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.R;
import com.myapplication.adapter.OnPlaylistSongListener;
import com.myapplication.adapter.PlaylistSongAdapter;
import com.myapplication.database.PlaylistDB;
import com.myapplication.model.Song;

import java.util.ArrayList;

public class PlaylistSong extends AppCompatActivity implements OnPlaylistSongListener {
    private static final String KEY_POS = "position";
    private static final String KEY_LIST_PLAYLIST = "playlist_list";
    private ArrayList<Song> playlistSong = new ArrayList();
    private ArrayList<String> playlist = new ArrayList<>();
    private String tableName;
    private Toolbar toolbar;
    private TextView tvSongName;
    private TextView tvArtistName;
    private ImageView ivMusic;
    String mTitle, mSinger;
    Bitmap mIcon;
    private PlaylistDB playlistDatabase;
    private RecyclerView recyclerView;
    private PlaylistSongAdapter playlistSongAdapter;

    public static void launch(Context context, int position, ArrayList<String> playlist){
        Intent intent = new Intent(context, PlaylistSong.class);
        intent.putExtra(KEY_POS, position);
        intent.putStringArrayListExtra(KEY_LIST_PLAYLIST, playlist);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_song);
        initViews();
        loadListSong();
        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListSong();
        setAdapter();
    }

    private void loadListSong(){
        playlistSong.clear();
        playlist = getIntent().getStringArrayListExtra(KEY_LIST_PLAYLIST);
        int position = getIntent().getIntExtra(KEY_POS, 0);
        tableName = playlist.get(position);
        toolbar.setTitle(tableName);
        String sql = "SELECT * FROM " + "'" + tableName + "'";

        playlistDatabase = new PlaylistDB(this, "playlist.db", null, 1);
        Cursor cursor = playlistDatabase.getData(sql);
        while (cursor.moveToNext()){
            String title = cursor.getString(0);
            String path = cursor.getString(1);
            String album = cursor.getString(2);
            String artist = cursor.getString(3);
//            String author = cursor.getString(4);
//            String composer = cursor.getString(5);
            playlistSong.add(new Song(title,artist,path,album,"","composer",getIconSong(Uri.parse(path))));
        }
    }

    private Bitmap getIconSong(Uri uri ) {
        Bitmap art = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_demo_endoftime);
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            BitmapFactory.Options bfo = new BitmapFactory.Options();

            mmr.setDataSource(this, uri);
            rawArt = mmr.getEmbeddedPicture();

            if (null != rawArt) {
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return art;
    }

//    private void updateUISong(Song song){
////        if (state == PLAY) {
////            binding.mediaController.ivPlay.setImageResource(R.drawable.ic_pause);
////            sendDataToService();
////        } else if (state == PAUSE) {
////            binding.mediaController.ivPlay.setImageResource(R.drawable.ic_play);
////            sendDataToService();
////        }
//
//        String songName = song.getTitle();
//        String artistName =song.getArtist();
//        Bitmap icon = song.getIcon();
//
//        tvSongName.setText(songName);
//        tvArtistName.setText(artistName);
//
//        if (icon != null){
//            Glide.with(this).load(song.getIcon()).into(ivMusic);
//        }
//    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        playlistSongAdapter = new PlaylistSongAdapter(playlistSong, getApplicationContext());
        playlistSongAdapter.setOnPlaylistSongListener(this);
        recyclerView.setAdapter(playlistSongAdapter);
        playlistSongAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rv_playlist_song);
        tvSongName= findViewById(R.id.tv_title_song);
        tvArtistName = findViewById(R.id.tv_singer);

        loadListSong();
        toolbar.setOnClickListener(v-> onBackPressed());
    }

    @Override
    public void onPlaylistSong(int pos) {

    }
}
