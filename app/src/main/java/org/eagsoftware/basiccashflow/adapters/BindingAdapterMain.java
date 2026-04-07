package org.eagsoftware.basiccashflow.adapters;

import static org.eagsoftware.basiccashflow.utilities.Constants.ANI_TEXT_CHANGED;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import org.eagsoftware.basiccashflow.utilities.CurrencyFormatter;

public class BindingAdapterMain {

    @BindingAdapter({"amountString", "currencyCode", "forceSymbolAfter"})
    public static void bindCurrency(TextView txw, String amountString, String currencyCode,
                                    boolean forceSymbolAfter) {
        if (amountString == null || amountString.isEmpty()) { txw.setText(""); return; }

        setAnimatedText(txw, CurrencyFormatter.format(amountString, currencyCode, forceSymbolAfter));
    }

    @BindingAdapter({"amountString", "currencyCode", "isIncome", "forceSymbolAfter"})
    public static void bindCurrency(TextView txw, String amountString, String currencyCode,
                                    boolean isIncome, boolean forceSymbolAfter) {
        if (amountString == null || amountString.isEmpty()) { txw.setText(""); return; }

        String signedAmountString = (isIncome) ? amountString: "-" + amountString;
        setAnimatedText(txw, CurrencyFormatter.format(signedAmountString, currencyCode, forceSymbolAfter));
    }

    @BindingAdapter("android:animatedText")
    public static void setAnimatedText(TextView view, String newText) {
        String oldText = view.getText().toString();
        if (oldText.equals(newText)) return;

        view.animate()
                .alpha(0f)
                .setDuration(ANI_TEXT_CHANGED / 2)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setText(newText);
                        view.animate().alpha(1f).setDuration(ANI_TEXT_CHANGED / 2).start();
                    }
                })
                .start();
    }

}
