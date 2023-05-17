package com.Zakovskiy.lwaf.news;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.enums.CommentReaction;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private String id;
    private int likes_count = 0;
    private int dislikes_count = 0;
    private boolean like_pressed = false;
    private boolean dis_pressed = false;
    private Drawable drawableLike;
    private Drawable drawableFilled;
    private List<PostComment> comments;
    private RecyclerView commentsView;
    private CommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        this.id = (String) getIntent().getSerializableExtra("id");
        findViewById(R.id.likesButton).setOnClickListener(this);
        findViewById(R.id.dislikesButton).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.adapter = new CommentsAdapter(this, getSupportFragmentManager(), comments);
        this.commentsView = (RecyclerView) findViewById(R.id.commentsView);
        this.commentsView.setAdapter(this.adapter);
        this.commentsView.setLayoutManager(linearLayoutManager);
        this.drawableLike = getResources().getDrawable(R.drawable.like);
        this.drawableFilled = getResources().getDrawable(R.drawable.ic_filled_like);
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
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
                            //Avatar
                            UserAvatar ua = (UserAvatar) findViewById(R.id.userAvatar2);
                            ua.setUser(post.author, this);
                            //Author
                            TextView pv = (TextView) findViewById(R.id.postAuthor);
                            pv.setText(post.author.nickname);
                            //Date
                            TextView pd = (TextView) findViewById(R.id.postDate);
                            pd.setText(TimeUtils.getDateAndTime(post.time * 1000));
                            //Title
                            TextView pt = (TextView) findViewById(R.id.postTitle);
                            pt.setText(post.title);
                            //Content
                            TextView pc = (TextView) findViewById(R.id.postContent);
                            pc.setText(post.content);
                            //Likes
                            TextView pl = (TextView) findViewById(R.id.postLikes);
                            pl.setText(String.valueOf(post.likes.size()));
                            likes_count = post.likes.size();
                            //Dislikes
                            TextView pdl = (TextView) findViewById(R.id.postDislikes);
                            pdl.setText(String.valueOf(post.dislikes.size()));
                            dislikes_count = post.dislikes.size();
                            for (PostReaction r : post.likes) {if (r.user.userId.equals(Application.lwafCurrentUser.userId)) {like_pressed = true;changeReaction(CommentReaction.LIKE);}}
                            for (PostReaction r : post.dislikes) {if (r.user.userId.equals(Application.lwafCurrentUser.userId)) {dis_pressed = true;changeReaction(CommentReaction.DISLIKE);}}
                            this.comments = (List<PostComment>) JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.POST_INFO).get(PacketDataKeys.POST_COMMENTS), PostComment.class);
                            this.adapter.notifyDataSetChanged();
                            break;
                        } else {
                            new DialogTextBox(PostActivity.this, Config.ERRORS.get(10101)).show();
                        }
                }
            }
        });
    }
    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceiveError(String str) {
        Logs.info("SOCKET ERROR");
    }

    protected void changeReaction(CommentReaction type) {
        if (type == CommentReaction.LIKE) {
            ImageView btnl = (ImageView) findViewById(R.id.likesButton);
            btnl.setImageDrawable(drawableFilled);
            ImageView btnd = (ImageView) findViewById(R.id.dislikesButton);
            btnd.setImageDrawable(drawableLike);
            dis_pressed = false;
            like_pressed = true;
        } else {
            ImageView btnl = (ImageView) findViewById(R.id.likesButton);
            btnl.setImageDrawable(drawableLike);
            ImageView btnd = (ImageView) findViewById(R.id.dislikesButton);
            btnd.setImageDrawable(drawableFilled);
            dis_pressed = true;
            like_pressed = false;
        }
    }

    protected void clearReaction(CommentReaction type) {
        if (type == CommentReaction.LIKE) {
            ImageView btnl = (ImageView) findViewById(R.id.likesButton);
            btnl.setImageDrawable(drawableFilled);
        } else {
            ImageView btnd = (ImageView) findViewById(R.id.dislikesButton);
            btnd.setImageDrawable(drawableLike);
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
            TextView ltext = (TextView) findViewById(R.id.postLikes);
            if (like_pressed) {
                likes_count--;
                ltext.setText(String.valueOf(likes_count));
                clearReaction(CommentReaction.LIKE);
                like_pressed=false;
            } else {
                likes_count++;
                ltext.setText(String.valueOf(likes_count));
                if (dis_pressed) {
                    TextView dtext = (TextView) findViewById(R.id.postDislikes);
                    dtext.setText(String.valueOf(dislikes_count));
                }
                changeReaction(CommentReaction.LIKE);
            }

        } else if (id == R.id.dislikesButton) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_SET_REACTION);
            data.put(PacketDataKeys.POST_ID, this.id);
            data.put(PacketDataKeys.TYPE, 0);
            this.socketHelper.sendData(new JSONObject(data));
            TextView dtext = (TextView) findViewById(R.id.postDislikes);
            if (dis_pressed) {
                dislikes_count--;
                clearReaction(CommentReaction.DISLIKE);
                dis_pressed=false;
            } else {
                dislikes_count++;
                dtext.setText(String.valueOf(dislikes_count));
                if (like_pressed) {
                    TextView ltext = (TextView) findViewById(R.id.postLikes);
                    ltext.setText(String.valueOf(likes_count));
                }
                changeReaction(CommentReaction.DISLIKE);
            }
        }
    }
}
