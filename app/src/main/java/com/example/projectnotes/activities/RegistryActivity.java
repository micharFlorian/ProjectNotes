package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.projectnotes.R;

public class RegistryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        getSupportActionBar().hide();
    }

    public void logIn(View view) {
        Intent intent = new Intent(RegistryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
