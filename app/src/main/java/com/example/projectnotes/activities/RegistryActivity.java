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
import com.example.projectnotes.hash.Sha;
import com.example.projectnotes.pojos.User;

import java.util.ArrayList;
import java.util.Iterator;

/*
 *Pantalla donde registramos al usuario y cambiamos la contraseña
 */
public class RegistryActivity extends AppCompatActivity {

    //Creamos todos los objetos de la interfaz
    private TextView textViewWrongPassword, textViewNoMatch, textViewEmpty, editTextEmail;
    private EditText editTextPassword, editTextPasswordRepeated, editTextNewPassword;
    private Button buttonLogin, buttonChange, buttonBack;

    private User user;                          //Creamos un POJO de apoyo
    private ComponentNotes componentNotes;      //Objeto que nos permite realizar las operaciones con la BDD

    private final String SHA = "SHA-1";         //Constante que guarda el tipo de hash

    /**
     * Se crea la interfaz del activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        getSupportActionBar().hide();

        init();

        userLogin();

        checkActivity();
    }

    /**
     * Leemos el usuario de la BDD
     */
    private void userLogin() {
        ArrayList<User> users = componentNotes.readUsers();
        if (users != null) {
            Iterator itr = users.iterator();
            while (itr.hasNext()) {
                user = (User) itr.next();
            }
        }
    }

    /**
     * Inicializamos los obejtos de la interfaz y componentNotes
     */
    private void init() {
        componentNotes = new ComponentNotes(this);

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

    /**
     * Comprobamos si el usuario viene del SplashActivity o del SettingsActivity
     * Dependiendo de que pantalla venga mostramos unos botones u otros
     */
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

    /**
     * Insertamos un usuario con los datos recogidos de los campos del activity en la BDD
     */
    public void singIn(View view) {
        //Comprobamos que ningun de los EditText esté vacío
        //En caso de que alguno esté mostramos un TextView que pone que "Se deben de completar todos los campos"
        if (editTextEmail.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty() ||
                editTextPasswordRepeated.getText().toString().isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewNoMatch.setVisibility(View.INVISIBLE);
        } else {
            textViewEmpty.setVisibility(View.INVISIBLE);
            //Comprobamos que las contraseñas coinciden
            //En caso de que no coincidan mostramos un TextView que pone "Las constraseñas no coinciden"
            if (editTextPassword.getText().toString().equals(editTextPasswordRepeated.getText().toString())) {
                textViewNoMatch.setVisibility(View.INVISIBLE);
                //Leemos los datos de la interfaz, hacemos un insert en la BDD y lanzamos MainActivity
                User user = new User(editTextEmail.getText().toString(), passwordConvertHash(editTextPassword));
                if (componentNotes.insertUser(user) != 0) {
                    goToMain();
                } else {
                    Toast.makeText(getApplicationContext(), "Fallo al registrar el usuario",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                textViewNoMatch.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Actualizamos la contraseña del usuario en la BDD
     */
    public void changePassword(View view) {
        //Comprobamos que ningun de los EditText esté vacío
        //En caso de que alguno esté mostramos un TextView que pone que "Se deben de completar todos los campos"
        if (editTextNewPassword.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewWrongPassword.setVisibility(View.INVISIBLE);
        } else {
            textViewEmpty.setVisibility(View.INVISIBLE);
            //Comprobamos que la contraseña sea la correcta
            //En caso de que no lo sea mostramos un TexteView que pone "Contraseña incorrecta"
            if (user.getPassword().equals(passwordConvertHash(editTextPassword))) {
                textViewWrongPassword.setVisibility(View.INVISIBLE);
                //Hacemos un update con la nueva contraseña
                componentNotes.updateUser(user.getEmail(), new User(editTextEmail.getText().toString(),
                        passwordConvertHash(editTextNewPassword)));
                Toast.makeText(getApplicationContext(), "Se ha cambiado la contraseña",
                        Toast.LENGTH_SHORT).show();
                goToSettings(new View(this));
            } else {
                textViewWrongPassword.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Convierte el contenido del EditText en un Hash tipo SHA-1
     */
    private String passwordConvertHash(EditText editText) {
        return Sha.stringToHash(editText.getText().toString(), SHA);
    }

    /**
     * Se lanza MainActivity
     */
    private void goToMain() {
        Intent intent = new Intent(RegistryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Se lanza SettingsActivity
     */
    public void goToSettings(View view) {
        Intent intent = new Intent(RegistryActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
