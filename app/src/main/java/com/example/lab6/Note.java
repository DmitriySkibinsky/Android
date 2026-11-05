package com.example.lab6;

public class Note {
    public String title;
    public String description;
    public boolean completed;
    public int imageResId;

    public Note(String title, String description) {
        this(title, description, false, R.drawable.ic_note);
    }

    public Note(String title, String description, boolean completed) {
        this(title, description, completed, R.drawable.ic_note);
    }

    public Note(String title, String description, boolean completed, int imageResId) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.imageResId = imageResId;
    }

    @Override
    public String toString() {
        return title;
    }
}
