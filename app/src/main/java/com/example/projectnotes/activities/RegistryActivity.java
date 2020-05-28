package com.example.projectnotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectnotes.R;
import com.example.projectnotes.componentBd.ComponentNotes;
import com.example.projectnotes.hash.sha;
import com.example.projectnotes.pojos.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

public class RegistryActivity extends AppCompatActivity {

    private TextView textViewWrongPassword, textViewNoMatch, textViewEmpty, editTextEmail;
    private EditText editTextPassword, editTextPasswordRepeated, editTextNewPassword;
    private Button buttonLogin, buttonChange, buttonBack;

    private User user;
    private ComponentNotes componentNotes;

    private final String SHA = "SHA-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        getSupportActionBar().hide();

        init();
        componentNotes = new ComponentNotes(this);
        userLogin();

        checkActivity();
    }

    private void userLogin() {
        ArrayList<User> users = componentNotes.readUsers();
        if (users != null) {
            Iterator itr = users.iterator();
            while (itr.hasNext()) {
                user = (User) itr.next();
            }
        }
    }

    private void init() {
        textViewWrongPassword = (TextView) findViewById(R.id.textViewWrongPassword);
        textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);
        textViewNoMatch = (TextView) findViewById(R.id.textViewNoMatch);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordRepeated = (EditText) findViewById(R.id.editTextPasswordRepeated);
        editTextNewPassword = (EditText) findViewById(R.id.editTextPasswordChange);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonChange = (Button) findViewById(R.id.buttonChange);
        buttonBack = (Button) findViewById(R.id.buttonBack);
    }

    private void checkActivity() {
        if (SplashActivity.nameActivity.equals("SplashActivity")) {
            buttonLogin.setVisibility(View.VISIBLE);
            editTextPasswordRepeated.setVisibility(View.VISIBLE);
            editTextNewPassword.setVisibility(View.INVISIBLE);
            buttonChange.setVisibility(View.INVISIBLE);
            buttonBack.setVisibility(View.INVISIBLE);
        } else if (SplashActivity.nameActivity.equals("SettingsActivity")) {
            editTextEmail.setText(user.getEmail());
            buttonLogin.setVisibility(View.INVISIBLE);
            editTextPasswordRepeated.setVisibility(View.INVISIBLE);
            editTextNewPassword.setVisibility(View.VISIBLE);
            buttonChange.setVisibility(View.VISIBLE);
            buttonBack.setVisibility(View.VISIBLE);
        }
    }

    public void logIn(View view) {
        if (editTextEmail.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty() || editTextPasswordRepeated.getText().toString().isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewNoMatch.setVisibility(View.INVISIBLE);
        } else {
            textViewEmpty.setVisibility(View.INVISIBLE);
            if (editTextPassword.getText().toString().equals(editTextPasswordRepeated.getText().toString())) {
                textViewNoMatch.setVisibility(View.INVISIBLE);
                User user = new User(editTextEmail.getText().toString(), passwordConvertHash().toString(16));
                if (componentNotes.insertUser(user) != 0) {
                    goToMain();
                } else {
                    Toast.makeText(getApplicationContext(), "Fallo al registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            } else {
                textViewNoMatch.setVisibility(View.VISIBLE);
            }
        }
    }

    public void changePassword(View view) {
        if (editTextNewPassword.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewWrongPassword.setVisibility(View.INVISIBLE);
        } else {
            textViewEmpty.setVisibility(View.INVISIBLE);
            if (user.getPassword().equals(passwordConvertHash().toString(16))) {
                textViewWrongPassword.setVisibility(View.INVISIBLE);
                byte[] inputData = editTextNewPassword.getText().toString().getBytes();
                byte[] outputData = new byte[0];
                try {
                    outputData = sha.encryptSHA(inputData, SHA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                componentNotes.updateUser(user.getEmail(), new User(editTextEmail.getText().toString(),
                        new BigInteger(1, outputData).toString(16)));
                Toast.makeText(getApplicationContext(), "Se ha cambiado la contraseña",
                        Toast.LENGTH_SHORT).show();
                goToSettings(new View(this));
            } else {
                textViewWrongPassword.setVisibility(View.VISIBLE);
            }
        }
    }

    private BigInteger passwordConvertHash() {
        byte[] inputData = editTextPassword.getText().toString().getBytes();
        byte[] outputData = new byte[0];
        try {
            outputData = sha.encryptSHA(inputData, SHA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigInteger(1, outputData);
    }

    private void goToMain() {
        Intent intent = new Intent(RegistryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(RegistryActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
