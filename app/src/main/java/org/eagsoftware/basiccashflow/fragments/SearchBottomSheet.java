package org.eagsoftware.basiccashflow.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.eagsoftware.basiccashflow.MyViewModel;
import org.eagsoftware.basiccashflow.R;
import org.eagsoftware.basiccashflow.utilities.ViewModelUtil;

public class SearchBottomSheet extends BottomSheetDialogFragment {
    private EditText edtSrc;
    private MyViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_bottom_sheet, container, false);
        edtSrc = view.findViewById(R.id.edt_search);

        viewModel = ViewModelUtil.getViewModel(requireActivity().getApplication());

        setOnTextChanged();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        edtSrc.requestFocus();
    }


    @Override
    public int getTheme() {
        return R.style.noScrim_bottomSheet;
    }

    private void setOnTextChanged() {
        edtSrc.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setSearchQuery(charSequence.toString());
            }
        });
    }

}
