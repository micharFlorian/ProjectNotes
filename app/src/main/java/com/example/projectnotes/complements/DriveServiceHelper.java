package com.example.projectnotes.complements;


import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

/*
 *Clase que realiza la conexión de la app con Google Drive y con la que manejamos las copias de seguridad
 */
public class DriveServiceHelper {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Drive drive;

    /*
     *Metodo constructor que inicializa el atributo drive
     */
    public DriveServiceHelper(Drive driveService) {
        drive = driveService;
    }

    /*
     *Obtiene las credenciales de Google y comienza la conexión con Google Drive
     */
    public static Drive getGoogleDriveService(Context context, GoogleSignInAccount account, String appName) {

        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());

        Drive googleDriveService =
                new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName(appName)
                        .build();
        return googleDriveService;
    }

    /*
     *Borra un fichero a la cuenta de Google Drive
     */
    public Task<Void> deleteFolderFile(final String fileId) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Retrieve the metadata as a File object.
                if (fileId != null) {
                    drive.files().delete(fileId).execute();
                }
                return null;
            }
        });
    }

    /*
     *Sube un fichero a la cuenta de Google Drive
     */
    public Task<GoogleDriveFileHolder> uploadFile(final java.io.File localFile, final String mimeType,
                                                  @Nullable final String folderId) {
        return Tasks.call(executor, new Callable<GoogleDriveFileHolder>() {
            @Override
            public GoogleDriveFileHolder call() throws Exception {
                List<String> root;
                if (folderId == null) {
                    root = Collections.singletonList("root");
                } else {
                    root = Collections.singletonList(folderId);
                }

                File metadata = new File()
                        .setParents(root)
                        .setMimeType(mimeType)
                        .setName(localFile.getName());

                FileContent fileContent = new FileContent(mimeType, localFile);
                File fileMeta = drive.files().create(metadata, fileContent).execute();
                GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                googleDriveFileHolder.setId(fileMeta.getId());
                googleDriveFileHolder.setName(fileMeta.getName());
                return googleDriveFileHolder;
            }
        });
    }

    /*
     *Busca un fichero en la cuenta de Google Drive
     */
    public Task<List<GoogleDriveFileHolder>> searchFile(final String fileName, final String mimeType) {
        return Tasks.call(executor, new Callable<List<GoogleDriveFileHolder>>() {
            @Override
            public List<GoogleDriveFileHolder> call() throws Exception {
                List<GoogleDriveFileHolder> googleDriveFileHolderList = new ArrayList<>();

                FileList result = drive.files().list()
                        .setQ("name = '" + fileName + "' and mimeType ='" + mimeType + "'")
                        .setSpaces("drive")
                        .setFields("files(id, name,size,createdTime,modifiedTime,starred)")
                        .execute();

                if (!result.isEmpty()) {
                    for (int i = 0; i < result.getFiles().size(); i++) {
                        GoogleDriveFileHolder googleDriveFileHolder = new GoogleDriveFileHolder();
                        googleDriveFileHolder.setId(result.getFiles().get(i).getId());
                        googleDriveFileHolder.setName(result.getFiles().get(i).getName());
                        googleDriveFileHolder.setModifiedTime(result.getFiles().get(i).getModifiedTime());
                        googleDriveFileHolder.setSize(result.getFiles().get(i).getSize());
                        googleDriveFileHolderList.add(googleDriveFileHolder);

                    }
                    if (googleDriveFileHolderList != null) {
                        return googleDriveFileHolderList;
                    }
                }
                return null;
            }
        });
    }

    /*
     *Descarga un fichero de la cuenta de Google Drive
     */
    public Task<Void> downloadFile(final java.io.File fileSaveLocation, final String fileId) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                OutputStream outputStream = new FileOutputStream(fileSaveLocation);
                drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                return null;
            }
        });
    }

}