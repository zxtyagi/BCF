package org.eagsoftware.basiccashflow.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {TransactionEntity.class, SettingsEntity.class, AccountEntity.class}, version = 2)
public abstract class MyDB extends RoomDatabase {
    private static MyDB db;

    public abstract MyDAO getTransactionDAO();

    public static synchronized MyDB getInstance(Context context) {
        if (db == null) {
        db = Room.databaseBuilder
                        (context.getApplicationContext(), MyDB.class, "cash_flow_manager_db")
                .addMigrations(MIGRATION_1_2)
                .build();
        }
        return db;
    }

    public static synchronized void closeInstance() {
        if(db != null) {
            db.close();
            db = null;
        }
    }

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE settings ADD COLUMN multi_account INTEGER NOT NULL DEFAULT 0");
        }
    };

}
