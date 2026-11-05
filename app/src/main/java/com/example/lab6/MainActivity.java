package com.example.lab6;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NoteListFragment.OnNoteSelectedListener,
        NoteListFragment.OnNoteCheckedListener,
        NoteDetailFragment.OnEditNoteListener {

    private ArrayList<Note> notes = new ArrayList<>();
    private int selectedPosition = -1;
    private boolean isTwoPane = false;

    private NoteListFragment listFragment;
    private NoteDetailFragment detailFragment;

    private static final String PREFS_NAME = "notes_prefs";
    private static final String PREFS_KEY_NOTES = "notes_json";
    private static final String KEY_NOTES_TITLES = "notes_titles";
    private static final String KEY_NOTES_DESCRIPTIONS = "notes_descriptions";
    private static final String KEY_NOTES_COMPLETED = "notes_completed";
    private static final String KEY_SELECTED_POSITION = "selected_position";

    private final ActivityResultLauncher<Intent> editNoteLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            String title = result.getData().getStringExtra(EditNoteActivity.EXTRA_TITLE);
                            String description = result.getData().getStringExtra(EditNoteActivity.EXTRA_DESCRIPTION);
                            boolean completed = result.getData().getBooleanExtra(EditNoteActivity.EXTRA_COMPLETED, false);

                            if (title == null) title = "";
                            if (description == null) description = "";

                            if (selectedPosition == -1) {
                                notes.add(new Note(title, description, completed));
                                selectedPosition = notes.size() - 1;
                            } else if (selectedPosition >= 0 && selectedPosition < notes.size()) {
                                Note note = notes.get(selectedPosition);
                                note.title = title;
                                note.description = description;
                                note.completed = completed;
                            }

                            saveNotes();
                            listFragment.notifyDataSetChanged();

                            if (isTwoPane && detailFragment != null) {
                                detailFragment.updateContent(title, description, completed);
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
                            boolean completed = result.getData().getBooleanExtra(DetailActivity.EXTRA_COMPLETED, false);

                            if (pos >= 0 && pos < notes.size()) {
                                Note note = notes.get(pos);
                                note.title = title != null ? title : "";
                                note.description = description != null ? description : "";
                                note.completed = completed;
                                selectedPosition = pos;
                                saveNotes();
                                listFragment.notifyDataSetChanged();
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            SpannableString title = new SpannableString(getString(R.string.app_name));
            title.setSpan(
                    new ForegroundColorSpan(Color.BLACK),
                    0, title.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(title);
        }

        // Загружаем заметки: сначала из Bundle (поворот экрана), иначе из SharedPreferences
        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        } else {
            loadNotes();
        }

        isTwoPane = findViewById(R.id.detail_container) != null;

        FragmentManager fm = getSupportFragmentManager();

        listFragment = (NoteListFragment) fm.findFragmentById(R.id.list_container);
        if (listFragment == null) {
            listFragment = new NoteListFragment();
            fm.beginTransaction()
                    .replace(R.id.list_container, listFragment)
                    .commitNow();
        }

        listFragment.setNotes(notes);

        if (isTwoPane) {
            detailFragment = (NoteDetailFragment) fm.findFragmentById(R.id.detail_container);
            if (detailFragment == null) {
                detailFragment = NoteDetailFragment.newInstance("", "", false, -1);
                fm.beginTransaction()
                        .replace(R.id.detail_container, detailFragment)
                        .commitNow();
            }

            if (selectedPosition >= 0 && selectedPosition < notes.size()) {
                Note note = notes.get(selectedPosition);
                detailFragment.updateContent(note.title, note.description, note.completed);
                detailFragment.setPosition(selectedPosition);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            addNewNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        boolean[] completedFlags = new boolean[notes.size()];

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            titles.add(note.title);
            descriptions.add(note.description);
            completedFlags[i] = note.completed;
        }

        outState.putStringArrayList(KEY_NOTES_TITLES, titles);
        outState.putStringArrayList(KEY_NOTES_DESCRIPTIONS, descriptions);
        outState.putBooleanArray(KEY_NOTES_COMPLETED, completedFlags);
        outState.putInt(KEY_SELECTED_POSITION, selectedPosition);
    }

    @Override
    public void onNoteSelected(int position) {
        selectedPosition = position;
        Note note = notes.get(position);

        if (isTwoPane) {
            if (detailFragment != null) {
                detailFragment.updateContent(note.title, note.description, note.completed);
                detailFragment.setPosition(position);
            }
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_TITLE, note.title);
            intent.putExtra(DetailActivity.EXTRA_DESCRIPTION, note.description);
            intent.putExtra(DetailActivity.EXTRA_COMPLETED, note.completed);
            intent.putExtra(DetailActivity.EXTRA_POSITION, position);
            detailLauncher.launch(intent);
        }
    }

    @Override
    public void onNoteChecked(int position, boolean isChecked) {
        if (position >= 0 && position < notes.size()) {
            notes.get(position).completed = isChecked;
            saveNotes();
            listFragment.notifyDataSetChanged();

            if (isTwoPane && detailFragment != null && position == selectedPosition) {
                Note note = notes.get(position);
                detailFragment.updateContent(note.title, note.description, note.completed);
            }
        }
    }

    @Override
    public void onEditNote(int position, String title, String description, boolean completed) {
        selectedPosition = position;
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_TITLE, title);
        intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, description);
        intent.putExtra(EditNoteActivity.EXTRA_COMPLETED, completed);
        editNoteLauncher.launch(intent);
    }

    private void addNewNote() {
        selectedPosition = -1;
        int number = notes.size() + 1;
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EditNoteActivity.EXTRA_TITLE, "заметка " + number);
        intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, "Описание заметки " + number);
        intent.putExtra(EditNoteActivity.EXTRA_COMPLETED, false);
        editNoteLauncher.launch(intent);
    }

    private void restoreFromBundle(Bundle savedInstanceState) {
        ArrayList<String> titles = savedInstanceState.getStringArrayList(KEY_NOTES_TITLES);
        ArrayList<String> descriptions = savedInstanceState.getStringArrayList(KEY_NOTES_DESCRIPTIONS);
        boolean[] completedFlags = savedInstanceState.getBooleanArray(KEY_NOTES_COMPLETED);
        selectedPosition = savedInstanceState.getInt(KEY_SELECTED_POSITION, -1);

        notes.clear();
        if (titles != null && descriptions != null) {
            for (int i = 0; i < titles.size(); i++) {
                boolean completed = completedFlags != null && i < completedFlags.length && completedFlags[i];
                notes.add(new Note(titles.get(i), descriptions.get(i), completed));
            }
        }
    }

    /** Сохранение всех заметок в SharedPreferences */
    private void saveNotes() {
        try {
            JSONArray array = new JSONArray();
            for (Note note : notes) {
                JSONObject obj = new JSONObject();
                obj.put("title", note.title != null ? note.title : "");
                obj.put("description", note.description != null ? note.description : "");
                obj.put("completed", note.completed);
                array.put(obj);
            }
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putString(PREFS_KEY_NOTES, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Загрузка заметок из SharedPreferences */
    private void loadNotes() {
        notes.clear();
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String json = prefs.getString(PREFS_KEY_NOTES, null);
            if (json == null || json.isEmpty()) {
                return;
            }
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String title = obj.optString("title", "");
                String description = obj.optString("description", "");
                boolean completed = obj.optBoolean("completed", false);
                notes.add(new Note(title, description, completed));
            }
        } catch (Exception e) {
            e.printStackTrace();
            notes.clear();
        }
    }
}
