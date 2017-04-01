package com.myntra.gopi.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gopikrishna on 01/12/16.
 */

public class GameDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "game.db";
    private static final int DB_VERSION = 1;

    public GameDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SqlliteTables.GamesListingTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
