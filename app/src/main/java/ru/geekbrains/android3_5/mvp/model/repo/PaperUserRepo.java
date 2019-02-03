package ru.geekbrains.android3_5.mvp.model.repo;

import android.graphics.Bitmap;

import io.paperdb.Paper;
import io.reactivex.Single;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;

import java.util.List;

public class PaperUserRepo implements ICache {

    @Override
    public User saveUser(User user, String username) {
        Paper.book("users").write(username, user);
        return user;
    }

    @Override
    public Single<Object> findByLogin(String username) {
        if(!Paper.book("users").contains(username)){
            return Single.error(new RuntimeException("No such user in cache"));
        }
        return Single.fromCallable(() -> Paper.book("users").read(username));
    }

    @Override
    public List<Repository> saveRepos(List<Repository> repos, User user) {
        Paper.book("repos").write(user.getLogin(), repos);
        return repos;
    }

    @Override
    public Single<Object> getRepos(User user) {
        if(!Paper.book("repos").contains(user.getLogin())){
            return Single.error(new RuntimeException("No repos for such user in cache"));
        }
        return Single.fromCallable(() -> Paper.book("repos").read(user.getLogin()));
    }

    @Override
    public void saveImage(Bitmap resource, String url) {

    }

    @Override
    public Single<Bitmap> getImage(String url) {
        return null;
    }
}
