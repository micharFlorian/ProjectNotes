package com.example.projectnotes.complements;

import com.google.api.client.util.DateTime;

/*
 *Clase con la que controlamos los ficheros de la copia de seguridad y la restauración de la base de datos
 */
public class GoogleDriveFileHolder {

    private String id;
    private String name;
    private DateTime modifiedTime;
    private long size;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModifiedTime(DateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "GoogleDriveFileHolder{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", modifiedTime=" + modifiedTime +
                ", size=" + size +
                '}';
    }
}
