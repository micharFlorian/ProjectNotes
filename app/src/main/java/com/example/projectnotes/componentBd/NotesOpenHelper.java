package com.example.projectnotes.componentBd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NotesOpenHelper extends SQLiteOpenHelper {

    public NotesOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase notes) {
        notes.execSQL("create table USER(USER_ID Integer primary key autoincrement, EMAIL text not null UNIQUE," +
                " PASSWORD text not null)");
        notes.execSQL("create table NOTE(NOTE_ID Integer primary key autoincrement, TITLE text, DESCRIPTION text," +
                " IMAGE blob, USER_ID Integer, FOREIGN KEY(USER_ID) REFERENCES USER(USER_ID))");

        notes.execSQL("INSERT INTO USER(EMAIL, PASSWORD) VALUES('michar.florian@gmail.com','12345')");
        notes.execSQL("INSERT INTO NOTE(TITLE, DESCRIPTION, USER_ID) VALUES('Ejemplo','Esto es una prueba" +
                " de la base de datos',1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase notes, int i, int i1) {
        notes.execSQL("drop table USER");
        notes.execSQL("drop table NOTE");
        onCreate(notes);
    }
}
