package com.example.projectnotes.pojos;

import java.io.Serializable;

public class Note implements Serializable {

    private Integer noteId;
    private String title;
    private String description;
    private byte[] image;
    private User userId;

    public Note() {
    }

    public Note(String title, String description, byte[] image, User userId) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.userId = userId;
    }

    public Note(Integer noteId, String title, String description) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
    }

    public Note(Integer noteId, String title, String description, User userId) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.userId = userId;
    }

    public Note(Integer noteId, String title, String description, byte[] image) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.image = image;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
