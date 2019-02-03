package ru.geekbrains.android3_5.mvp.model.repo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.App;
import ru.geekbrains.android3_5.mvp.common.Utils;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;
import ru.geekbrains.android3_5.mvp.model.entity.room.RoomImage;
import ru.geekbrains.android3_5.mvp.model.entity.room.RoomRepository;
import ru.geekbrains.android3_5.mvp.model.entity.room.RoomUser;
import ru.geekbrains.android3_5.mvp.model.entity.room.db.UserDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomUserRepo implements ICache {

    @Override
    public User saveUser(User user, String username) {
        RoomUser roomUser = UserDatabase.getInstance().getUserDao()
                .findByLogin(username);

        if (roomUser == null) {
            roomUser = new RoomUser();
            roomUser.setLogin(username);
        }

        roomUser.setAvatarUrl(user.getAvatarUrl());
        roomUser.setReposUrl(user.getReposUrl());

        UserDatabase.getInstance().getUserDao()
                .insert(roomUser);

        return user;
    }

    @Override
    public Single<Object> findByLogin(String username) {
        return Single.create(emitter -> {
            RoomUser roomUser = UserDatabase.getInstance().getUserDao()
                    .findByLogin(username);

            if (roomUser == null) {
                emitter.onError(new RuntimeException("No such user in cache"));
            } else {
                emitter.onSuccess(new User(roomUser.getLogin(), roomUser.getAvatarUrl(), roomUser.getReposUrl()));
            }
        });
    }

    @Override
    public List<Repository> saveRepos(List<Repository> repos, User user) {
        RoomUser roomUser = UserDatabase.getInstance().getUserDao()
                .findByLogin(user.getLogin());

        if (roomUser == null) {
            roomUser = new RoomUser();
            roomUser.setLogin(user.getLogin());
            roomUser.setAvatarUrl(user.getAvatarUrl());
            roomUser.setReposUrl(user.getReposUrl());
            UserDatabase.getInstance()
                    .getUserDao()
                    .insert(roomUser);
        }

        if (!repos.isEmpty()) {
            List<RoomRepository> roomRepositories = new ArrayList<>();
            for (Repository repository : repos) {
                RoomRepository roomRepository = new RoomRepository(repository.getId(), repository.getName(), user.getLogin());
                roomRepositories.add(roomRepository);
            }

            UserDatabase.getInstance()
                    .getRepositoryDao()
                    .insert(roomRepositories);
        }
        return repos;
    }

    @Override
    public Single<Object> getRepos(User user) {
        return Single.create(emitter -> {
            RoomUser roomUser = UserDatabase.getInstance()
                    .getUserDao()
                    .findByLogin(user.getLogin());

            if(roomUser == null){
                emitter.onError(new RuntimeException("No such user in cache"));
            } else {
                List<RoomRepository> roomRepositories = UserDatabase.getInstance().getRepositoryDao()
                        .getAll();

                List<Repository> repos = new ArrayList<>();
                for (RoomRepository roomRepository: roomRepositories){
                    repos.add(new Repository(roomRepository.getId(), roomRepository.getName()));
                }

                emitter.onSuccess(repos);
            }
        });
    }

    @Override
    public void saveImage(Bitmap resource, String url) {
        Completable.fromAction(() -> {
                    String sha1 = Utils.SHA1(url);
                    RoomImage roomImage = UserDatabase.getInstance().getImageDao()
                            .findBySha(sha1);

                    if (roomImage == null) {
                        roomImage = new RoomImage();
                        roomImage.setSha1(sha1);
                    }

                    String path = App.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            + File.separator + sha1 + ".PNG";
                    roomImage.setPath(path);

                    try (FileOutputStream outputStream = new FileOutputStream(path)){
                        resource.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    UserDatabase.getInstance().getImageDao()
                            .insert(roomImage);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public Single<Bitmap> getImage(String url) {
        return Single
                .create(emitter -> {
            String sha = Utils.SHA1(url);
            RoomImage roomImage = UserDatabase.getInstance().getImageDao()
                    .findBySha(sha);
            String path = roomImage.getPath();
            Bitmap bitmap = null;

            try (FileInputStream inputStream = new FileInputStream(path)) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            emitter.onSuccess(bitmap);
        });
    }
}
