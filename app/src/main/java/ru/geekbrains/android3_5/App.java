package ru.geekbrains.android3_5;

import android.app.Application;

import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.geekbrains.android3_5.mvp.model.entity.room.db.UserDatabase;
import ru.geekbrains.android3_5.mvp.model.repo.MyRepository;
import ru.geekbrains.android3_5.mvp.model.repo.RoomUserRepo;
import timber.log.Timber;

public class App extends Application {
    static private App instance;
    static private MyRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Timber.plant(new Timber.DebugTree());
        UserDatabase.create(this);
        Paper.init(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
        repository = new MyRepository(new RoomUserRepo());
    }

    public static App getInstance() {
        return instance;
    }

    public static MyRepository getRepository(){
        return repository;
    }
}
