package com.Zakovskiy.lwaf.ratings;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.RatingUser;
import com.Zakovskiy.lwaf.models.enums.RatingsType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.ratings.adapters.RatingsAdapter;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RatingsActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private MaterialButton btnMenuSuperLikes;
    private MaterialButton btnMenuCoins;
    private List<RatingUser> coinsRatingUsersList;
    private List<RatingUser> superLikesRatingUsersList;
    private RecyclerView rvRatingUsers;
    private List<RatingUser> ratingUsersList = new ArrayList<>();
    private RatingsAdapter ratingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        this.btnMenuCoins = findViewById(R.id.btnMenuCoins);
        this.btnMenuSuperLikes = findViewById(R.id.btnMenuSuperLikes);
        this.rvRatingUsers = findViewById(R.id.rvRatingsUsers);
        this.btnMenuSuperLikes.setOnClickListener(this);
        this.btnMenuCoins.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.ratingsAdapter = new RatingsAdapter(this, getSupportFragmentManager(), ratingUsersList);
        this.rvRatingUsers.setLayoutManager(linearLayoutManager);
        this.rvRatingUsers.setAdapter(this.ratingsAdapter);
        this.rvRatingUsers.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GET_RATING);
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    protected void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.socketHelper.unsubscribe(this);
        super.onDestroy();
    }

    private void setMenu(RatingsType type) {

        if(type == RatingsType.COINS) {
            this.btnMenuCoins.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            this.btnMenuCoins.setTextColor(getResources().getColor(R.color.black_primary));
            this.btnMenuSuperLikes.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.transparent)));
            this.btnMenuSuperLikes.setTextColor(getResources().getColor(R.color.white));
            this.changeRatingsUser(coinsRatingUsersList);
        } else {
            this.btnMenuCoins.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.transparent)));
            this.btnMenuCoins.setTextColor(getResources().getColor(R.color.white));
            this.btnMenuSuperLikes.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            this.btnMenuSuperLikes.setTextColor(getResources().getColor(R.color.black_primary));
            this.changeRatingsUser(superLikesRatingUsersList);
        }
    }

    private void changeRatingsUser(List<RatingUser> ratingUsers) {
        this.ratingUsersList.clear();
        this.ratingUsersList.addAll(ratingUsers);
        this.ratingsAdapter.notifyDataSetChanged();
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
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(RatingsActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case PacketDataKeys.GET_RATING:
                        List<RatingUser> newCoinsRatingUsers = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.BALANCE), RatingUser.class);
                        List<RatingUser> newSuperLikesRatingUsers = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.SUPER_LIKES), RatingUser.class);
                        this.coinsRatingUsersList = new ArrayList<>(newCoinsRatingUsers);
                        this.superLikesRatingUsersList = new ArrayList<>(newSuperLikesRatingUsers);
                        setMenu(RatingsType.SUPER_LIKES);
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnMenuCoins) {
            setMenu(RatingsType.COINS);
        } else if(id == R.id.btnMenuSuperLikes) {
            setMenu(RatingsType.SUPER_LIKES);
        }
    }
}
