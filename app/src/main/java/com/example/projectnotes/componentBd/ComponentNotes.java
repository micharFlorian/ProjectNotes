package com.example.projectnotes.componentBd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectnotes.pojos.Note;
import com.example.projectnotes.pojos.User;

import java.util.ArrayList;

public class ComponentNotes {

    private SQLiteDatabase notes;
    private NotesOpenHelper notesOpenHelper;

    public ComponentNotes(Context context) {
        notesOpenHelper = new NotesOpenHelper(context, "notes", null, 1);
    }

    public void openForWrite() {
        notes = notesOpenHelper.getWritableDatabase();
    }

    public void close() {
        notes.close();
    }

    public long insertUser(User user) {
        openForWrite();
        long registers = 0;
        ContentValues content = new ContentValues();
        content.put("EMAIL", user.getEmail());
        content.put("PASSWORD", user.getPassword());
        registers = notes.insert("USER", null, content);
        close();
        return registers;
    }

    public long deleteUser(String email) {
        openForWrite();
        long registers = 0;
        registers = notes.delete("USER", "EMAIL = '" + email + "'", null);
        close();
        return registers;
    }

    public long updateUser(String email, User user) {
        openForWrite();
        long registers = 0;
        ContentValues content = new ContentValues();
        content.put("EMAIL", user.getEmail());
        content.put("PASSWORD", user.getPassword());
        registers = notes.update("USER", content, "EMAIL = '" + email + "'", null);
        close();
        return registers;
    }

    public User readUser(Integer userId) {
        openForWrite();
        Cursor cursor = notes.rawQuery("select USER_ID, EMAIL, PASSWORD from USER where USER_ID = " + userId, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        }
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        }
        cursor.close();
        close();
        return user;
    }

    public ArrayList<User> readUsers() {
        openForWrite();
        Cursor cursor = notes.rawQuery("select USER_ID, EMAIL, PASSWORD from USER", null);
        if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        }
        ArrayList<User> users = new ArrayList<>();
        while (cursor.moveToNext()) {
            users.add(new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        close();
        return users;
    }

    public long insertNote(Note note) {
        openForWrite();
        long registers = 0;
        ContentValues content = new ContentValues();
        content.put("TITLE", note.getTitle());
        content.put("DESCRIPTION", note.getDescription());
        content.put("IMAGE", note.getImage());
        content.put("ENCODE", note.getEncode());
        content.put("USER_ID", note.getUserId().getUserId());
        registers = notes.insert("NOTE", null, content);
        close();
        return registers;
    }

    public long deleteNote(Integer noteId) {
        openForWrite();
        long registers = 0;
        registers = notes.delete("NOTE", "NOTE_ID = " + noteId, null);
        close();
        return registers;
    }

    public long updateNote(Integer noteId, Note note) {
        openForWrite();
        long registers = 0;
        ContentValues content = new ContentValues();
        content.put("TITLE", note.getTitle());
        content.put("DESCRIPTION", note.getDescription());
        content.put("IMAGE", note.getImage());
        content.put("ENCODE", note.getEncode());
        content.put("USER_ID", note.getUserId().getUserId());
        registers = notes.update("NOTE", content, "NOTE_ID = " + noteId, null);
        close();
        return registers;
    }

    public Note readNote(Integer noteId) {
        openForWrite();
        Cursor cursor = notes.rawQuery("select NOTE_ID, TITLE, DESCRIPTION, ENCODE, IMAGE, USER_ID" +
                " from NOTE where NOTE_ID = " + noteId, new String[]{});
        if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        }
        Note note = null;
        if (cursor.moveToFirst()) {
            note = new Note(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getBlob(4), new User(cursor.getInt(5)));
        }
        cursor.close();
        close();
        return note;
    }

    public ArrayList<Note> readNotes() {
        openForWrite();
        Cursor cursor = notes.rawQuery("select NOTE_ID, TITLE, DESCRIPTION, ENCODE, USER_ID from NOTE", null);
        if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        }
        ArrayList<Note> listNotes = new ArrayList<>();
        while (cursor.moveToNext()) {
            listNotes.add(new Note(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getInt(3), readUser(cursor.getInt(4))));
        }
        cursor.close();
        close();
        return listNotes;
    }
}
