package com.example.projectnotes.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listNotes.add((Note) bundle.getSerializable("note"));
        }
        NotesListAdapter notesListAdapter = new NotesListAdapter(this, listNotes);
        listViewNotes.setAdapter(notesListAdapter);

        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note note = (Note) listViewNotes.getItemAtPosition(i);
                alertDialog(note);
            }
        });
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        boolean sync = preferences.getBoolean("pref_sync", true);
//        String typeSync = preferences.getString("pref_sync_connection_type", "WiFi");
//        String passsword = preferences.getString("text_password", "");
//        Toast.makeText(getApplicationContext(), passsword + "", Toast.LENGTH_SHORT).show();

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
}
