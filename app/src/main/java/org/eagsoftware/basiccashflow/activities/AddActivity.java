package org.eagsoftware.basiccashflow.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.clickhandlers.AddActivityClickHandler;
import org.eagsoftware.basiccashflow.data.AccountEntity;
import org.eagsoftware.basiccashflow.data.SettingsEntity;
import org.eagsoftware.basiccashflow.data.TransactionEntity;
import org.eagsoftware.basiccashflow.databinding.ActivityAddBinding;

import java.math.BigDecimal;
import java.util.Currency;

public class AddActivity extends AppCompatActivity {
    private ActivityAddBinding bndAdd;
    /** @noinspection FieldCanBeLocal*/
    private AddActivityClickHandler clhAdd;
    private MyViewModel viewModel;

    private TransactionEntity transaction = new TransactionEntity();

    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NOTE: TOGLIERE LA SEGUENTE RIGA PER AVERE ESTENSIONE CORRETTA DEL LAYOUT
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getMainActivityIntent();

        setMVVMcomponents();

        checkFractionDigits();

        setObserver();

        bndAdd.edtAddCash.requestFocus();
    }

    private void getMainActivityIntent() {
        // Get transaction for editing if available
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null)
            transaction = (TransactionEntity) bundle.getSerializable("transaction");
    }

    private void setMVVMcomponents(){
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        bndAdd = DataBindingUtil.setContentView(this, R.layout.activity_add);
        bndAdd.setTransaction(transaction);
        clhAdd = new AddActivityClickHandler(this, viewModel, transaction);
        bndAdd.setClickHandler(clhAdd);
    }

    private void checkFractionDigits(){
        viewModel.getSettings().observe(this, new Observer<SettingsEntity>() {
            @Override
            public void onChanged(SettingsEntity settings) {
                if(settings != null) {
                    // Rimuove il listener se già impostato (ossia se il textWatcher è già esistente)
                    if (textWatcher != null) bndAdd.edtAddCash.removeTextChangedListener(textWatcher);
                    textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            int fracDgt = Currency.getInstance(settings.getCurrencyCode()).getDefaultFractionDigits();
                            if (s.toString().isEmpty()) return;
                            BigDecimal bdcNum = new BigDecimal(s.toString());
                            if(bdcNum.scale() > fracDgt)
                                Snackbar.make(bndAdd.getRoot(), getString(R.string.troppi_decimali),
                                        Snackbar.LENGTH_LONG).show();
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    };
                    bndAdd.edtAddCash.addTextChangedListener(textWatcher);
                }
            }
        });
    }

    private void setObserver(){
        viewModel.getActiveAccount().observe(this, new Observer<AccountEntity>() {
            @Override
            public void onChanged(AccountEntity account) {
                transaction.setAccountId(account.getId());
            }
        });
    }
}