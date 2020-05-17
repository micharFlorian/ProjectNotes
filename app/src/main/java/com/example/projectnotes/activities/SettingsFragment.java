package com.example.projectnotes.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.projectnotes.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.preference.Preference;
import android.widget.Toast;


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference preference = (Preference) findPreference("text_reset_notes");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Las notas que se hayan creado recientemente se borrarán")
                        .setPositiveButton("Restaurar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getActivity(), "Notas restuaradas", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
                return false;
            }
        });
    }
}
