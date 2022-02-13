package com.leon.counter_reading.di.view_model;

import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.content.Context;
import android.database.Cursor;

import androidx.room.Room;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.MyDatabase;

import javax.inject.Inject;

public class MyDatabaseClientModel {

    private static MyDatabaseClientModel instance;
    private final MyDatabase myDatabase;

    @Inject
    public MyDatabaseClientModel(Context context) {
        myDatabase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBName())
                .allowMainThreadQueries().build();
    }

    public static synchronized MyDatabaseClientModel getInstance(Context context) {
        if (instance == null) {
            instance = new MyDatabaseClientModel(context);
        }
        return instance;
    }

    public static void migration(Context context) {
        Room.databaseBuilder(context, MyDatabase.class,
                MyApplication.getDBName()).
                fallbackToDestructiveMigration().
                addMigrations(MyDatabase.MIGRATION_6_7).
                allowMainThreadQueries().
                build();
    }

    public static boolean customTransaction(String... queries) {
        String query = "BEGIN TRANSACTION;\n";
        for (String s : queries) {
            query = query.concat(s).concat("\n");
        }
        query = query.concat("COMMIT;");

        Cursor cursor = getApplicationComponent().MyDatabase().getOpenHelper().getWritableDatabase().query(query);
        cursor.moveToFirst();

        return true;
    }

    public MyDatabase getMyDatabase() {
        return myDatabase;
    }
}