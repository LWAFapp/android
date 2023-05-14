package com.Zakovskiy.lwaf.news;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.globalConversation.GlobalConversationActivity;
import com.Zakovskiy.lwaf.models.ShortPost;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.adapters.PostsAdapter;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class NewsActivity extends ABCActivity implements SocketHelper.SocketListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RecyclerView postsView;
    private List<ShortPost> posts;
    private PostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        this.postsView = findViewById(R.id.postsView);
        this.adapter = new PostsAdapter(this, getSupportFragmentManager(), posts);
        this.postsView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POSTS_GET_LIST);
        this.socketHelper.sendData(new JSONObject(data));
    }

    public void changePosts(List<ShortPost> list) {
        posts.clear();
        posts.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            Logs.info("JSON "+json.asText());
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(NewsActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();

                switch (typeEvent) {
                    case "pl":
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
