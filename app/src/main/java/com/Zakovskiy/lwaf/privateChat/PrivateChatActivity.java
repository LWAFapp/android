package com.Zakovskiy.lwaf.privateChat;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.globalConversation.adapters.MessagesAdapter;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PrivateChatActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private MessagesAdapter messagesAdapter;
    private RecyclerView listMessages;
    private List<Message> globalMessages = new ArrayList<>();
    private String replyId = "";
    private LinearLayout replyToLayout;
    public List<String> ids = new ArrayList<>();
    private TextInputLayout inputNewMessage;
    private String friend_id;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return messagesAdapter.isSwipeable(viewHolder.getAdapterPosition()) ? super.getSwipeDirs(recyclerView, viewHolder) : 0;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //Работает только для LEFT, если хочешь еще вправо свапать то switch делай
            int position = viewHolder.getAdapterPosition();
            Message swappedMessage = globalMessages.get(position);
            globalMessages.remove(position);
            globalMessages.add(position, swappedMessage);
            messagesAdapter.notifyDataSetChanged();
            setReply(swappedMessage);
        }
    };

    public void setReply(Message message) {
        replyToLayout.setVisibility(View.VISIBLE);
        TextView replyToUser = (TextView) replyToLayout.findViewById(R.id.replyTo_username);
        replyToUser.setText(message.user.nickname);
        TextView replyToMessage = (TextView) replyToLayout.findViewById(R.id.replyTo_message);
        replyToMessage.setText(message.message);
        replyId = message.messageId;
        ImageButton cancelButton = (ImageButton) replyToLayout.findViewById(R.id.cancelReply);
        cancelButton.setOnClickListener(v -> {
            replyId = "";
            replyToLayout.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        this.replyToLayout = findViewById(R.id.replyTo);
        this.listMessages = findViewById(R.id.listViewMessages);
        this.inputNewMessage = findViewById(R.id.inputLayoutSendMessage);
        messagesAdapter = new MessagesAdapter(this, getSupportFragmentManager(), globalMessages, this);
        this.listMessages.setAdapter(messagesAdapter);
        this.listMessages.setLayoutManager(new LinearLayoutManager(this));
        this.listMessages.getRecycledViewPool().setMaxRecycledViews(0, 0);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(listMessages);
        this.inputNewMessage.setEndIconOnClickListener(v -> {
            String messageString = this.inputNewMessage.getEditText().getText().toString();
            if (messageString.isEmpty()) return;
            replyToLayout.findViewById(R.id.replyTo).setVisibility(View.GONE);
            HashMap<String, Object> dataMessage = new HashMap<>();
            dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_SEND_MESSAGE);
            dataMessage.put(PacketDataKeys.MESSAGE, messageString);
            dataMessage.put(PacketDataKeys.REPLY_MESSAGE_ID, replyId);
            this.inputNewMessage.getEditText().setText("");
            replyId = "";
            this.socketHelper.sendData(new JSONObject(dataMessage));
        });
        this.friend_id = (String) getIntent().getSerializableExtra("friend");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
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
                new DialogTextBox(PrivateChatActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {

                }
            }
        });
    }

    public void changesMessages(List<Message> list) {
        Logs.debug("messages"+list.toString());
        if(list.size() > 1) {
            globalMessages.clear();
            globalMessages.addAll(list);
            messagesAdapter.notifyDataSetChanged();
            return;
        }
        globalMessages.add(list.get(0));
        messagesAdapter.notifyItemInserted(globalMessages.size()-1);
    }

    @Override
    public void onReceiveError(String str) {

    }
}
