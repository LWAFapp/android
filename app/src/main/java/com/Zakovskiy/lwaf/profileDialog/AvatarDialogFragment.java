package com.Zakovskiy.lwaf.profileDialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.menuDialog.MenuDialogFragment;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class AvatarDialogFragment extends DialogFragment {

    private String url;
    private Context context;
    private String nickname;

    public AvatarDialogFragment (Context context, String url, String nick) {
        super();
        this.url = url;
        this.context = context;
        this.nickname = nick;
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
        avatarImage.setOnClickListener(v -> {
            ArrayList<MenuButton> btns = new ArrayList<>();
            btns.add(new MenuButton(this.context.getString(R.string.download_image), "#FFFFFF", (vb) -> {
                File downloads_dir = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS);
                OutputStream fOut = null;
                avatarImage.buildDrawingCache();
                Bitmap bm=avatarImage.getDrawingCache();
                try {
                    File sdImageMainDirectory = new File(downloads_dir, (System.currentTimeMillis()/1000)+".png");
                    fOut = new FileOutputStream(sdImageMainDirectory);
                } catch (Exception e) {
                    new DialogTextBox(this.context, getString(R.string.error), Config.ERRORS.get(1)).show();
                    e.printStackTrace();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    new DialogTextBox(this.context, getString(R.string.error), getString(R.string.error_save_photo)).show();
                    e.printStackTrace();
                }
            }));
            MenuDialogFragment.newInstance(this.context, btns).show(getFragmentManager(), "MenuButtons");
        });
        return dialog;
    }
}
