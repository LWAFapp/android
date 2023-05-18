package com.Zakovskiy.lwaf.news.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.post.PostComment;
import com.Zakovskiy.lwaf.news.PostActivity;
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
    private PostComment thiscomment;
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
        this.thiscomment = comment;

        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        try {
            commentViewHolder.bind(comment);
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        private UserAvatar avatar;
        private TextView commentAuthor;
        private TextView commentContent;
        private TextView commentDate;
        private View item;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.commentAvatar);
            this.commentAuthor = itemView.findViewById(R.id.commentUsername);
            this.commentContent = itemView.findViewById(R.id.commentContent);
            this.commentDate = itemView.findViewById(R.id.commentDate);
            this.item = itemView;
        }
        public void bind(PostComment comment) throws IOException, XmlPullParserException {
            this.avatar.setUser(comment.user);
            this.commentAuthor.setText(comment.user.nickname);
            this.commentContent.setText(comment.content);
            this.commentDate.setText(TimeUtils.getTime(comment.timestamp));
            this.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ac.replyComment = thiscomment;
                    ac.setReply(thiscomment);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.comments == null ? 0 : this.comments.size();
    }
}
