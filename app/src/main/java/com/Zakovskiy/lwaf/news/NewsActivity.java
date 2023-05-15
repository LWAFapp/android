package com.Zakovskiy.lwaf.news;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.post.PostInList;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.adapters.PostsAdapter;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsActivity extends ABCActivity implements SocketHelper.SocketListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RecyclerView postsView;
    private List<PostInList> posts = new ArrayList<>();
    private PostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        this.postsView = findViewById(R.id.postsView);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.adapter = new PostsAdapter(this, getSupportFragmentManager(), posts, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.postsView.setLayoutManager(linearLayoutManager);
        this.postsView.setAdapter(adapter);
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POSTS_GET_LIST);
        data.put(PacketDataKeys.POST_AUTHOR, Application.lwafServerConfig.rootUserId);
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.adapter = new PostsAdapter(this, getSupportFragmentManager(), posts, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.postsView.setLayoutManager(linearLayoutManager);
        this.postsView.setAdapter(adapter);
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POSTS_GET_LIST);
        data.put(PacketDataKeys.POST_AUTHOR, Application.lwafServerConfig.rootUserId);
        this.socketHelper.sendData(new JSONObject(data));
    }

    public void changePosts(List<PostInList> list) {
        posts.clear();
        posts.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(NewsActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();

                switch (typeEvent) {
                    case "pgl": // исправил pl на pgl. возвращаемый тип всегда такой как и в запросе
                        List<PostInList> newPosts = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.POSTS_LIST), PostInList.class);
                        changePosts(newPosts);
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected() {
        Logs.info("SOCKET CONNECTED");
    }
    @Override
    public void onDisconnected() {}
    @Override
    public void onReceiveError(String str) {}


}
