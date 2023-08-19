package com.Zakovskiy.lwaf.adminPanel.dialogs.clients;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.adminPanel.dialogs.clients.adapters.AdminClientsAdapter;
import com.Zakovskiy.lwaf.models.Client;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.privateChat.PrivateChatActivity;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AClientsDialogFragment extends DialogFragment implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RecyclerView rvAdminClients;
    private AdminClientsAdapter adminClientsAdapter;
    private List<Client> clientList = new ArrayList<>();
    private Context context;
    private FragmentManager fragmentManager;

    public AClientsDialogFragment(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ADMIN_GET_CLIENTS);
        this.socketHelper.sendData(new JSONObject(data));
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
        dialog.setContentView(R.layout.dialog_admin_clients);
        this.rvAdminClients = dialog.findViewById(R.id.rvAdminClients);
        adminClientsAdapter = new AdminClientsAdapter(context, fragmentManager, clientList);
        this.rvAdminClients.setAdapter(adminClientsAdapter);
        this.rvAdminClients.setLayoutManager(new LinearLayoutManager(context));
        return dialog;
    }

    public void changesClient(List<Client> list) {
        Logs.info("changesClient");
        clientList.clear();
        clientList.addAll(list);
        adminClientsAdapter.notifyDataSetChanged();
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
                    case PacketDataKeys.ADMIN_GET_CLIENTS:
                        changesClient(JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.CLIENTS), Client.class));
                        break;
                    case PacketDataKeys.ADMIN_DELETE_USER_FROM_SERVER:
                        for (int i = 0; i < clientList.size(); i++) {
                            if (clientList.get(i).userId.equals(json.get(PacketDataKeys.USER_ID).asText())) {
                                clientList.remove(i);
                                adminClientsAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}
