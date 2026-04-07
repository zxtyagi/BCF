package org.eagsoftware.basiccashflow.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.adapters.SpinnerCurrenciesAdapter;
import org.eagsoftware.basiccashflow.clickhandlers.SettingsActivityClickHandler;
import org.eagsoftware.basiccashflow.data.BackupDBManager;
import org.eagsoftware.basiccashflow.data.SettingsEntity;
import org.eagsoftware.basiccashflow.databinding.ActivitySettingsBinding;
import org.eagsoftware.basiccashflow.utilities.ViewModelUtil;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding mBndSet;
    SettingsActivityClickHandler hndSet;
    MyViewModel mViewModel;

    SettingsEntity mSettings;

    ActivityResultLauncher<String> mExportDBLauncher;
    ActivityResultLauncher<String[]> mImportDBLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NOTE: TOGLIERE LA SEGUENTE RIGA PER AVERE ESTENSIONE CORRETTA DEL LAYOUT
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getMainActivityIntent();

        setActivityResultLauncher();

        setMVVMcomponents();

        setCurrencySpinner();

        setOnBackPressed();

    }

    /** Registra tutti i launcher (sostituitivo del onActivityResult) */
    private void setActivityResultLauncher() {
        mExportDBLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.CreateDocument("*/*"),
                        new ActivityResultCallback<Uri>() {
                            @Override
                            public void onActivityResult(Uri uri) {
                                if (uri != null) {
                                    mViewModel.exportDB(uri, new BackupDBManager.Callback() {
                                        @Override
                                        public void onComplete(Exception error) {
                                            if (error == null) {
                                                runOnUiThread(() -> getRestartDialog(SettingsActivity.this,
                                                            getString(R.string.backup_esportato)).show());
                                            } else if (error.getCause() != null) {
                                                getCopyErrSnb(
                                                        mBndSet.getRoot(),
                                                        getString(R.string.impossibile_esportare),
                                                        error.getCause().getMessage()
                                                ).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                );


        mImportDBLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null) {
                    mViewModel.importDB(uri, new BackupDBManager.Callback() {
                        @Override
                        public void onComplete(Exception error) {
                            if (error == null)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getRestartDialog(SettingsActivity.this,
                                                getString(R.string.backup_importato)).show();
                                    }
                                });
                            else if (error.getCause() != null) getCopyErrSnb(mBndSet.getRoot(),
                                    getString(R.string.impossibile_importare), error.getCause().getMessage()).show();

                        }
                    });
                }
            }
        });
    }



    /**
     * Permette di ottenere l'oggetto <code>SettingsEntity</code> passato dalla MainActivity.</br>
     * Non viene recuperato dal viewModel per avere una gestione sincrona.
     */
    private void getMainActivityIntent(){
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null)
            mSettings = (SettingsEntity) bundle.getSerializable("settings");
    }

    private void setMVVMcomponents(){
        mBndSet = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        mBndSet.setLifecycleOwner(this);
        mViewModel = ViewModelUtil.getViewModel(getApplication());
        mBndSet.setSettings(mSettings);
        hndSet = new SettingsActivityClickHandler(SettingsActivity.this, mViewModel, mExportDBLauncher,
                mImportDBLauncher);
        mBndSet.setClickHandler(hndSet);
        mBndSet.setViewModel(mViewModel);
    }

    private void setCurrencySpinner(){
        SpinnerCurrenciesAdapter spnAdp = new SpinnerCurrenciesAdapter(this, R.layout.item_spn_curr);
        mBndSet.spnSetCurr.setAdapter(spnAdp);
    }

    private void setOnBackPressed(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Blocca il back quando in caricamento del DB da importare
                Boolean importInProg = mViewModel.getCopyingDBInProgress().getValue();
                if (importInProg != null && importInProg) {
                    Snackbar.make(mBndSet.getRoot(), R.string.solo_un_momento, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Se ci sono differenze, aggiorna l'mSettings
                if(!mSettings.equals(mViewModel.getSettings().getValue()))
                    mViewModel.setSettings(mSettings);
                finish();
            }
        });
    }


    public static Snackbar getCopyErrSnb(View view, String msg, String error) {
        return Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction(R.string.copia_errore, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clpMgr =
                                (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("Errore", error);
                        clpMgr.setPrimaryClip(clipData);

                        Snackbar.make(view, R.string.errore_copiato, Snackbar.LENGTH_LONG).show();
                    }
                });
    }


    public AlertDialog getRestartDialog(Context context, String message) {
        return new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.riavvio_necessario)
                .setMessage(message + "\n\n" + getString(R.string.riavvio_msg))
                .setCancelable(false)
                .setPositiveButton(R.string.riavvia, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartApp(context);
                    }
                }).create();
    }


    private void restartApp(Context context) {
        Intent intRst = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if(intRst == null) throw new IllegalStateException("Launch intent not found");
        intRst.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intRst);
        Runtime.getRuntime().exit(0);
    }


}