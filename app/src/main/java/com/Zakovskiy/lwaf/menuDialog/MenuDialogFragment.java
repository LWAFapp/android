package com.Zakovskiy.lwaf.menuDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.globalConversation.adapters.MessagesAdapter;
import com.Zakovskiy.lwaf.globalConversation.adapters.UsersAdapter;
import com.Zakovskiy.lwaf.menuDialog.adapters.MenuButtonAdapter;
import com.Zakovskiy.lwaf.models.ShortUser;

import java.util.List;

public class MenuDialogFragment extends DialogFragment {

    private List<MenuButton> buttons;
    private Context context;
    private List<MenuButton> menuButtons;
    private MenuButtonAdapter menuButtonAdapter;

    public MenuDialogFragment() {}

    public static MenuDialogFragment newInstance(Context context, List<MenuButton> buttons) {
        MenuDialogFragment fragment = new MenuDialogFragment();
        fragment.context = context;
        fragment.buttons = buttons;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_menu, null);
        builder.setView(view);

        menuButtonAdapter = new MenuButtonAdapter(this.context, this, buttons);
        ((ListView)view.findViewById(R.id.menuButtonsList)).setAdapter(menuButtonAdapter);
        menuButtonAdapter.notifyDataSetChanged();
        return builder.create();
    }
}
