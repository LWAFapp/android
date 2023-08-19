package com.Zakovskiy.lwaf.adminPanel.dialogs.services.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.adminPanel.dialogs.clients.adapters.AdminClientsAdapter;
import com.Zakovskiy.lwaf.models.AdminService;
import com.Zakovskiy.lwaf.models.Client;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdminServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AdminService> services;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Context context;
    private FragmentManager fragmentManager;

    public AdminServicesAdapter(Context context, FragmentManager fragmentManager, List<AdminService> services) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.services = services;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_admin_service, parent, false);
        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdminService service = this.services.get(position);
        ServicesViewHolder textHolder = (ServicesViewHolder) holder;
        textHolder.bind(service);
    }

    @Override
    public int getItemCount() {
        return this.services.size();
    }

    private class ServicesViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName;
        private CheckBox cbService;

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvServiceName = itemView.findViewById(R.id.tvServiceName);
            this.cbService = itemView.findViewById(R.id.cbService);
        }

        public void bind(AdminService service) {
            this.tvServiceName.setText(service.name);
            this.cbService.setChecked(service.value);
            this.cbService.setOnClickListener((view)->{
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ADMIN_ENGINE_SERVICE);
                data.put(PacketDataKeys.SERVICE_NAME, service.name);
                socketHelper.sendData(new JSONObject(data));
            });
        }
    }
}