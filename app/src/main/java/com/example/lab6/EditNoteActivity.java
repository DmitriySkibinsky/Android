package com.example.lab6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";

    private EditText etTitle;
    private EditText etDescription;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        // Получаем данные, переданные из MainActivity
        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);

        if (title != null) {
            etTitle.setText(title);
        }
        if (description != null) {
            etDescription.setText(description);
        }

        // Кнопка «Сохранить» — возвращаем результат
        btnSave.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_TITLE, etTitle.getText().toString());
            resultIntent.putExtra(EXTRA_DESCRIPTION, etDescription.getText().toString());

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}