package org.eagsoftware.basiccashflow.clickhandlers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.activities.AddActivity;
import org.eagsoftware.basiccashflow.activities.SettingsActivity;
import org.eagsoftware.basiccashflow.data.TransactionEntity;
import org.eagsoftware.basiccashflow.fragments.AccountsBottomSheet;
import org.eagsoftware.basiccashflow.fragments.SearchBottomSheet;

/** @noinspection unused*/
public class MainActivityClickHandler {
    final FragmentActivity activity;
    final MyViewModel viewModel;

    public MainActivityClickHandler(FragmentActivity activity, MyViewModel viewModel) {
        this.activity = activity;
        this.viewModel = viewModel;
    }

    public void onFABaddClicked(View view) {
        // Imposta una transaction vuota che verrà riempita nell'AddActivity
        viewModel.setActiveTransaction(new TransactionEntity());
        Intent itnShowAddAct = new Intent(activity, AddActivity.class);
        activity.startActivity(itnShowAddAct);
    }

    public void onFABsettingsClicked(View view) {
        Intent itnShowSetAct = new Intent(activity, SettingsActivity.class);
        Bundle bundleSet = new Bundle();
        bundleSet.putSerializable("settings", viewModel.getSettings().getValue());
        itnShowSetAct.putExtra("bundle", bundleSet);
        activity.startActivity(itnShowSetAct);
    }

    public void onFABsearchClicked(View view) {
        String query = viewModel.getSearchQuery().getValue();
        if (query == null) return;
        if (query.isEmpty()) {
            SearchBottomSheet srcBtmSht = new SearchBottomSheet();
            srcBtmSht.show(activity.getSupportFragmentManager(), "SearchSheet");
        } else
            viewModel.setSearchQuery("");
    }

    public void onBtnAccountClicked(View view) {
        AccountsBottomSheet accBtmSht = new AccountsBottomSheet();
        accBtmSht.show(activity.getSupportFragmentManager(), "AccountsSheets");
    }


}
