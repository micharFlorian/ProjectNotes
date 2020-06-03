package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.projectnotes.R;

/**
 * Pantalla de Ajustes
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Se carga la interfaz del activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Ajustes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.fragment_settings) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getFragmentManager().beginTransaction().add(R.id.fragment_settings, new SettingsFragment()).commit();
            SplashActivity.nameActivity = "SettingsActivity";
        }
    }
}
