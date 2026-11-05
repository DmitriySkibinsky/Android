package com.example.lab6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_COMPLETED = "extra_completed";

    private EditText etTitle;
    private EditText etDescription;
    private CheckBox cbCompleted;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        cbCompleted = findViewById(R.id.cbCompleted);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);
        boolean completed = intent.getBooleanExtra(EXTRA_COMPLETED, false);

        if (title != null) {
            etTitle.setText(title);
        }
        if (description != null) {
            etDescription.setText(description);
        }
        cbCompleted.setChecked(completed);

        btnSave.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_TITLE, etTitle.getText().toString());
            resultIntent.putExtra(EXTRA_DESCRIPTION, etDescription.getText().toString());
            resultIntent.putExtra(EXTRA_COMPLETED, cbCompleted.isChecked());

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }
}
