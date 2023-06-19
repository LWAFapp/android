package com.Zakovskiy.lwaf.friends;

import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.friends.adapters.FriendsAdapter;
import com.Zakovskiy.lwaf.globalConversation.GlobalConversationActivity;
import com.Zakovskiy.lwaf.models.Friend;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendList = new ArrayList<>();
    private TextInputLayout tilSearchUsers;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        RecyclerView rvFriend = findViewById(R.id.rvFriends);
        tilSearchUsers = findViewById(R.id.tilSearchUsers);
        tvTitle = findViewById(R.id.textTitle);
        this.friendsAdapter = new FriendsAdapter(this, getSupportFragmentManager(), friendList);
        rvFriend.setAdapter(this.friendsAdapter);
        rvFriend.getRecycledViewPool().setMaxRecycledViews(0, 0);
        rvFriend.setLayoutManager(new LinearLayoutManager(this));
        tilSearchUsers.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
                String searchText = v.getText().toString();
                if(searchText.isEmpty()) {
                    callFriendList();
                    return true;
                }
                tvTitle.setText(getString(R.string.searching));
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.SEARCH_USER);
                data.put(PacketDataKeys.CONTENT, searchText);
                this.socketHelper.sendData(new JSONObject(data));
            }
            return true;
        });
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    private void callFriendList() {
        tvTitle.setText(getString(R.string.friends));
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.FRIEND_LIST);
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    public void onStart() {
        this.socketHelper.subscribe(this);
        super.onStart();
        callFriendList();
    }

    @Override
    public void onConnected() {}

    @Override
    public void onDisconnected() {}

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(FriendsActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "fli":
                        List<Friend> friendList = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.FRIEND_LIST), Friend.class);
                        changesFriends(friendList);
                        break;
                    case "fdf":
                    case "faf":
                        callFriendList();
                        break;
                    case "su":
                        List<ShortUser> searchedUsers = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.USERS), ShortUser.class);
                        List<Friend> tempFriends = new ArrayList<>();
                        searchedUsers.forEach((searchedUser)->{
                            Friend tempFriend = new Friend();
                            tempFriend.user = searchedUser;
                            tempFriends.add(tempFriend);
                        });
                        changesFriends(tempFriends);
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {}

    public void changesFriends(List<Friend> list) {
        friendList.clear();
        friendList.addAll(list);
        friendsAdapter.notifyDataSetChanged();
    }

}
