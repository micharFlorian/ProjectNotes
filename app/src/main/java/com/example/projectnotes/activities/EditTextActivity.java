package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projectnotes.R;
import com.example.projectnotes.componentBd.ComponentNotes;
import com.example.projectnotes.pojos.Note;
import com.example.projectnotes.pojos.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class EditTextActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private ImageView imageView;

    private ComponentNotes componentNotes;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        getSupportActionBar().setTitle("Editor de Notas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        catchNote();
    }

    private void catchNote() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Note note = (Note) bundle.getSerializable("note");
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
            Note noteImage = componentNotes.readNote(note.getNoteId());
            if (noteImage.getImage() != null) {
                if (!Arrays.equals(noteImage.getImage(), imageViewToByte())) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(noteImage.getImage(),
                            0, noteImage.getImage().length);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void init() {
        componentNotes = new ComponentNotes(this);
        progressDialog = new ProgressDialog(EditTextActivity.this);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_text, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clip:
                if (MainActivity.isPermission) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent, "Selecione la Aplicación"), 10);
                    break;
                }
                break;
            case R.id.item_share:
                Toast.makeText(getApplicationContext(), "Compartir nota", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri path = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(path);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void confirmNote(View view) {
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Note note = new Note(editTextTitle.getText().toString(), editTextDescription.getText().toString(),
                imageViewToByte(), new User(1));
        componentNotes.insertNote(note);
        goMain();
    }

    private void goMain() {
        Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private byte[] imageViewToByte() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream(20480);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
    }
}
