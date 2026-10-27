package com.example.lab6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity
        implements NoteDetailFragment.OnEditNoteListener {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_POSITION = "extra_position";

    private int position = -1;
    private String title;
    private String description;

    private final ActivityResultLauncher<Intent> editNoteLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            title = result.getData().getStringExtra(EditNoteActivity.EXTRA_TITLE);
                            description = result.getData().getStringExtra(EditNoteActivity.EXTRA_DESCRIPTION);

                            NoteDetailFragment fragment = (NoteDetailFragment)
                                    getSupportFragmentManager().findFragmentById(R.id.detail_container);
                            if (fragment != null) {
                                fragment.updateContent(title, description);
                            }

                            Intent data = new Intent();
                            data.putExtra(EXTRA_TITLE, title);
                            data.putExtra(EXTRA_DESCRIPTION, description);
                            data.putExtra(EXTRA_POSITION, position);
                            setResult(Activity.RESULT_OK, data);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        title = intent.getStringExtra(EXTRA_TITLE);
        description = intent.getStringExtra(EXTRA_DESCRIPTION);
        position = intent.getIntExtra(EXTRA_POSITION, -1);

        if (savedInstanceState == null) {
            NoteDetailFragment fragment = NoteDetailFragment.newInstance(title, description, position);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onEditNote(int position, String title, String description) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_TITLE, title);
        intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, description);
        editNoteLauncher.launch(intent);
    }
}