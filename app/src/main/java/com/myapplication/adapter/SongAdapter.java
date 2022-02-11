package com.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapplication.R;
import com.myapplication.model.Song;
import com.myapplication.utils.App;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    private static final String TAG = "aaa";
    private final List<Song> songList;
    private final Context mContext;
    private Song selectedSong;
    private OnItemClick callback;

    public SongAdapter(List<Song> songList, Context mContext) {
        this.songList = songList;
        this.mContext = mContext;
    }

    public void setOnItemClick(OnItemClick callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        Song item = songList.get(position);

        Glide.with(mContext).load(item.getIcon()).into(holder.ivSong);
        Log.e(TAG, "postion" +position +" icon" +item.getIcon());

        holder.tvTitle.setText(item.getTitle());
        holder.tvSinger.setText(item.getArtist() == null ? item.getAlbum() : item.getArtist());
        holder.ctSong.setBackgroundResource(item == selectedSong && item.isSelected()
              ? R.color.color_select : R.color.purple_200);
        holder.Song = item;
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void loadSong(List<Song> list) {
        songList.addAll(list);
        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void onItemClick(Bitmap icon, String title, String singer, int pos);
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        private ImageView ivSong;
        private TextView tvTitle, tvSinger;
        private LinearLayout ctSong;
        private Song Song;

        public SongHolder(@NonNull View itemView) {
            super(itemView);
            ivSong = itemView.findViewById(R.id.iv_music);
            tvTitle = itemView.findViewById(R.id.tv_title_song);
            tvSinger = itemView.findViewById(R.id.tv_singer);
            ctSong = itemView.findViewById(R.id.ct_song);
            itemView.setOnClickListener(v -> {

                v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_click));
                if (selectedSong != null) {
                    selectedSong.setSelected(true);
                }
                Song.setSelected(true);
                selectedSong = Song;
                App.getInstance().getStorage().setSongList(songList);
                if (callback != null) {
                    int pos = songList.lastIndexOf(selectedSong);
                    callback.onItemClick(selectedSong.getIcon(), selectedSong.getTitle(),
                            selectedSong.getArtist() == null ? selectedSong.getAlbum() : selectedSong.getArtist(), pos);
                }
                notifyDataSetChanged();
            });
        }
    }
}
