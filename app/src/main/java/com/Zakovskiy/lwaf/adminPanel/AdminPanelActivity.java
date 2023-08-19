package com.Zakovskiy.lwaf.adminPanel;

import android.os.Bundle;
import android.view.View;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.adminPanel.dialogs.clients.AClientsDialogFragment;
import com.Zakovskiy.lwaf.adminPanel.dialogs.db.ADBDialogFragment;
import com.Zakovskiy.lwaf.adminPanel.dialogs.reports.AReportsDialogFragment;
import com.Zakovskiy.lwaf.adminPanel.dialogs.services.AServicesDialogFragment;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.ServerConfig;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.google.android.material.button.MaterialButton;

public class AdminPanelActivity  extends ABCActivity {

    private MaterialButton mbServices;
    private MaterialButton mbSql;
    private MaterialButton mbClients;
    private MaterialButton mbReports;
    private User currentUser = Application.lwafCurrentUser;
    private ServerConfig.Allowed allowed = Application.lwafServerConfig.allowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        this.mbServices = findViewById(R.id.btnServices);
        this.mbSql = findViewById(R.id.btnSQL);
        this.mbClients = findViewById(R.id.btnClients);
        this.mbReports = findViewById(R.id.btnReports);
        if(currentUser.isClients()) {
            this.mbClients.setVisibility(View.VISIBLE);
            this.mbClients.setOnClickListener((view)->{
                new AClientsDialogFragment(this, getSupportFragmentManager()).show(getSupportFragmentManager(), "AClientsDialogFragment");
            });
        }
        if(currentUser.isReports()) {
            this.mbReports.setVisibility(View.VISIBLE);
            this.mbReports.setOnClickListener((view)->{
                new AReportsDialogFragment(this, getSupportFragmentManager()).show(getSupportFragmentManager(), "AClientsDialogFragment");
            });
        }
        if(currentUser.isDb()) {
            this.mbSql.setVisibility(View.VISIBLE);
            this.mbSql.setOnClickListener((view) -> {
                new ADBDialogFragment(this, getSupportFragmentManager()).show(getSupportFragmentManager(), "ADBDialogFragment");
            });
        }
        if(currentUser.isUnallowedService()) {
            this.mbServices.setVisibility(View.VISIBLE);
            this.mbServices.setOnClickListener((view) -> {
                new AServicesDialogFragment(this, getSupportFragmentManager()).show(getSupportFragmentManager(), "AServicesDialogFragment");
            });
        }
    }
}
