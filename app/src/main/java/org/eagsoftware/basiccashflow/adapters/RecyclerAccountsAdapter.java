package org.eagsoftware.basiccashflow.adapters;

import static org.eagsoftware.basiccashflow.utilities.ThemeAttributesUtil.getDefaultEditTextDrawable;
import static org.eagsoftware.basiccashflow.utilities.ThemeAttributesUtil.getOnPrimaryColor;
import static org.eagsoftware.basiccashflow.utilities.ThemeAttributesUtil.getOnSurfaceColor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.data.AccountEntity;

import java.util.List;

public class RecyclerAccountsAdapter extends RecyclerView.Adapter<RecyclerAccountsAdapter.ViewHolder>{

    private final List<AccountEntity> accountsList;
    private final OnClick callback;


    public RecyclerAccountsAdapter(List<AccountEntity> accountsList, OnClick callback) {
        this.accountsList = accountsList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcy_acc,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountEntity account = accountsList.get(position);
        holder.bind(account);
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }



    public interface OnClick{
        void onClick(AccountEntity account);
        void onEdit(AccountEntity account, String newName);
        void onDelete(AccountEntity account);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final EditText edtName;
        final MaterialButton btnEdit;
        final MaterialButton btnDel;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            edtName = itemView.findViewById(R.id.txw_rcy_acc_name);
            btnEdit = itemView.findViewById(R.id.btn_rcy_acc_edit);
            btnDel = itemView.findViewById(R.id.btn_rcy_acc_del);
        }

        public void bind(AccountEntity account) {
            // Nascondi il pulsante di eliminazione se c'è un solo conto disponibile
            if (getItemCount() == 1) btnDel.setVisibility(View.INVISIBLE);
            else btnDel.setVisibility(View.VISIBLE);

            edtName.setText(account.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onClick(account);
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (edtName.isEnabled()) callback.onEdit(account, edtName.getText().toString());
                    toggleEditMode();
                }
            });

            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onDelete(account);
                }
            });
        }


        private void toggleEditMode(){
            if(!edtName.isEnabled()) {
                // Modalità EDIT
                edtName.setEnabled(true);
                edtName.setFocusableInTouchMode(true);
                edtName.setClickable(true);
                edtName.setLongClickable(true);
                edtName.requestFocus();
                edtName.setBackground(getDefaultEditTextDrawable(itemView.getContext()));
                edtName.setSelection(edtName.getText().length());   // Sposta il cursore alla fine
                btnEdit.setIconResource(R.drawable.ic_save_no_borders);
                btnEdit.setIconTint(ColorStateList.valueOf(getOnPrimaryColor(itemView.getContext())));
                InputMethodManager imm =
                        (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtName, InputMethodManager.SHOW_IMPLICIT);

            } else {
                // Modalità VIEW (TextView-like)
                edtName.setEnabled(false);
                edtName.setFocusable(false);
                edtName.setClickable(false);
                edtName.setLongClickable(false);
                edtName.setBackgroundColor(Color.TRANSPARENT);
                edtName.setPadding(0, 0, 0, 0);
                btnEdit.setIconResource(R.drawable.ic_edit);
                btnEdit.setIconTint(ColorStateList.valueOf(getOnSurfaceColor(itemView.getContext())));
            }
        }


    }

}
