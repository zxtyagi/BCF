package org.eagsoftware.basiccashflow.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_account")
    public int id;
    public String name;

    // Il costruttore vuoto è usato da Room
    public AccountEntity() {
    }

    public AccountEntity(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
}
