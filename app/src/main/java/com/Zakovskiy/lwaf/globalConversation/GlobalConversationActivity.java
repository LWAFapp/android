package com.Zakovskiy.lwaf.globalConversation;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.globalConversation.adapters.*;
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
import java.util.Iterator;
import java.util.List;

public class GlobalConversationActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private TextInputLayout inputNewMessage;
    private ListView listUsers;
    private RecyclerView listMessages;
    private ShimmerFrameLayout messagesShimmer;
    private ShimmerFrameLayout usersShimmer;
    private List<Message> messagesInConversation;
    private List<ShortUser> usersInConversation;
    private MessagesAdapter messagesAdapter;
    private UsersAdapter usersAdapter;
    private List<Message> globalMessages = new ArrayList<>();
    private List<ShortUser> globalUsers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_conversation);
        this.listUsers = findViewById(R.id.listViewUsers);
        this.listMessages = findViewById(R.id.listViewMessages);
        this.messagesShimmer = findViewById(R.id.shimmerMessages);
        this.usersShimmer = findViewById(R.id.shimmerViewUsers);
        this.inputNewMessage = findViewById(R.id.inputLayoutSendMessage);
        messagesAdapter = new MessagesAdapter(this, getSupportFragmentManager(), globalMessages);
        this.listMessages.setAdapter(messagesAdapter);
        this.listMessages.setLayoutManager(new LinearLayoutManager(this));
        usersAdapter = new UsersAdapter(this, getSupportFragmentManager(), globalUsers);
        this.listUsers.setAdapter(usersAdapter);
        this.inputNewMessage.setEndIconOnClickListener(v -> {
            String messageString = this.inputNewMessage.getEditText().getText().toString();
            if (messageString.isEmpty()) return;
            HashMap<String, Object> dataMessage = new HashMap<>();
            dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_SEND_MESSAGE);
            dataMessage.put(PacketDataKeys.MESSAGE, messageString);
            this.inputNewMessage.getEditText().setText("");
            dataMessage.put(PacketDataKeys.REPLY_MESSAGE_ID, "");
            this.socketHelper.sendData(new JSONObject(dataMessage));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_JOIN);
        this.socketHelper.sendData(new JSONObject(data));
        changeShimmerMessages(true);
        changeShimmerUsers(true);
    }

    private void changeShimmerMessages(boolean type) {
        if(type) {
            this.messagesShimmer.startShimmer();
            this.inputNewMessage.setEnabled(false);
        } else {
            this.messagesShimmer.stopShimmer();
            this.inputNewMessage.setEnabled(true);
        }
        this.messagesShimmer.setVisibility(type ? View.VISIBLE : View.INVISIBLE);
        this.listMessages.setVisibility(type ? View.INVISIBLE : View.VISIBLE);
    }

    private void changeShimmerUsers(boolean type) {
        if(type) {
            this.usersShimmer.startShimmer();
        } else {
            this.usersShimmer.stopShimmer();
        }
        this.usersShimmer.setVisibility(type ? View.VISIBLE : View.INVISIBLE);
        this.listUsers.setVisibility(type ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onStop() {
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_LEFT);
        this.socketHelper.unsubscribe(this);
        this.socketHelper.sendData(new JSONObject(data));
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
                new DialogTextBox(GlobalConversationActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "gcj":
                        /*
                        Успешный джоин в чат и отправка данных на получение
                            юзеров и сообщений
                         */
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_GET_USERS_LIST);
                        this.socketHelper.sendData(new JSONObject(data));
                        data = new HashMap<>();
                        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_GET_MESSAGES);
                        this.socketHelper.sendData(new JSONObject(data));
                        break;
                    case "gcgul":
                        /*
                        Событие о юзерах чата. Здесь должны быть обработки ListView и адаптеров
                         */
                        usersInConversation = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.PLAYERS), ShortUser.class);
                        changesUsers(usersInConversation);
                        changeShimmerUsers(false);
                        break;
                    case "gcgm":
                        /*
                        Событие о сообщениях чата. Тоже самое что и выше.
                         */
                        Logs.info("GCGM RUNNING");
                        messagesInConversation = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.CONVERSATION_MESSAGE), Message.class);
                        changesMessages(messagesInConversation);
                        this.listMessages.post(()->{
                            this.listMessages.smoothScrollToPosition(messagesAdapter.getCount() - 1);
                            changeShimmerMessages(false);
                        });
                        break;
                    case "gcnm":
                        /*
                        Событие о нью месседж. Тоже самое что и выше.
                         */
                        Message newMessage = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.CONVERSATION_MESSAGE), Message.class);
                        messagesInConversation.add(newMessage);
                        changesMessages(messagesInConversation);
                        this.listMessages.smoothScrollToPosition(messagesAdapter.getCount() - 1);
                        break;
                    case "gcdm":
                        /*
                        Событие об удаление сообщения. Тоже самое что и выше.
                         */
                        String messageId = json.get(PacketDataKeys.MESSAGE_ID).asText();
                        Iterator<Message> iterator = messagesInConversation.iterator();
                        while (iterator.hasNext()) {
                            Message message = iterator.next();
                            if (message.messageId.equals(messageId)) {
                                iterator.remove();
                            }
                        }
                        changesMessages(messagesInConversation);
                        break;
                    case "gcpl":
                        /*
                        Событие если кто-то выйдет из чата. Тоже самое что и выше.
                         */
                        List<ShortUser> newUsersOfLeft = new ArrayList<>(usersInConversation);
                        String userIdLeft = json.get(PacketDataKeys.USER_ID).asText();
                        Iterator<ShortUser> it = newUsersOfLeft.iterator();
                        while (it.hasNext()) {
                            if (it.next().userId.equals(userIdLeft)) {
                                Logs.debug("user left from GC");
                                it.remove();
                            }
                        }
                        changesUsers(newUsersOfLeft);
                        break;
                    case "gcpj":
                        /*
                        Событие если кто-то войдет в чат. Тоже самое что и выше.
                         */
                        ShortUser user = (ShortUser) JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.USER), ShortUser.class);
                        usersInConversation.add(user);
                        changesUsers(usersInConversation);
                        break;
                }
            }
        });
    }

    public void changesMessages(List<Message> list) {
        Logs.debug("messages"+list.toString());
        globalMessages.clear();
        globalMessages.addAll(list);
        messagesAdapter.notifyDataSetChanged();
    }

    public void changesUsers(List<ShortUser> list) {
        globalUsers.clear();
        globalUsers.addAll(list);
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReceiveError(String str) {

    }
}
