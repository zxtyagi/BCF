package org.eagsoftware.basiccashflow.data;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * La classe che definisce la tabella transaction. L'estensione della classe <code>BaseObservable</code> è
 * necessaria
 * per il dataBinding dell'addActivity. L'implementazione dell'interfaccia <code>Serializable</code> è
 * necessaria per passare un oggetto da un'activity ad un'altra tramite bundle.
 */

@Entity(
    tableName = "transactions",
    foreignKeys = @ForeignKey(
        entity = AccountEntity.class,
        parentColumns = "id_account",
        childColumns = "id_account",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("id_account")}
)
public class TransactionEntity extends BaseObservable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id_transaction")
    int id;
    String amount;
    String desc;
    @ColumnInfo(name="is_income")
    boolean isIncome = false;
    long timestamp;
    @ColumnInfo(name="id_account")
    int accountId;


    public TransactionEntity() {
        this.accountId = 0; // Se 0 allora non è stato impostato (la numerazione ID di MySQL inizia da 1)
    }

    public TransactionEntity(Float amount, String desc) {
        this.amount = Float.toString(amount);
        this.desc = desc;
        this.timestamp = System.currentTimeMillis();
        this.accountId = 0;
    }

    public TransactionEntity(Float amount, String desc, long timestamp) {
        this.amount = Float.toString(amount);
        this.desc = desc;
        this.timestamp = timestamp;
        this.accountId = 0;
    }

    public TransactionEntity(Float amount, String desc, long timestamp, int accountId) {
        this.amount = Float.toString(amount);
        this.desc = desc;
        this.timestamp = timestamp;
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public Float getFloatAmount() {
        return (amount == null) ? null : Float.parseFloat(amount);
    }

    public void setAmount(String amount) {
        // verifica se la stringa è numero (intero o decimale) tramite regex
        if (amount != null && amount.matches("\\d+(\\.\\d+)?")) this.amount = amount;
        else this.amount = null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isIsIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean income) {
        isIncome = income;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAccountId(){
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

}
