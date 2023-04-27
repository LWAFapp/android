package com.Zakovskiy.lwaf.dashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.dashboard.dialogs.DialogEnterPassword;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class RoomsAdapter extends ArrayAdapter<RoomInLobby> {

    private List<RoomInLobby> roomsData;
    private Context context;
    private LayoutInflater layoutInflater;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    public RoomsAdapter(Context context, List<RoomInLobby> list) {
        super(context, R.layout.item_room_list, list);
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.roomsData = list;
    }

    @Override
    public int getCount() {
        return this.roomsData.size();
    }

    @Override
    public RoomInLobby getItem(int position) {
        return this.roomsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Logs.debug("xc");
        View view = this.layoutInflater.inflate(R.layout.item_room_list, parent, false);
        RoomInLobby roomObject = getItem(position);
        TextView roomTitle = view.findViewById(R.id.irl__title);
        TextView roomPlayers = view.findViewById(R.id.irl__players_count);
        ImageView roomLock = view.findViewById(R.id.irl__lock);
        roomTitle.setText(roomObject.title);
        roomPlayers.setText(String.format("%s %s/%s", this.context.getString(R.string.players_size), roomObject.players.size(), roomObject.playersCountSize));
        if (roomObject.password)
            roomLock.setVisibility(View.VISIBLE);
        view.setOnClickListener(v -> {
            if (roomObject.password) {
                new DialogEnterPassword(this.context, roomObject.roomId).show();
            } else {
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_JOIN);
                data.put(PacketDataKeys.ROOM_IDENTIFICATOR, roomObject.roomId);
                data.put(PacketDataKeys.ROOM_PASSWORD, "");
                socketHelper.sendData(new JSONObject(data));
            }
        });
        return view;
    }
}
