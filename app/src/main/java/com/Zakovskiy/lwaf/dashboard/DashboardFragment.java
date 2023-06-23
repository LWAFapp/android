package com.Zakovskiy.lwaf.dashboard;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.BaseActivity;
import com.Zakovskiy.lwaf.DialogCreateRoom;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.SetNicknameDialog;
import com.Zakovskiy.lwaf.WelcomeActivity;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.createPost.CreatePostActivity;
import com.Zakovskiy.lwaf.dashboard.adapters.RoomsAdapter;
import com.Zakovskiy.lwaf.dashboard.dialogs.DialogWheel;
import com.Zakovskiy.lwaf.friends.FriendsActivity;
import com.Zakovskiy.lwaf.globalConversation.GlobalConversationActivity;
import com.Zakovskiy.lwaf.models.Player;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.models.enums.NewsType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.news.NewsActivity;
import com.Zakovskiy.lwaf.news.PostActivity;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.ratings.RatingsActivity;
import com.Zakovskiy.lwaf.room.RoomActivity;
import com.Zakovskiy.lwaf.settings.SettingsActivity;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DashboardFragment extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    public List<RoomInLobby> rooms = new ArrayList<>();
    public RoomsAdapter roomsAdapter;
    private ShimmerFrameLayout roomsShimmer;
    private ListView roomsListView;
    private ImageView ivAdmin;
    private TextView tvNewsFriends;


    private final View.OnClickListener downButtonsListener = (view) -> {
        int itemId = view.getId();
        if (itemId == R.id.menu__button_create_room) {
            new DialogCreateRoom(this).show();
        } else if (itemId == R.id.menu__button_global_chat) {
            newActivity(GlobalConversationActivity.class);
        } else if (itemId == R.id.menu__button_profile) {
            ProfileDialogFragment profileDialogFragment = ProfileDialogFragment.newInstance(this, Application.lwafCurrentUser.userId);
            profileDialogFragment.show(getSupportFragmentManager(), "ProfileDialogFragment");
        } else if (itemId == R.id.menu__button_ratings) {
            newActivity(RatingsActivity.class);
        } else if(itemId == R.id.menu__button_friends) {
            newActivity(FriendsActivity.class);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.roomsListView = findViewById(R.id.menu__rooms_list);
        roomsAdapter = new RoomsAdapter(this, rooms);
        this.roomsShimmer = findViewById(R.id.roomsShimmer);
        changeShimmer(true);
        this.roomsListView.setAdapter(roomsAdapter);
        findViewById(R.id.menu__button_create_room).setOnClickListener(downButtonsListener);
        findViewById(R.id.menu__logout).setOnClickListener(this);
        findViewById(R.id.menu__button_global_chat).setOnClickListener(downButtonsListener);
        findViewById(R.id.menu__button_profile).setOnClickListener(downButtonsListener);
        findViewById(R.id.menu__news).setOnClickListener(this);
        findViewById(R.id.menu__button_ratings).setOnClickListener(downButtonsListener);
        findViewById(R.id.menu__settings).setOnClickListener(this);
        findViewById(R.id.menu__button_friends).setOnClickListener(downButtonsListener);
        this.ivAdmin = findViewById(R.id.menu__admin_panel);
        this.tvNewsFriends = findViewById(R.id.tvNewsFriends);
        if(Application.lwafCurrentUser.freeNickname) {
            new SetNicknameDialog(this, 0).show();
        }
    }

    private void changeShimmer(boolean type) {
        if(type) {
            this.roomsShimmer.startShimmer();
        } else {
            this.roomsShimmer.stopShimmer();
        }
        this.roomsShimmer.setVisibility(type ? View.VISIBLE : View.INVISIBLE);
        this.roomsListView.setVisibility(type ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.DASHBOARD);
        data.put(PacketDataKeys.VERSION, Config.VERSION);
        this.socketHelper.sendData(new JSONObject(data));
        changeShimmer(true);
    }

    @Override
    public void onDestroy() {
        this.socketHelper.unsubscribe(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {
        newActivity(BaseActivity.class, true, new Bundle());
    }

    @Override
    public void onReceiveError(String str) {
        newActivity(BaseActivity.class, true, new Bundle());
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(DashboardFragment.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "db": // dashboard
                        Application.lwafCurrentUser = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ACCOUNT), User.class);
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_LIST);
                        this.socketHelper.sendData(new JSONObject(data));
                        if(Application.lwafCurrentUser.isAdmin())
                            this.ivAdmin.setVisibility(View.VISIBLE);
                        findViewById(R.id.newsRedCircle).setVisibility(Application.lwafCurrentUser.news == NewsType.UNCHEKED ? View.VISIBLE : View.GONE);
                        if(Application.lwafCurrentUser.friendsNews > 0) {
                            String newsText = "99+";
                            if(Application.lwafCurrentUser.friendsNews <= 99) {
                                newsText = String.valueOf(Application.lwafCurrentUser.friendsNews);
                            }
                            this.tvNewsFriends.setText(newsText);
                            this.tvNewsFriends.setVisibility(View.VISIBLE);
                        }
                        break;
                    case "rli": // room list
                        List<RoomInLobby> roomsInLobby = JsonUtils.convertJsonNodeToList(json.get("rr"), RoomInLobby.class);
                        changesRooms(roomsInLobby);
                        changeShimmer(false);
                        if(Application.lwafCurrentUser.wheelCount > 0)
                            new DialogWheel( DashboardFragment.this).show();
                        break;
                    case "lo": // logout
                        SharedPreferences sPref = getSharedPreferences("lwaf_user", 0);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.clear();
                        ed.apply();
                        newActivity(WelcomeActivity.class, true, null);
                        break;
                    case "rj": // room join
                        RoomInLobby room = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ROOM), RoomInLobby.class);
                        Bundle b = new Bundle();
                        b.putSerializable("room", room);
                        newActivity(RoomActivity.class, b);
                        break;
                }
            } else if (json.has(PacketDataKeys.ROOM_TYPE_EVENT)) {
                String roomTypeEvent = json.get(PacketDataKeys.ROOM_TYPE_EVENT).asText();
                switch(roomTypeEvent) {
                    case "rc": // room create
                        RoomInLobby newRoom = JsonUtils.convertJsonNodeToObject(json.get("rr"), RoomInLobby.class);
                        List<RoomInLobby> newList = new ArrayList<>(rooms);
                        newList.add(newRoom);
                        changesRooms(newList);
                        break;
                    case "pc": // players count size
                       String roomId = json.get(PacketDataKeys.ROOM_IDENTIFICATOR).asText();
                        List<RoomInLobby> newListPCS = new ArrayList<>(rooms);
                        for (RoomInLobby roomInLobby : newListPCS) {
                            if (roomInLobby.roomId.equals(roomId)) {
                                roomInLobby.players = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.PLAYERS), Player.class);
                            }
                        }
                        changesRooms(newListPCS);
                        break;
                    case "dr": // delete room
                        List<RoomInLobby> newRooms = new ArrayList<>(rooms);
                        Iterator<RoomInLobby> it = newRooms.iterator();
                        while (it.hasNext()) {
                            if (it.next().roomId.equals(json.get(PacketDataKeys.ROOM_IDENTIFICATOR).asText())) {
                                Logs.debug("remove room");
                                it.remove();
                            }
                        }
                        changesRooms(newRooms);
                        break;
                }
            }
        });
    }

    public void changesRooms(List<RoomInLobby> list) {
        rooms.clear();
        rooms.addAll(list);
        roomsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.menu__logout) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.LOGOUT);
            this.socketHelper.sendData(new JSONObject(data));
        } else if (id == R.id.menu__news) {
            newActivity(NewsActivity.class);
        } else if (id == R.id.menu__settings) {
            newActivity(SettingsActivity.class);
        }
    }
}