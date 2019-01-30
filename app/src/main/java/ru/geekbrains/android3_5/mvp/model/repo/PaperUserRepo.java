package ru.geekbrains.android3_5.mvp.model.repo;

import io.paperdb.Paper;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.mvp.model.api.ApiHolder;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;
import ru.geekbrains.android3_5.ui.NetworkStatus;

import java.util.List;

public class PaperUserRepo {
    public Single<User> getUser(String username) {
        if (NetworkStatus.isOnline()) {
            return ApiHolder.getApi().getUser(username)
                    .subscribeOn(Schedulers.io())
                    .map(user -> {
                        Paper.book("users").write(username, user);
                        return user;
                    });
        } else {
            if(!Paper.book("users").contains(username)){
                return Single.error(new RuntimeException("No such user in cache"));
            }
            return Single.fromCallable(() -> Paper.book("users").read(username));
        }
    }

    public Single<List<Repository>> getUserRepos(User user) {
        if (NetworkStatus.isOnline()) {
            return ApiHolder.getApi().getUserRepos(user.getReposUrl()).subscribeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .map(repos -> {
                        Paper.book("repos").write(user.getLogin(), repos);
                        return repos;
                    });
        } else {
            if(!Paper.book("repos").contains(user.getLogin())){
                return Single.error(new RuntimeException("No repos for such user in cache"));
            }
            return Single.fromCallable(() -> Paper.book("repos").read(user.getLogin()));
        }


    }
}
