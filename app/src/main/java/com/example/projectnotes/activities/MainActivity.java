package com.example.projectnotes.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectnotes.R;
import com.example.projectnotes.adapters.NotesGridAdapter;
import com.example.projectnotes.adapters.NotesListAdapter;
import com.example.projectnotes.pojos.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ListView listViewNotes;
    private GridView gridViewNotes;
    private EditText editTextSearch;
    private ViewStub stubList;
    private ViewStub stubGrid;

    private ArrayList<Note> listNotes;
    private String typeViewsNotes;
    int order = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fill();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listNotes.add((Note) bundle.getSerializable("note"));
        }

        readInterface();

        stubList.inflate();
        stubGrid.inflate();

        listViewNotes = (ListView) findViewById(R.id.listViewNotes);
        gridViewNotes = (GridView) findViewById(R.id.gridViewNotes);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        typeViewsNotes = preferences.getString("pref_view_notes_type", "Lista");
        choiceViews();

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note note = (Note) listViewNotes.getItemAtPosition(i);
                alertDialog(note);
            }
        });

        gridViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note note = (Note) gridViewNotes.getItemAtPosition(i);
                alertDialog(note);
            }
        });
    }

    private void readInterface() {
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
    }

    private void choiceViews() {
        switch (typeViewsNotes) {
            case "Lista":
                stubList.setVisibility(View.VISIBLE);
                stubGrid.setVisibility(View.GONE);
                NotesListAdapter notesListAdapter = new NotesListAdapter(this, R.layout.listview_item, listNotes);
                listViewNotes.setAdapter(notesListAdapter);
                break;
            case "Columna":
                stubList.setVisibility(View.GONE);
                stubGrid.setVisibility(View.VISIBLE);
                NotesGridAdapter notesGridAdapter = new NotesGridAdapter(this, R.layout.gridview_item, listNotes);
                gridViewNotes.setAdapter(notesGridAdapter);
                break;
        }
    }

    private void choiceViews(ArrayList<Note> notes) {
        switch (typeViewsNotes) {
            case "Lista":
                stubList.setVisibility(View.VISIBLE);
                stubGrid.setVisibility(View.GONE);
                NotesListAdapter notesListAdapter = new NotesListAdapter(this, R.layout.listview_item, notes);
                listViewNotes.setAdapter(notesListAdapter);
                break;
            case "Columna":
                stubList.setVisibility(View.GONE);
                stubGrid.setVisibility(View.VISIBLE);
                NotesGridAdapter notesGridAdapter = new NotesGridAdapter(this, R.layout.gridview_item, notes);
                gridViewNotes.setAdapter(notesGridAdapter);
                break;
        }
    }

    private void performSearch() {
        ArrayList<Note> notesCopy = (ArrayList<Note>) listNotes.clone();
        if (editTextSearch.getText().toString().equals("")) {
            choiceViews(notesCopy);
        } else {
            ArrayList<Note> notes = new ArrayList<Note>();
            Iterator itr = notesCopy.iterator();
            while (itr.hasNext()) {
                Note note = (Note) itr.next();
                if (note.getTitle().toLowerCase().contains(editTextSearch.getText().toString().toLowerCase())) {
                    notes.add(note);
                }
            }
            choiceViews(notes);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNote(View view) {
        Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
        startActivity(intent);
    }

    private void alertDialog(final Note note) {
        final CharSequence[] options = {"Ver o Modificar", "Cifrar", "Eliminar"};
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Seleccione una opción")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equals("Ver o Modificar")) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("note", note);
                            Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else if (options[i].equals("Cifrar")) {
                            Toast.makeText(getApplicationContext(), "Nota Cifrada", Toast.LENGTH_SHORT).show();
                        } else if (options[i].equals("Eliminar")) {
                            Toast.makeText(getApplicationContext(), "Nota Eliminada", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialogBuilder.show();
    }

    public void alphabeticalOrder(View view) {
        ArrayList<Note> listNotesCopy = (ArrayList<Note>) listNotes.clone();
        switch (order) {
            case 1:
                order = 2;
                ArrayList<Note> listNotesSupport = new ArrayList<>();
                ArrayList<String> notes = new ArrayList<>();
                Iterator itr = listNotesCopy.iterator();
                while (itr.hasNext()) {
                    Note note = (Note) itr.next();
                    notes.add(note.getTitle() + "," + note.getNoteId().toString());
                }
                Collections.sort(notes);
                itr = notes.iterator();
                while (itr.hasNext()) {
                    String[] titleId = itr.next().toString().split(",");
                    Iterator itrCopy = listNotesCopy.iterator();
                    while (itrCopy.hasNext()) {
                        Note note = (Note) itrCopy.next();
                        if (note.getNoteId().toString().equals(titleId[1]))
                            listNotesSupport.add(note);
                    }
                }
                choiceViews(listNotesSupport);
                break;
            case 2:
                order = 1;
                choiceViews(listNotesCopy);
                break;
        }
    }

    private void fill() {
        listNotes = new ArrayList<Note>() {{
            add(new Note(1, "Titulo 1", "Esta es una nota de prueba " +
                    "Esta es una linea de prueba"));
            add(new Note(2, "AAAA", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(3, "Titulo 3", "Prueba una nota de prueba Esta es una linea de prueba"));
            add(new Note(4, "Titulo 4", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(5, "Titulo 5", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(6, "Titulo 6", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(7, "Titulo 7", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(8, "Titulo 8", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(9, "Titulo 9", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(10, "Titulo 10", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(11, "Titulo 11", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(12, "Titulo 12", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(13, "Titulo 13", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(14, "Titulo 14", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(15, "Titulo 15", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(16, "Titulo 16", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
            add(new Note(17, "BBBBB", "Esta es la nota B de prueba " +
                    "Esta es una nota de prueba Esta es una linea de prueba" +
                    "Esta es una nota de prueba Esta es una nota de prueba"));
        }};
    }
}
