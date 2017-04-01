package com.myntra.gopi.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.myntra.gopi.utils.GameDbHelper;

/**
 * Created by gopikrishna on 30/11/16.
 */

public class MemoryGameApplication extends Application {

    private static MemoryGameApplication instance;
    private GameDbHelper notesDbHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initializeDb();
    }

    public static MemoryGameApplication getInstance() {
        return instance;
    }

    private void initializeDb() {
        notesDbHelper = new GameDbHelper(this);
    }

    public SQLiteDatabase getReadableDb() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = notesDbHelper.getReadableDatabase();
        }
        return sqLiteDatabase;
    }

    public SQLiteDatabase getWritableDB() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = notesDbHelper.getWritableDatabase();
        } else if (!sqLiteDatabase.isOpen()) {
            sqLiteDatabase = notesDbHelper.getWritableDatabase();
        }
        return sqLiteDatabase;
    }
}
