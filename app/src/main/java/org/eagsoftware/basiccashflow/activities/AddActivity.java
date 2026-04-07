package org.eagsoftware.basiccashflow.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.clickhandlers.AddActivityClickHandler;
import org.eagsoftware.basiccashflow.data.AccountEntity;
import org.eagsoftware.basiccashflow.data.SettingsEntity;
import org.eagsoftware.basiccashflow.data.TransactionEntity;
import org.eagsoftware.basiccashflow.databinding.ActivityAddBinding;
import org.eagsoftware.basiccashflow.utilities.ViewModelUtil;

import java.math.BigDecimal;
import java.util.Currency;

public class AddActivity extends AppCompatActivity {
    private ActivityAddBinding bndAdd;
    /** @noinspection FieldCanBeLocal*/
    private AddActivityClickHandler clhAdd;
    private MyViewModel viewModel;

    private TransactionEntity transaction;

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

        setMVVMcomponents();

        checkFractionDigits();

        setObserver();

    }


    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();

        // Richiede il focus e apre la tastiera
        bndAdd.edtAddCash.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(bndAdd.edtAddCash, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setMVVMcomponents(){
        viewModel = ViewModelUtil.getViewModel(getApplication());

        // Imposta la transaction in base a nuovo / modifica
        transaction = viewModel.getActiveTransaction().getValue();

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