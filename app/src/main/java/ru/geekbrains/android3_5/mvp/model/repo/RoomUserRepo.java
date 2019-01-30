package ru.geekbrains.android3_5.mvp.model.repo;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.mvp.model.api.ApiHolder;
import ru.geekbrains.android3_5.mvp.model.entity.Repository;
import ru.geekbrains.android3_5.mvp.model.entity.User;
import ru.geekbrains.android3_5.mvp.model.entity.room.RoomRepository;
import ru.geekbrains.android3_5.mvp.model.entity.room.RoomUser;
import ru.geekbrains.android3_5.mvp.model.entity.room.db.UserDatabase;
import ru.geekbrains.android3_5.ui.NetworkStatus;

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
}
