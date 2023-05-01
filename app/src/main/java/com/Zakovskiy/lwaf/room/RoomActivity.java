package com.Zakovskiy.lwaf.room;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.globalConversation.adapters.UsersAdapter;
import com.Zakovskiy.lwaf.room.adapters.MessagesAdapter;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.MessageRoom;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.adapters.UsersInRoomAdapter;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RoomInLobby room;
    private ListView listMessages;
    private ListView listUsers;
    private MessagesAdapter messagesAdapter;
    private UsersInRoomAdapter usersAdapter;
    private List<ShortUser> roomUsers = new ArrayList<>();
    private List<MessageRoom> messagesRoom = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        this.socketHelper.subscribe(this);
        this.room = (RoomInLobby) getIntent().getSerializableExtra("room");
        ((TextView)findViewById(R.id.textTitle)).setText(this.room.title);
        this.listMessages = (ListView) findViewById(R.id.listViewMessages);
        this.listUsers = (ListView) findViewById(R.id.listViewUsers);
        TextInputLayout inputNewMessage = (TextInputLayout)findViewById(R.id.inputLayoutSendMessage);
        usersAdapter = new UsersInRoomAdapter(this, getSupportFragmentManager(), roomUsers);
        messagesAdapter = new MessagesAdapter(this, getSupportFragmentManager(), messagesRoom);
        this.listMessages.setAdapter(messagesAdapter);
        this.listUsers.setAdapter(usersAdapter);
        changesUsers(this.room.players);
        inputNewMessage.setEndIconOnClickListener(v -> {
            String messageString = inputNewMessage.getEditText().getText().toString();
            if (messageString.isEmpty()) return;
            HashMap<String, Object> dataMessage = new HashMap<>();
            dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_SEND_MESSAGE);
            dataMessage.put(PacketDataKeys.MESSAGE, messageString);
            inputNewMessage.getEditText().setText("");
            this.socketHelper.sendData(new JSONObject(dataMessage));
        });
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(RoomActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
            } else if (json.has(PacketDataKeys.ROOM_TYPE_EVENT)) {
                String roomTypeEvent = json.get(PacketDataKeys.ROOM_TYPE_EVENT).asText();
                Logs.debug(roomTypeEvent);
                switch(roomTypeEvent) {
                    case "nm": // new message
                        MessageRoom newMessage = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.MESSAGE), MessageRoom.class);
                        List<MessageRoom> newList = new ArrayList<>(messagesRoom);
                        newList.add(newMessage);
                        changesMessages(newList);
                        this.listMessages.smoothScrollToPosition(messagesAdapter.getCount() - 1);
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceiveError(String str) {

    }

    public void changesMessages(List<MessageRoom> list) {
        Logs.debug("messages"+list.toString());
        messagesRoom.clear();
        messagesRoom.addAll(list);
        messagesAdapter.notifyDataSetChanged();
    }

    public void changesUsers(List<ShortUser> list) {
        Logs.debug("users"+list.toString());
        roomUsers.clear();
        roomUsers.addAll(list);
        usersAdapter.notifyDataSetChanged();
    }
}
