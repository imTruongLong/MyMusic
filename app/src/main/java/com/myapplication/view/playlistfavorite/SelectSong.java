package com.myapplication.view.playlistfavorite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.R;
import com.myapplication.adapter.OnSelectSongListener;
import com.myapplication.adapter.SelectSongAdapter;
import com.myapplication.database.PlaylistDB;
import com.myapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SelectSong extends AppCompatActivity implements OnSelectSongListener {
    private static final String KEY_TABLE_NAME = "list_song";
    RecyclerView recyclerView;
    private List<Song> listSongs = new ArrayList<>();
    private List<Song> list = new ArrayList<>();
    private SelectSongAdapter selectSongAdapter;
    private PlaylistDB playlistDB;

    public static void lunch(Context context, String tableName) {
        Intent intent = new Intent(context, SelectSong.class);
        intent.putExtra(KEY_TABLE_NAME, tableName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_song_layout);

        initViews();
        loadSong();
        setAdapter();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.list_song);
        ImageView ivBack = findViewById(R.id.iv_back);
        ImageView imvAddSong = findViewById(R.id.add_song);


        ivBack.setOnClickListener(view -> onBackPressed());

        imvAddSong.setOnClickListener(view -> {
            createPlaylist();
            onBackPressed();
        });
    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        selectSongAdapter = new SelectSongAdapter(listSongs, this);
        selectSongAdapter.setOnPlaylistListener(this);
        recyclerView.setAdapter(selectSongAdapter);
        selectSongAdapter.notifyDataSetChanged();
    }

    private void createPlaylist() {
        String tableName = getIntent().getStringExtra(KEY_TABLE_NAME);

        String createTable = "CREATE TABLE IF NOT EXISTS " + "'" + tableName + "'" +
                "(songName Text PRIMARY KEY, artistName Text, urlSong Text, album Text, Duration Text)";

        playlistDB = new PlaylistDB(this, "playlist.db", null, 1);
        playlistDB.queryData(createTable);

        for (int i = 0; i < list.size(); i++) {
            String addSong = "INSERT INTO " + "'" + tableName + "' " +
                    "VALUES (" + "'" + list.get(i).getTitle() + "', " +
                    "'" + list.get(i).getArtist() + "', " +
                    "'" + list.get(i).getPath() + "', " +
                    "'" + list.get(i).getAlbum() + "', " +
                    "'" + list.get(i).getIcon() + "')";
            playlistDB.queryData(addSong);
        }
    }

    private void loadSong() {
        listSongs.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor c = getContentResolver().query(uri, null, null, null, null); //order by
        if (c != null) {
            int titleIndex = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIndex = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int dataIndex = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String title = c.getString(titleIndex);
                String artist = c.getString(artistIndex);
                String album = c.getString(albumIndex);
                String path = c.getString(dataIndex);
                Song songModel = new Song(title, path, album, artist, "author",
                        "composor", getIconSong(Uri.parse(path)));
                listSongs.add(songModel);
                c.moveToNext();
            }
            c.close();
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

    public void onPlaylist(List<Song> listSong) {
        list = listSong;
    }
}
