package ru.geekbrains.android3_5.mvp.model.repo;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.mvp.model.api.ApiHolder;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;
import ru.geekbrains.android3_5.ui.NetworkStatus;

public class MyRepository {

    private final ICache cache;

    public MyRepository(ICache cache){
        this.cache = cache;
    }

    public Single<User> getUser(String username) {
        if (NetworkStatus.isOnline()) {
            return ApiHolder.getApi().getUser(username)
                    .subscribeOn(Schedulers.io())
                    .map(user -> cache.saveUser(user, username));
        } else {
            return cache.findByLogin(username)
                    .subscribeOn(Schedulers.io()).cast(User.class);
        }
    }

    public Single<List<Repository>> getUserRepos(User user) {
        if (NetworkStatus.isOnline()) {
            return ApiHolder.getApi().getUserRepos(user.getReposUrl())
                    .subscribeOn(Schedulers.io())
                    .map(repos -> cache.saveRepos(repos, user));
        } else {

            return cache.getRepos(user)
                    .subscribeOn(Schedulers.io())
                    .cast((Class<List<Repository>>)(Class)List.class);
        }
    }

    public void saveImage(Bitmap resource, String url) {
        cache.saveImage(resource, url);
    }

    public Single<Bitmap> getImage(String url) {
        return cache.getImage(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
