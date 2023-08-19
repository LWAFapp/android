package com.Zakovskiy.lwaf.news.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.globalConversation.GlobalConversationActivity;
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.menuDialog.MenuDialogFragment;
import com.Zakovskiy.lwaf.models.post.PostComment;
import com.Zakovskiy.lwaf.models.post.PostReaction;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.PostActivity;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.profileDialog.adapters.LastTracksAdapter;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private FragmentManager fragmentManager;
    private List<PostComment> comments = new ArrayList<>();
    private PostActivity ac;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    public CommentsAdapter(Context context, FragmentManager fragmentManager, List<PostComment> comments, PostActivity ac) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.comments = comments;
        this.ac = ac;
    }

    @Override
    public int getItemViewType(int position) {
        return comments.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_comment_post, parent, false);
            return new CommentViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_shimmer_comment_post, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CommentViewHolder) {
            PostComment comment = this.comments.get(position);

            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            try {
                commentViewHolder.bind(comment, position);
            } catch (IOException | XmlPullParserException e) {
                throw new RuntimeException(e);
            }
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        private UserAvatar avatar;
        private TextView commentAuthor;
        private TextView commentContent;
        private TextView commentDate;
        private TextView clickableReply;
        private LinearLayout clickableShowHide;
        private TextView labelShowReplies;
        private ImageView decorativeArrow;
        private View item;
        private RecyclerView lvReplyComments;
        private CommentsAdapter replyCommentsAdapter;
        private List<PostComment> replyComments = new ArrayList<>();

        public CommentViewHolder(View itemView) {
            super(itemView);
            Logs.info("v");
            this.avatar = itemView.findViewById(R.id.commentAvatar);
            this.commentAuthor = itemView.findViewById(R.id.commentUsername);
            this.commentContent = itemView.findViewById(R.id.commentContent);
            this.commentDate = itemView.findViewById(R.id.commentDate);
            this.clickableReply = itemView.findViewById(R.id.commentReply);
            this.clickableShowHide = itemView.findViewById(R.id.clickableShowHide);
            this.labelShowReplies = itemView.findViewById(R.id.labelShowReplies);
            this.decorativeArrow = itemView.findViewById(R.id.decorativeArrow);
            this.item = itemView;
            this.lvReplyComments = itemView.findViewById(R.id.replyerComments);
            this.replyCommentsAdapter = new CommentsAdapter(context, fragmentManager, replyComments, ac);
            this.lvReplyComments.setAdapter(this.replyCommentsAdapter);
            this.lvReplyComments.setLayoutManager(new LinearLayoutManager(context));
        }

        public void changeReplys(List<PostComment> list, int position) {
            clickableShowHide.setVisibility(View.VISIBLE);
            replyComments.clear();
            replyComments.addAll(list);
            replyCommentsAdapter.notifyDataSetChanged();
            //notifyItemChanged(position);
        }

        public void bind(PostComment comment, int position) throws IOException, XmlPullParserException {
            this.avatar.setUser(comment.user);
            this.item.setOnClickListener(view -> {
                List<MenuButton> btns = new ArrayList<>();
                btns.add(new MenuButton(ac.getString(R.string.reply), "#FFFFFF", (vb) -> {
                    ac.setReply(comment);
                }));
                btns.add(new MenuButton(ac.getString(R.string.copy), "#FFFFFF", (vb) -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied message", comment.content);
                    clipboard.setPrimaryClip(clip);
                }));
                btns.add(new MenuButton(ac.getString(R.string.gotoprofile), "#FFFFFF", (vb) -> {
                    ProfileDialogFragment.newInstance(context, comment.user.userId).show(fragmentManager, "ProfileDialogFragment");
                }));
                if(comment.user.userId.equals(Application.lwafCurrentUser.userId) || Application.lwafCurrentUser.isAdmin()) {
                    btns.add(new MenuButton(ac.getString(R.string.delete), "#e10f4a", (vb) -> {
                        HashMap<String, Object> dataMessage = new HashMap<>();
                        dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POSTS_REMOVE_COMMENT);
                        dataMessage.put(PacketDataKeys.COMMENT_ID, comment.commentId);
                        socketHelper.sendData(new JSONObject(dataMessage));
                    }));
                }
                MenuDialogFragment.newInstance(context, btns).show(ac.getSupportFragmentManager(), "MenuButtons");
            });
            this.avatar.setOnClickListener(view -> {
                ProfileDialogFragment.newInstance(context, comment.user.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.commentAuthor.setText(comment.user.nickname);
            this.commentContent.setText(comment.content);
            this.commentDate.setText(TimeUtils.getDateAndTime(comment.timestamp));
            this.clickableReply.setOnClickListener(v -> {
                ac.setReply(comment);
            });
            labelShowReplies.setText(String.format("%s (%s)", context.getString(R.string.show_replies), comment.replyComments.size()));
            this.clickableShowHide.setOnClickListener(v -> {
                if (lvReplyComments.getVisibility() == View.VISIBLE) {
                    lvReplyComments.setVisibility(View.GONE);
                    labelShowReplies.setText(String.format("%s (%s)", context.getString(R.string.show_replies), comment.replyComments.size()));
                    decorativeArrow.setRotationX(0);
                }
                else {
                    lvReplyComments.setVisibility(View.VISIBLE);
                    labelShowReplies.setText(String.format("%s (%s)", context.getString(R.string.hide_replies), comment.replyComments.size()));
                    decorativeArrow.setRotationX(180);
                }
            });
            if (!comment.replyComments.isEmpty()) {
                changeReplys(comment.replyComments, position);
            }
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}