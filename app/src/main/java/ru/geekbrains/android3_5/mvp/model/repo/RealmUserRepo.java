package ru.geekbrains.android3_5.mvp.model.repo;

import android.graphics.Bitmap;

import io.reactivex.Single;
import io.realm.Realm;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;
import ru.geekbrains.android3_5.mvp.model.entity.realm.RealmRepository;
import ru.geekbrains.android3_5.mvp.model.entity.realm.RealmUser;

import java.util.ArrayList;
import java.util.List;

public class RealmUserRepo implements ICache {

    @Override
    public User saveUser(User user, String username) {
        Realm realm = Realm.getDefaultInstance();
        RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();
        if (realmUser == null) {
            realm.executeTransaction(innerRealm -> {
                RealmUser newRealmUser = innerRealm.createObject(RealmUser.class, username);
                newRealmUser.setAvatarUrl(user.getAvatarUrl());
                newRealmUser.setReposUrl(user.getReposUrl());
            });
        } else {
            realm.executeTransaction(innerRealm -> {
                realmUser.setAvatarUrl(user.getAvatarUrl());
                realmUser.setReposUrl(user.getReposUrl());
            });
        }
        realm.close();
        return user;
    }

    @Override
    public Single<Object> findByLogin(String username) {
        return Single.create(emitter -> {
            Realm realm = Realm.getDefaultInstance();
            RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();
            if (realmUser == null) {
                emitter.onError(new RuntimeException("No such user in cache"));
            } else {
                emitter.onSuccess(new User(realmUser.getLogin(), realmUser.getAvatarUrl(), realmUser.getReposUrl()));
            }
            realm.close();
        });
    }

    @Override
    public List<Repository> saveRepos(List<Repository> repos, User user) {
        Realm realm = Realm.getDefaultInstance();
        RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

        if (realmUser == null) {
            realm.executeTransaction(innerRealm -> {
                RealmUser newRealmUser = innerRealm.createObject(RealmUser.class, user.getLogin());
                newRealmUser.setAvatarUrl(user.getAvatarUrl());
                newRealmUser.setReposUrl(user.getReposUrl());
            });
        }

        realm.executeTransaction(innerRealm -> {
            realmUser.getRepos().deleteAllFromRealm();
            for (Repository repository : repos) {
                RealmRepository realmRepository = innerRealm.createObject(RealmRepository.class, repository.getId());
                realmRepository.setName(repository.getName());
                realmUser.getRepos().add(realmRepository);
            }
        });
        realm.close();
        return repos;
    }

    @Override
    public Single<Object> getRepos(User user) {
        return Single.create(emitter -> {

            Realm realm = Realm.getDefaultInstance();
            RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

            if (realmUser == null) {
                emitter.onError(new RuntimeException("No such user in cache"));
            } else {
                List<Repository> repos = new ArrayList<>();
                for (RealmRepository realmRepository : realmUser.getRepos()) {
                    repos.add(new Repository(realmRepository.getId(), realmRepository.getName()));
                }
                emitter.onSuccess(repos);
            }
        });
    }

    @Override
    public void saveImage(Bitmap resource, String url) {

    }

    @Override
    public Single<Bitmap> getImage(String url) {
        return null;
    }
}
