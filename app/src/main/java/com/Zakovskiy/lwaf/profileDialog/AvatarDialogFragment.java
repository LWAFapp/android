package com.Zakovskiy.lwaf.profileDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.utils.ImageUtils;

public class AvatarDialogFragment extends DialogFragment {

    private String url;

    public AvatarDialogFragment (Context context, String url) {
        super();
        this.url = url;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.dark_transparent)));
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_fragment_avatar);
        ImageView avatarImage = dialog.findViewById(R.id.avatarImage);
        ImageUtils.loadImage(getContext(), url, avatarImage, false, false);
        return dialog;
    }
}
