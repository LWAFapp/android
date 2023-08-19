package com.Zakovskiy.lwaf.adminPanel.dialogs.db;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.adminPanel.dialogs.services.adapters.AdminServicesAdapter;
import com.Zakovskiy.lwaf.models.AdminService;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADBDialogFragment extends DialogFragment implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Context context;
    private TextView tvResponse;
    private FragmentManager fragmentManager;

    public ADBDialogFragment(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        Dialog dialog = getDialog();
        if(dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onStop() {
        socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_admin_db);
        this.tvResponse = dialog.findViewById(R.id.tvResponse);
        dialog.findViewById(R.id.btnSend).setOnClickListener((view) -> {
            String content = ((TextInputLayout) dialog.findViewById(R.id.editRequest)).getEditText().getText().toString();
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ADMIN_DATABASE);
            data.put(PacketDataKeys.ADMIN_DATABASE_REQUEST, content);
            this.socketHelper.sendData(new JSONObject(data));
        });
        return dialog;
    }


    @Override
    public void onConnected() {}

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(JsonNode json) {
        getActivity().runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(context, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case PacketDataKeys.ADMIN_DATABASE:
                        String response = json.get(PacketDataKeys.RESPONSE).asText();
                        this.tvResponse.setText(String.format("%s: %s", getString(R.string.response), response));
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}
