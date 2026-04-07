package org.eagsoftware.basiccashflow.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_account")
    private int id;
    private String name;

    // Il costruttore vuoto è usato da Room
    public AccountEntity() {
    }

    public AccountEntity(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }
}
