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
    Context context;
    MyViewModel viewModel;
    TransactionEntity transaction;

    public AddActivityClickHandler(Context context, MyViewModel viewModel, TransactionEntity transaction) {
        this.context = context;
        this.viewModel = viewModel;
        this.transaction = transaction;
    }

    public void onFABclicked(View view){
        if(transaction.getAmount() == null || transaction.getAmount() == 0) {
            Snackbar.make(view, context.getString(R.string.valore_non_nullo), Snackbar.LENGTH_LONG).show();
            return;
        }
        if(transaction.getId() == 0) viewModel.addTransaction(transaction);
        else viewModel.updateTransaction(transaction);
        MainActivity.resetUpdatableAdapterPosition();   // Per evitare l'update manuale dell'elemento
        Intent itnShowMainAct = new Intent(context, MainActivity.class);
        itnShowMainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(itnShowMainAct);
    }
}
