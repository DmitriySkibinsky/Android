package com.example.lab6;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(int position);
    }

    public interface OnNoteCheckedChangeListener {
        void onNoteCheckedChanged(int position, boolean isChecked);
    }

    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener clickListener;
    private OnNoteCheckedChangeListener checkedChangeListener;

    public NotesAdapter(List<Note> notes) {
        if (notes != null) {
            this.notes = notes;
        }
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnNoteCheckedChangeListener(OnNoteCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note, position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivNoteIcon;
        private final TextView tvNoteTitle;
        private final CheckBox cbCompleted;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNoteIcon = itemView.findViewById(R.id.ivNoteIcon);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onNoteClick(pos);
                }
            });
        }

        void bind(Note note, int position) {
            tvNoteTitle.setText(note.title);
            ivNoteIcon.setImageResource(note.imageResId);

            // Зачёркиваем текст, если задача выполнена
            if (note.completed) {
                tvNoteTitle.setPaintFlags(tvNoteTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvNoteTitle.setPaintFlags(tvNoteTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            // Чтобы не срабатывал слушатель при программной установке
            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(note.completed);
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && checkedChangeListener != null) {
                    checkedChangeListener.onNoteCheckedChanged(pos, isChecked);
                }
            });
        }
    }
}
