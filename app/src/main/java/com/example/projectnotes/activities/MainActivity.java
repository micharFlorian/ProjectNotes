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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectnotes.R;
import com.example.projectnotes.adapters.NotesGridAdapter;
import com.example.projectnotes.adapters.NotesListAdapter;
import com.example.projectnotes.componentBd.ComponentNotes;
import com.example.projectnotes.pojos.Note;
import com.example.projectnotes.pojos.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private ListView listViewNotes;
    private GridView gridViewNotes;
    private EditText editTextSearch;
    private ViewStub stubList, stubGrid;

    private ComponentNotes componentNotes;
    private ArrayList<Note> listNotes;
    private String typeViewsNotes;
    int order = 1;
    public static boolean isPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        componentNotes = new ComponentNotes(this);

        fill();

        init();

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

        if (validatePermissions()) {
            isPermission = true;
        } else {
            isPermission = false;
        }
    }

    private void init() {
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
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
        if (editTextSearch.getText().toString().isEmpty()) {
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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("PASSWORD");
        alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(MainActivity.this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
        alertDialog.setView(input);
//        alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        password = input.getText().toString();
//                        if (password.compareTo("") == 0) {
//                            if (pass.equals(password)) {
//                                Toast.makeText(getApplicationContext(),
//                                        "Password Matched", Toast.LENGTH_SHORT).show();
//                                Intent myIntent1 = new Intent(view.getContext(),
//                                        Show.class);
//                                startActivityForResult(myIntent1, 0);
//                            } else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Wrong Password!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
//        final CharSequence[] options = {"Ver o Modificar", "Cifrar", "Eliminar"};
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setTitle("Seleccione una opción")
//                .setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (options[i].equals("Ver o Modificar")) {
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("note", note);
//                            Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        } else if (options[i].equals("Cifrar")) {
//                            Toast.makeText(getApplicationContext(), "Nota Cifrada", Toast.LENGTH_SHORT).show();
//                        } else if (options[i].equals("Eliminar")) {
//                            Toast.makeText(getApplicationContext(), "Nota Eliminada", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        alertDialogBuilder.show();
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
        listNotes = componentNotes.readNotes();
    }
}
