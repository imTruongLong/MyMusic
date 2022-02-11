package com.myapplication.view.playlistfavorite;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapplication.R;
import com.myapplication.adapter.OnPlaylistListener;
import com.myapplication.adapter.PlaylistAdapter;
import com.myapplication.database.PlaylistDB;
import com.myapplication.utils.TinyDB;

import java.util.ArrayList;


public class PlaylistFavoriteAct extends AppCompatActivity implements OnPlaylistListener {
    private static final String KEY_LIST = "list" ;

    ImageView ivBack, ivAdd;
    Button btCancel, btSave;
    private EditText edtPlaylistName;

    private ArrayList<String> listPlaylist = new ArrayList();
    private PlaylistDB playlistDatabase;
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list_favorite);
        initViews();
//        loadList();
//        setAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
        setAdapter();
    }

    private void loadList(){
        TinyDB tinyDB = new TinyDB(this);
        listPlaylist = tinyDB.getListString(KEY_LIST);
    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        playlistAdapter = new PlaylistAdapter(listPlaylist, this);
        playlistAdapter.setOnPlaylistListener(this);
        recyclerView.setAdapter(playlistAdapter);
        playlistAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivAdd = findViewById(R.id.iv_add);
        recyclerView = findViewById(R.id.rv_list_favorite);

        playlistDatabase = new PlaylistDB(getApplicationContext(), "playlist.db", null, 1);

        ivBack.setOnClickListener(view -> onBackPressed());
        ivAdd.setOnClickListener(view -> {
            Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(R.layout.create_playlist);
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            btCancel        = dialog.findViewById(R.id.bt_cancel);
            btSave          = dialog.findViewById(R.id.bt_save);
            edtPlaylistName = dialog.findViewById(R.id.edt_playlist_name);

            btCancel.setOnClickListener(view1 -> dialog.dismiss());

            btSave.setOnClickListener(view12 -> createTable(dialog));
            dialog.show();
        });
    }

    public void createTable(Dialog dialog){
        TinyDB tinyDB = new TinyDB(this);
        listPlaylist = tinyDB.getListString(KEY_LIST);

        String tableName = edtPlaylistName.getText().toString();

        boolean checkExist = true;

        for (int i = 0; i < listPlaylist.size(); i++){
            if (tableName.equals(listPlaylist.get(i))){
                checkExist = false;
            }
        }

        if (checkExist){
            String sql = "CREATE TABLE IF NOT EXISTS " + "'" + tableName + "' "+
                    "(songName text PRIMARY KEY, artistName text, urlSong text, album text, duration text)";
            playlistDatabase.queryData(sql);

            listPlaylist.add(tableName);
            tinyDB.putListString(KEY_LIST, listPlaylist);
            SelectSong.lunch(this, edtPlaylistName.getText().toString());
            dialog.dismiss();
        } else {
            Toast.makeText(this,"Playlist Already exist", Toast.LENGTH_SHORT).show();
        }
    }
    public void onPlaylist(int position, ArrayList<String> listSong) {
        PlaylistSong.launch(this, position, listPlaylist);
    }
}
