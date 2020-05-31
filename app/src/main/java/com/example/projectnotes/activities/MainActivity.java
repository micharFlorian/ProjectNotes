package com.example.projectnotes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectnotes.R;
import com.example.projectnotes.adapters.NotesListAdapter;
import com.example.projectnotes.componentBd.ComponentNotes;
import com.example.projectnotes.hash.sha;
import com.example.projectnotes.pojos.Note;
import com.example.projectnotes.pojos.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private ListView listViewNotes;
    private EditText editTextSearch;

    private ComponentNotes componentNotes;
    private ArrayList<Note> listNotes;

    private int order = 1;
    private final String SHA = "SHA-1";
    public static boolean isPermission;
    public static boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        fillListView();

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
                showAlertDialog(note);
            }
        });

        if (validatePermissions()) {
            isPermission = true;
        } else {
            isPermission = false;
        }
    }

    private void init() {
        isUpdate = false;

        componentNotes = new ComponentNotes(this);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        listViewNotes = (ListView) findViewById(R.id.listViewNotes);
    }

    private boolean validatePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            loadRecommendationDialog();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 100);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermission = true;
            } else {
                isPermission = false;
            }
        }
    }

    private void loadRecommendationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Permisos Desactivados");
        alertDialogBuilder.setMessage("Debe aceptar los permisos para el correcto funcionamiento" +
                "de la aplicación");
        alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 100);
            }
        });
        alertDialogBuilder.show();
    }

    private void performSearch() {
        if (listNotes != null) {
            ArrayList<Note> notesCopy = (ArrayList<Note>) listNotes.clone();
            if (editTextSearch.getText().toString().isEmpty()) {
                fillListView(notesCopy);
            } else {
                ArrayList<Note> notes = new ArrayList<Note>();
                Iterator itr = notesCopy.iterator();
                while (itr.hasNext()) {
                    Note note = (Note) itr.next();
                    if (note.getTitle().toLowerCase().contains(editTextSearch.getText().toString().toLowerCase())) {
                        notes.add(note);
                    }
                }
                fillListView(notes);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
//                Intent intent = new Intent(MainActivity.this, CopiaSeguridad.class);
//                startActivity(intent);
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

    private void showAlertDialog(final Note note) {
        switch (note.getEncode()) {
            case 0:
                CharSequence[] options = {"Ver o Modificar", "Ocultar contenido", "Eliminar"};
                alertDialog(note, options);
                break;
            case 1:
                alertDialogPassword(note);
                break;
        }
    }

    private void alertDialog(final Note note, final CharSequence[] options) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Seleccione una opción")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equals("Ver o Modificar")) {
                            isUpdate = true;
                            User user = componentNotes.readUser(note.getUserId().getUserId());
                            note.setUserId(user);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("note", note);
                            Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else if (options[i].equals("Ocultar contenido")) {
                            Note noteUpdate = componentNotes.readNote(note.getNoteId());
                            noteUpdate.setEncode(1);
                            if (componentNotes.updateNote(note.getNoteId(), noteUpdate) != 0) {
                                fillListView();
                                Toast.makeText(getApplicationContext(), "Contenido de la nota ocultado",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else if (options[i].equals("Mostrar contenido")) {
                            Note noteUpdate = componentNotes.readNote(note.getNoteId());
                            noteUpdate.setEncode(0);
                            if (componentNotes.updateNote(note.getNoteId(), noteUpdate) != 0) {
                                fillListView();
                            }
                        } else if (options[i].equals("Eliminar")) {
                            if (componentNotes.deleteNote(note.getNoteId()) != 0) {
                                fillListView();
                                Toast.makeText(getApplicationContext(), "Nota Eliminada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        alertDialogBuilder.show();
    }

    private void alertDialogPassword(final Note note) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_password, null);
        alertDialog.setView(customLayout);

        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editTextPassword = customLayout.findViewById(R.id.editTextPassword);
                        byte[] inputData = editTextPassword.getText().toString().getBytes();
                        byte[] outputData = new byte[0];
                        try {
                            outputData = sha.encryptSHA(inputData, SHA);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        BigInteger shaData = new BigInteger(1, outputData);
                        User user = componentNotes.readUser(note.getUserId().getUserId());
                        if (user.getPassword().equals(shaData.toString(16))) {
                            CharSequence[] options = {"Ver o Modificar", "Mostrar contenido", "Eliminar"};
                            alertDialog(note, options);
                        }

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void alphabeticalOrder(View view) {
        if (listNotes != null) {
            ArrayList<Note> listNotesCopy = (ArrayList<Note>) listNotes.clone();
            switch (order) {
                case 1:
                    order = 2;
                    ArrayList<Note> listNotesSupport = new ArrayList<>();
                    ArrayList<String> notes = new ArrayList<>();
                    Iterator itr = listNotesCopy.iterator();
                    while (itr.hasNext()) {
                        Note note = (Note) itr.next();
                        notes.add(note.getTitle().toLowerCase() + "," + note.getNoteId().toString());
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
                    fillListView(listNotesSupport);
                    break;
                case 2:
                    order = 1;
                    fillListView(listNotesCopy);
                    break;

            }
        }
    }

    private void fillListView() {
        listNotes = componentNotes.readNotes();
        if (listNotes != null) {
            NotesListAdapter notesListAdapter = new NotesListAdapter(this,
                    R.layout.listview_item, listNotes);
            listViewNotes.setAdapter(notesListAdapter);
        } else {
            listViewNotes.setAdapter(null);
        }
    }

    private void fillListView(ArrayList<Note> notes) {
        NotesListAdapter notesListAdapter = new NotesListAdapter(this,
                R.layout.listview_item, notes);
        listViewNotes.setAdapter(notesListAdapter);
    }
}
