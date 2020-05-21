package com.example.projectnotes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.example.projectnotes.R;

    public class SettingsActivity extends AppCompatActivity {

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

//        Preference myPref = (Preference) findPreference("text_reset_notes");
//        myPref.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                Intent intent = new Intent();
//                return false;
//            }
//        });

    }


}
