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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
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
    private ImageButton imageButtonSearch;
    private ViewStub stubList;
    private ViewStub stubGrid;

    private ArrayList<Note> listNotes;

    int order = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        imageButtonSearch = (ImageButton) findViewById(R.id.imageButtonSearch);

//        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);

        stubGrid.inflate();
//        stubList.inflate();

        listViewNotes = (ListView) findViewById(R.id.listViewNotes);
        gridViewNotes = (GridView) findViewById(R.id.gridViewNotes);

        listNotes = new ArrayList<Note>() {{
            add(new Note("Titulo 1", "Esta es una nota de prueba " +
                    "Esta es una linea de prueba"));
            add(new Note("AAAA", "Esta es una nota de prueba Esta es una nota de prueba Esta es una linea de prueba"));
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
                    "Esta es una nota de prueba Esta es una linea de prueba" +
                    "Esta es una nota de prueba Esta es una nota de prueba"));
        }};
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listNotes.add((Note) bundle.getSerializable("note"));
        }


//        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Note note = (Note) listViewNotes.getItemAtPosition(i);
//                alertDialog(note);
//            }
//        });

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        boolean sync = preferences.getBoolean("pref_sync", true);


//        stubList.setVisibility(View.GONE);
//        stubGrid.setVisibility(View.VISIBLE);
        NotesGridAdapter notesListAdapter = new NotesGridAdapter(this, R.layout.gridview_item, listNotes);
        gridViewNotes.setAdapter(notesListAdapter);

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

    }

    private void performSearch() {
        if (editTextSearch.getText().toString().equals("")) {
            NotesListAdapter notesListAdapter = new NotesListAdapter(this, listNotes);
//            listViewNotes.setAdapter(notesListAdapter);
        } else {
            ArrayList<Note> notes = new ArrayList<>();
            Iterator itr = listNotes.iterator();
            while (itr.hasNext()) {
                Note note = (Note) itr.next();
                if (note.getTitle().equals(editTextSearch.getText().toString())) {
                    notes.add(note);
                }
            }
            NotesListAdapter notesListAdapter = new NotesListAdapter(this, notes);
//            listViewNotes.setAdapter(notesListAdapter);
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
        NotesListAdapter notesListAdapter;
        switch (order) {
            case 1:
                order = 2;
                ArrayList<String> notes = new ArrayList<>();
                Iterator itr = listNotes.iterator();
                while (itr.hasNext()) {
                    notes.add(((Note) itr.next()).getTitle());
                }
                Collections.sort(notes);
                listNotes.clear();
                Iterator itr1 = notes.iterator();
                while (itr1.hasNext()) {
                    Note note = new Note((String) itr1.next(), "");
                    listNotes.add(note);
                }
                notesListAdapter = new NotesListAdapter(this, listNotes);
//                listViewNotes.setAdapter(notesListAdapter);
                break;
            case 2:
                order = 1;
                Note note = new Note("prueba", "esto es una prueba");
                listNotes.clear();
                listNotes.add(note);
                notesListAdapter = new NotesListAdapter(this, listNotes);
//                listViewNotes.setAdapter(notesListAdapter);
                break;
        }
    }
}
