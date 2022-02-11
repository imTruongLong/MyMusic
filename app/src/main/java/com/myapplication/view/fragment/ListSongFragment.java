package com.myapplication.view.fragment;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.adapter.SongAdapter;
import com.myapplication.base.BaseFragment;
import com.myapplication.databinding.FragmentListSongBinding;
import com.myapplication.model.Song;
import com.myapplication.service.MusicService;
import com.myapplication.utils.App;
import com.myapplication.view.main.MainActivity;
import com.myapplication.view.main.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListSongFragment extends BaseFragment<MainViewModel, FragmentListSongBinding> {

    private RecyclerView rvListSong;
    private SongAdapter adapter;
    private List<Song> songModelList;
    private MainActivity mainActivity;
    private FragmentListSongBinding binding;

    @Override
    protected FragmentListSongBinding getBinding() {
        binding = FragmentListSongBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected Class<MainViewModel> getClassViewModel() {
        return MainViewModel.class;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void initEvents() {
        mainActivity= (MainActivity) requireActivity();
        songModelList = new ArrayList<>();
        rvListSong = binding.rvListSong;
        rvListSong.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SongAdapter(songModelList, getContext());
        rvListSong.setAdapter(adapter);

        adapter.loadSong(mModel.getListSongOffline(mContext));
        adapter.setOnItemClick((icon, title, singer, pos) -> {

            App.getInstance().getStorage().getMusicService().setIndex(pos);
            App.getInstance().getStorage().getMusicService().setState(MusicService.IDLE);
            App.getInstance().getStorage().getMusicService().playMusic();
            mainActivity.sendDataToService();
        });
        try {
            BackList callback = mainActivity;
            callback.backList(songModelList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface BackList{
        void backList(List<Song> list);
    }
}
