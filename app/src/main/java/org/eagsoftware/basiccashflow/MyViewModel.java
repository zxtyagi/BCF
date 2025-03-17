package org.eagsoftware.basiccashflow;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.eagsoftware.basiccashflow.data.MyRepository;
import org.eagsoftware.basiccashflow.data.SettingsEntity;
import org.eagsoftware.basiccashflow.data.TransactionEntity;
import org.eagsoftware.basiccashflow.utilities.Constants;

import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;

public class MyViewModel extends AndroidViewModel {
    MyRepository repository;
    LiveData<List<TransactionEntity>> transactionsList;
    MutableLiveData<Float> balance;

    LiveData<SettingsEntity> settings;
    MutableLiveData<Currency> currency = new MutableLiveData<>();


    public MyViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MyRepository(application);
    }

    public void addTransaction(TransactionEntity transaction){
        repository.addTransaction(transaction);
    }

    public void deleteTransaction(TransactionEntity transaction){
        repository.deleteTransaction(transaction);
    }

    public void updateTransaction(TransactionEntity transaction){
        repository.updateTransaction(transaction);
    }

    public LiveData<List<TransactionEntity>> getTransactionsList(){
        if (transactionsList == null) transactionsList = repository.getAllTransactions();
        return transactionsList;
    }

    public LiveData<Float> getBalance(){
        if (balance == null) {
            balance = new MutableLiveData<>();
            updateBalance();
        }
        return balance;
    }

    public void updateBalance(){
        repository.getBalance().thenAccept(new Consumer<Float>() {
            @Override
            public void accept(Float aFloat) {
                balance.postValue(aFloat);
            }
        });
    }

    public void deleteAllTransactions(Runnable onDeleteCompleted){
        repository.deleteAllTransactions(onDeleteCompleted);
    }



    public LiveData<Currency> getCurrency() {
        return currency;
    }

    public void setCurrency(Currency newCurrency){
        currency.postValue(newCurrency);
    }


    /* SETTINGS METHODS */

    public void newSettings(SettingsEntity userSettings) {
        repository.insertSettigs(userSettings);
    }

    public LiveData<SettingsEntity> getSettings(){
        if(settings == null) settings = repository.getUserSettings(Constants.USER_ID);
        return settings;
    }

    public void setSettings(SettingsEntity userSettings){
        repository.updateSettings(userSettings);
    }

}
