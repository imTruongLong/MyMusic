package com.myapplication.view.main.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.myapplication.R;
import com.myapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG = "aaa";
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private List<Song> songList;

    public List<Song> getSongList() {
        return songList;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public List<Song> getListSongOffline( Context context ) {
        mContext = context;
        songList = new ArrayList<>();
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String name = MediaStore.Audio.Media.DISPLAY_NAME;
            Cursor c = context.getContentResolver()
                    .query(uri, null,
                            null,
                            null,
                            name + " ASC"); //order by
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
                    Song songModel = new Song (title,path,album,artist,"author",
                                "composor", getIconSong(Uri.parse(path)));
                    songList.add(songModel);
                    Log.i(TAG, "data"+path +"icon "+songModel.getIcon());
                    c.moveToNext();
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songList;
    }

    private Bitmap getIconSong(Uri uri ) {
        Bitmap art = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_demo_endoftime);
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            BitmapFactory.Options bfo = new BitmapFactory.Options();

            mmr.setDataSource(mContext, uri);
            rawArt = mmr.getEmbeddedPicture();

            if (null != rawArt) {
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return art;
    }
}
