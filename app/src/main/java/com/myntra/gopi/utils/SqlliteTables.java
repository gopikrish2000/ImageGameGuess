package com.myntra.gopi.utils;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by gopikrishna on 01/12/16.
 */

public class SqlliteTables {

    public static class GamesListingTable {

        public static final String TABLE_NAME = "notes_listing";
        public static final String ID = "id";
        public static final String MOVES = "moves";
        public static final String CREATED_ON = "created_on";


        public static void createTable(SQLiteDatabase database) {
            String CREATE_DIRECTORY_TABLE =
                    "CREATE TABLE " +
                            TABLE_NAME + "(" +
                            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                            MOVES + " TEXT," +
                            CREATED_ON + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")";
            database.execSQL(CREATE_DIRECTORY_TABLE);
        }

        public static void dropTable(SQLiteDatabase database) {
            String DROP_DIRECTORY_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
            database.execSQL(DROP_DIRECTORY_TABLE);
        }
    }
}
