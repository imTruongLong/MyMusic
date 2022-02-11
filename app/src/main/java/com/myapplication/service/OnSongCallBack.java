package com.myapplication.service;

import com.myapplication.model.Song;

public interface OnSongCallBack {
    void updateUISong(Song songModel, int index, int state );

    //void notifyError( Exception e );
}
