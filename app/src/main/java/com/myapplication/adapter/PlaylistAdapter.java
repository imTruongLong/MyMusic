package com.myapplication.adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.R;
import com.myapplication.database.PlaylistDB;
import com.myapplication.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{
    private ArrayList<String> listPlaylist;
    private ArrayList<String> listResult = new ArrayList<>();
    private Context context;
    private SQLiteDatabase db;
    private PlaylistDB playlistDatabase;
    private List list = new ArrayList();
    private OnPlaylistListener onPlaylistListener;

    private Button btSave;
    private Button btCancel;
    private EditText edtPlaylistName;
    private TextView tvTitle;
    public static final String KEY_LIST = "list";

    public void setOnPlaylistListener(OnPlaylistListener onPlaylistListener) {
        this.onPlaylistListener = onPlaylistListener;
    }

    public PlaylistAdapter(ArrayList<String> listPlaylist, Context context) {
        this.listPlaylist = listPlaylist;
        this.context = context;
    }
    @NonNull
    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_favorite, parent, false);
        return new PlaylistAdapter.PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.PlaylistViewHolder holder, int position) {
        holder.tvPlaylistName.setText(listPlaylist.get(position));
        String sizeTable = "SELECT * FROM " + "'" + listPlaylist.get(position) + "'";

        playlistDatabase = new PlaylistDB(context, "playlist.db", null, 1);

        list.clear();

        Cursor cursor = playlistDatabase.getData(sizeTable);
        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            list.add(name);
        }

        holder.tvSizeList.setText(list.size() + " Bài hát");

        holder.layoutPlaylist.setOnClickListener(view -> {
            if (onPlaylistListener != null){
                onPlaylistListener.onPlaylist(position, listPlaylist);
            }
        });

        holder.imvSetting.setOnClickListener(view -> showMenu(view, position));
    }

    private void showMenu(View view, int pos){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.playlist_item_menu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.edit:
                    renamePlaylist(pos);
                    return true;
                case R.id.delete:
                    deletePlaylist(pos);
                    return true;
                default: return false;
            }
        });
    }

    private void deletePlaylist(int pos){
        String tableName = listPlaylist.get(pos);

        String deleteTable = "DROP TABLE " + "'" + tableName + "'";
        playlistDatabase.queryData(deleteTable);

        TinyDB tinyDB = new TinyDB(context);
        listResult = tinyDB.getListString(KEY_LIST);
        listResult.remove(pos);
        tinyDB.putListString(KEY_LIST, listResult);

        listPlaylist = listResult;
        notifyDataSetChanged();
    }

    private void renamePlaylist(int pos){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.create_playlist);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        btCancel        = dialog.findViewById(R.id.bt_cancel);
        btSave          = dialog.findViewById(R.id.bt_save);
        edtPlaylistName = dialog.findViewById(R.id.edt_playlist_name);
        tvTitle         = dialog.findViewById(R.id.tv_title);

        playlistDatabase = new PlaylistDB(context, "playlist.db", null, 1);
        String tableName = listPlaylist.get(pos);

        tvTitle.setText("Rename");
        edtPlaylistName.setText(tableName);

        btCancel.setOnClickListener(view -> dialog.dismiss());

        btSave.setOnClickListener(view -> {
            String rename = "ALTER TABLE " + "'" + tableName + "' " + "RENAME TO" + " '" + edtPlaylistName.getText().toString() + "';";
            playlistDatabase.queryData(rename);

            TinyDB tinyDB = new TinyDB(context);
            listPlaylist.remove(tableName);
            listPlaylist.add(edtPlaylistName.getText().toString());
            tinyDB.putListString(KEY_LIST, listPlaylist);
            dialog.dismiss();
            notifyDataSetChanged();
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return listPlaylist.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaylistName;
        TextView tvSizeList;
        ConstraintLayout layoutPlaylist;
        ImageView imvSetting;

        public PlaylistViewHolder(View itemView) {
            super(itemView);

            tvPlaylistName = itemView.findViewById(R.id.playlist_name);
            tvSizeList      = itemView.findViewById(R.id.size);
            layoutPlaylist  = itemView.findViewById(R.id.playlist);
            imvSetting      = itemView.findViewById(R.id.setting_playlist);
        }
    }
}
