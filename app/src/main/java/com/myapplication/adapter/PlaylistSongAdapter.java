package com.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapplication.R;
import com.myapplication.model.Song;

import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.ViewHolder>{
    private List<Song> playlistSong;
    private Context context;
    private Song selectedSong;
    private OnPlaylistSongListener onPlaylistSongListener;

    public void setOnPlaylistSongListener(OnPlaylistSongListener onPlaylistSongListener) {
        this.onPlaylistSongListener = onPlaylistSongListener;
    }

    public PlaylistSongAdapter(List<Song> playlistSong, Context context) {
        this.playlistSong = playlistSong;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongAdapter.ViewHolder holder, int position) {
        Song item = playlistSong.get(position);
        holder.tvSongName.setText(item.getTitle());
        holder.tvArtistName.setText(item.getArtist());
        Glide.with(context).load(item.getIcon()).into(holder.imvAlbum);
        holder.linearLayout.setBackgroundResource(item == selectedSong && item.isSelected()
                ? R.color.color_select : R.color.purple_200);

        holder.linearLayout.setOnClickListener(view -> {
            if (onPlaylistSongListener != null){
                onPlaylistSongListener.onPlaylistSong(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistSong.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSongName;
        private final TextView tvArtistName;
        private final ImageView imvAlbum;
        private final LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongName      = itemView.findViewById(R.id.tv_title_song);
            tvArtistName    = itemView.findViewById(R.id.tv_singer);
            imvAlbum        = itemView.findViewById(R.id.iv_music);
            linearLayout    = itemView.findViewById(R.id.ct_song);
        }
    }
}
