package com.example.lab6;

public class Note {
    public String title;
    public String description;

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return title;
    }
}