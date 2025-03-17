package org.eagsoftware.basiccashflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.data.TransactionEntity;
import org.eagsoftware.basiccashflow.databinding.ItemRcyTransBinding;
import org.eagsoftware.basiccashflow.utilities.MyDiffUtil;

import java.util.ArrayList;
import java.util.List;

public class RecyclerTransactionsAdapter extends RecyclerView.Adapter<RecyclerTransactionsAdapter.MyViewHolder> {

    private ArrayList<TransactionEntity> transactionsList;
    private final Context context;
    private final int COLOR_TXT_INCOME;
    private final int COLOR_TXT_OUTCOME;

    private final MyViewModel viewModel;



    public RecyclerTransactionsAdapter(List<TransactionEntity> transactionsList, Context context,
                                       MyViewModel viewModel) {
        this.transactionsList = (ArrayList<TransactionEntity>) transactionsList;
        this.context = context;
        this.viewModel = viewModel;

        COLOR_TXT_INCOME = ContextCompat.getColor(context, R.color.green);
        COLOR_TXT_OUTCOME = ContextCompat.getColor(context, R.color.light_red);
    }

    @NonNull
    @Override
    public RecyclerTransactionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRcyTransBinding bndItem = DataBindingUtil.inflate(
          LayoutInflater.from(parent.getContext()),
          R.layout.item_rcy_trans,
          parent,
          false
        );
        return new MyViewHolder(bndItem, viewModel, context);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TransactionEntity currTransaction = transactionsList.get(position);
        holder.bndItem.setTransaction(currTransaction);
        if(currTransaction.isIsIncome()) {
            holder.bndItem.txwRcyTransAmount.setTextColor(COLOR_TXT_INCOME);
            holder.bndItem.txwRcyTransSign.setTextColor(COLOR_TXT_INCOME);
            holder.bndItem.txwRcyTransCurr.setTextColor(COLOR_TXT_INCOME);
        } else {
            holder.bndItem.txwRcyTransAmount.setTextColor(COLOR_TXT_OUTCOME);
            holder.bndItem.txwRcyTransSign.setTextColor(COLOR_TXT_OUTCOME);
            holder.bndItem.txwRcyTransCurr.setTextColor(COLOR_TXT_OUTCOME);
        }
        // Aggiunge uno spazio dopo l'ultimo elemento (per evitare sovrapposizioni coi FAB) altrimenti resetta
        RecyclerView.LayoutParams lytParams = (RecyclerView.LayoutParams) holder.bndItem.getRoot().getLayoutParams();
        lytParams.bottomMargin = (position == getItemCount() - 1) ?
                (int) context.getResources().getDimension(R.dimen.rcy_bottom_void_space) : 0;
    }


    @Override
    public int getItemCount() {
        return (transactionsList != null) ? transactionsList.size() : 0;
    }

    public void updateTransactions(List<TransactionEntity> newTransactionsList){
        // Calcola la differenza tra le due liste
        MyDiffUtil diffUtil = new MyDiffUtil(transactionsList, newTransactionsList);
        DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(this);

        this.transactionsList = (ArrayList<TransactionEntity>) newTransactionsList;
    }

    public TransactionEntity getCurrTransaction(RecyclerView.ViewHolder viewHolder){
        return transactionsList.get(viewHolder.getAdapterPosition());
    }




    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final ItemRcyTransBinding bndItem;

        public MyViewHolder(@NonNull ItemRcyTransBinding bndItem, MyViewModel viewModel, Context context) {
            super(bndItem.getRoot());
            this.bndItem = bndItem;

            bndItem.setViewModel(viewModel);
            bndItem.setLifecycleOwner((LifecycleOwner) context);
        }
    }
}
