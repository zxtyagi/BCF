package org.eagsoftware.basiccashflow.data;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDAO {
    @Insert
    void insert(TransactionEntity transaction);

    @Delete
    void delete(TransactionEntity transaction);

    @Update
    void update(TransactionEntity transaction);

    @Query("SELECT * FROM transactions ORDER BY id_transaction DESC")
    LiveData<List<TransactionEntity>> getAllTransactions();

    /** Query per la restituzione del saldo. La funzione coalesce restituisce il primo valore non nullo
     * dalla lista di valori passati come argomenti. In questo caso, restituisce il valore della somma dei
     * valori del campo"amount", oppure 0 se la tabella è vuota.*/
    @Query("SELECT COALESCE(SUM(CASE WHEN is_income THEN amount ELSE -amount END), 0) AS balance FROM " +
            "transactions")
    Cursor getBalance();

    @Query("DELETE FROM transactions")
    void deleteAllTransactions();



    /* SETTINGS METHODS */
    @Insert
    void insert(SettingsEntity settings);

    @Update
    void update(SettingsEntity settings);

    @Query("SELECT * FROM settings WHERE id_settings = :userId")
    LiveData<SettingsEntity> getUserSettings(int userId);



    /* ACCOUNTS METHODS */
    @Insert
    void insert(AccountEntity account);

    @Query("SELECT * FROM accounts ORDER BY id_account DESC")
    LiveData<List<AccountEntity>> getAllAccounts();
}
