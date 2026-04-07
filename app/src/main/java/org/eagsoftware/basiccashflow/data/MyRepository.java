package org.eagsoftware.basiccashflow.data;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import org.eagsoftware.basiccashflow.interfaces.ResultCallback;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MyRepository {
    private final MyDAO myDAO;
    private final BackupDBManager bkpMgr;

    final ExecutorService execService = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());


    public MyRepository(Application application){
        MyDB db = MyDB.getInstance(application);
        myDAO = db.getTransactionDAO();
        bkpMgr = new BackupDBManager(application.getApplicationContext(), db,
                db.getOpenHelper().getDatabaseName());
    }

    public void addTransaction(TransactionEntity transaction){
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.insert(transaction);
            }
        });
    }

    public void deleteTransaction(TransactionEntity transaction){
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.delete(transaction);
            }
        });
    }

    public void updateTransaction(TransactionEntity transaction){
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.update(transaction);
            }
        });
    }

    public LiveData<List<TransactionEntity>> getAllTransactions(int accountId){
        // Non serve il thread separato perché il LiveData ha il suo thread.
        return myDAO.getAllTransactions(accountId);
    }

    public CompletableFuture<String> getBalance(int accountId){
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                Cursor cursor = myDAO.getBalance(accountId);
                String balance = "0";
                if(cursor != null && cursor.moveToFirst()){
                    int columnIndex = cursor.getColumnIndex("balance"); // -1 se non trovato
                    if (columnIndex >= 0) balance = cursor.getString(columnIndex);
                    cursor.close();
                }

                return balance;
            }
        }, execService);
    }


    public CompletableFuture<String> getFiltererdBalance(int accountId, String query) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                Cursor cursor = myDAO.getFilteredBalance(accountId, query);
                String balance = "0";

                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("balance");
                    if (columnIndex >= 0) balance = cursor.getString(columnIndex);
                    cursor.close();
                }

                return balance;
            }
        }, execService);
    }


    public void deleteAllTransactions(Runnable onDeleteCompleted){
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.deleteAllTransactions();
                handler.post(onDeleteCompleted);
            }
        });
    }


    public LiveData<List<TransactionEntity>> searchTransactions(int accountId, String query) {
        return myDAO.searchTransactions(accountId, query);
    }



    /* SETTINGS METHODS */

    public void insertSettigs(SettingsEntity settings){
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.insert(settings);
            }
        });
    }

    public void updateSettings(SettingsEntity userSettings){
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.update(userSettings);
            }
        });
    }

    public LiveData<SettingsEntity> getUserSettings(int userId){
        // Non serve il thread separato perché il LiveData ha il suo thread.
        return myDAO.getUserSettings(userId);
    }

    public void exportDB(Uri destUri, BackupDBManager.Callback callback) {
        bkpMgr.asyncExportDB(destUri, callback);
    }

    public void importDB(Uri sourceUri, BackupDBManager.Callback callback) {
        bkpMgr.asyncImportDB(sourceUri, callback);
    }



    /* ACCOUNTS METHODS */
    public void insertAccount(AccountEntity account) {
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.insert(account);
            }
        });
    }

    public LiveData<List<AccountEntity>> getAllAccounts(){
        // Non serve il thread separato perché il LiveData ha il suo thread.
        return myDAO.getAllAccounts();
    }

    public void deleteAccount(AccountEntity account, ResultCallback callback) {
        execService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    myDAO.delete(account);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
                        }
                    });
                } catch (Exception exc) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(exc);
                        }
                    });
                }
            }
        });
    }

    public void updateAccountName(AccountEntity account, String newName) {
        account.setName(newName);
        execService.execute(new Runnable() {
            @Override
            public void run() {
                myDAO.update(account);
            }
        });
    }

}
