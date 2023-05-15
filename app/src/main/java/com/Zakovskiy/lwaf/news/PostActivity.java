package com.Zakovskiy.lwaf.news;

import android.os.Bundle;
import android.widget.TextView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.models.post.Post;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class PostActivity extends ABCActivity implements SocketHelper.SocketListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        this.id = (String) getIntent().getSerializableExtra("id");

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
                            ua.setUser(post.author);
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
                            pl.setText(String.valueOf(post.postLikes.size()));
                            //Dislikes
                            TextView pdl = (TextView) findViewById(R.id.postDislikes);
                            pdl.setText(String.valueOf(post.postDislikes.size()));
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

    }
}
