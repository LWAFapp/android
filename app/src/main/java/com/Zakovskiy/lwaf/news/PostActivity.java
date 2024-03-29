package com.Zakovskiy.lwaf.news;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.menuDialog.MenuDialogFragment;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.post.Post;
import com.Zakovskiy.lwaf.models.post.PostComment;
import com.Zakovskiy.lwaf.models.post.PostReaction;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.adapters.CommentsAdapter;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.StringUtils;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Lists;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PostActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private String id;
    private String authorId;
    public  PostComment replyComment;
    private UserAvatar userAvatar;
    private TextView tvPostAuthor;
    private TextView tvPostDate;
    private TextView tvPostTitle;
    private TextView tvPostContent;
    private TextView tvCommentsTitle;
    private Drawable drawableLike;
    private Drawable drawableFilled;
    private List<PostComment> comments = new ArrayList<>();
    private RecyclerView commentsView;
    private CommentsAdapter commentsAdapter;
    private TextView tvPostLikes;
    private TextView tvPostDislikes;
    private ImageView btnLikes;
    private ImageView btnDislikes;
    private ImageView ivPreviewPost;
    private LinearLayout authorLayout;
    private NestedScrollView svPost;
    private ImageButton ibMenu;
    private UserAvatar uaOwner;
    private boolean canLoadComments = true;
    private boolean isLoadingComments = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        this.id = (String) getIntent().getSerializableExtra("id");
        findViewById(R.id.likesButton).setOnClickListener(this);
        findViewById(R.id.dislikesButton).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.commentsAdapter = new CommentsAdapter(this, getSupportFragmentManager(), comments, this);
        this.commentsView = findViewById(R.id.commentsView);
        this.commentsView.setAdapter(this.commentsAdapter);
        this.commentsView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        this.commentsView.setLayoutManager(linearLayoutManager);
        this.svPost = findViewById(R.id.svPost);
        this.svPost.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                Logs.info("x");
                if (svPost.getChildAt(0).getBottom()
                        <= (svPost.getHeight() + svPost.getScrollY())
                        && !isLoadingComments && canLoadComments) {
                    Logs.info("z");
                    getNewComments();
                }
            }
        });
        this.tvPostDislikes = findViewById(R.id.postDislikes);
        this.tvPostLikes = findViewById(R.id.postLikes);
        this.btnLikes = findViewById(R.id.likesButton);
        this.btnDislikes = findViewById(R.id.dislikesButton);
        this.userAvatar = findViewById(R.id.userAvatar2);
        this.tvPostAuthor = findViewById(R.id.postAuthor);
        this.tvPostDate = findViewById(R.id.postDate);
        this.tvPostTitle = findViewById(R.id.postTitle);
        this.tvPostContent = findViewById(R.id.postContent);
        this.authorLayout = findViewById(R.id.authorLayout);
        this.ibMenu = findViewById(R.id.menuButtons);
        this.uaOwner = findViewById(R.id.uaOwner);
        this.ivPreviewPost = findViewById(R.id.ivPreviewPost);
        this.tvCommentsTitle = findViewById(R.id.tvCommentsTitle);
        this.authorLayout.setOnClickListener((view)->{
            if(this.authorId.isEmpty()) return;
            ProfileDialogFragment.newInstance(this, this.authorId).show(getSupportFragmentManager(), "ProfileDialogFragment");
        });
        this.drawableLike = getResources().getDrawable(R.drawable.like);
        this.drawableFilled = getResources().getDrawable(R.drawable.ic_filled_like);
        TextInputLayout til = findViewById(R.id.commentInput);
        til.setEndIconOnClickListener(v -> {
            String text = til.getEditText().getText().toString();
            if (text.isEmpty()) {
                return;
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_CREATE_COMMENT);
            data.put(PacketDataKeys.POST_ID, id);
            data.put(PacketDataKeys.CONTENT, text);
            til.getEditText().setText("");
            data.put(PacketDataKeys.REPLY_COMMENT_ID, replyComment == null ? "" : replyComment.commentId);
            socketHelper.sendData(new JSONObject(data));
            findViewById(R.id.replyComment).setVisibility(View.GONE);
        });
        this.uaOwner.setUser(Application.lwafCurrentUser);
        findViewById(R.id.cancelReplyComment).setOnClickListener(v -> {
            replyComment = null;
            findViewById(R.id.replyComment).setVisibility(View.GONE);
        });
        this.ibMenu.setOnClickListener((view)->{
            List<MenuButton> btns = new ArrayList<>();
            btns.add(new MenuButton(getString(R.string.delete), "#E10F4A", (vb) -> {
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POSTS_DELETE);
                data.put(PacketDataKeys.POST_ID, id);
                socketHelper.sendData(new JSONObject(data));
            }));
            MenuDialogFragment.newInstance(this, btns).show(getSupportFragmentManager(), "MenuDialogFragment");
        });
    }

    public void setReply(PostComment comment) {
        this.replyComment = comment;
        findViewById(R.id.replyComment).setVisibility(View.VISIBLE);
        TextView rca = findViewById(R.id.replyCommentUsername);
        TextView rcc = findViewById(R.id.replyCommentContent);
        rca.setText(comment.user.nickname);
        rcc.setText(comment.content);
        TextInputEditText til = findViewById(R.id.CommentTIEText);
        String text = comment.user.nickname + ", " + til.getText().toString();
        til.setText(text);
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    public void changeComments(List<PostComment> list) {
        Logs.debug(String.format("%s %s", list.size(), comments.size()));
        if(list.size() == 0 || list.size() == (comments.size() - 1)) {
            canLoadComments = false;
        }
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
        getNewComments();
        super.onStart();

    }

    public void getNewComments() {
        comments.add(null);
        commentsAdapter.notifyItemInserted(comments.size() - 1);
        this.isLoadingComments = true;
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_GET_COMMENTS);
        data.put(PacketDataKeys.POST_ID, id);
        data.put(PacketDataKeys.OFFSET, (comments.size() - 1));
        data.put(PacketDataKeys.SIZE, 10);
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(PostActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case PacketDataKeys.POST_GET_INFO:
                        Post post = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.POST_INFO), Post.class);
                        if(post.previewId != null && !post.previewId.isEmpty()) {
                            ImageUtils.loadImage(this, String.format("%spreviews/%s", Application.lwafServerConfig.imgsPath, post.previewId), this.ivPreviewPost, false, true);
                            this.ivPreviewPost.setVisibility(View.VISIBLE);
                        }
                        this.userAvatar.setUser(post.author, this);
                        this.tvPostAuthor.setText(post.author.nickname);
                        this.tvPostDate.setText(TimeUtils.getDateAndTime(post.time * 1000));
                        this.tvPostTitle.setText(post.title);
                        String content = post.content.replace("\n", "<br>");
                        this.tvPostContent.setText(Html.fromHtml(content));
                        this.authorId = post.author.userId;
                        this.tvCommentsTitle.setText(String.format("%s (%s)", getString(R.string.comments_counts),  post.comments));

                        if(post.author.userId.equals(Application.lwafCurrentUser.userId)) {
                            this.ibMenu.setVisibility(View.VISIBLE);
                        }
                        changeReactions(post.likes, post.dislikes);
                        break;
                    case PacketDataKeys.POST_GET_COMMENTS:
                        List<PostComment> responseComments = new ArrayList<>(comments);
                        if(isLoadingComments) {
                            responseComments.removeAll(Collections.singleton(null));
                            isLoadingComments = false;
                        }
                        responseComments.addAll(JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.POST_COMMENTS), PostComment.class));
                        changeComments(responseComments);
                        break;
                    case PacketDataKeys.POST_SET_REACTION:
                        List<PostReaction> newLikes = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.POST_LIKES), PostReaction.class);
                        List<PostReaction> newDislikes = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.POST_DISLIKES), PostReaction.class);
                        changeReactions(newLikes, newDislikes);
                        break;
                    case PacketDataKeys.POST_CREATE_COMMENT: // post_create_comment
                        PostComment newComment = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.POST_COMMENTS), PostComment.class);
                        List<PostComment> newCommentsWithAdd = new ArrayList<>(this.comments);
                        if(!newComment.replyCommentId.isEmpty()) {
                            for (PostComment finderComment : newCommentsWithAdd) {
                                if (finderComment.commentId.equals(newComment.replyCommentId)) {
                                    finderComment.replyComments.add(newComment);
                                    break;
                                } else {
                                    for (PostComment finderReplyComment : finderComment.replyComments) {
                                        if (finderReplyComment.commentId.equals(newComment.replyCommentId)) {
                                            finderComment.replyComments.add(newComment);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            newCommentsWithAdd.add(0, newComment);
                        }
                        changeComments(newCommentsWithAdd);
                        break;
                    case PacketDataKeys.POSTS_REMOVE_COMMENT:
                        String removedCommentId = json.get(PacketDataKeys.COMMENT_ID).asText();
                        List<PostComment> newCommentsWithRemoved = new ArrayList<>(this.comments);
                        Iterator<PostComment> it = newCommentsWithRemoved.iterator();
                        while (it.hasNext()) {
                            PostComment finderComment = it.next();
                            if (finderComment.commentId.equals(removedCommentId)) {
                                it.remove();
                                break;
                            } else {
                                Iterator<PostComment> itReply = finderComment.replyComments.iterator();
                                while(itReply.hasNext()) {
                                    PostComment finderReplyComment = itReply.next();
                                    if(finderReplyComment.commentId.equals(removedCommentId)) {
                                        itReply.remove();
                                        break;
                                    }
                                }
                            }
                        }
                        changeComments(newCommentsWithRemoved);
                        break;
                    case PacketDataKeys.POSTS_DELETE:
                        finish();
                        break;
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
