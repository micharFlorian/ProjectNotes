package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        init();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Note note = (Note) bundle.getSerializable("note");
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
            if (note.getImageId() != null) {
                imageView.setImageResource(note.getImageId());
            }
        }

    }

    private void init() {
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
            imageView.setImageURI(path);
        }
    }

    public void confirmNote(View view) {
        Note note = new Note((Integer) imageView.getTag(), 100, editTextTitle.getText().toString(), editTextDescription.getText().toString());
        goMain(note);
    }

    private void goMain(Note note) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("note", note);
        Intent intent = new Intent(EditTextActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
