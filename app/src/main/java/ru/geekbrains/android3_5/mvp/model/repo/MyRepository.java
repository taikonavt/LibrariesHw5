package ru.geekbrains.android3_5.mvp.model.repo;

import java.util.List;

import io.reactivex.Single;
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
}
