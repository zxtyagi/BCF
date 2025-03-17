package org.eagsoftware.basiccashflow.clickhandlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.activities.AddActivity;
import org.eagsoftware.basiccashflow.activities.SettingsActivity;

/** @noinspection unused*/
public class MainActivityClickHandler {
    Context context;
    MyViewModel viewModel;

    public MainActivityClickHandler(Context context, MyViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    public void onFABaddClicked(View view) {
        Intent itnShowAddAct = new Intent(context, AddActivity.class);
        context.startActivity(itnShowAddAct);
    }

    public void onFABsettingsClicked(View view) {
        Intent itnShowSetAct = new Intent(context, SettingsActivity.class);
        Bundle bundleSet = new Bundle();
        bundleSet.putSerializable("settings", viewModel.getSettings().getValue());
        itnShowSetAct.putExtra("bundle", bundleSet);
        context.startActivity(itnShowSetAct);
    }
}
