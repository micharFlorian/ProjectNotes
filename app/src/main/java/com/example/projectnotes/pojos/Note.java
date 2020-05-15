package com.example.projectnotes.pojos;

import java.io.Serializable;

public class Note implements Serializable {

    private Integer noteId;
    private String title;
    private String description;
    private User userId;

    public Note() {
    }

    public Note(String title, String description) {
        if (title != null && description == null) {
            this.title = title;
            this.description = "";
        }else if (title == null && description != null) {
            this.title = "";
            this.description = description;
        }else if (title != null && description != null){
            this.title = title;
            this.description = description;
        }
    }

    public Note(Integer noteId, String title, String description, User userId) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.userId = userId;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                '}';
    }
}
