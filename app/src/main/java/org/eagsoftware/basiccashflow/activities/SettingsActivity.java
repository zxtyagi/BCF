package org.eagsoftware.basiccashflow.activities;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.adapters.SpinnerCurrenciesAdapter;
import org.eagsoftware.basiccashflow.clickhandlers.SettingsActivityClickHandler;
import org.eagsoftware.basiccashflow.data.SettingsEntity;
import org.eagsoftware.basiccashflow.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding mBndSet;
    SettingsActivityClickHandler hndSet;
    MyViewModel mViewModel;

    SettingsEntity mSettings;

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

        setMVVMcomponents();

        setCurrencySpinner();

        setOnBackPressed();

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
        mViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        mBndSet.setSettings(mSettings);
        hndSet =new SettingsActivityClickHandler(SettingsActivity.this, mViewModel);
        mBndSet.setClickHandler(hndSet);
    }

    private void setCurrencySpinner(){
        SpinnerCurrenciesAdapter spnAdp = new SpinnerCurrenciesAdapter(this, R.layout.item_spn_curr);
        mBndSet.spnSetCurr.setAdapter(spnAdp);
    }

    private void setOnBackPressed(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Se ci sono differenze, aggiorna l'mSettings
                if(!mSettings.equals(mViewModel.getSettings().getValue()))
                    mViewModel.setSettings(mSettings);
                finish();
            }
        });
    }

}