package org.eagsoftware.basiccashflow.utilities;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyFormatter {
    public static String format(String amountString, String currencyCode, boolean forceSymbolAfter) {
        if (amountString == null || amountString.isEmpty()) return "";
        if (currencyCode == null || currencyCode.isEmpty()) return amountString;

        BigDecimal value = new BigDecimal(amountString);
        NumberFormat numFrm;

        // Se deve forzare, prende solo le cifre, altrimenti prende tutto
        if (forceSymbolAfter) numFrm = NumberFormat.getNumberInstance(Locale.getDefault());
        else numFrm = NumberFormat.getCurrencyInstance();

        // Formatta le cifre decimali
        Currency currency = Currency.getInstance(currencyCode);
        numFrm.setMinimumFractionDigits(1);
        numFrm.setMaximumFractionDigits(currency.getDefaultFractionDigits());

        // Se deve forzare, aggiunge il simbolo alla cifra
        if (forceSymbolAfter){
            return numFrm.format(value) + " " + currency.getSymbol();
        } else {
            numFrm.setCurrency(currency);
            return numFrm.format(value);
        }
    }
}
