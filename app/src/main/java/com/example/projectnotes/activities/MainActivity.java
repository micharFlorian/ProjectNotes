package com.example.projectnotes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

/**
 * Pantalla donde se cargan todas las notas
 */
public class MainActivity extends AppCompatActivity {

    //Objetos de la interfaz
    private ListView listViewNotes;
    private EditText editTextSearch;

    private ComponentNotes componentNotes;          //Objeto que nos permite realizar las operaciones con la BDD
    private ArrayList<Note> listNotes;              //ArrayList que contendrá todas las notas de la BDD

    private int alphabeticalOrder = 1;              //Variable que controla el orden alfabetico
    private final String SHA = "SHA-1";             //Constante que guarda el tipo de hash
    public static boolean isPermission;             //Variable que controla los permisos
    public static boolean isUpdate;                 //Variable que controla si hacemos un update o insert en el EditTextActivity

    /**
     * Se crea la interfaz del activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        fillListView();

        //Indicamos que el editTextSearch este pendiente del boton ENTER del teclado del usuario
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

        //Cuando se selecciona un item del ListView mostramos una ventana de dialogo
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

    /**
     * Inicializamos los objetos de la interfaz, el objeto de la BDD y isUpdate
     */
    private void init() {
        isUpdate = false;

        componentNotes = new ComponentNotes(this);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        listViewNotes = (ListView) findViewById(R.id.listViewNotes);
    }

    /**
     * Pedimos los permisos al usuario
     */
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

    /**
     * Comprobamos si se han concedido los permisos
     */
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

    /**
     * Mostramos una ventana de advertencia en caso de que no se hayan concedido los permisos
     */
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

    /**
     * Buscamos una nota por su titulo a partir del String que ingrese el usuario en el editTextSearch
     */
    private void performSearch() {
        if (listNotes != null) {

            //Clonamos el ArrayList listNotes para no tener que hacer una consulta a la base de datos
            ArrayList<Note> notesCopy = (ArrayList<Note>) listNotes.clone();
            if (editTextSearch.getText().toString().isEmpty()) {
                //Si editTextSearch está vacío y se le da al ENTER con el teclado volvemos a mostrar todas las notas
                fillListView(notesCopy);
            } else {
                //Buscamos todas las notas que coincidan con el String de editTextSearch
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

    /*
     *Se crea el boton de Ajustes en el ActionBar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /*
     *Comprobamos si han seleccionado el boton de Ajuste y llamamos a SettingsActivity
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     *Llamamos a EditTextActivity
     */
    public void addNote(View view) {
        Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
        startActivity(intent);
    }

    /*
     *Según el atributo Encode de Note mostramos el la ventana con las opciones de la nota o pedimos la contraseña
     */
    private void showAlertDialog(final Note note) {
        switch (note.getEncode()) {
            case 0:
                CharSequence[] options = {"Ver o Modificar", "Ocultar contenido", "Eliminar"};
                defaultAlertDialog(note, options);
                break;
            case 1:
                passwordAlertDialog(note);
                break;
        }
    }

    /*
     *Ventana de dialogo con las opciones de las notas
     */
    private void defaultAlertDialog(final Note note, final CharSequence[] options) {
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

    /*
     *Ventana de dialogo que pide la contraseña
     */
    private void passwordAlertDialog(final Note note) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_password, null);
        alertDialog.setView(customLayout);

        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editTextPassword = customLayout.findViewById(R.id.editTextPassword);
                        User user = componentNotes.readUser(note.getUserId().getUserId());
                        if (user.getPassword().equals(sha.stringToHash(editTextPassword.getText().toString(), SHA))) {
                            CharSequence[] options = {"Ver o Modificar", "Mostrar contenido", "Eliminar"};
                            defaultAlertDialog(note, options);
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

    /*
     *Orden alfabetico de las notas
     */
    public void alphabeticalOrder(View view) {
        if (listNotes != null) {
            //Clonamos listNotes para que luego no tengamos que consultar la BDD
            ArrayList<Note> listNotesCopy = (ArrayList<Note>) listNotes.clone();
            switch (alphabeticalOrder) {
                case 1:
                    //Ordenamos las notas alfabeticamente
                    alphabeticalOrder = 2;
                    ArrayList<Note> listNotesSupport = new ArrayList<>();
                    ArrayList<String> notes = new ArrayList<>();        //La usamos para guardar los titulos de las notas
                    Iterator itr = listNotesCopy.iterator();
                    while (itr.hasNext()) {
                        Note note = (Note) itr.next();
                        //Añadimos al titulo de la nota su id separado por "," ("prueba,1")
                        notes.add(note.getTitle().toLowerCase() + "," + note.getNoteId().toString());
                    }
                    Collections.sort(notes);        //Ordena alfabeticamente el ArrayList notes
                    itr = notes.iterator();
                    while (itr.hasNext()) {
                        //Dividimos el String a partir de la "," y lo añadimos a un String[]
                        //String[0] irá el titulo de la nota
                        //String[1] irá el id de la nota
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
                    //Deshacemos el orden alfabetico y mostramos las notas como estaban
                    alphabeticalOrder = 1;
                    fillListView(listNotesCopy);
                    break;

            }
        }
    }

    /*
     *Consultamos todas las notas de la BDD y las añadimos al listViewNotes
     */
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

    /*
     *Añadimos las notas que contenga el ArrayList al listViewNotes
     */
    private void fillListView(ArrayList<Note> notes) {
        NotesListAdapter notesListAdapter = new NotesListAdapter(this,
                R.layout.listview_item, notes);
        listViewNotes.setAdapter(notesListAdapter);
    }
}
