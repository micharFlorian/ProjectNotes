package com.example.projectnotes.pojos;

import java.io.Serializable;

public class Note implements Serializable {

    private Integer noteId;
    private String title;
    private String description;
    private byte[] image;
    private Integer encode;
    private User userId;

    public Note() {
    }

    public Note(Integer noteId, String title, String description, Integer encode, byte[] image, User userId) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.image = image;
        this.encode = encode;
        this.userId = userId;
    }

    public Note(Integer noteId, String title, String description, Integer encode, User userId) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.encode = encode;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Integer getEncode() {
        return encode;
    }

    public void setEncode(Integer encode) {
        this.encode = encode;
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", encode=" + encode +
                '}';
    }
}
