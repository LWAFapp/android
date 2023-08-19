package com.Zakovskiy.lwaf.adminPanel.dialogs.reports;

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
import com.Zakovskiy.lwaf.adminPanel.dialogs.reports.adapters.AdminReportsAdapter;
import com.Zakovskiy.lwaf.models.AdminReport;
import com.Zakovskiy.lwaf.models.Client;
import com.Zakovskiy.lwaf.models.enums.ReportStatus;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AReportsDialogFragment extends DialogFragment implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RecyclerView rvReports;
    private AdminReportsAdapter adminReportsAdapter;
    private List<AdminReport> reports = new ArrayList<>();
    private Context context;
    private FragmentManager fragmentManager;

    public AReportsDialogFragment(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ADMIN_GET_REPORTS);
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
        dialog.setContentView(R.layout.dialog_admin_reports);
        this.rvReports = dialog.findViewById(R.id.rvReports);
        adminReportsAdapter = new AdminReportsAdapter(context, fragmentManager, reports);
        this.rvReports.setAdapter(adminReportsAdapter);
        this.rvReports.setLayoutManager(new LinearLayoutManager(context));
        return dialog;
    }

    public void changesReports(List<AdminReport> list) {
        reports.clear();
        reports.addAll(list);
        adminReportsAdapter.notifyDataSetChanged();
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
                    case PacketDataKeys.ADMIN_GET_REPORTS:
                        changesReports(JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.REPORTS), AdminReport.class));
                        break;
                    case PacketDataKeys.ADMIN_CLOSE_REPORT:
                        for (int i = 0; i < reports.size(); i++) {
                            if (reports.get(i).reportId.equals(json.get(PacketDataKeys.REPORT_ID).asText())) {
                                reports.get(i).status = ReportStatus.CLOSE;
                                adminReportsAdapter.notifyDataSetChanged();
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