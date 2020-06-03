package com.example.projectnotes.componentBd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/*
 *Clase con la que creamos la BDD
 */
public class NotesOpenHelper extends SQLiteOpenHelper {

    /*
     *Método constructor de la clase
     */
    public NotesOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /*
     *Se crean las tablas de la BDD
     */
    @Override
    public void onCreate(SQLiteDatabase notes) {

        notes.execSQL("create table USER(USER_ID Integer primary key autoincrement, EMAIL text not null UNIQUE," +
                " PASSWORD text not null)");

        notes.execSQL("create table NOTE(NOTE_ID Integer primary key autoincrement, TITLE text, DESCRIPTION text," +
                " IMAGE blob, ENCODE Integer DEFAULT 0, USER_ID Integer, FOREIGN KEY(USER_ID) REFERENCES USER(USER_ID))");

    }

    /*
     *En caso de que existan las tablas, se borrán y se crean de nuevo
     */
    @Override
    public void onUpgrade(SQLiteDatabase notes, int i, int i1) {

        notes.execSQL("drop table USER");
        notes.execSQL("drop table NOTE");

        onCreate(notes);
    }
}