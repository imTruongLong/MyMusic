package com.myapplication.view.main;

import static com.myapplication.service.MusicService.PAUSE;
import static com.myapplication.service.MusicService.PLAY;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.myapplication.R;
import com.myapplication.base.BaseAct;
import com.myapplication.databinding.ActivityMainBinding;
import com.myapplication.model.Song;
import com.myapplication.service.MusicService;
import com.myapplication.service.OnSongCallBack;
import com.myapplication.utils.App;
import com.myapplication.view.fragment.ListSongFragment;
import com.myapplication.view.main.viewmodel.MainViewModel;
import com.myapplication.view.playlistfavorite.PlaylistFavoriteAct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseAct<MainViewModel, ActivityMainBinding> implements View.OnClickListener,
        ServiceConnection, OnSongCallBack, SeekBar.OnSeekBarChangeListener, ListSongFragment.BackList {
    private static final String TAG = "aaa";
    String mTitle, mSinger;
    Bitmap mIcon;
    List<Song> songList = new ArrayList<>();
    private ActivityMainBinding binding;
    private MusicService musicService;
    private Handler handler;
    private Runnable runnable;
    private int stateShuffle;

    @Override
    public ActivityMainBinding getBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding;
    }

    protected void initViews() {
        grantedPermission();

        binding.mediaController.ivPrevious.setOnClickListener(this);
        binding.mediaController.ivPlay.setOnClickListener(this);
        binding.mediaController.ivNext.setOnClickListener(this);
        binding.ivListFavorite.setOnClickListener(this);
        binding.mediaController.ivRepeat.setOnClickListener(this);
        binding.mediaController.ivShuffle.setOnClickListener(this);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    public void startListFavorite() {
        Intent intent = new Intent(this, PlaylistFavoriteAct.class);
        startActivity(intent);
    }

    public void startMediaController() {
        handler = new Handler();

        runOnUiThread(runnable = new Runnable() {
            @Override
            public void run() {
                int currentDuration = musicService.getCurrentDuration();
                int totalDuration = musicService.getTotalDuration();
                String startTime = musicService.getStartTime();
                String totalTime = musicService.getTotalTime();
//                Log.i(TAG, "currentDuration: "+currentDuration);
//                Log.i(TAG, "total: "+totalDuration);
//                Log.i(TAG, "startTime  : "+startTime);
//                Log.i(TAG, "totalTime: "+totalTime);

                binding.mediaController.seekbar.setMax(totalDuration);
                binding.mediaController.seekbar.setProgress(currentDuration);
                binding.mediaController.tvStartTime.setText(startTime);
                binding.mediaController.tvEndTime.setText(totalTime);

                handler.postDelayed(this, 500);
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        Log.e(TAG, musicService + "");
        App.getInstance().getStorage().setMusicService(musicService);
        App.getInstance().getStorage().getMusicService().setCallBack(this);
        startMediaController();

        if (musicService != null) {
            Song currentSong = musicService.getCurrentSong();
            binding.mediaController.tvTitleSong.setText(currentSong != null ? currentSong.getTitle() : "No Song");
            binding.mediaController.tvSinger.setText(currentSong != null ? currentSong.getArtist() : "No Song");
            binding.mediaController.ivMusic.setImageBitmap(currentSong != null ? currentSong.getIcon() : null);
            binding.mediaController.ivPlay.setImageResource(musicService.isSongPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
        Log.e("Disconected ", musicService + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onBindingDied(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected Class<MainViewModel> getClassViewModel() {
        return MainViewModel.class;
    }

    private void grantedPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            setListSongFrg();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 105);
        }
    }

    private void setListSongFrg() {
        ListSongFragment listSongFragment = new ListSongFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_list, listSongFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_play:
                    App.getInstance().getStorage().getMusicService().playMusic();
                break;
            case R.id.iv_next:
                    App.getInstance().getStorage().getMusicService().nextSong();
                break;
            case R.id.iv_previous:
                    App.getInstance().getStorage().getMusicService().backSong();
                break;
            case R.id.iv_list_favorite:
                startListFavorite();
                break;
            case R.id.iv_repeat:
                repeat();
                break;
            case R.id.iv_shuffle:
                if (stateShuffle == 0) {
                    App.getInstance().setShuffle(true);
                    binding.mediaController.ivShuffle.setColorFilter(R.color.black);
                    Collections.shuffle(songList);
                    App.getInstance().getStorage().setShuffle(songList);
                    stateShuffle = 1;
                }
                if (stateShuffle == 1){
                    App.getInstance().setShuffle(false);
                    App.getInstance().getStorage().setSongList(songList);
                    binding.mediaController.ivShuffle.setColorFilter(R.color.white);
                    stateShuffle = 0;
                }
                break;
            default:
                break;
        }
    }

    private void repeat() {
        binding.mediaController.ivRepeat.setColorFilter(R.color.black);
        musicService.repeat();
    }

    public void sendDataToService() {
        String title = mTitle;
        String singer = mSinger;
        Bitmap icon = mIcon;
        App.getInstance().getStorage().getMusicService().updateThumbMusic(icon, title, singer);
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    @Override
    public void updateUISong(Song songModel, int index, int state) {
        if (state == PLAY) {
            binding.mediaController.ivPlay.setImageResource(R.drawable.ic_pause);
            sendDataToService();
        } else if (state == PAUSE) {
            binding.mediaController.ivPlay.setImageResource(R.drawable.ic_play);
            sendDataToService();
        }
        mTitle = songModel.getTitle();
        binding.mediaController.tvTitleSong.setText(mTitle);
        mSinger = songModel.getArtist();
        binding.mediaController.tvSinger.setText(mSinger);
        mIcon = songModel.getIcon();
        binding.mediaController.ivMusic.setImageBitmap(mIcon);
    }

    private void seekTo(int progress) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_SEEK);
        intent.putExtra(MusicService.EXTRA_PROGRESS, progress);

        startService(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (b) {
            seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void backList(List<Song> list) {
        songList = list;
    }
}