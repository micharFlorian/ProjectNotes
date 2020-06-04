package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectnotes.R;
import com.example.projectnotes.complements.TtsManager;
import com.example.projectnotes.componentBd.ComponentNotes;
import com.example.projectnotes.pojos.Note;
import com.example.projectnotes.pojos.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

/*
 *Pantalla del Editor de Notas
 */
public class EditTextActivity extends AppCompatActivity {

    //Creación de los objetos de la interfaz 
    private EditText editTextTitle, editTextDescription;
    private TextView textViewId, textViewEncode, textViewUserId;
    private ImageView imageViewAttached, imageViewDialog;
    private ImageButton imageButtonVolume;

    private Dialog dialogShowImage;                 //Objeto que nos muestra un dialogo con la imagen adjuntada
    private ComponentNotes componentNotes;          //Objeto que nos permite realizar las operaciones con la BDD
    private ProgressDialog progressDialog;          //Objeto que nos muestra la ventana de carga
    private TtsManager ttsManager = null;           //Objeto que nos permite la convertir el texto a voz

    private int stopTtsManager = 0;                 //Variable de apoyo para parar la lectura del texto 

    /*
     *Método que crea la vista del Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        getSupportActionBar().setTitle("Editor de Notas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        catchNote();

        checkDescription();

        //Espera que presionen la imagen para lanzar el método showImage()
        imageViewAttached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage();
            }
        });
    }

    /*
     *Se crea una ventana diálogo y  añade la imagen que se ha pinchado
     */
    private void showImage() {
        dialogShowImage = new Dialog(EditTextActivity.this);
        dialogShowImage.setContentView(R.layout.dialog_show_image);
        imageViewDialog = dialogShowImage.findViewById(R.id.imageViewDialog);
        imageViewDialog.setImageBitmap(((BitmapDrawable) imageViewAttached.getDrawable()).getBitmap());
        dialogShowImage.show();
    }

    /*
     *Comprueba que el campo de la descripción no está vacío y muestra el botón de lectura a voz
     */
    private void checkDescription() {
        if (!editTextDescription.getText().toString().isEmpty()) {

            //Se inicializa el obejeto ttsManager y se llama al metodo init() para inicializar los atributos de la clase
            ttsManager = new TtsManager();
            ttsManager.init(this);
            imageButtonVolume.setVisibility(View.VISIBLE);
        } else {
            imageButtonVolume.setVisibility(View.INVISIBLE);
        }
    }

    /*
     *Captura una nota, si se ha mandado desde el MainActivity, y se meten los valores de la nota
     * en la pantalla
     */
    private void catchNote() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            //Se obtiene el la nota del objeto Bundle y se ñadden los valores a la interfaz
            Note note = (Note) bundle.getSerializable("note");
            Note noteImage = componentNotes.readNote(note.getNoteId());
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
            textViewId.setText(noteImage.getNoteId().toString());
            textViewEncode.setText(noteImage.getEncode().toString());
            textViewUserId.setText(noteImage.getUserId().getUserId().toString());

            //Se comprueba que la nota tiene una imagen
            if (noteImage.getImage() != null) {
                if (!Arrays.equals(noteImage.getImage(), imageViewToByte())) {
                    //Se convierte el byte[] a Bitmap y se añade a la imagen del Activity
                    Bitmap bitmap = BitmapFactory.decodeByteArray(noteImage.getImage(),
                            0, noteImage.getImage().length);
                    imageViewAttached.setImageBitmap(bitmap);
                }
            }
        }
    }

    /*
     *Se inicializan todos los obejetos de la interfaz, el objeto componentNotes y el progressDialog
     */
    private void init() {
        componentNotes = new ComponentNotes(this);
        progressDialog = new ProgressDialog(EditTextActivity.this);

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        textViewId = (TextView) findViewById(R.id.textViewId);
        textViewEncode = (TextView) findViewById(R.id.textViewEncode);
        textViewUserId = (TextView) findViewById(R.id.textViewUserId);
        imageButtonVolume = (ImageButton) findViewById(R.id.imageButtonVolume);
        imageViewAttached = (ImageView) findViewById(R.id.imageView);
    }

    /*
     *Se crean los botones del menú del ActionBar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!editTextTitle.getText().toString().isEmpty() || !editTextDescription.getText().toString().isEmpty()) {
            getMenuInflater().inflate(R.menu.menu_edit_text_share, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.menu_edit_text_attach, menu);
            return true;
        }
    }

    /*
     *Comprobamos cual de los botones del ActionBar ha sido selecionado y la lanzamos la funcion correspondiente
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_clip:
                openGalery();
                break;

            case R.id.item_share:
                shareNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     *Abrimos la galería del dispositivo
     */
    private void openGalery() {
        if (MainActivity.isPermission) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent, "Selecione la Aplicación"), 10);
        } else {
            Toasty.normal(getApplicationContext(), "La aplicación no tiene permisos " +
                    "para abrir la galería", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *Leemos el titulo y la descripcion de la nota para mandarla como texto plano a la aplicacion
     * que elija el usuario
     */
    private void shareNote() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, editTextTitle.getText().toString()
                + "\n\n" + editTextDescription.getText().toString());
        intent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(intent, "Elige la aplicación");
        startActivity(shareIntent);
    }

    /*
     *Capturamos la imagen que elieg el usaurio y la mostramos en el ImageView de la pantalla
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri path = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(path);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewAttached.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *Se crea una nota con los datos que se han metido en la interfaz, dependiendo de la variable isUpdate
     * hacemos un update de la nota o un insert
     */
    public void confirmNote(View view) {

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_save);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Note note = new Note(Integer.parseInt(textViewId.getText().toString()), editTextTitle.getText().toString(),
                editTextDescription.getText().toString(), Integer.parseInt(textViewEncode.getText().toString()),
                imageViewToByte(), new User(Integer.parseInt(textViewUserId.getText().toString())));

        if (MainActivity.isUpdate) {
            componentNotes.updateNote(note.getNoteId(), note);
        } else {
            componentNotes.insertNote(note);
        }

        goMain();
    }

    /*
     *Nos lleva al MainActivity
     */
    private void goMain() {
        Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*
     *Convertimos un ImageView en un byte[]
     */
    private byte[] imageViewToByte() {
        Bitmap bitmap = ((BitmapDrawable) imageViewAttached.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream(20480);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    /*
     *Dependiendo de la variable stopTtsManager inciamos la lectura a voz o la paramos
     */
    public void readingDescription(View view) {
        switch (stopTtsManager) {
            case 0:
                stopTtsManager = 1;
                //le pasamos el string que queremos que convierta a voz el Objeto ttsManager
                ttsManager.initQueue(editTextDescription.getText().toString());
                break;
            case 1:
                stopTtsManager = 0;
                ttsManager.stop();
                break;
        }
    }

    /*
     *Cuando el activity se destruye apagamos el objeto ttsManager para que no consuma recursos del sistema
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ttsManager != null) {
            ttsManager.shutDown();
        }
        progressDialog.dismiss();
    }
}
