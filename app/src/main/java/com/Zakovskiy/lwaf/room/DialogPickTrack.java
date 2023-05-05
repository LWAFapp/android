package com.Zakovskiy.lwaf.room;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.VKApi;
import com.Zakovskiy.lwaf.api.models.ModelTrackResponse;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.adapters.TracksResponseAdapter;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.MD5Hash;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DialogPickTrack extends Dialog {

    private Context context;
    private List<ModelTrackResponse> tracks = new ArrayList<ModelTrackResponse>();
    private TracksResponseAdapter tracksResponseAdapter;
    private VKApi vkApi;
    private ShimmerFrameLayout shimmerTracks;
    private ListView viewTracks;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    public DialogPickTrack(@NonNull Context context) {
        super(context);
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Config.VK_API).build();
        this.vkApi = retrofit.create(VKApi.class);
    }

    public void changesTracks(List<ModelTrackResponse> list) {
        Logs.debug("tracks"+list.toString());
        tracks.clear();
        tracks.addAll(list);
        tracksResponseAdapter.notifyDataSetChanged();
    }

    private void setSelfTracks() {
        changeShimmerTracks(true);
        String sig = MD5Hash.md5(String.format("/method/audio.get?v=5.102&count=100&access_token=%s&owner_id=%s%s", Application.lwafCurrentUser.vkontakteToken, Application.lwafCurrentUser.vkontakteId, Application.lwafCurrentUser.vkontakteSecret));
        vkApi.audioGet(Application.lwafCurrentUser.vkontakteToken, Application.lwafCurrentUser.vkontakteId, sig).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JsonNode json = JsonUtils.convertJsonStringToJsonNode(response.body().string()).get("response");
                    List<ModelTrackResponse> tracks = JsonUtils.convertJsonNodeToList(json.get("items"), ModelTrackResponse.class);
                    changesTracks(tracks);
                    changeShimmerTracks(false);
                } catch (IOException e) {
                    Logs.error(e.getMessage());
                    e.printStackTrace();
                    DialogPickTrack.this.dismiss();
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
        if(type) {
            this.shimmerTracks.startShimmer();
        } else {
            this.shimmerTracks.stopShimmer();
        }
        this.shimmerTracks.setVisibility(type ? View.VISIBLE : View.INVISIBLE);
        this.viewTracks.setVisibility(type ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_pick_track);
        setCancelable(true);
        this.viewTracks = findViewById(R.id.listTracks);
        this.shimmerTracks = findViewById(R.id.shimmerListTracks);
        TextInputLayout searchTextLayout = findViewById(R.id.searchTrackLayout);
        searchTextLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                String searchText = v.getText().toString();
                if (searchText.isEmpty()) {
                    setSelfTracks();
                    return true;
                }
                changeShimmerTracks(true);
                String sig = MD5Hash.md5(String.format("/method/audio.search?v=5.102&count=50&access_token=%s&q=%s%s", Application.lwafCurrentUser.vkontakteToken, searchText, Application.lwafCurrentUser.vkontakteSecret));
                vkApi.audioSearch(Application.lwafCurrentUser.vkontakteToken, searchText, sig).enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            JsonNode json = JsonUtils.convertJsonStringToJsonNode(response.body().string()).get("response");
                            List<ModelTrackResponse> tracks = JsonUtils.convertJsonNodeToList(json.get("items"), ModelTrackResponse.class);
                            changesTracks(tracks);
                            changeShimmerTracks(false);
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
                return true;
            }
            return false;
        });
        tracksResponseAdapter = new TracksResponseAdapter(this, this.socketHelper, this.tracks);
        this.viewTracks.setAdapter(tracksResponseAdapter);
        setSelfTracks();
    }
}
