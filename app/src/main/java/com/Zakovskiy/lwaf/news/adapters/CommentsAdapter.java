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
import com.Zakovskiy.lwaf.models.post.PostComment;
import com.Zakovskiy.lwaf.models.post.PostReaction;
import com.Zakovskiy.lwaf.news.PostActivity;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private FragmentManager fragmentManager;
    private List<PostComment> comments = new ArrayList<>();
    private PostActivity ac;
    private CommentsAdapter adapter;
    public CommentsAdapter(Context context, FragmentManager fragmentManager, List<PostComment> comments, PostActivity ac) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.comments = comments;
        this.ac = ac;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_comment_post, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostComment comment = this.comments.get(position);

        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        try {
            commentViewHolder.bind(comment, position);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
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
        private LinearLayout clickableReply;
        private LinearLayout clickableShowHide;
        private TextView labelShowReplies;
        private ImageView decorativeArrow;
        private View item;
        private RecyclerView lvReplyComments;
        private CommentsAdapter replyCommentsAdapter;
        private List<PostComment> replyComments = new ArrayList<>();

        public CommentViewHolder(View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.commentAvatar);
            this.commentAuthor = itemView.findViewById(R.id.commentUsername);
            this.commentContent = itemView.findViewById(R.id.commentContent);
            this.commentDate = itemView.findViewById(R.id.commentDate);
            this.clickableReply = itemView.findViewById(R.id.clickableReply);
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
            this.commentAuthor.setText(comment.user.nickname);
            this.commentContent.setText(comment.content);
            this.commentDate.setText(TimeUtils.getDateAndTime(comment.timestamp * 1000));
            this.clickableReply.setOnClickListener(v -> {
                ac.setReply(comment);
            });
            this.clickableShowHide.setOnClickListener(v -> {
                if (lvReplyComments.getVisibility() == View.VISIBLE) {
                    lvReplyComments.setVisibility(View.GONE);
                    labelShowReplies.setText(R.string.show_replies);
                    decorativeArrow.setRotationX(0);
                }
                else {
                    lvReplyComments.setVisibility(View.VISIBLE);
                    labelShowReplies.setText(R.string.hide_replies);
                    decorativeArrow.setRotationX(180);
                }
            });
            if (!comment.replyComments.isEmpty()) {
                Logs.info("zxc");
                changeReplys(comment.replyComments, position);

            }
        }
    }
}