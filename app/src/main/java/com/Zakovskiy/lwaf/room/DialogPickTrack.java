package com.Zakovskiy.lwaf.room;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.VKApi;
import com.Zakovskiy.lwaf.api.models.ModelTrackResponse;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.LastTrack;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.adapters.TracksResponseAdapter;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.MD5Hash;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DialogPickTrack extends Dialog implements SocketHelper.SocketListener{

    private Context context;
    private List<ModelTrackResponse> tracks = new ArrayList<ModelTrackResponse>();
    private TracksResponseAdapter tracksResponseAdapter;
    private VKApi vkApi;
    private RecyclerView viewTracks;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Integer type = 0;
    private TextView tvOutOfTurnTrackTitle;
    private ABCActivity activity;
    private String searchText = "";
    private boolean isLoadingTracks = true;
    private boolean canLoadingTracks = true;

    public DialogPickTrack(@NonNull Context context, ABCActivity activity, Integer type) {
        /*
            type:
                0 - поставить трек в руму
                1 - поставить фаворитный трек в профиль
                2 - поставить фаворитный трек в профиль, если уже стоит
                3 - заменить трек в руме в очереди
                4 - поставить трек вне очереди
                5 - поставить трек в руму из недавних
         */
        super(context);
        this.context = context;
        this.activity = activity;
        this.type = type;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Config.VK_API).build();
        this.vkApi = retrofit.create(VKApi.class);
        this.socketHelper.subscribe(this);
    }

    public void changesTracks(List<ModelTrackResponse> list) {
        if(isLoadingTracks) {
            list.removeAll(Collections.singleton(null));
            isLoadingTracks = false;
        }
        Logs.debug(String.format("%s %s", list.size(), tracks.size()));
        if(list.size() == 0 || list.size() == tracks.size()) {
            canLoadingTracks = false;
        }
        tracks.clear();
        tracks.addAll(list);
        tracksResponseAdapter.notifyDataSetChanged();
    }

    private void setSelfTracks(int offset) {
        changeShimmerTracks(true);
        String sig = MD5Hash.md5(String.format("/method/audio.get?v=5.102&count=20&offset=%s&access_token=%s&owner_id=%s%s", offset, Application.lwafCurrentUser.vkontakteToken, Application.lwafCurrentUser.vkontakteId, Application.lwafCurrentUser.vkontakteSecret));
        vkApi.audioGet(offset, Application.lwafCurrentUser.vkontakteToken, Application.lwafCurrentUser.vkontakteId, sig).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JsonNode json = JsonUtils.convertJsonStringToJsonNode(response.body().string()).get("response");
                    List<ModelTrackResponse> newTracks = new ArrayList<>(tracks);
                    newTracks.addAll(JsonUtils.convertJsonNodeToList(json.get("items"), ModelTrackResponse.class));
                    changesTracks(newTracks);
                } catch (IOException | NullPointerException e) {
                    Logs.error(e.getMessage());
                    e.printStackTrace();
                    DialogPickTrack.this.dismiss();
                    new DialogTextBox(context, context.getString(R.string.error_load_vk_tracks)).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logs.error(t.getMessage());
                t.printStackTrace();
                DialogPickTrack.this.dismiss();
            }
        });
    }

    private void changeShimmerTracks(boolean type) {
        List<ModelTrackResponse> newTracks = new ArrayList<>(tracks);
        if(type) {
            newTracks.clear();
            for(int i = 0; i <= 10; i++) {
                newTracks.add(null);
            }
        } else {
            newTracks.removeAll(Collections.singleton(null));
        }
        changesTracks(newTracks);
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    private void getTracks() {
        List<ModelTrackResponse> offsetsTracks = new ArrayList<>(tracks);
        offsetsTracks.removeAll(Collections.singleton(null));
        int offset = offsetsTracks.size();
        this.isLoadingTracks = true;
        if(type == 5) {
            HashMap<String, Object> dataMessage = new HashMap<>();
            dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GET_LAST_TRACKS);
            dataMessage.put(PacketDataKeys.SIZE, 10);
            dataMessage.put(PacketDataKeys.OFFSET, offset);
            dataMessage.put(PacketDataKeys.USER_ID, Application.lwafCurrentUser.userId);
            this.socketHelper.sendData(new JSONObject(dataMessage));
        } else {
            if (searchText.isEmpty()) {
                setSelfTracks(offset);
                return;
            }
            String sig = MD5Hash.md5(String.format("/method/audio.search?v=5.102&count=20&offset=%s&access_token=%s&q=%s%s", offset, Application.lwafCurrentUser.vkontakteToken, searchText, Application.lwafCurrentUser.vkontakteSecret));
            vkApi.audioSearch(offset, Application.lwafCurrentUser.vkontakteToken, searchText, sig).enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try {
                        JsonNode json = JsonUtils.convertJsonStringToJsonNode(response.body().string()).get("response");
                        List<ModelTrackResponse> newTracks = new ArrayList<>(tracks);
                        newTracks.addAll(JsonUtils.convertJsonNodeToList(json.get("items"), ModelTrackResponse.class));
                        changesTracks(newTracks);
                    } catch (IOException | NullPointerException e) {
                        Logs.error(e.getMessage());
                        e.printStackTrace();
                        DialogPickTrack.this.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Logs.error(t.getMessage());
                    t.printStackTrace();
                    DialogPickTrack.this.dismiss();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_pick_track);
        setCancelable(true);
        this.viewTracks = findViewById(R.id.listTracks);
        this.tvOutOfTurnTrackTitle = findViewById(R.id.tvOutOfTurnTrackTitle);
        TextInputLayout searchTextLayout = findViewById(R.id.searchTrackLayout);
        if(type == 2) {
            MaterialButton btnRemoveFavoriteTrack = findViewById(R.id.btnRemoveFavoriteTrack);
            btnRemoveFavoriteTrack.setVisibility(View.VISIBLE);
            btnRemoveFavoriteTrack.setOnClickListener((v)->{
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.REMOVE_FAVORITE_TRACK);
                this.socketHelper.sendData(JsonUtils.convertObjectToJsonString(data));
                dismiss();
            });
        } else if (type == 4) {
            this.tvOutOfTurnTrackTitle.setVisibility(View.VISIBLE);
        } else if (type == 5) {
            searchTextLayout.setVisibility(View.GONE);
        }
        searchTextLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                String searchText = v.getText().toString();
                changeShimmerTracks(true);
                this.searchText = searchText;
                this.canLoadingTracks = true;
                getTracks();
                return true;
            }
            return false;
        });
        tracksResponseAdapter = new TracksResponseAdapter(this, this.socketHelper, this.tracks, this.type);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.viewTracks.setLayoutManager(linearLayoutManager);
        this.viewTracks.setAdapter(tracksResponseAdapter);
        this.viewTracks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                Logs.info(String.valueOf(totalItemCount));
                Logs.info(String.valueOf(lastVisibleItem));
                Logs.info(String.valueOf(isLoadingTracks));
                Logs.info(String.valueOf(canLoadingTracks));
                if (!isLoadingTracks && totalItemCount <= lastVisibleItem + 1 && canLoadingTracks) {
                    getTracks();
                }
            }
        });
        getTracks();
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(JsonNode json) {
        activity.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(context, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch(typeEvent) {
                    case PacketDataKeys.GET_LAST_TRACKS:
                        List<ModelTrackResponse> newTracks = new ArrayList<>(tracks);
                        JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.LAST_TRACKS), LastTrack.class).forEach((obj)->{
                            ModelTrackResponse newTrack = new ModelTrackResponse();
                            newTrack.ownerId = obj.ownerId;
                            newTrack.id = obj.trackId;
                            newTrack.artist = obj.artist;
                            newTrack.title = obj.title;
                            newTrack.duration = obj.duration;
                            ModelTrackResponse.Album album = new ModelTrackResponse.Album();
                            ModelTrackResponse.Album.Thumb thumb = new ModelTrackResponse.Album.Thumb();
                            thumb.photo_1200 = obj.icon;
                            album.thumb = thumb;
                            newTrack.album = album;
                            newTracks.add(newTrack);
                        });
                        changesTracks(newTracks);
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}
