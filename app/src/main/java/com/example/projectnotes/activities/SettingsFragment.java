package com.example.projectnotes.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.projectnotes.R;
import com.example.projectnotes.complements.DriveServiceHelper;
import com.example.projectnotes.complements.GoogleDriveFileHolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.example.projectnotes.complements.DriveServiceHelper.getGoogleDriveService;


public class SettingsFragment extends PreferenceFragment {

    private static final int REQUEST_CODE_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private DriveServiceHelper driveServiceHelper;
    private static final String TAG = "CopiaSeguridad";
    static String idFile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference preferenceBackup = (Preference) findPreference("pref_sync");
        preferenceBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(final Preference preference) {
                askGoogleCredentials();
                showDialogBackup();
                return false;
            }
        });

        Preference preferenceResetNotes = (Preference) findPreference("text_reset_notes");
        preferenceResetNotes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(final Preference preference) {
                showDialogReset();
                return false;
            }
        });

        Preference preferenceChangePassword = (Preference) findPreference("pref_password");
        preferenceChangePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(final Preference preference) {
                showDialogChangePassword();
                return false;
            }
        });
    }

    private void askGoogleCredentials() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            googleSignInClient = buildGoogleSignInClient();
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        } else {
            driveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getActivity(),
                    account, "appName"));
        }
    }

    private void showDialogChangePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Quiere cambiar la contraseña?")
                .setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), RegistryActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void showDialogReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Los datos que se hayan ingresado recientemente se perderán")
                .setPositiveButton("Restaurar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        askGoogleCredentials();
                        importDatabase();
                        Toast.makeText(getActivity(), "Notas restuaradas", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void showDialogBackup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Se creará una copia de seguridad de todos los datos y se guardará en" +
                " su cuenta de Google Drive")
                .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        exportDatabase();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        driveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getActivity(),
                                googleSignInAccount, "appName"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(getActivity(), signInOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportDatabase() {
        if (driveServiceHelper == null) {
            return;
        }

        driveServiceHelper.searchFile("notes", "application/octet-stream")
                .addOnSuccessListener(new OnSuccessListener<List<GoogleDriveFileHolder>>() {
                    @Override
                    public void onSuccess(List<GoogleDriveFileHolder> googleDriveFileHolders) {
                        Gson gson = new Gson();
                        if (googleDriveFileHolders == null) {
                            GoogleDriveFileHolder googleDriveFileHolder = googleDriveFileHolders.get(0);
                            idFile = googleDriveFileHolder.getId();
                        }
                        Log.d(TAG, "onSuccess2: " + idFile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

        driveServiceHelper.deleteFolderFile(idFile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccesDelete");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

        driveServiceHelper.uploadFile(new java.io.File("/data/data/com.example.projectnotes/databases/", "notes"),
                "application/octet-stream", null)
                .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                    @Override
                    public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                        Gson gson = new Gson();
                        Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
                        idFile = googleDriveFileHolder.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void importDatabase() {
        if (driveServiceHelper == null) {
            return;
        }
        driveServiceHelper.searchFile("notes", "application/octet-stream")
                .addOnSuccessListener(new OnSuccessListener<List<GoogleDriveFileHolder>>() {
                    @Override
                    public void onSuccess(List<GoogleDriveFileHolder> googleDriveFileHolders) {
                        Gson gson = new Gson();
                        GoogleDriveFileHolder googleDriveFileHolder = googleDriveFileHolders.get(0);
                        idFile = googleDriveFileHolder.getId();
                        Log.d("TAG 3", "" + idFile);

                        File dir = new File("/data/data/com.example.projectnotes/databases");
                        if (dir.isDirectory()) {
                            String[] hijos = dir.list();
                            for (int i = 0; i < hijos.length; i++) {
                                new File(dir, hijos[i]).delete();
                            }
                            Log.d("TAG", "Archivo Database Borrado");
                            File ficheroDatabase = new File("/data/data/com.example.projectnotes/databases/notes");
                            try {
                                ficheroDatabase.createNewFile();
                                Log.d("TAG", "Archivo Database Creado");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("Id file", "" + idFile);
                        driveServiceHelper.downloadFile(new java.io.File("/data/data/com.example.projectnotes/databases/", "notes"),
                                "" + idFile + "")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccesDownload: ");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
}
