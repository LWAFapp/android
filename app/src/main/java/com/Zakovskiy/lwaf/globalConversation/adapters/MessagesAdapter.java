package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.TimeUtils;

import java.net.HttpCookie;
import java.util.List;

//color gold D28726


public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_VIEW_TYPE = 0;
    private static final int SYSTEM_VIEW_TYPE = 1;
    private Context context;
    private List<Message> messages;
    private FragmentManager fragmentManager;

    public MessagesAdapter(Context context,
                           FragmentManager fragmentManager, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.fragmentManager = fragmentManager;
    }

    public int getCount() {
        return this.messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message messageGlobal = messages.get(position);
        if (messageGlobal.type == MessageType.TEXT) {
            return TEXT_VIEW_TYPE;
        } else {
            return SYSTEM_VIEW_TYPE;
        }
    }

    public Message getItem(int position) {
        return this.messages.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == TEXT_VIEW_TYPE) {
            view = inflater.inflate(R.layout.item_user_message, parent, false);
            return new TextMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            return new SystemMessageViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message messageGlobal = getItem(position);
        if (messageGlobal.type == MessageType.TEXT) {
            TextMessageViewHolder textHolder = (TextMessageViewHolder) holder;
            textHolder.replyLayout.setVisibility(View.GONE);
            textHolder.bind(messageGlobal);
        } else if (messageGlobal.type == MessageType.JOIN) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setTextColor(0xFF5FBD43);
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.join))));

        } else if (messageGlobal.type == MessageType.LEFT) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setTextColor(Color.parseColor(Application.lwafServerConfig.colors.leftChat));
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.left))));
        } else if (messageGlobal.type == MessageType.ADD_TRACK) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.add_track),
                    messageGlobal.message)));
        } else if (messageGlobal.type == MessageType.SET_REACTION_LIKE) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setTextColor(Color.parseColor(Application.lwafServerConfig.colors.setLikeOnTrack));
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.set),
                    this.context.getString(R.string.set_like))));
        } else if (messageGlobal.type == MessageType.SET_REACTION_DISLIKE) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setTextColor(0xFFC10005);
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.set),
                    this.context.getString(R.string.set_dislike))));
        } else if (messageGlobal.type == MessageType.SET_REACTION_SUPER_LIKE) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setTextColor(Color.parseColor(Application.lwafServerConfig.colors.setSuperLikeOnTrack));
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.set),
                    this.context.getString(R.string.set_superlike))));
        } else if (messageGlobal.type == MessageType.REPLACE_TRACK) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.replace),
                    this.context.getString(R.string.current_track))));
        } else if (messageGlobal.type == MessageType.DELETE_TRACK) {
            SystemMessageViewHolder systemHolder = (SystemMessageViewHolder) holder;
            systemHolder.message.setText(Html.fromHtml(String.format("<b>%s</b> %s %s",
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.delete),
                    this.context.getString(R.string.current_track))));
        }
    }

    private class TextMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView message;
        private TextView date;
        private TextView replyUsername;
        private TextView replyMessage;
        private LinearLayout replyLayout;

        public TextMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_view);
            message = itemView.findViewById(R.id.plain_message_view);
            date = itemView.findViewById(R.id.message_datetime);
            replyUsername = itemView.findViewById(R.id.replyUsernameMessage);
            replyMessage = itemView.findViewById(R.id.replyTextMessage);
            replyLayout = itemView.findViewById(R.id.replyLayout);
        }

        public void bind(Message message) {
            username.setText(message.user.nickname);
            this.message.setText(message.message);
            date.setText(TimeUtils.getDateAndTime(message.timeSend * 1000));
            if (message.replyMessage != null) {
                replyUsername.setText(message.replyMessage.user.nickname);
                replyMessage.setText(message.replyMessage.message);
                replyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class SystemMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView message;

        SystemMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_view);
        }
    }
}
