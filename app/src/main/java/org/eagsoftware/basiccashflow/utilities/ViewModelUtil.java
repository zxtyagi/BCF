package org.eagsoftware.basiccashflow.utilities;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;

import org.eagsoftware.basiccashflow.BasicCashFlowApp;
import org.eagsoftware.basiccashflow.MyViewModel;

public class ViewModelUtil {
    public static MyViewModel getViewModel(Application application) {
        return new ViewModelProvider(
                (BasicCashFlowApp) application,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(MyViewModel.class);
    }
}

