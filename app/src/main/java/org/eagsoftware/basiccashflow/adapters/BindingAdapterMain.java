package org.eagsoftware.basiccashflow.adapters;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import org.eagsoftware.basiccashflow.utilities.CurrencyFormatter;

public class BindingAdapterMain {

    @BindingAdapter({"amountString", "currencyCode", "forceSymbolAfter"})
    public static void bindCurrency(TextView txw, String amountString, String currencyCode,
                                    boolean forceSymbolAfter) {
        if (amountString == null || amountString.isEmpty()) { txw.setText(""); return; }

        txw.setText(CurrencyFormatter.format(amountString, currencyCode, forceSymbolAfter));
    }

    @BindingAdapter({"amountString", "currencyCode", "isIncome", "forceSymbolAfter"})
    public static void bindCurrency(TextView txw, String amountString, String currencyCode,
                                    boolean isIncome, boolean forceSymbolAfter) {
        if (amountString == null || amountString.isEmpty()) { txw.setText(""); return; }

        String signedAmountString = (isIncome) ? amountString: "-" + amountString;
        txw.setText(CurrencyFormatter.format(signedAmountString, currencyCode, forceSymbolAfter));
    }
}
