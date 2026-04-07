package org.eagsoftware.basiccashflow.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "settings")
public class SettingsEntity extends BaseObservable implements Serializable {
    @PrimaryKey
    @ColumnInfo(name="id_settings")
    int id;
    String currencyCode;
    @ColumnInfo(name="force_symbol")
    boolean forceSymbolAfter;

    @ColumnInfo(name="multi_account")
    boolean multiAccount;

    public SettingsEntity() {
    }

    public SettingsEntity(int id, String currencyCode, boolean forceSymbolAfter, boolean multiAccount) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.forceSymbolAfter = forceSymbolAfter;
        this.multiAccount = multiAccount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        notifyPropertyChanged(BR.currencyCode);
    }

    @Bindable
    public boolean isForceSymbolAfter() {
        return forceSymbolAfter;
    }

    public void setForceSymbolAfter(boolean forceSymbolAfter){
        this.forceSymbolAfter = forceSymbolAfter;
        notifyPropertyChanged(BR.forceSymbolAfter);
    }

    @Bindable
    public boolean isMultiAccount() {
        return multiAccount;
    }

    public void setMultiAccount(boolean multiAccount) {
        this.multiAccount = multiAccount;
    }
}
