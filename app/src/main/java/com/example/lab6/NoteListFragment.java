package com.example.lab6;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class NoteListFragment extends Fragment {

    public interface OnNoteSelectedListener {
        void onNoteSelected(int position);
    }

    private OnNoteSelectedListener listener;
    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayAdapter<Note> adapter;
    private ListView listView;

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
        if (adapter != null) {
            // Пересоздаём адаптер с актуальным списком
            adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, this.notes);
            if (listView != null) {
                listView.setAdapter(adapter);
            }
        }
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNoteSelectedListener) {
            listener = (OnNoteSelectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnNoteSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        listView = view.findViewById(R.id.listViewNotes);

        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, notes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            if (listener != null) {
                listener.onNoteSelected(position);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}