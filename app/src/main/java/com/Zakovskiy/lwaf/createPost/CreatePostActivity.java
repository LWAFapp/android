package com.Zakovskiy.lwaf.createPost;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.PostActivity;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.HashMap;

public class CreatePostActivity extends ABCActivity implements SocketHelper.SocketListener {
    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private EditText contentField;
    private EditText titleField;
    private boolean isPreviewMode = false;
    private String oldContent = "";

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_create_post);
        contentField = findViewById(R.id.createPostContent);
        titleField = findViewById(R.id.createPostTitle);
        findViewById(R.id.createPostBold).setOnClickListener(v -> {setTag("b");});
        findViewById(R.id.createPostItalic).setOnClickListener(v -> {setTag("i");});
        findViewById(R.id.createPostStrike).setOnClickListener(v -> {setTag("strike");});
        findViewById(R.id.createPostUnderline).setOnClickListener(v -> {setTag("u");});
        LinearLayout tagsLayout = findViewById(R.id.createPostTagsLayout);
        AppCompatButton submitBtn = findViewById(R.id.createPostSubmit);
        submitBtn.setOnClickListener(v -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_CREATE);
            data.put(PacketDataKeys.POST_CONTENT, contentField.getText().toString());
            data.put(PacketDataKeys.POST_TITLE, titleField.getText().toString());
            this.socketHelper.sendData(new JSONObject(data));
        });
        findViewById(R.id.createPostPreview).setOnClickListener(v -> {
            isPreviewMode = !isPreviewMode;
            contentField.setEnabled(!isPreviewMode);
            titleField.setEnabled(!isPreviewMode);
            tagsLayout.setVisibility(isPreviewMode ? View.GONE : View.VISIBLE);
            submitBtn.setVisibility(isPreviewMode ? View.GONE : View.VISIBLE);

            Logs.info("CONTENT "+contentField.getText().toString());
            if (isPreviewMode) {
                oldContent = contentField.getText().toString();
                contentField.setText(Html.fromHtml(contentField.getText().toString().replace("\n", "<br>")));
            } else {
                contentField.setText(oldContent);
            }
        });
    }

    @Override
    protected void onStart() {
        this.socketHelper.subscribe(this);
        super.onStart();
    }

    private void setTag(String tag) {
        int start = contentField.getSelectionStart();
        int end = contentField.getSelectionEnd();
        String content = contentField.getText().toString();
        content = content.substring(0, start) + String.format("<%s>", tag) +
                content.substring(start, end) + String.format("</%s>", tag) +
                content.substring(end, content.length());
        contentField.setText(content);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (!json.has(PacketDataKeys.ERROR)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "pc":
                        String id = json.get(PacketDataKeys.POST_ID).asText();
                        Bundle b = new Bundle();
                        b.putSerializable("id", id);
                        Intent intent = new Intent(this, PostActivity.class);
                        intent.putExtras(b);
                        this.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}
