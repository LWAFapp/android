package com.Zakovskiy.lwaf.friends.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.globalConversation.adapters.MessagesAdapter;
import com.Zakovskiy.lwaf.models.Friend;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.enums.FriendType;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private List<Friend> friends;
    private Context context;
    private FragmentManager fragmentManager;

    public FriendsAdapter(Context context, FragmentManager fragmentManager, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getItemViewType(int position) {
        Friend messageGlobal = getItem(position);
        if (messageGlobal.friendType == FriendType.REMOVE_FRIEND || messageGlobal.friendType == FriendType.ACCEPT_REQUEST) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if(viewType == 0) {
            view = layoutInflater.inflate(R.layout.item_friend_in_list, parent, false);
            return new FriendViewHolder(view);
        }
        view = layoutInflater.inflate(R.layout.item_frienship_in_list, parent, false);
        return new FriendshipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Friend friendObject = getItem(position);
        if(friendObject.friendType == FriendType.REMOVE_FRIEND || friendObject.friendType == FriendType.ACCEPT_REQUEST) {
            FriendsAdapter.FriendViewHolder friendHolder = (FriendsAdapter.FriendViewHolder) holder;
            friendHolder.bind(friendObject);
        } else {
            FriendsAdapter.FriendshipViewHolder friendshipHolder = (FriendsAdapter.FriendshipViewHolder) holder;
            friendshipHolder.bind(friendObject);
        }
    }

    public Friend getItem(int position) {
        return this.friends.get(position);
    }

    @Override
    public int getItemCount() {
        return this.friends.size();
    }

    private class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView lastMessage;
        private TextView dateLastMessage;
        private UserAvatar avatar;
        private View itemView;


        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            avatar = itemView.findViewById(R.id.userAvatar);
            dateLastMessage = itemView.findViewById(R.id.tvTime);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            this.itemView = itemView;
        }

        public void bind(Friend friend) {
            this.username.setText(String.format("%s", friend.user.nickname));
            this.avatar.setUser(friend.user);
            this.avatar.setOnClickListener((view)->{
                ProfileDialogFragment.newInstance(context, friend.user.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.itemView.setOnClickListener((view)->{
                if(friend.friendType == FriendType.REMOVE_FRIEND) {
                    // Тут зробиш переход в приват чат
                }
            });
            if(friend.lastMessage.messageId != null && !friend.lastMessage.messageId.equals("")) {
                this.lastMessage.setText(Html.fromHtml(String.format("<b>%s</b> %s", friend.lastMessage.user.nickname, friend.lastMessage.message)));
                this.dateLastMessage.setVisibility(View.VISIBLE);
                this.dateLastMessage.setText(TimeUtils.getDateAndTime(friend.lastMessage.timeSend*1000));
            }
        }
    }

    private class FriendshipViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView ivAcceptFriendship;
        private ImageView ivCancelFriendship;
        private UserAvatar avatar;


        public FriendshipViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            avatar = itemView.findViewById(R.id.userAvatar);
            ivAcceptFriendship = itemView.findViewById(R.id.ivAcceptFriendship);
            ivCancelFriendship = itemView.findViewById(R.id.ivDecFriendship);
        }

        public void bind(Friend friend) {
            this.username.setText(String.format("%s", friend.user.nickname));
            this.avatar.setUser(friend.user);
            this.avatar.setOnClickListener((view)->{
                ProfileDialogFragment.newInstance(context, friend.user.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.ivAcceptFriendship.setOnClickListener((view)->{
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.FRIEND_ACCEPT_FRIENDSHIP);
                data.put(PacketDataKeys.FRIEND_ID, friend.friendId);
                socketHelper.sendData(new JSONObject(data));
            });
            this.ivCancelFriendship.setOnClickListener((view)->{
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.FRIEND_DELETE_FRIENDSHIP);
                data.put(PacketDataKeys.FRIEND_ID, friend.friendId);
                socketHelper.sendData(new JSONObject(data));
            });
        }
    }
}
