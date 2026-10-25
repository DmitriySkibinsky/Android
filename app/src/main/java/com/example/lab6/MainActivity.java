package com.example.lab6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listViewNotes;
    private TextView tvTitle;
    private TextView tvDescription;
    private Button btnEdit;
    private Button btnAddNote;

    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayAdapter<Note> adapter;

    private int selectedPosition = -1;   // какая заметка выбрана

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
                                // Добавляли новую
                                notes.add(new Note(title, description));
                                selectedPosition = notes.size() - 1;
                            } else {
                                // Редактировали существующую
                                notes.get(selectedPosition).title = title;
                                notes.get(selectedPosition).description = description;
                            }

                            adapter.notifyDataSetChanged();
                            showSelectedNote();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewNotes = findViewById(R.id.listViewNotes);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnEdit = findViewById(R.id.btnEdit);
        btnAddNote = findViewById(R.id.btnAddNote);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listViewNotes.setAdapter(adapter);

        // выбор заметки в списке
        listViewNotes.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            showSelectedNote();
        });

        // Кнопка «Редактировать»
        btnEdit.setOnClickListener(v -> {
            if (selectedPosition == -1) {
                Toast.makeText(this, "Сначала выберите заметку", Toast.LENGTH_SHORT).show();
                return;
            }

            Note note = notes.get(selectedPosition);
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.EXTRA_TITLE, note.title);
            intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, note.description);
            editNoteLauncher.launch(intent);
        });

        // добавить заметку
        btnAddNote.setOnClickListener(v -> {
            selectedPosition = -1;   // флаг, что добавляем новую

            int number = notes.size() + 1;
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.EXTRA_TITLE, "заметка " + number);
            intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, "Описание заметки " + number);
            editNoteLauncher.launch(intent);
        });
    }

    private void showSelectedNote() {
        if (selectedPosition >= 0 && selectedPosition < notes.size()) {
            Note note = notes.get(selectedPosition);
            tvTitle.setText(note.title);
            tvDescription.setText(note.description);
        } else {
            tvTitle.setText("");
            tvDescription.setText("");
        }
    }
}