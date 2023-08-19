package com.Zakovskiy.lwaf.createPost;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.LWAFApi;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.PostActivity;
import com.Zakovskiy.lwaf.utils.FileUtils;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatePostActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {
    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private EditText contentField;
    private EditText titleField;
    private boolean isPreviewMode = false;
    private String oldContent = "";
    private ImageView ivPreviewPost;
    private String previewId = "";

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
        this.ivPreviewPost = findViewById(R.id.ivPreviewPost);
        this.ivPreviewPost.setOnClickListener(this);
        LinearLayout tagsLayout = findViewById(R.id.createPostTagsLayout);
        AppCompatButton submitBtn = findViewById(R.id.createPostSubmit);
        submitBtn.setOnClickListener(v -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.POST_CREATE);
            data.put(PacketDataKeys.POST_CONTENT, contentField.getText().toString());
            data.put(PacketDataKeys.POST_TITLE, titleField.getText().toString());
            data.put(PacketDataKeys.PREVIEW_ID, this.previewId);
            this.socketHelper.sendData(new JSONObject(data));
        });
        findViewById(R.id.createPostPreview).setOnClickListener(v -> {
            isPreviewMode = !isPreviewMode;
            contentField.setEnabled(!isPreviewMode);
            titleField.setEnabled(!isPreviewMode);
            tagsLayout.setVisibility(isPreviewMode ? View.GONE : View.VISIBLE);
            submitBtn.setVisibility(isPreviewMode ? View.GONE : View.VISIBLE);

            if (isPreviewMode) {
                oldContent = contentField.getText().toString();
                contentField.setText(Html.fromHtml(contentField.getText().toString().replace("\n", "<br>")));
            } else {
                contentField.setText(oldContent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logs.info("zxc");
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver contentResolver = this.getContentResolver();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Application.lwafServerConfig.restHost).build();
            LWAFApi lwafApi = retrofit.create(LWAFApi.class);
            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(uri);
            } catch (FileNotFoundException e) {
                new DialogTextBox(this, getString(R.string.error_upload_photo)).show();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            RequestBody requestBody = null;
            try {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), FileUtils.getBytes(inputStream));
            } catch (IOException e) {
                new DialogTextBox(this, getString(R.string.error_upload_photo)).show();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", FileUtils.getFileName(this, uri), requestBody);
            lwafApi.uploadPreview(Application.lwafCurrentUser.accessToken, filePart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String responseString = null;
                    try {
                        responseString = response.body().string();
                        Logs.debug(responseString);
                        JsonNode json = JsonUtils.convertJsonStringToJsonNode(responseString);
                        if (json.has("error")) {
                            new DialogTextBox(CreatePostActivity.this, getString(R.string.error_upload_photo)).show();
                        } else {
                            previewId = json.get(PacketDataKeys.PREVIEW_ID).asText();
                            ivPreviewPost.setImageURI(uri);
                        }
                    } catch (IOException e) {
                        new DialogTextBox(CreatePostActivity.this, getString(R.string.error_upload_photo)).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Logs.debug(t.getMessage().toString());
                }
            });
        }
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

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if(_id == R.id.ivPreviewPost) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            chooseFile = Intent.createChooser(chooseFile, getString(R.string.pick_avatar));
            startActivityForResult(chooseFile, 100);
        }
    }
}
