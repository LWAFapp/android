package com.Zakovskiy.lwaf.room;

import android.os.Bundle;
import android.widget.ListView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.dashboard.adapters.RoomsAdapter;
import com.Zakovskiy.lwaf.globalConversation.GlobalConversationActivity;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

public class RoomActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RoomInLobby room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        this.socketHelper.subscribe(this);
        this.room = (RoomInLobby) getIntent().getSerializableExtra("room");
        /* V razrabotke */
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
}
