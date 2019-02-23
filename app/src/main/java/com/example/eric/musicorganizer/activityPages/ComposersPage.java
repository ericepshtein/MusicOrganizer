package com.example.eric.musicorganizer.activityPages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Composer;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.ComposerAdapter;

import java.util.List;

public class ComposersPage extends AppCompatActivity {

    private ListView composersListView;
    private List<Composer> composersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_composers_page);

        //MusicManager.getInstance().fillMusicDataFromStorage(getContentResolver());
        composersListView = (ListView)findViewById(R.id.composers_page_list);
        getComposersList();
        if (composersList == null || composersList.isEmpty()) {
            Toast.makeText(this, R.string.no_composers, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        ComposerAdapter composersAdt = new ComposerAdapter(this, composersList);
        composersListView.setAdapter(composersAdt);
    }

    public void getComposersList() {
        MusicManager.getInstance().sortComposersList();
        composersList = MusicManager.getInstance().getComposersList();
    }

    public void songPicked(View view) {
        // TODO - all_songs_list pick method
    }
}
