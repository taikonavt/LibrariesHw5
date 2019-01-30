package ru.geekbrains.android3_5.mvp.model.repo;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.mvp.model.api.ApiHolder;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;

import java.util.List;

public class UserRepo
{
    public Single<User> getUser(String username)
    {
        return ApiHolder.getApi().getUser(username).subscribeOn(Schedulers.io());
    }

    public Single<List<Repository>> getUserRepos(String url)
    {
        return ApiHolder.getApi().getUserRepos(url).subscribeOn(Schedulers.io());
    }
}
