package org.eagsoftware.basiccashflow.clickhandlers;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivityClickHandler {
    final Context context;
    final MyViewModel viewModel;
    final ActivityResultLauncher<String> exportDBLauncher;
    final ActivityResultLauncher<String[]> importDBLauncher;

    public SettingsActivityClickHandler(Context context, MyViewModel viewModel,
                                        ActivityResultLauncher<String> exportDBLauncher,
                                        ActivityResultLauncher<String[]> importDBLauncher) {
        this.viewModel = viewModel;
        this.context = context;
        this.exportDBLauncher = exportDBLauncher;
        this.importDBLauncher = importDBLauncher;

    }

    public void onDeleteAllClick(View view){
        MaterialAlertDialogBuilder bldDlg = new MaterialAlertDialogBuilder(context);
        bldDlg.setTitle(context.getString(R.string.conferma_eliminazione));
        bldDlg.setMessage(context.getString(R.string.conferma_elim_trans_body));
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


    @SuppressWarnings("unused")
    public void onExportDBClick(View view){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        exportDBLauncher.launch("basicCashFlow_db_" + timestamp + ".bkp");
    }

    @SuppressWarnings("unused")
    public void onImportDBClick(View view) {
        importDBLauncher.launch(new String[]{"*/*"});
    }

}
