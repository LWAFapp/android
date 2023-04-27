package com.Zakovskiy.lwaf.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogCreateRoom;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.WelcomeActivity;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.adapters.RoomsAdapter;
import com.Zakovskiy.lwaf.globalConversation.GlobalConversationActivity;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.RoomActivity;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DashboardFragment extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    public List<RoomInLobby> rooms = new ArrayList<>();
    public RoomsAdapter roomsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ListView roomsListView = findViewById(R.id.menu__rooms_list);
        roomsAdapter = new RoomsAdapter(this, rooms);
        roomsListView.setAdapter(roomsAdapter);
        findViewById(R.id.menu__button_create_room).setOnClickListener(this);
        findViewById(R.id.menu__logout).setOnClickListener(this);
        findViewById(R.id.menu__button_global_chat).setOnClickListener(this);
        this.socketHelper.subscribe(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.DASHBOARD);
        data.put(PacketDataKeys.VERSION, Config.VERSION);
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(DashboardFragment.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "db": // dashboard
                        Application.lwafCurrentUser = (User) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ACCOUNT), User.class);
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_LIST);
                        this.socketHelper.sendData(new JSONObject(data));
                        break;
                    case "rli": // room list
                        List<RoomInLobby> roomsInLobby = JsonUtils.convertJsonNodeToList(json.get("rr"), RoomInLobby.class);
                        changesRooms(roomsInLobby);
                        break;
                    case "lo": // logout
                        SharedPreferences sPref = getSharedPreferences("lwaf_user", 0);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("access_token", "");
                        ed.apply();
                        newActivity(WelcomeActivity.class, true, null);
                        break;
                    case "rj": // room join
                        RoomInLobby room = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ROOM), RoomInLobby.class);
                        Bundle b = new Bundle();
                        b.putSerializable("room", room);
                        newActivity(RoomActivity.class, b);
                        break;
                }
            } else if (json.has(PacketDataKeys.ROOM_TYPE_EVENT)) {
                String roomTypeEvent = json.get(PacketDataKeys.ROOM_TYPE_EVENT).asText();
                switch(roomTypeEvent) {
                    case "rc": // room create
                        RoomInLobby newRoom = JsonUtils.convertJsonNodeToObject(json.get("rr"), RoomInLobby.class);
                        List<RoomInLobby> newList = new ArrayList<>(rooms);
                        newList.add(newRoom);
                        changesRooms(newList);
                        break;
                    case "pcs": // players count size
                        String roomId = json.get(PacketDataKeys.ROOM_IDENTIFICATOR).asText();
                        for (RoomInLobby roomInLobby : rooms) {
                            if (roomInLobby.roomId.equals(roomId)) {
                                roomInLobby.players = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.PLAYERS), ShortUser.class);
                            }
                        }
                        changesRooms(rooms);
                        break;
                    case "dr": // delete room
                        Iterator<RoomInLobby> it = rooms.iterator();
                        while (it.hasNext()) {
                            if (it.next().roomId.equals(json.get(PacketDataKeys.ROOM_IDENTIFICATOR).asText())) {
                                it.remove();
                            }
                        }
                        changesRooms(this.rooms);
                        break;
                }
            }
        });
    }

    public void changesRooms(List<RoomInLobby> list) {
        rooms.clear();
        rooms.addAll(list);
        roomsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReceiveError(String str) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.menu__button_create_room) {
            new DialogCreateRoom(this).show();
        } else if (id == R.id.menu__button_global_chat) {
            newActivity(GlobalConversationActivity.class);
        } else if (id == R.id.menu__logout) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.LOGOUT);
            this.socketHelper.sendData(new JSONObject(data));
        }
    }
}