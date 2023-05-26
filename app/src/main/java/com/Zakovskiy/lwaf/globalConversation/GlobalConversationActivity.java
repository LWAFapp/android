package com.Zakovskiy.lwaf.globalConversation;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.globalConversation.adapters.*;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
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
    public RecyclerView listMessages;
    private ShimmerFrameLayout messagesShimmer;
    private ShimmerFrameLayout usersShimmer;
    public List<Message> messagesInConversation;
    private List<ShortUser> usersInConversation;
    private MessagesAdapter messagesAdapter;
    private UsersAdapter usersAdapter;
    private List<Message> globalMessages = new ArrayList<>();
    private List<ShortUser> globalUsers = new ArrayList<>();
    private View currentView;
    private String replyId = "";
    private LinearLayout replyToLayout;
    public HashMap<Message, Integer> replyPositions = new HashMap<>();
    public List<String> ids = new ArrayList<>();

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
            Message swappedMessage = messagesInConversation.get(position);
            messagesInConversation.remove(position);
            messagesInConversation.add(position, swappedMessage);
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
        setContentView(R.layout.activity_global_conversation);
        this.replyToLayout = findViewById(R.id.replyTo);
        this.listUsers = findViewById(R.id.listViewUsers);
        this.listMessages = findViewById(R.id.listViewMessages);
        this.messagesShimmer = findViewById(R.id.shimmerMessages);
        this.usersShimmer = findViewById(R.id.shimmerViewUsers);
        this.inputNewMessage = findViewById(R.id.inputLayoutSendMessage);
        messagesAdapter = new MessagesAdapter(this, getSupportFragmentManager(), globalMessages, this);
        this.listMessages.setAdapter(messagesAdapter);
        this.listMessages.setLayoutManager(new LinearLayoutManager(this));
        this.listMessages.getRecycledViewPool().setMaxRecycledViews(0, 0);
        usersAdapter = new UsersAdapter(this, getSupportFragmentManager(), globalUsers);
        this.listUsers.setAdapter(usersAdapter);
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
        } else {
            this.messagesShimmer.stopShimmer();
        }
        this.inputNewMessage.setEnabled(!type);
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
                        messagesInConversation = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.CONVERSATION_MESSAGE), Message.class);
                        for (int i = 0; i < messagesInConversation.size()-1; i++) {
                            ids.add(messagesInConversation.get(i).messageId);
                            String date1 = TimeUtils.getTime(messagesInConversation.get(i).timeSend*1000, "dd");
                            String finalDay = TimeUtils.getTime(messagesInConversation.get(i+1).timeSend*1000, "dd");
                            int month1 = Integer.parseInt(TimeUtils.getTime(messagesInConversation.get(i).timeSend*1000, "MM"));
                            int finalMonth = Integer.parseInt(TimeUtils.getTime(messagesInConversation.get(i+1).timeSend*1000, "MM"));
                            String year = TimeUtils.getTime(System.currentTimeMillis(), "yyyy").equals(TimeUtils.getTime(messagesInConversation.get(i + 1).timeSend * 1000, "yyyy")) ? "" : TimeUtils.getTime(messagesInConversation.get(i + 1).timeSend * 1000, "yyyy");
                            //Logs.info(String.format("MESSAGE %s.%s %s.%s", date1, month1, finalDay, finalMonth));
                            if (Integer.parseInt(date1) < Integer.parseInt(finalDay) || month1 < finalMonth) {
                                Message msg = new Message();
                                msg.type = MessageType.MESSAGE_DATE;
                                msg.message = finalDay + " " + TimeUtils.convertToWords(this, finalMonth, true) + " " + year;
                                //Logs.info(TimeUtils.getTime(System.currentTimeMillis(), "dd:MM:yyyy"));
                                if (TimeUtils.getTime(System.currentTimeMillis(), "dd:MM:yyyy").equals(TimeUtils.getTime(messagesInConversation.get(i + 1).timeSend * 1000, "dd:MM:yyyy"))) {
                                    msg.message = this.getString(R.string.today);
                                }
                                messagesInConversation.add(i+1, msg);
                                ids.add(null);
                                i++;
                            }

                        }
                        changesMessages(messagesInConversation);

                        this.listMessages.postDelayed(()->{
                            this.listMessages.scrollToPosition(messagesAdapter.getCount() - 1);
                            changeShimmerMessages(false);
                        }, 200);
                        break;
                    case "gcnm":
                        /*
                        Событие о нью месседж. Тоже самое что и выше.
                         */
                        Message newMessage = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.CONVERSATION_MESSAGE), Message.class);
                        Message lastMessage = messagesInConversation.get(messagesInConversation.size()-1);
                        String lastMessageDate = TimeUtils.getTime(lastMessage.timeSend*1000, "dd");
                        String lastMessageMonth = TimeUtils.getTime(lastMessage.timeSend*1000, "MM");
                        String newMessageDate = TimeUtils.getTime(newMessage.timeSend*1000, "dd");
                        String newMessageMonth = TimeUtils.getTime(newMessage.timeSend*1000, "MM");
                        if (Integer.parseInt(lastMessageDate) < Integer.parseInt(newMessageDate) || Integer.parseInt(lastMessageMonth) < Integer.parseInt(newMessageMonth)) {
                            Message msg = new Message();
                            msg.type = MessageType.MESSAGE_DATE;
                            msg.message = newMessageDate + " " + TimeUtils.convertToWords(this, Integer.parseInt(newMessageMonth), true);
                            messagesInConversation.add(msg);
                        }
                        messagesInConversation.add(newMessage);
                        changesMessages(messagesInConversation);
                        this.listMessages.scrollToPosition(messagesAdapter.getCount() - 1);
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
        if(list.size() > 1) {
            globalMessages.clear();
            globalMessages.addAll(list);
            messagesAdapter.notifyDataSetChanged();
            return;
        }
        globalMessages.add(list.get(0));
        messagesAdapter.notifyItemInserted(globalMessages.size()-1);
    }

    public void changesUsers(List<ShortUser> list) {
        globalUsers.clear();
        globalUsers.addAll(list);
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReceiveError(String str) {}

}
