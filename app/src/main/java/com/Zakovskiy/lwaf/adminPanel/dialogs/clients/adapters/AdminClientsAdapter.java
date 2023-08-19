package com.Zakovskiy.lwaf.adminPanel.dialogs.clients.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.Client;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdminClientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Client> clients;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Context context;
    private FragmentManager fragmentManager;

    public AdminClientsAdapter(Context context, FragmentManager fragmentManager, List<Client> clients) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.clients = clients;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_admin_client, parent, false);
        return new ClientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Client client = this.clients.get(position);
        ClientsViewHolder textHolder = (ClientsViewHolder) holder;
        textHolder.bind(client);
    }

    @Override
    public int getItemCount() {
        return this.clients.size();
    }

    private class ClientsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserData;
        private ImageView ivRemoveClient;

        public ClientsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvUserData = itemView.findViewById(R.id.tvUserData);
            this.ivRemoveClient = itemView.findViewById(R.id.ivRemoveClient);
        }

        public void bind(Client client) {
            this.tvUserData.setText(Html.fromHtml(String.format("<b>%s</b> (%s:%s)", client.nickname, client.ip, client.port)));
            this.tvUserData.setOnClickListener((view)->{
                ProfileDialogFragment.newInstance(context, client.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.ivRemoveClient.setOnClickListener((view)->{
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ADMIN_DELETE_USER_FROM_SERVER);
                data.put(PacketDataKeys.USER_ID, client.userId);
                socketHelper.sendData(new JSONObject(data));
            });
        }
    }
}
