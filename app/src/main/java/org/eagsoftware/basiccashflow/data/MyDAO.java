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

    @Query("SELECT * FROM transactions WHERE id_account = :accountId ORDER BY id_transaction DESC")
    LiveData<List<TransactionEntity>> getAllTransactions(int accountId);

    /** Query per la restituzione del saldo. La funzione coalesce restituisce il primo valore non nullo
     * dalla lista di valori passati come argomenti. In questo caso, restituisce il valore della somma dei
     * valori del campo"amount", oppure 0 se la tabella è vuota.*/
    @Query("SELECT COALESCE(SUM(CASE WHEN is_income THEN amount ELSE -amount END), 0) AS balance FROM " +
            "transactions WHERE id_account = :accountId")
    Cursor getBalance(int accountId);

    @Query("SELECT COALESCE(SUM(CASE WHEN is_income THEN amount ELSE -amount END), 0) AS balance FROM " +
            "transactions WHERE id_account = :accountId AND `desc` LIKE '%' || :query || '%'")
    Cursor getFilteredBalance(int accountId, String query);

    @Query("DELETE FROM transactions")
    void deleteAllTransactions();

    @Query("SELECT * FROM transactions " +
            "WHERE id_account = :accountId AND `desc` LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    LiveData<List<TransactionEntity>> searchTransactions(int accountId, String query);


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

    @Delete
    void delete(AccountEntity account);

    @Update
    void update(AccountEntity account);

    @Query("SELECT * FROM accounts ORDER BY id_account DESC")
    LiveData<List<AccountEntity>> getAllAccounts();
}
