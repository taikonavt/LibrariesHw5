package ru.geekbrains.android3_5.mvp.model.entity.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class RoomImage {

    @NonNull
    @PrimaryKey
    private String sha1;
    private String path;

    public void setSha1(@NonNull String sha1) {
        this.sha1 = sha1;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NonNull
    public String getSha1() {
        return sha1;
    }

    public String getPath() {
        return path;
    }
}
