package com.Zakovskiy.lwaf.globalConversation;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.GlobalMessage;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GlobalConversationActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private TextInputLayout inputNewMessage;
    private ListView listUsers;
    private ListView listMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_JOIN);
        this.socketHelper.sendData(new JSONObject(data));
        setContentView(R.layout.activity_global_conversation);
        /*
        В папке ./globaConversation/adapters создашь 2 адаптера для юзеров и сообщений, посмотришь
            как я сделал это в списке рум (./dashboard/), и на основе этого сделаешь.
        Все события с сервера можешь получать в логах (отдел Logcat).
         */
        this.listUsers = (ListView) findViewById(R.id.listViewUsers);
        this.listMessages = (ListView) findViewById(R.id.listViewMessages);
        this.inputNewMessage = (TextInputLayout)findViewById(R.id.inputLayoutSendMessage);
        this.inputNewMessage.setEndIconOnClickListener(v -> {
            /*
            Отправка сообщения.
            Можешь сделать ещё возможность отвечать на сообщения (параметр REPLY_MESSAGE_ID).
             */
            HashMap<String, Object> dataMessage = new HashMap<>();
            dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_SEND_MESSAGE);
            dataMessage.put(PacketDataKeys.CONTENT, this.inputNewMessage.getEditText().getText().toString());
            dataMessage.put(PacketDataKeys.REPLY_MESSAGE_ID, "");
            this.socketHelper.sendData(new JSONObject(dataMessage));
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GLOBAL_CONVERSATION_LEFT);
        this.socketHelper.sendData(new JSONObject(data));
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
                        List<ShortUser> usersInConversation = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.PLAYERS), ShortUser.class);
                        break;
                    case "gcgm":
                        /*
                        Событие о сообщениях чата. Тоже самое что и выше.
                         */
                        List<GlobalMessage> messagesInConversation = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.CONVERSATION_MESSAGE), GlobalMessage.class);
                        break;
                    case "gcnm":
                        /*
                        Событие о нью месседж. Тоже самое что и выше.
                         */
                        GlobalMessage newMessage = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.CONVERSATION_MESSAGE), GlobalMessage.class);
                        break;
                    case "gcdm":
                        /*
                        Событие об удаление сообщения. Тоже самое что и выше.
                         */
                        String messageId = json.get(PacketDataKeys.MESSAGE_ID).asText();
                        break;
                    case "gcpl":
                        /*
                        Событие если кто-то выйдет из чата. Тоже самое что и выше.
                         */
                        String userId = json.get(PacketDataKeys.USER_ID).asText();
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}
