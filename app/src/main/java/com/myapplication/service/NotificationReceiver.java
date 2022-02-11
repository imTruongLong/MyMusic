package com.myapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.myapplication.utils.App;


public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_PREV = "ACTION_PREV";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_NEXT = "ACTION_NEXT";

    @Override
    public void onReceive(Context context, Intent intent ) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    App.getInstance().getStorage().getMusicService().playMusic();
                    break;
                case ACTION_NEXT:
                    App.getInstance().getStorage().getMusicService().nextSong();
                    break;
                case ACTION_PREV:
                    App.getInstance().getStorage().getMusicService().backSong();
                    break;

            }
        }
    }
}
