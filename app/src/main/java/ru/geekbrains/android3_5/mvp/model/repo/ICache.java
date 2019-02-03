package ru.geekbrains.android3_5.mvp.model.repo;

import android.graphics.Bitmap;

import java.util.List;

import io.reactivex.Single;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;

public interface ICache {
    User saveUser(User user, String username);

    Single<Object> findByLogin(String username);

    List<Repository> saveRepos(List<Repository> repos, User user);

    Single<Object> getRepos(User user);

    void saveImage(Bitmap resource, String url);

    Single<Bitmap> getImage(String url);
}
