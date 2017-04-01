package com.myntra.gopi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.myntra.gopi.application.MemoryGameApplication;
import com.myntra.gopi.domains.GameResultItem;
import com.myntra.gopi.utils.SqlliteTables.GamesListingTable;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.myntra.gopi.utils.CommonUtils.getCurrentDateTimeForDb;

/**
 * Created by gopikrishna on 01/12/16.
 */

public class DatabaseManager {

    public static Observable<Long> insertGameResult(GameResultItem notes) {
        return Observable.create(subscriber -> {
            long result;
            try {
                String currentDateTimeForDb = getCurrentDateTimeForDb();
                ContentValues contentValues = new ContentValues();
                contentValues.put(GamesListingTable.MOVES, notes.getMoves());
                contentValues.put(GamesListingTable.CREATED_ON, currentDateTimeForDb);
                result = MemoryGameApplication.getInstance().getWritableDB().insert(GamesListingTable.TABLE_NAME, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("exception in insertion", e.getLocalizedMessage());
                result = -1;
            }
            subscriber.onNext(result);
        });
    }

    public static Observable<List<GameResultItem>> getAllGameResults() {
        return Observable.create(subscriber -> {
            List<GameResultItem> GameResultItems = new ArrayList<>();
            Cursor cursor = MemoryGameApplication.getInstance().getReadableDb()
                    .rawQuery("SELECT * FROM " + GamesListingTable.TABLE_NAME + " ORDER BY " + GamesListingTable.CREATED_ON + " DESC ", new String[]{});
            try {
                while (cursor.moveToNext()) {
                    GameResultItem GameResultItem = new GameResultItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                    GameResultItems.add(GameResultItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            subscriber.onNext(GameResultItems);
        });
    }
}
