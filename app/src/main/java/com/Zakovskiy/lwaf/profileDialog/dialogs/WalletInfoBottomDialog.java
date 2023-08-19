package com.Zakovskiy.lwaf.profileDialog.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Zakovskiy.lwaf.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WalletInfoBottomDialog extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.dialog_info_wallet, container, false);
        return view;
    }
}
