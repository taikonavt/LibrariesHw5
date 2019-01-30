package ru.geekbrains.android3_5;

import android.app.Application;

import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.geekbrains.android3_5.mvp.model.entity.room.db.UserDatabase;
import timber.log.Timber;

public class App extends Application {
    static private App instance;

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
    }

    public static App getInstance() {
        return instance;
    }
}
