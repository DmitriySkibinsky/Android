package com.example.lab6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NoteListFragment.OnNoteSelectedListener,
        NoteDetailFragment.OnEditNoteListener {

    private ArrayList<Note> notes = new ArrayList<>();
    private int selectedPosition = -1;
    private boolean isTwoPane = false;

    private NoteListFragment listFragment;
    private NoteDetailFragment detailFragment;
    private static final String KEY_NOTES_TITLES = "notes_titles";
    private static final String KEY_NOTES_DESCRIPTIONS = "notes_descriptions";
    private static final String KEY_SELECTED_POSITION = "selected_position";

    private final ActivityResultLauncher<Intent> editNoteLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            String title = result.getData().getStringExtra(EditNoteActivity.EXTRA_TITLE);
                            String description = result.getData().getStringExtra(EditNoteActivity.EXTRA_DESCRIPTION);

                            if (title == null) title = "";
                            if (description == null) description = "";

                            if (selectedPosition == -1) {
                                notes.add(new Note(title, description));
                                selectedPosition = notes.size() - 1;
                            } else {
                                notes.get(selectedPosition).title = title;
                                notes.get(selectedPosition).description = description;
                            }

                            listFragment.notifyDataSetChanged();

                            if (isTwoPane && detailFragment != null) {
                                detailFragment.updateContent(title, description);
                                detailFragment.setPosition(selectedPosition);
                            }
                        }
                    }
            );

    private final ActivityResultLauncher<Intent> detailLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            int pos = result.getData().getIntExtra(DetailActivity.EXTRA_POSITION, -1);
                            String title = result.getData().getStringExtra(DetailActivity.EXTRA_TITLE);
                            String description = result.getData().getStringExtra(DetailActivity.EXTRA_DESCRIPTION);

                            if (pos >= 0 && pos < notes.size()) {
                                notes.get(pos).title = title != null ? title : "";
                                notes.get(pos).description = description != null ? description : "";
                                selectedPosition = pos;
                                listFragment.notifyDataSetChanged();
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Сначала восстанавливаем данные
        if (savedInstanceState != null) {
            ArrayList<String> titles = savedInstanceState.getStringArrayList(KEY_NOTES_TITLES);
            ArrayList<String> descriptions = savedInstanceState.getStringArrayList(KEY_NOTES_DESCRIPTIONS);
            selectedPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION, -1);

            notes.clear();
            if (titles != null && descriptions != null) {
                for (int i = 0; i < titles.size(); i++) {
                    notes.add(new Note(titles.get(i), descriptions.get(i)));
                }
            }
        }

        isTwoPane = findViewById(R.id.detail_container) != null;

        FragmentManager fm = getSupportFragmentManager();

        // 2. Потом работаем с фрагментом
        listFragment = (NoteListFragment) fm.findFragmentById(R.id.list_container);
        if (listFragment == null) {
            listFragment = new NoteListFragment();
            fm.beginTransaction()
                    .replace(R.id.list_container, listFragment)
                    .commitNow();
        }

        // 3. Обязательно передаём актуальный список
        listFragment.setNotes(notes);

        if (isTwoPane) {
            detailFragment = (NoteDetailFragment) fm.findFragmentById(R.id.detail_container);
            if (detailFragment == null) {
                detailFragment = NoteDetailFragment.newInstance("", "", -1);
                fm.beginTransaction()
                        .replace(R.id.detail_container, detailFragment)
                        .commitNow();
            }

            if (selectedPosition >= 0 && selectedPosition < notes.size()) {
                Note note = notes.get(selectedPosition);
                detailFragment.updateContent(note.title, note.description);
                detailFragment.setPosition(selectedPosition);
            }
        }

        Button btnAddNote = findViewById(R.id.btnAddNote);
        if (btnAddNote != null) {
            btnAddNote.setOnClickListener(v -> addNewNote());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();

        for (Note note : notes) {
            titles.add(note.title);
            descriptions.add(note.description);
        }

        outState.putStringArrayList(KEY_NOTES_TITLES, titles);
        outState.putStringArrayList(KEY_NOTES_DESCRIPTIONS, descriptions);
        outState.putInt(KEY_SELECTED_POSITION, selectedPosition);
    }

    @Override
    public void onNoteSelected(int position) {
        selectedPosition = position;
        Note note = notes.get(position);

        if (isTwoPane) {
            if (detailFragment != null) {
                detailFragment.updateContent(note.title, note.description);
                detailFragment.setPosition(position);
            }
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_TITLE, note.title);
            intent.putExtra(DetailActivity.EXTRA_DESCRIPTION, note.description);
            intent.putExtra(DetailActivity.EXTRA_POSITION, position);
            detailLauncher.launch(intent);
        }
    }

    @Override
    public void onEditNote(int position, String title, String description) {
        selectedPosition = position;
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_TITLE, title);
        intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, description);
        editNoteLauncher.launch(intent);
    }

    private void addNewNote() {
        selectedPosition = -1;
        int number = notes.size() + 1;
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_TITLE, "заметка " + number);
        intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, "Описание заметки " + number);
        editNoteLauncher.launch(intent);
    }
}