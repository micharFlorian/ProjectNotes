package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.projectnotes.R;

public class RegistryActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordRepeated;
    private EditText editTextNewPassword;
    private Button buttonLogin;
    private Button buttonChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        getSupportActionBar().hide();

        init();

        checkActivity();
    }

    private void init() {
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordRepeated = (EditText) findViewById(R.id.editTextPasswordRepeated);
        editTextNewPassword = (EditText) findViewById(R.id.editTextPasswordChange);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonChange = (Button) findViewById(R.id.buttonChange);
    }

    private void checkActivity() {
        if (SplashActivity.nameActivity.equals("SplashActivity")) {
            buttonLogin.setVisibility(View.VISIBLE);
            editTextPasswordRepeated.setVisibility(View.VISIBLE);
        } else if (SplashActivity.nameActivity.equals("SettingsActivity")) {
            editTextNewPassword.setVisibility(View.VISIBLE);
            buttonChange.setVisibility(View.VISIBLE);
        }
    }

    public void logIn(View view) {
        Intent intent = new Intent(RegistryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void changePassword(View view) {
        Intent intent = new Intent(RegistryActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
