package org.eagsoftware.basiccashflow.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TransactionEntity.class, SettingsEntity.class}, version = 1)
public abstract class MyDB extends RoomDatabase {
    private static MyDB db;

    public abstract MyDAO getTransactionDAO();

    public static synchronized MyDB getInstance(Context context) {
        if (db == null) {
        db = Room.databaseBuilder
                        (context.getApplicationContext(), MyDB.class, "cash_flow_manager_db")
                .fallbackToDestructiveMigration()
                .build();
        }
        return db;
    }
}
