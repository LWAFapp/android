package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.TimeUtils;

import java.net.HttpCookie;
import java.util.List;

//color gold D28726

public class MessagesAdapter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> messages;
    private FragmentManager fragmentManager;

    public MessagesAdapter(Context context,
                           FragmentManager fragmentManager, List<Message> messages) {
        super(context, R.layout.item_user_message, messages);
        this.context = context;
        this.messages = messages;
        this.fragmentManager = fragmentManager;
    }

    public int getCount() {
        return this.messages.size();
    }

    @Override
    public Message getItem(int position) {
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
        Message messageGlobal = getItem(position);
        Logs.info("MessagesAdapter RUNNING");
        View view = new View(this.context);
        if (messageGlobal.type == MessageType.TEXT) {
            view = inflater.inflate(R.layout.item_user_message, parent, false);
            TextView username = (TextView) view.findViewById(R.id.username_view);
            username.setText(messageGlobal.user.nickname);
            TextView message = (TextView) view.findViewById(R.id.plain_message_view);
            message.setText(messageGlobal.message);
            TextView date = (TextView) view.findViewById(R.id.message_datetime);
            date.setText((String) TimeUtils.getDateAndTime(messageGlobal.timeSend*1000)); //Moscow time
            if (messageGlobal.replyMessage != null) {
                TextView reply_username = (TextView) view.findViewById(R.id.replyUsernameMessage);
                reply_username.setText(messageGlobal.replyMessage.user.nickname);
                TextView reply_message = (TextView) view.findViewById(R.id.replyTextMessage);
                reply_message.setText(messageGlobal.replyMessage.message);
                view.findViewById(R.id.replyLayout).setVisibility(View.VISIBLE);
            }
        } else if (messageGlobal.type == MessageType.JOIN) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setTextColor(0xFF5FBD43);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.join))));

        } else if (messageGlobal.type == MessageType.LEFT) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setTextColor(0xFFC10005);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.left))));
        } else if (messageGlobal.type == MessageType.ADD_TRACK) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.add_track),
                    messageGlobal.message)));
        } else if (messageGlobal.type == MessageType.SET_REACTION_LIKE) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setTextColor(0xFF5FBD43);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.set),
                    this.context.getString(R.string.like),
                    this.context.getString(R.string.on_track))));
        } else if (messageGlobal.type == MessageType.SET_REACTION_DISLIKE) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setTextColor(0xFFC10005);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.set),
                    this.context.getString(R.string.dislike),
                    this.context.getString(R.string.on_track))));
        } else if (messageGlobal.type == MessageType.SET_REACTION_SUPER_LIKE) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setTextColor(0xFFD28726);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.set),
                    this.context.getString(R.string.superlike),
                    this.context.getString(R.string.on_track))));
        } else if (messageGlobal.type == MessageType.REPLACE_TRACK) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.replace),
                    this.context.getString(R.string.current_track))));
        } else if (messageGlobal.type == MessageType.DELETE_TRACK) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setText(Html.fromHtml(String.format("%s <b>%s</b> %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.delete),
                    this.context.getString(R.string.current_track))));
        }

        return view;
    }
}
