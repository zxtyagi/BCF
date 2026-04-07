package org.eagsoftware.basiccashflow;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.eagsoftware.basiccashflow.data.AccountEntity;
import org.eagsoftware.basiccashflow.data.BackupDBManager;
import org.eagsoftware.basiccashflow.data.MyRepository;
import org.eagsoftware.basiccashflow.data.SettingsEntity;
import org.eagsoftware.basiccashflow.data.TransactionEntity;
import org.eagsoftware.basiccashflow.interfaces.ResultCallback;
import org.eagsoftware.basiccashflow.utilities.Constants;

import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;

public class MyViewModel extends AndroidViewModel {
    final MyRepository repository;
    MediatorLiveData<List<TransactionEntity>> transactionsList;
    final MutableLiveData<TransactionEntity> activeTransaction = new MutableLiveData<>();
    LiveData<List<TransactionEntity>> currTransSource;
    MutableLiveData<String> balance;
    final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    LiveData<SettingsEntity> settings;
    final MutableLiveData<Currency> currency = new MutableLiveData<>();

    LiveData<List<AccountEntity>> accountsList;
    final MutableLiveData<AccountEntity> activeAccount = new MutableLiveData<>();


    final MutableLiveData<Boolean> copyingDBInProgress = new MutableLiveData<Boolean>(false);


    public MyViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MyRepository(application);
    }

    public void addTransaction(TransactionEntity transaction){
        if (transaction.getAccountId() < 1 ) return;
        repository.addTransaction(transaction);
    }

    public void deleteTransaction(TransactionEntity transaction){
        repository.deleteTransaction(transaction);
    }

    public void updateTransaction(TransactionEntity transaction){
        repository.updateTransaction(transaction);
    }

    public LiveData<List<TransactionEntity>> getTransactionsList(){
        if (transactionsList == null) {
            transactionsList = new MediatorLiveData<>();
            transactionsList.addSource(searchQuery, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    reloadTransactions();
                }
            });
            transactionsList.addSource(activeAccount, new Observer<AccountEntity>() {
                @Override
                public void onChanged(AccountEntity accountEntity) {
                    reloadTransactions();
                }
            });
        }

        return transactionsList;
    }

    public void setActiveTransaction(@Nullable TransactionEntity activeTransaction) {
        this.activeTransaction.setValue(activeTransaction);
    }

    public LiveData<TransactionEntity> getActiveTransaction() {
        return activeTransaction;
    }

    public LiveData<String> getBalance(){
        if (balance == null) {
            balance = new MutableLiveData<>();
            updateBalance();
        }
        return balance;
    }


    public void updateBalance(){
        AccountEntity account = activeAccount.getValue();
        if (account == null) { balance.postValue("0"); return; }

        String query = searchQuery.getValue();

        if (query == null || query.isEmpty()) {
            repository.getBalance(account.getId()).thenAccept(new Consumer<String>() {
                @Override
                public void accept(String sValue) {
                    balance.postValue(sValue);
                }
            });
        } else {
            repository.getFiltererdBalance(account.getId(), query).thenAccept(new Consumer<String>() {
                @Override
                public void accept(String sValue) {
                    balance.postValue(sValue);
                }
            });
        }
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


    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }


    private void reloadTransactions() {
        String query = searchQuery.getValue();
        AccountEntity account = activeAccount.getValue();

        if (account == null) { transactionsList.setValue(Collections.emptyList()); return; }

        if (currTransSource != null) transactionsList.removeSource(currTransSource);

        if (query == null || query.isEmpty()) currTransSource = repository.getAllTransactions(account.getId());
        else currTransSource = repository.searchTransactions(account.getId(), query);

        transactionsList.addSource(currTransSource, new Observer<List<TransactionEntity>>() {
            @Override
            public void onChanged(List<TransactionEntity> transList) {
                transactionsList.setValue(transList);
            }
        });
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

    public void exportDB(Uri destUri, BackupDBManager.Callback callback) {
        copyingDBInProgress.setValue(true);

        repository.exportDB(destUri, new BackupDBManager.Callback() {
            @Override
            public void onComplete(Exception error) {
                // Aggiorna il livedata
                copyingDBInProgress.postValue(false);
                //innesca il callback passato come parametro
                if (callback != null) callback.onComplete(error);
            }
        });
    }

    public void importDB(Uri sourceUri, BackupDBManager.Callback callback) {
        copyingDBInProgress.setValue(true);

        repository.importDB(sourceUri, new BackupDBManager.Callback() {
            @Override
            public void onComplete(Exception error) {
                // aggiorna il livedata
                copyingDBInProgress.postValue(false);
                // innesca il callback passato come parametro
                if (callback != null) callback.onComplete(error);
            }
        });
    }

    public LiveData<Boolean> getCopyingDBInProgress(){
        return copyingDBInProgress;
    }


    /* ACCOUNTS METHODS */


    public void newAccount(AccountEntity account) {
        repository.insertAccount(account);
    }

    public LiveData<List<AccountEntity>>getAccountsList() {
        if (accountsList == null) accountsList = repository.getAllAccounts();
        return accountsList;
    }

    public LiveData<AccountEntity> getActiveAccount(){
        if (activeAccount.getValue() == null)
            getAccountsList().observeForever(new Observer<List<AccountEntity>>() {
                @Override
                public void onChanged(List<AccountEntity> accounts) {
                    if (accounts != null && !accounts.isEmpty())
                        activeAccount.setValue(accounts.get(0));
                }
            });
        return activeAccount;
    }

    public void setActiveAccount(AccountEntity account){
        activeAccount.setValue(account);
    }

    public void deleteAccount(AccountEntity account, ResultCallback resClb){
        repository.deleteAccount(account, resClb);
    }

    public void updateAccountName(AccountEntity account, String newName) {
        if (newName == null || newName.isEmpty()) return;
        String cleanedName = newName.trim()
                .replaceAll("\\s+", " ")        // Rimpiazza spazi multipli
                .replaceAll("\\p{Cntrl}", "");  // Rimuove caratteri di controllo
        if (cleanedName.isEmpty()) return;
        repository.updateAccountName(account, cleanedName);
    }

}
