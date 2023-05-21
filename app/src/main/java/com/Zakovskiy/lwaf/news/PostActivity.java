package com.Zakovskiy.lwaf.news;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.post.Post;
import com.Zakovskiy.lwaf.models.post.PostComment;
import com.Zakovskiy.lwaf.models.post.PostReaction;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.adapters.CommentsAdapter;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private String id;
    public  PostComment replyComment;
    private UserAvatar userAvatar;
    private TextView tvPostAuthor;
    private TextView tvPostDate;
    private TextView tvPostTitle;
    private TextView tvPostContent;
    private Drawable drawableLike;
    private Drawable drawableFilled;
    private List<PostComment> comments = new ArrayList<>();
    private RecyclerView commentsView;
    private CommentsAdapter commentsAdapter;
    private TextView tvPostLikes;
    private TextView tvPostDislikes;
    private ImageView btnLikes;
    private ImageView btnDislikes;
    public List<PostComment> needAdapter = new ArrayList<PostComment>();
    public HashMap<String, List<String>> repliesForAdapter = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        this.id = (String) getIntent().getSerializableExtra("id");
        findViewById(R.id.likesButton).setOnClickListener(this);
        findViewById(R.id.dislikesButton).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.commentsAdapter = new CommentsAdapter(this, getSupportFragmentManager(), comments, this);
        this.commentsView = (RecyclerView) findViewById(R.id.commentsView);
        this.commentsView.setAdapter(this.commentsAdapter);
        this.commentsView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        this.commentsView.setLayoutManager(linearLayoutManager);
        this.tvPostDislikes = findViewById(R.id.postDislikes);
        this.tvPostLikes = findViewById(R.id.postLikes);
        this.btnLikes = findViewById(R.id.likesButton);
        this.btnDislikes = findViewById(R.id.dislikesButton);
        this.userAvatar = (UserAvatar) findViewById(R.id.userAvatar2);
        this.tvPostAuthor = (TextView) findViewById(R.id.postAuthor);
        this.tvPostDate = (TextView) findViewById(R.id.postDate);
        this.tvPostTitle = (TextView) findViewById(R.id.postTitle);
        this.tvPostContent = (TextView) findViewById(R.id.postContent);
        this.drawableLike = getResources().getDrawable(R.drawable.like);
        this.drawableFilled = getResources().getDrawable(R.drawable.ic_filled_like);

        TextInputLayout til = (TextInputLayout) findViewById(R.id.commentInput);
        til.setEndIconOnClickListener(v -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_CREATE_COMMENT);
            data.put(PacketDataKeys.POST_ID, id);
            data.put(PacketDataKeys.CONTENT, til.getEditText().getText().toString());
            data.put(PacketDataKeys.REPLY_COMMENT_ID, replyComment == null ? "" : replyComment.commentId);
            socketHelper.sendData(new JSONObject(data));
            til.getEditText().setText("");
        });
        ImageButton crc = (ImageButton) findViewById(R.id.cancelReplyComment);
        crc.setOnClickListener(v -> {
            replyComment = null;
            findViewById(R.id.replyComment).setVisibility(View.GONE);
        });

    }

    public void setReply(PostComment comment) {
        this.replyComment = comment;
        findViewById(R.id.replyComment).setVisibility(View.VISIBLE);
        TextView rca = (TextView) findViewById(R.id.replyCommentUsername);
        TextView rcc = (TextView) findViewById(R.id.replyCommentContent);
        rca.setText(comment.user.nickname);
        rcc.setText(comment.content);
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    public void changeComments(List<PostComment> list) {
        comments.clear();
        comments.addAll(list);
        commentsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_GET_INFO);
        data.put(PacketDataKeys.POST_ID, id);
        this.socketHelper.sendData(new JSONObject(data));
        super.onStart();

    }
    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(PostActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "pgi":

                        Post post = (Post) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.POST_INFO), Post.class);
                        if (post != null) {
                            this.userAvatar.setUser(post.author, this);
                            this.tvPostAuthor.setText(post.author.nickname);
                            this.tvPostDate.setText(TimeUtils.getDateAndTime(post.time * 1000));
                            this.tvPostTitle.setText(post.title);
                            this.tvPostContent.setText(post.content);
                            List<PostComment> resp_comms = post.comments;
                            changeComments(resp_comms);
                            changeReactions(post.likes, post.dislikes);
                            break;
                        } else {
                            new DialogTextBox(PostActivity.this, Config.ERRORS.get(10101)).show();
                        }
                        break;
                    case "psr":
                        List<PostReaction> newLikes = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.POST_LIKES), PostReaction.class);
                        List<PostReaction> newDislikes = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.POST_DISLIKES), PostReaction.class);
                        changeReactions(newLikes, newDislikes);
                        break;
                    case "pcc":
                        PostComment response = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.POST_COMMENTS), PostComment.class);
                        this.comments.add(response);
                        changeComments(comments);
                }
            }
        });
    }

    @Override
    public void onConnected() {}

    @Override
    public void onDisconnected() {}

    @Override
    public void onReceiveError(String str) {
        Logs.info("SOCKET ERROR");
    }

    protected void changeReactions(List<PostReaction> likes, List<PostReaction> dislikes) {
        tvPostLikes.setText(String.valueOf(likes.size()));
        tvPostDislikes.setText(String.valueOf(dislikes.size()));
        for (PostReaction r : likes) {
            if (r.user.userId.equals(Application.lwafCurrentUser.userId)) {
                this.btnDislikes.setImageDrawable(drawableLike);
                this.btnLikes.setImageDrawable(drawableFilled);
            }
        }
        for (PostReaction r : dislikes) {
            if (r.user.userId.equals(Application.lwafCurrentUser.userId)) {
                this.btnDislikes.setImageDrawable(drawableFilled);
                this.btnLikes.setImageDrawable(drawableLike);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Logs.info("PRESSED");
        if (id == R.id.likesButton) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_SET_REACTION);
            data.put(PacketDataKeys.POST_ID, this.id);
            data.put(PacketDataKeys.TYPE, 1);
            this.socketHelper.sendData(new JSONObject(data));

        } else if (id == R.id.dislikesButton) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_SET_REACTION);
            data.put(PacketDataKeys.POST_ID, this.id);
            data.put(PacketDataKeys.TYPE, 0);
            this.socketHelper.sendData(new JSONObject(data));
        }
    }
}
