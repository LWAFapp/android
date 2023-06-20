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
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrivateChatActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private MessagesAdapter messagesAdapter;
    private RecyclerView listMessages;
    private List<Message> globalMessages = new ArrayList<>();
    private String replyId = "";
    private LinearLayout replyToLayout;
    private TextInputLayout inputNewMessage;
    private String friendId;
    private TextView tvTitle;
    private ShimmerFrameLayout messagesShimmer;

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
        this.messagesShimmer = findViewById(R.id.shimmerMessages);
        this.inputNewMessage = findViewById(R.id.inputLayoutSendMessage);
        this.tvTitle = findViewById(R.id.textTitle);
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
            dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.PRIVATE_CONVERSATION_SEND_MESSAGE);
            dataMessage.put(PacketDataKeys.MESSAGE, messageString);
            dataMessage.put(PacketDataKeys.FRIEND_ID, this.friendId);
            dataMessage.put(PacketDataKeys.REPLY_MESSAGE_ID, replyId);
            this.inputNewMessage.getEditText().setText("");
            replyId = "";
            this.socketHelper.sendData(new JSONObject(dataMessage));
        });
        this.friendId = (String) getIntent().getSerializableExtra("friend");
        HashMap<String, Object> dataMessage = new HashMap<>();
        dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.PRIVATE_CONVERSATION_JOIN);
        dataMessage.put(PacketDataKeys.FRIEND_ID, this.friendId);
        this.socketHelper.sendData(new JSONObject(dataMessage));
        changeShimmerMessages(true);
    }

    @Override
    public void onStart() {

        this.socketHelper.subscribe(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    private void changeShimmerMessages(boolean type) {
        if(type) {
            this.messagesShimmer.startShimmer();
        } else {
            this.messagesShimmer.stopShimmer();
        }
        this.inputNewMessage.setEnabled(!type);
        this.messagesShimmer.setVisibility(type ? View.VISIBLE : View.INVISIBLE);
        this.listMessages.setVisibility(type ? View.INVISIBLE : View.VISIBLE);
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
                if(typeEvent.equals(PacketDataKeys.PRIVATE_CONVERSATION_JOIN)) {
                    ShortUser shortUser = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.USER), ShortUser.class);
                    this.tvTitle.setText(String.format("%s", shortUser.nickname));
                    HashMap<String, Object> dataMessage = new HashMap<>();
                    dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.PRIVATE_CONVERSATION_GET_MESSAGES);
                    dataMessage.put(PacketDataKeys.FRIEND_ID, this.friendId);
                    this.socketHelper.sendData(new JSONObject(dataMessage));
                } else if(typeEvent.equals(PacketDataKeys.PRIVATE_CONVERSATION_GET_MESSAGES)) {
                    List<Message> newMessages = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.CONVERSATION_MESSAGE), Message.class);
                    changesMessages(newMessages);
                    this.listMessages.postDelayed(()->{
                        this.listMessages.scrollToPosition(messagesAdapter.getCount() - 1);
                        changeShimmerMessages(false);
                    }, 200);
                } else if(typeEvent.equals(PacketDataKeys.PRIVATE_CONVERSATION_NEW_MESSAGE)) {
                    List<Message> newMessages = new ArrayList<>(globalMessages);
                    Message newMessage = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.CONVERSATION_MESSAGE), Message.class);
                    newMessages.add(newMessage);
                    changesMessages(newMessages);
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