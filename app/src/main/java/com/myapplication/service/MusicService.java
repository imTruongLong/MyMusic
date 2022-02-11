package com.myapplication.service;


import static com.myapplication.service.NotificationReceiver.ACTION_NEXT;
import static com.myapplication.service.NotificationReceiver.ACTION_PLAY;
import static com.myapplication.service.NotificationReceiver.ACTION_PREV;
import static com.myapplication.utils.App.CHANEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.myapplication.R;
import com.myapplication.model.Song;
import com.myapplication.utils.App;
import com.myapplication.view.main.MainActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MusicService extends Service {

    public static final String ACTION_SEEK = "SEEK";
    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static int IDLE = 0;
    public static int PLAY = 1;
    public static int PAUSE = 2;
    public static MediaPlayer player = new MediaPlayer();
    private IBinder iBinder = new MyBinder();
    private Song currentSong;
    private int index, state;
    private Bitmap mIcon;
    private String mTitle, mSinger;

    private NotificationReceiver receiver;
    private MediaSessionCompat mediaSession;

    private OnSongCallBack callBack;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setCallBack(OnSongCallBack callBack) {
        this.callBack = callBack;
    }

    public void repeat() {
        try {
            if (state == IDLE) {
                player.reset();
                currentSong = App.getInstance().getStorage().getSongList().get(index);
                player.setDataSource(currentSong.getPath());
                player.prepare();
                player.start();
                mTitle = currentSong.getTitle();
                mSinger = currentSong.getArtist();
                mIcon = currentSong.getIcon();
                if (callBack != null) {
                    callBack.updateUISong(currentSong, index, state);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playMusic() {
        try {
            if (state == IDLE) {
                player.reset();
                if (App.getInstance().isShuffle()) {
                    currentSong = App.getInstance().getStorage().getShuffle().get(index);
                } else {
                    currentSong = App.getInstance().getStorage().getSongList().get(index);
                }
                player.setDataSource(currentSong.getPath());
                player.prepare();
                player.start();
                state = PLAY;
                mTitle = currentSong.getTitle();
                mSinger = currentSong.getArtist();
                mIcon = currentSong.getIcon();
            } else if (state == PLAY) {
                player.pause();
                state = PAUSE;
            } else if (state == PAUSE) {
                player.start();
                state = PLAY;
            }
            if (callBack != null) {
                callBack.updateUISong(currentSong, index, state);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSong() {
        if (!App.getInstance().isShuffle()) {
            if (index >= (App.getInstance().getStorage().getSongList().size() - 1)) {
                index = -1;
            }
        } else {
            if (index >= (App.getInstance().getStorage().getShuffle().size() - 1)) {
                index = -1;
            }
        }
        index++;
        state = IDLE;
        playMusic();
        callBack.updateUISong(currentSong, index, state);
    }

    public void backSong() {
        if (!App.getInstance().isShuffle()) {
            if (index == 0) {
                index = App.getInstance().getStorage().getSongList().size();
            }
        } else {
            if (index == 0) {
                index = App.getInstance().getStorage().getShuffle().size();
            }
        }
        index--;
        state = IDLE;
        playMusic();
        callBack.updateUISong(currentSong, index, state);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new NotificationReceiver();
        mediaSession = new MediaSessionCompat(this, "play audio");
        IntentFilter filter = new IntentFilter();
        filter.addAction("MediaPlayer");
        registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        player.start();
        return iBinder;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    public void updateThumbMusic(Bitmap icon, String title, String singer) {
        mIcon = icon;
        mTitle = title;
        mSinger = singer;
    }

    public void showNotification() {
        int playPauseBtn = 1;
        if (state == PLAY) {
            playPauseBtn = R.drawable.ic_pause;
        } else if (state == PAUSE) {
            playPauseBtn = R.drawable.ic_play;
        } else if (state == IDLE) {
            playPauseBtn = R.drawable.ic_play;
        }

        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.putExtra("TITLE", mTitle);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,
                0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this,
                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,
                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle(mTitle)
                .setContentText(mSinger)
                .setLargeIcon(mIcon)
                .setSmallIcon(R.drawable.ic_music)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_previous, "previous", prevPendingIntent)
                .addAction(playPauseBtn, "play", playPendingIntent)
                .addAction(R.drawable.ic_next, "next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .build();

        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        showNotification();
//
//        if (action.equals(ACTION_SEEK)) {
//            int progress = intent.getIntExtra(EXTRA_PROGRESS, 0);
//            seekTo(progress);
//        }
        return START_STICKY;
    }

    public int getCurrentDuration() {
        int time = 0;
        if (isSongPlaying() && state == PLAY) {
            time = player.getCurrentPosition();
        }
        return time;
    }

    public static void seekTo(int progress) {
        if (progress > 0) {
            int a = (progress * player.getDuration()) / 100;
            player.seekTo(a);
        }
    }

    public boolean isSongPlaying() {
        return player != null && player.isPlaying() && currentSong != null;
    }

    public int getTotalDuration() {
        int time = 0;
        if (isSongPlaying()) {
            time = player.getDuration();
        }
        return time;
    }

    public String getStartTime() {
        String time = "--:--";
        try {
            int duration = player.getCurrentPosition();
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            time = df.format(new Date(duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public String getTotalTime() {
        String time = "--:--";
        try {
            int duration = player.getDuration();
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            time = df.format(new Date(duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
