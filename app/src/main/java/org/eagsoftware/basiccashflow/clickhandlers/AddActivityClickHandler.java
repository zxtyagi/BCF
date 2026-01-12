package org.eagsoftware.basiccashflow.clickhandlers;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.activities.MainActivity;
import org.eagsoftware.basiccashflow.data.TransactionEntity;

public class AddActivityClickHandler {
    final Context context;
    final MyViewModel viewModel;
    final TransactionEntity transaction;

    public AddActivityClickHandler(Context context, MyViewModel viewModel, TransactionEntity transaction) {
        this.context = context;
        this.viewModel = viewModel;
        this.transaction = transaction;
    }

    public void onFABclicked(View view){
        if(transaction.getAmount() == null || transaction.getFloatAmount() == 0) {
            Snackbar.make(view, context.getString(R.string.valore_non_nullo), Snackbar.LENGTH_LONG).show();
            return;
        }
        // Se l'account id non è ancora impostato (caricamento asincrono). Quasi impossibile accada.
        if(transaction.getAccountId() < 1) {
            Snackbar.make(view, R.string.solo_un_momento, Snackbar.LENGTH_LONG).show();
            return;
        }
        // Se la transazione è nuova
        if(transaction.getId() == 0) {
            transaction.setTimestamp(System.currentTimeMillis());
            viewModel.addTransaction(transaction);
        }
        // Se la transazione esiste già
        else viewModel.updateTransaction(transaction);
        MainActivity.resetUpdatableAdapterPosition();   // Per evitare l'update manuale dell'elemento
        Intent itnShowMainAct = new Intent(context, MainActivity.class);
        itnShowMainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(itnShowMainAct);
    }
}
