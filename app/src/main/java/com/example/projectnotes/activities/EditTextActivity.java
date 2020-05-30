package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

public class EditTextActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private TextView textViewId, textViewEncode, textViewUserId;
    private ImageView imageView;
    private ImageButton imageButtonVolume;

    private ComponentNotes componentNotes;
    private ProgressDialog progressDialog;
    private TtsManager ttsManager = null;

    private int stopTtsManager = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        getSupportActionBar().setTitle("Editor de Notas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        catchNote();

        checkDescription();
    }

    private void checkDescription() {
        if (!editTextDescription.getText().toString().isEmpty()) {
            ttsManager = new TtsManager();
            ttsManager.init(this);
            imageButtonVolume.setVisibility(View.VISIBLE);
        } else {
            imageButtonVolume.setVisibility(View.INVISIBLE);
        }
    }

    private void catchNote() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Note note = (Note) bundle.getSerializable("note");
            Note noteImage = componentNotes.readNote(note.getNoteId());
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
            textViewId.setText(noteImage.getNoteId().toString());
            textViewEncode.setText(noteImage.getEncode().toString());
            textViewUserId.setText(noteImage.getUserId().getUserId().toString());
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
        textViewId = (TextView) findViewById(R.id.textViewId);
        textViewEncode = (TextView) findViewById(R.id.textViewEncode);
        textViewUserId = (TextView) findViewById(R.id.textViewUserId);
        imageButtonVolume = (ImageButton) findViewById(R.id.imageButtonVolume);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (!editTextTitle.getText().toString().isEmpty() || !editTextDescription.getText().toString().isEmpty()) {
            getMenuInflater().inflate(R.menu.menu_edit_text_share, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.menu_edit_text_attach, menu);
            return true;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clip:
                if (MainActivity.isPermission) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent, "Selecione la Aplicación"), 10);
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), "La aplicación no tiene permisos " +
                            "para abrir la galería", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_share:
                shareNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareNote() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, editTextTitle.getText().toString()
                + "\n\n" + editTextDescription.getText().toString());
        intent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(intent, null);
        startActivity(shareIntent);
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

    private void goMain() {
        Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private byte[] imageViewToByte() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream(20480);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    public void readingDescription(View view) {
        switch (stopTtsManager) {
            case 0:
                stopTtsManager = 1;
                ttsManager.initQueue(editTextDescription.getText().toString());
                break;
            case 1:
                stopTtsManager = 0;
                ttsManager.stop();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ttsManager != null) {
            ttsManager.shutDown();
        }
        progressDialog.dismiss();
    }
}
