package org.eagsoftware.basiccashflow.utilities;

import androidx.recyclerview.widget.DiffUtil;

import org.eagsoftware.basiccashflow.data.TransactionEntity;

import java.util.Collections;
import java.util.List;

/**
 * Questa classe estende la classe {@link DiffUtil.Callback} per efficientare l'aggiornamento della
 * recyclerView al mutare dei dati.
 */
public class MyDiffUtil extends DiffUtil.Callback {
    private List<TransactionEntity> oldList;
    private List<TransactionEntity> newList;

    public MyDiffUtil(List<TransactionEntity> oldList, List<TransactionEntity> newList) {
        this.oldList = (oldList != null) ? oldList : Collections.emptyList();
        this.newList = (newList != null) ? newList : Collections.emptyList();
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        TransactionEntity oldItemList = oldList.get(oldItemPosition);
        TransactionEntity newItemList = newList.get(newItemPosition);
        if (oldItemList.getId() != newItemList.getId()) return false;
        else if (!oldItemList.getAmount().equals(newItemList.getAmount())) return false;
        // Il seguente codice serve per confrontare le stringhe anche nel caso di null.
        else if (oldItemList.getDesc() == null ? newItemList.getDesc() == null :
                oldItemList.getDesc().equals(newItemList.getDesc())) return false;
        return true;
    }
}
