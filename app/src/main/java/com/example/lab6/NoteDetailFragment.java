package com.example.lab6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NoteDetailFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_POSITION = "position";

    private TextView tvTitle;
    private TextView tvDescription;
    private Button btnEdit;

    private String title = "";
    private String description = "";
    private int position = -1;

    public interface OnEditNoteListener {
        void onEditNote(int position, String title, String description);
    }

    private OnEditNoteListener editListener;

    public static NoteDetailFragment newInstance(String title, String description, int position) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE, "");
            description = getArguments().getString(ARG_DESCRIPTION, "");
            position = getArguments().getInt(ARG_POSITION, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        btnEdit = view.findViewById(R.id.btnEdit);

        updateContent(title, description);

        btnEdit.setOnClickListener(v -> {
            if (editListener != null && position >= 0) {
                editListener.onEditNote(position, title, description);
            }
        });

        // Если фрагмент встроен в Activity, которая реализует интерфейс
        if (getActivity() instanceof OnEditNoteListener) {
            editListener = (OnEditNoteListener) getActivity();
        }

        return view;
    }

    public void updateContent(String title, String description) {
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";

        if (tvTitle != null) {
            tvTitle.setText(this.title);
        }
        if (tvDescription != null) {
            tvDescription.setText(this.description);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }
}