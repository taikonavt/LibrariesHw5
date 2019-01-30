package ru.geekbrains.android3_5.mvp.model.entity.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class RoomUser
{
    @NonNull
    @PrimaryKey
    private String login;
    private String avatarUrl;
    private String reposUrl;

    public RoomUser() {}

    public RoomUser(@NonNull String login, String avatarUrl, String reposUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.reposUrl = reposUrl;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getReposUrl()
    {
        return reposUrl;
    }

    public void setReposUrl(String reposUrl)
    {
        this.reposUrl = reposUrl;
    }
}
