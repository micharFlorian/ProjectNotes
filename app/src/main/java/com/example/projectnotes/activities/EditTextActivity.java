package com.example.projectnotes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.projectnotes.R;
import com.example.projectnotes.pojos.Note;

public class EditTextActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        getSupportActionBar().setTitle("Editor de Notas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        imageView = (ImageView) findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Note note = (Note) bundle.getSerializable("note");
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_text, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clip:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Selecione la Aplicación"), 10);
                break;
            case R.id.item_share:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri path = data.getData();
            imageView.setImageURI(path);
        }
    }

    public void confirmNote(View view) {
        if (editTextTitle.getText() != null && editTextDescription.getText() != null) {
            Note note = new Note(editTextTitle.getText().toString(), editTextDescription.getText().toString());
            goMain(note);
        } else if (editTextTitle.getText() == null && editTextDescription.getText() != null) {
            Note note = new Note("", editTextDescription.getText().toString());
            goMain(note);
        } else if (editTextTitle.getText() != null && editTextDescription.getText() == null) {
            Note note = new Note(editTextTitle.getText().toString(), "");
            goMain(note);
        } else if (editTextTitle.getText() == null && editTextDescription.getText() == null) {
            Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void goMain(Note note) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("note", note);
        Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
