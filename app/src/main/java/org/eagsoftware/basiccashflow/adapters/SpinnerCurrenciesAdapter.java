package org.eagsoftware.basiccashflow.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;

public class SpinnerCurrenciesAdapter extends ArrayAdapter<Currency> {
    private static final List<Currency> currencies;

    static {
        currencies = new ArrayList<>(Currency.getAvailableCurrencies());
        // Ordina la lista per nome
        currencies.sort(Comparator.comparing(Currency::getDisplayName));
    }

    public SpinnerCurrenciesAdapter(@NonNull Context context, int layoutId) {
        super(context, layoutId, currencies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView viewItem = (TextView) super.getView(position, convertView, parent);
        viewItem.setText(currencies.get(position).getDisplayName());
        return viewItem;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView viewItem = (TextView) super.getDropDownView(position, convertView, parent);
        viewItem.setText(currencies.get(position).getDisplayName());
        return viewItem;
    }

    /**
     * settings.currencyCode → @BindingAdapter → layout
     * @param spinner la spinner passata in automatico dal databinding
     * @param currCode stringa recuperata dall'oggetto SettingsEntity, passata in automatico dal dataBinding
     */
    @BindingAdapter("app:selectedCurrencyCode")
    public static void setSelectedCurrencyCode(Spinner spinner, String currCode) {
        // Se il currCode è nullo selezionare "Valuta sconosciuta".
        // currCode inizialmente è sempre vuoto perché settings è richiamato asincronicamente da LiveData.
        if(currCode == null || currCode.isEmpty())
            for(int i=0; i<=currencies.size(); i++) {
            if (i == currencies.size()) spinner.setSelection(0);
            if (currencies.get(i).getCurrencyCode().equals("XXX")) {
                spinner.setSelection(i);
                return;
            }
        }
        for(int i=0; i<=currencies.size(); i++) {
            if (i == currencies.size()) throw new NoSuchElementException("Impossible to set currency spinner.");
            if (currencies.get(i).getCurrencyCode().equals(currCode)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * layout → @InverseBindingAdapter → settings.currencyCode </br>
     * <b>Non funziona senza aver definito l'event</b> (cfr.
     * {@link #setSelectedCurrencyListener(Spinner, InverseBindingListener)}
     * @param spinner la spinner passata in automatico dal dataBinding
     * @return valore aggiornato del currencyCode
     */
    @InverseBindingAdapter(attribute = "app:selectedCurrencyCode", event="app:selectedCurrencyCodeAttrChanged")
    public static String getSelectedCurrencyCode(Spinner spinner) {
        if (spinner.getSelectedItem() != null) {
            return currencies.get(spinner.getSelectedItemPosition()).getCurrencyCode();
        } else {
            return null;
        }
    }

    /**
     * BindingAdapter per impostare il listener di cambio (attrChanged) che notifica al dataBinding
     * quando il valore cambia.
     */
    @BindingAdapter("app:selectedCurrencyCodeAttrChanged")
    public static void setSelectedCurrencyListener(Spinner spinner, InverseBindingListener listener) {
        if (listener == null) return;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
