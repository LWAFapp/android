package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.GlobalMessage;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.utils.Logs;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter<GlobalMessage> {

    private Context context;
    private List<GlobalMessage> messages;

    public MessagesAdapter(Context context, List<GlobalMessage> messages) {
        super(context, R.layout.item_user_message);
        this.context = context;
        this.messages = messages;
    }

    public int getCount() {
        return this.messages.size();
    }

    @Override
    public GlobalMessage getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_user_message, parent, false);
        GlobalMessage globalMessage = getItem(position);
        Logs.info("MessagesAdapter RUNNING");
        if (globalMessage.type == MessageType.TEXT) {
            TextView username = (TextView) view.findViewById(R.id.username_view);
            username.setText(globalMessage.user.nickname);
            TextView message = (TextView) view.findViewById(R.id.plain_message_view);
            message.setText(globalMessage.message);

        }

        return view;
    }
}
