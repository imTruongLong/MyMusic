package com.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.R;
import com.myapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SelectSongAdapter extends RecyclerView.Adapter<SelectSongAdapter.SelectSongViewHolder> {
    private List<Song> selectSongList;
    private List<Song> list = new ArrayList<>();
    private Context context;
    private OnSelectSongListener onSelectSongListener;

    public void setOnPlaylistListener(OnSelectSongListener onSelectSongListener) {
        this.onSelectSongListener = onSelectSongListener;
    }

    public SelectSongAdapter(List<Song> selectSongList, Context context) {
        this.selectSongList = selectSongList;
        this.context = context;
    }

    @NonNull
    @Override
    public SelectSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_song, parent, false);
        return new SelectSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectSongViewHolder holder, int position) {
        holder.tvSongName.setText(selectSongList.get(position).getTitle());
        holder.tvArtistName.setText(selectSongList.get(position).getArtist());

        holder.relativeLayout.setOnClickListener(view -> {
            if (holder.checkBox.isChecked()){
                holder.checkBox.setChecked(false);
                list.remove(selectSongList.get(position));
            } else {
                holder.checkBox.setChecked(true);
                list.add(selectSongList.get(position));
            }
        });

        onSelectSongListener.onPlaylist(list);

    }


    @Override
    public int getItemCount() {
        return selectSongList.size();
    }

    public class SelectSongViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        private TextView tvSongName;
        private TextView tvArtistName;
        private CheckBox checkBox;

        public SelectSongViewHolder(View view) {
            super(view);
            relativeLayout          = itemView.findViewById(R.id.layout_listView);
            tvSongName              = itemView.findViewById(R.id.song_name);
            tvArtistName            = itemView.findViewById(R.id.artist_name);
            checkBox            = itemView.findViewById(R.id.check_box);
        }
    }
}
