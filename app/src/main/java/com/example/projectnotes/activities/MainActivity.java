package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.example.projectnotes.R;
import com.example.projectnotes.adapters.NotesListAdapter;
import com.example.projectnotes.pojos.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listViewNotes;

    private ArrayList<Note> listNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewNotes = (ListView) findViewById(R.id.listViewNotes);

        listNotes = new ArrayList<Note>() {{
            add(new Note("Titulo 1", "Esta es una nota de prueba " +
                    "Esta es una linea de prueba"));
            add(new Note("Titulo 2", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 3", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 4", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 5", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 6", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 7", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 8", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 9", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 10", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 11", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 12", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 13", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 14", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 15", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 16", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note("Titulo 17", "Esta es una nota de prueba " +
                    "Esta es una nota de prueba Esta es una linea de prueba"));
        }};

        NotesListAdapter notesListAdapter = new NotesListAdapter(this, listNotes);
        listViewNotes.setAdapter(notesListAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
}
