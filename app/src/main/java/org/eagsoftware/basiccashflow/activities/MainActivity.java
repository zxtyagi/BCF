package org.eagsoftware.basiccashflow.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.adapters.RecyclerTransactionsAdapter;
import org.eagsoftware.basiccashflow.clickhandlers.MainActivityClickHandler;
import org.eagsoftware.basiccashflow.data.AccountEntity;
import org.eagsoftware.basiccashflow.data.SettingsEntity;
import org.eagsoftware.basiccashflow.data.TransactionEntity;
import org.eagsoftware.basiccashflow.databinding.ActivityMainBinding;
import org.eagsoftware.basiccashflow.utilities.Constants;
import org.eagsoftware.basiccashflow.utilities.LeaveBehindGenerator;

import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * <code>updatableAdapterPosition</code> serve per memorizzare la posizione dell'elemento passato in
 * editing all'addActivity. In caso di annullamento dell'editing occorre ripristinare manualmente lo swipe
 * dell'elemento della recyclerView e per farlo occorre questo dato. Se invece è impostato a -1 allora non
 * occorre fare alcun ripristino.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bndMain;
    /** @noinspection FieldCanBeLocal*/
    private MainActivityClickHandler clhMain;
    private MyViewModel viewModel;
    private RecyclerTransactionsAdapter adpRcyTrans;

    private static int adapterItemChangedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NOTE: TOGLIERE LA SEGUENTE RIGA PER AVERE ESTENSIONE CORRETTA DEL LAYOUT
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setMVVMcomponents();

        setObserver();

        setRecycler();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Necessario per aggiornare manualmente l'elemento della lista che ha subito lo swipe */
        if(adapterItemChangedPosition != -1)
            adpRcyTrans.notifyItemChanged(adapterItemChangedPosition);
        adapterItemChangedPosition = -1;
    }

    public static void resetUpdatableAdapterPosition() {
        MainActivity.adapterItemChangedPosition = -1;
    }


    private void setMVVMcomponents(){
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        bndMain = DataBindingUtil.setContentView(this, R.layout.activity_main);
        clhMain = new MainActivityClickHandler(this, viewModel);
        bndMain.setViewModel(viewModel);
        bndMain.setLifecycleOwner(this);
        bndMain.setClickHandler(clhMain);
    }

    private void setObserver(){
        viewModel.getTransactionsList().observe(this, new Observer<List<TransactionEntity>>() {
            @Override
            public void onChanged(List<TransactionEntity> transactionEntities) {
                // Aggiorna il saldo
                viewModel.updateBalance();
                // Aggiorna la recycler
                adpRcyTrans.updateTransactions(transactionEntities);
            }
        });

        viewModel.getSettings().observe(this, new Observer<SettingsEntity>() {
            @Override
            public void onChanged(SettingsEntity settingsEntity) {
                if (settingsEntity == null){
                    // Se non sono stati ancora definiti dei salvataggi
                    SettingsEntity defSets = new SettingsEntity(Constants.USER_ID, "EUR", false);
                    viewModel.newSettings(defSets);
                } else {
                    Currency curr = viewModel.getCurrency().getValue();
                    // Se il codice della valuta è diverso, aggiorna l'oggetto Currency del viewModel
                    if (curr == null || !settingsEntity.getCurrencyCode().equals(curr.getCurrencyCode()))
                        viewModel.setCurrency(Currency.getInstance(settingsEntity.getCurrencyCode()));
                }
            }
        });

        viewModel.getAccountsList().observe(this, new Observer<List<AccountEntity>>() {
            @Override
            public void onChanged(List<AccountEntity> accountEntities) {
                if (accountEntities.isEmpty()) {
                    AccountEntity accMain = new AccountEntity(getString(R.string.il_mio_conto));
                    viewModel.newAccount(accMain);
                }
            }
        });
    }

    private void setRecycler(){
        RecyclerView rcyTrans = bndMain.rcwTrans;
        rcyTrans.setHasFixedSize(true);
        adpRcyTrans = new RecyclerTransactionsAdapter(viewModel.getTransactionsList().getValue(),
                MainActivity.this, viewModel);
        rcyTrans.setAdapter(adpRcyTrans);

        /* RECYCLER EVENTS */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                 ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            private LeaveBehindGenerator leaveBehindGenerator;

            // Imposta la soglia oltre la quale lo swipe è considerato completo
            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f;
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // Se è già in corso un'altro swipe, blocca il corrente
                if (adapterItemChangedPosition != -1) return 0;
                return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT) {
                    adapterItemChangedPosition = viewHolder.getAdapterPosition();
                    TransactionEntity trans =
                            Objects.requireNonNull(viewModel.getTransactionsList().getValue(),
                                    "La transactionList è null").get(adapterItemChangedPosition);
                    Snackbar.make(bndMain.getRoot(), getString(R.string.confermi_eliminazione), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.conferma), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (leaveBehindGenerator != null) leaveBehindGenerator.destroy();
                                    viewModel.deleteTransaction(trans);
                                    adapterItemChangedPosition = -1;
                                }
                            })
                            .addCallback(new Snackbar.Callback(){
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    if(adapterItemChangedPosition != -1) {
                                        if (leaveBehindGenerator != null) leaveBehindGenerator.destroy();
                                        adpRcyTrans.notifyItemChanged(adapterItemChangedPosition);
                                        adapterItemChangedPosition = -1;
                                    }
                                }
                            }).show();

                } else if (direction == ItemTouchHelper.RIGHT){
                    // Apre la add activity passando l'intent
                    // NOTE: l'oggetto da modificare viene passato dall'intent e non letto dal viewmodel
                    // per mantenere tutto volutamente sul main thread.
                    Intent intShowAddAct = new Intent(MainActivity.this, AddActivity.class);
                    TransactionEntity currTrans = adpRcyTrans.getCurrTransaction(viewHolder);
                    Bundle bundleTrans = new Bundle();
                    bundleTrans.putSerializable("transaction", currTrans);
                    intShowAddAct.putExtra("bundle", bundleTrans);
                    startActivity(intShowAddAct);
                    adapterItemChangedPosition = viewHolder.getAdapterPosition();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                    boolean isCurrentlyActive) {
                if (leaveBehindGenerator == null) {
                    // Disegna il layout leave behind
                    leaveBehindGenerator = new LeaveBehindGenerator(recyclerView, MainActivity.this,
                            R.layout.leave_behind_delete, R.layout.leave_behind_edit);
                }
                leaveBehindGenerator.generate(canvas, viewHolder, dX);
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(rcyTrans);
    }
}