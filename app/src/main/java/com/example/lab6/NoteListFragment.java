package com.example.lab6;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteListFragment extends Fragment {

    public interface OnNoteSelectedListener {
        void onNoteSelected(int position);
    }

    public interface OnNoteCheckedListener {
        void onNoteChecked(int position, boolean isChecked);
    }

    private OnNoteSelectedListener selectedListener;
    private OnNoteCheckedListener checkedListener;
    private ArrayList<Note> notes = new ArrayList<>();
    private NotesAdapter adapter;
    private RecyclerView recyclerView;

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
        if (adapter != null) {
            adapter.setNotes(this.notes);
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
            selectedListener = (OnNoteSelectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnNoteSelectedListener");
        }
        if (context instanceof OnNoteCheckedListener) {
            checkedListener = (OnNoteCheckedListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewNotes);

        adapter = new NotesAdapter(notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnNoteClickListener(position -> {
            if (selectedListener != null) {
                selectedListener.onNoteSelected(position);
            }
        });

        adapter.setOnNoteCheckedChangeListener((position, isChecked) -> {
            if (checkedListener != null) {
                checkedListener.onNoteChecked(position, isChecked);
            } else if (position >= 0 && position < notes.size()) {
                // На случай, если Activity не реализует интерфейс — всё равно сохраняем
                notes.get(position).completed = isChecked;
                adapter.notifyItemChanged(position);
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectedListener = null;
        checkedListener = null;
    }
}
