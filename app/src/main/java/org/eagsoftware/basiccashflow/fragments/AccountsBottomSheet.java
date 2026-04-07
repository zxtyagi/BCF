package org.eagsoftware.basiccashflow.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.adapters.RecyclerAccountsAdapter;
import org.eagsoftware.basiccashflow.data.AccountEntity;
import org.eagsoftware.basiccashflow.interfaces.ResultCallback;
import org.eagsoftware.basiccashflow.utilities.ViewModelUtil;

import java.util.List;

public class AccountsBottomSheet extends BottomSheetDialogFragment {
    private RecyclerView mRcy;
    private MyViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_bottom_sheet, container, false);

        mViewModel = ViewModelUtil.getViewModel(requireActivity().getApplication());

        setRecycler(view);

        observeAccounts();
        
        setClickListeners(view);

        return view;
    }
    
    // ------------------------------------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------------------------------------

    private void setRecycler(View view) {
        mRcy = view.findViewById(R.id.rcw_accounts);
        mRcy.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    
    private void setClickListeners(View view) {
        MaterialButton btnAcc = view.findViewById(R.id.btn_add_account);
        btnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.newAccount(new AccountEntity(getString(R.string.nuovo_conto)));
            }
        });
    }


    private void observeAccounts() {
        mViewModel.getAccountsList().observe(getViewLifecycleOwner(), new Observer<List<AccountEntity>>() {
            @Override
            public void onChanged(List<AccountEntity> accountsList) {
                RecyclerAccountsAdapter rcyAdp = new RecyclerAccountsAdapter(accountsList,
                        new RecyclerAccountsAdapter.OnClick() {
                            @Override
                            public void onClick(AccountEntity account) {
                                mViewModel.setActiveAccount(account);
                                dismiss();
                            }

                            @Override
                            public void onEdit(AccountEntity account, String newName) {
                                mViewModel.updateAccountName(account, newName);
                            }

                            @Override
                            public void onDelete(AccountEntity account) {
                                onDeleteClick(account);
                            }
                        });
                mRcy.setAdapter(rcyAdp);
            }
        });
    }


    private void onDeleteClick(AccountEntity account){
        MaterialAlertDialogBuilder bldDlg = new MaterialAlertDialogBuilder(requireContext());
        bldDlg.setTitle(getString(R.string.conferma_eliminazione));
        bldDlg.setMessage(getString(R.string.conferma_elim_acc_body));
        bldDlg.setPositiveButton(getString(R.string.conferma), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.deleteAccount(account, new ResultCallback() {
                    @Override
                    public void onSuccess() { }

                    @Override
                    public void onError(Exception exc) {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getString(R.string.generic_error_text),  Snackbar.LENGTH_LONG).show();
                        dismiss();
                    }
                });
            }
        }).setNegativeButton(getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        bldDlg.create().show();
    }

}
