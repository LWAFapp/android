package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.Bubble;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.enums.BubbleType;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//color gold D28726


public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_VIEW_TYPE = 0;
    private static final int SYSTEM_VIEW_TYPE = 1;
    private Context context;
    private List<Message> messages;
    private FragmentManager fragmentManager;
    private boolean theReceiver = false;

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
            this.theReceiver = messageGlobal.user.userId.equals(Application.lwafCurrentUser.userId);
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
            if (this.theReceiver) {
                view = inflater.inflate(R.layout.item_user_message_receiver, parent, false);
            } else {
                view = inflater.inflate(R.layout.item_user_message_sender, parent, false);
            }
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
            try {
                textHolder.bind(messageGlobal);
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException(e);
            }
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

    public boolean isSwipeable(int position) {
        Message message = getItem(position);
        return message.type == MessageType.TEXT;
    }

    private class TextMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView username = null;
        private TextView message;
        private TextView date;
        private TextView replyUsername;
        private TextView replyMessage;
        private LinearLayout replyLayout;
        private LinearLayout messageBubble;
        private UserAvatar avatar;


        public TextMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBubble = itemView.findViewById(R.id.messageBubble);
            if (!theReceiver)
                username = itemView.findViewById(R.id.username_view);
            avatar = itemView.findViewById(R.id.circleImageView);
            message = itemView.findViewById(R.id.plain_message_view);
            date = itemView.findViewById(R.id.message_datetime);
            replyUsername = itemView.findViewById(R.id.replyUsernameMessage);
            replyMessage = itemView.findViewById(R.id.replyTextMessage);
            replyLayout = itemView.findViewById(R.id.replyLayout);
        }

        public void bind(Message message) throws IOException, XmlPullParserException {
            Bubble bubble = message.bubble;
            if (message.user.userId.equals(Application.lwafCurrentUser.userId)) {
                messageBubble.setBackgroundResource(R.drawable.message_bg_receiver);
                String[] resourceReceive = bubble.getSourceReceive();
                if (bubble.bubbleType == BubbleType.SOLID) {
                    username.setTextColor(Color.parseColor(resourceReceive[0]));
                    messageBubble.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(resourceReceive[1])));
                }
            } else {
                String[] resourceSend = bubble.getSourceSend();
                username.setText(message.user.nickname);
                if (bubble.bubbleType == BubbleType.SOLID) {
                    username.setTextColor(Color.parseColor(resourceSend[0]));
                    messageBubble.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(resourceSend[1])));
                }
            }
            avatar.setUser(message.user);
            avatar.setOnClickListener((v)->{
                ProfileDialogFragment.newInstance(context, message.user.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.message.setText(message.message);
            date.setText(TimeUtils.getTime(message.timeSend * 1000));
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
