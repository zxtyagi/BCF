package org.eagsoftware.basiccashflow.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * La classe che definisce la tabella transaction. L'estensione della classe <code>BaseObservable</code> è
 * necessaria
 * per il dataBinding dell'addActivity. L'implementazione dell'interfaccia <code>Serializable</code> è
 * necessaria per passare un oggetto da un'activity ad un'altra tramite bundle.
 */

@Entity(tableName = "transactions")
public class TransactionEntity extends BaseObservable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id_transaction")
    int id;
    Float amount;
    String desc;
    @ColumnInfo(name="is_income")
    boolean isIncome = false;

    public TransactionEntity() {
    }

    public TransactionEntity(Float amount, String desc) {
        this.amount = amount;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amountString);     // l'UI sta usando amountString
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

    /* ACTIVITY_ADD DATABINDING METHODS */

    @Bindable
    public String getAmountString() {
        return (amount != null) ? Float.toString(amount) : "";
    }

    public void setAmountString(String amountString){
        try{
            amount = Float.parseFloat(amountString);
        }catch(NumberFormatException exc){
            amount = null;
        }
    }




}
