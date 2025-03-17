package org.eagsoftware.basiccashflow.clickhandlers;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;

public class SettingsActivityClickHandler {
    Context context;
    MyViewModel viewModel;

    public SettingsActivityClickHandler(Context context, MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.context = context;
    }

    public void onDeleteAllClick(View view){
        MaterialAlertDialogBuilder bldDlg = new MaterialAlertDialogBuilder(context);
        bldDlg.setTitle(context.getString(R.string.conferma_eliminazione));
        bldDlg.setMessage(context.getString(R.string.conferma_eliminazione_body));
        bldDlg.setPositiveButton(context.getString(R.string.conferma), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.deleteAllTransactions(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, context.getString(R.string.eliminazione_completata),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }).setNegativeButton(context.getString(R.string.annulla), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(view, context.getString(R.string.eliminazione_annullata),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        bldDlg.create().show();
    }
}
