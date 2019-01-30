package ru.geekbrains.android3_5.mvp.model.api;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;

public interface ApiService
{
    @GET("users/{user}")
    Single<User> getUser(@Path("user") String userName);

    @GET
    Single<List<Repository>> getUserRepos(@Url String url);
}
