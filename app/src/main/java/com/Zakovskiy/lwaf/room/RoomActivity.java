package com.Zakovskiy.lwaf.room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.BaseActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.VKApi;
import com.Zakovskiy.lwaf.api.models.ModelTrackResponse;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.globalConversation.adapters.MessagesAdapter;
import com.Zakovskiy.lwaf.room.adapters.PlayersAdapter;
import com.Zakovskiy.lwaf.models.LotoWinner;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.Player;
import com.Zakovskiy.lwaf.models.RoomInLobby;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.Track;
import com.Zakovskiy.lwaf.models.enums.RoomType;
import com.Zakovskiy.lwaf.models.enums.TrackReactionsType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.adapters.TracksQueueAdapter;
import com.Zakovskiy.lwaf.room.views.PlayedTrackView;
import com.Zakovskiy.lwaf.utils.AudioPlayer;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.MD5Hash;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RoomActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RoomInLobby room;
    private AudioPlayer audioPlayer;

    // Widgets
    private RecyclerView listMessages;
    private ListView listUsers;
    private ListView listTracksQueue;

    private MaterialButton btnSetTrack;
    private PlayedTrackView llPlayerTrack;

    // Adapters
    private MessagesAdapter messagesAdapter;
    private PlayersAdapter usersAdapter;
    private TracksQueueAdapter tracksQueueAdapter;

    // Lists
    public List<Player> roomUsers = new ArrayList<>();
    public List<Message> messagesRoom = new ArrayList<>();;
    public List<Track> roomTracks = new ArrayList();
    public List<Track> queueTracks = new ArrayList();

    private Player currentDJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        this.socketHelper.subscribe(this);
        this.room = (RoomInLobby) getIntent().getSerializableExtra("room");
        this.roomTracks = new ArrayList<>(this.room.tracks);
        ((TextView)findViewById(R.id.textTitle)).setText(this.room.title);
        this.listMessages = findViewById(R.id.listViewMessages);
        this.listUsers = findViewById(R.id.listViewUsers);

        this.listTracksQueue = findViewById(R.id.tracksQueue);
        this.llPlayerTrack = findViewById(R.id.playerLayout);
        this.llPlayerTrack.setOnClickListener(this);
        this.llPlayerTrack.setRoom(this);
        this.audioPlayer = new AudioPlayer(this, ()->{
            llPlayerTrack.startAnimation();
            return null;
        }, ()->{
            llPlayerTrack.stopAnimation();
            return null;
        });
        this.btnSetTrack = findViewById(R.id.btnSetTrack);
        this.btnSetTrack.setOnClickListener(this);

        TextInputLayout inputNewMessage = findViewById(R.id.inputLayoutSendMessage);
        usersAdapter = new PlayersAdapter(this, getSupportFragmentManager(), roomUsers);
        messagesAdapter = new MessagesAdapter(this, getSupportFragmentManager(), messagesRoom, null);
        tracksQueueAdapter = new TracksQueueAdapter(this, queueTracks);
        this.listMessages.setAdapter(messagesAdapter);
        this.listMessages.setLayoutManager(new LinearLayoutManager(this));
        this.listUsers.setAdapter(usersAdapter);
        this.listTracksQueue.setAdapter(tracksQueueAdapter);
        changesUsers(this.room.players);
        refreshAudioPlayer();
        inputNewMessage.setEndIconOnClickListener(v -> {
            String messageString = inputNewMessage.getEditText().getText().toString();
            if (messageString.isEmpty()) return;
            HashMap<String, Object> dataMessage = new HashMap<>();
            dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_SEND_MESSAGE);
            dataMessage.put(PacketDataKeys.MESSAGE, messageString);
            inputNewMessage.getEditText().setText("");
            this.socketHelper.sendData(new JSONObject(dataMessage));
        });
    }

    @Override
    public void onStart() {
        this.socketHelper.subscribe(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        this.socketHelper.unsubscribe(this);
        this.audioPlayer.stopSong();
        super.onStop();
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(RoomActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
            } else if (json.has(PacketDataKeys.ROOM_TYPE_EVENT)) {
                String roomTypeEvent = json.get(PacketDataKeys.ROOM_TYPE_EVENT).asText();
                switch(roomTypeEvent) {
                    case "nm": // new message
                        Message newMessage = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.MESSAGE), Message.class);
                        List<Message> newList = new ArrayList<>(messagesRoom);
                        newList.add(newMessage);
                        changesMessages(newList);
                        this.listMessages.smoothScrollToPosition(messagesAdapter.getCount() - 1);
                        break;
                    case "pl": // player_left
                        List<Player> newUsersOfLeft = new ArrayList<>(roomUsers);
                        Player leftUser = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.PLAYERS), Player.class);
                        Iterator<Player> it = newUsersOfLeft.iterator();
                        while (it.hasNext()) {
                            if (it.next().userId.equals(leftUser.userId)) {
                                it.remove();
                            }
                        }
                        changesUsers(newUsersOfLeft);
                        break;
                    case "pj": // player_join
                        List<Player> newUsersOfJoin = new ArrayList<>(roomUsers);
                        Player joinUser = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.PLAYERS), Player.class);
                        newUsersOfJoin.add(joinUser);
                        Logs.info(newUsersOfJoin.toString());
                        changesUsers(newUsersOfJoin);
                        break;
                    case "et": // end_track
                        roomTracks.remove(0);
                        audioPlayer.stopSong();
                        refreshAudioPlayer();
                        break;
                    case "st": // set_track
                        roomTracks.add(JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.TRACK), Track.class));
                        refreshAudioPlayer();
                        break;
                    case "dt": // delete_track
                        String key = json.get(PacketDataKeys.KEY).asText();
                        Iterator<Track> itOfDeleteTrack = roomTracks.iterator();
                        int index = 0;
                        while (itOfDeleteTrack.hasNext()) {
                            if (itOfDeleteTrack.next().key.equals(key)) {
                                itOfDeleteTrack.remove();
                                if (index == 0)
                                    audioPlayer.stopSong();
                            }
                            index++;
                        }
                        refreshAudioPlayer();
                        break;
                    case "rtsr": // room_track_set_reaction
                        TrackReactionsType type = TrackReactionsType.fromInt(json.get(PacketDataKeys.TYPE).asInt());
                        if (type == TrackReactionsType.DISLIKE) {
                            roomTracks.get(0).dislikes++;
                        } else if (type == TrackReactionsType.SUPER_LIKE) {
                            roomTracks.get(0).superLikes++;
                            if (this.currentDJ != null) {
                                this.currentDJ.superLikesSize++;
                                this.usersAdapter.notifyDataSetChanged();
                            }
                        } else if (type == TrackReactionsType.LIKE) {
                            roomTracks.get(0).likes++;
                        }
                        llPlayerTrack.resetReactions(roomTracks.get(0));
                        break;
                    case "sl": // start_loto
                        new DialogLoto(this).show();
                        break;
                }
            }
        });
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

    public void changesMessages(List<Message> list) {
        messagesRoom.clear();
        messagesRoom.addAll(list);
        messagesAdapter.notifyDataSetChanged();
    }

    public void changesUsers(List<Player> list) {
        roomUsers.clear();
        roomUsers.addAll(list);
        usersAdapter.notifyDataSetChanged();
    }

    public void setCurrentTrack() {
        Track currentTrack = roomTracks.get(0);
        this.llPlayerTrack.setTitle(currentTrack.title);
        this.llPlayerTrack.setIcon(currentTrack.icon.isEmpty() ? R.drawable.without_preview : currentTrack.icon);
        llPlayerTrack.resetReactions(currentTrack);
        for (Player player : roomUsers) {
            if (player.userId.equals(currentTrack.user.userId)) {
                this.currentDJ = player;
                break;
            }
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Config.VK_API).build();
        VKApi vkApi = retrofit.create(VKApi.class);
        String sig = MD5Hash.md5(String.format("/method/audio.getById?v=5.102&access_token=%s&audios=%s%s", Application.lwafCurrentUser.vkontakteToken, currentTrack.key, Application.lwafCurrentUser.vkontakteSecret));
        vkApi.audioGetById(Application.lwafCurrentUser.vkontakteToken, currentTrack.key, sig).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    String body = response.body().string();
                    JsonNode json = JsonUtils.convertJsonStringToJsonNode(body).get("response");
                    List<ModelTrackResponse> tracks = JsonUtils.convertJsonNodeToList(json, ModelTrackResponse.class);
                    Logs.debug(tracks.get(0).url);
                    audioPlayer.playSong(tracks.get(0).url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void refreshAudioPlayer() {
        Logs.info("Refresh Audio Player");
        if(Application.lwafCurrentUser.vkontakteToken.isEmpty()) { // если мэн не привязал вк
            findViewById(R.id.errorUnsettingVK).setVisibility(View.VISIBLE);
            return;
        }

        if (this.roomTracks.size() > 0) { // если очередь из треков не пуста
            this.btnSetTrack.setVisibility(View.INVISIBLE);
            this.llPlayerTrack.setVisibility(View.VISIBLE);
            if (audioPlayer.mMediaPlayer == null
                    || !audioPlayer.mMediaPlayer.isPlaying()) { // проверяем не играет ли трек
                setCurrentTrack();
            }
            queueTracks.clear();
            if (this.roomTracks.size() > 1) { // если очередь треков больше 1 (то есть не только основной трек играет) то добавляем в очередь оставшиеся
                queueTracks.addAll(new ArrayList<>(this.roomTracks.subList(1, this.roomTracks.size())));
            }
            tracksQueueAdapter.notifyDataSetChanged();
            return;
        }
        this.btnSetTrack.setVisibility(View.VISIBLE);
        this.llPlayerTrack.setVisibility(View.INVISIBLE);
    }

    public void setTrack() {
        if (this.room.roomType == RoomType.DEFAULT) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("audio/mpeg");
            chooseFile = Intent.createChooser(chooseFile, "Выбери трек");
            startActivityForResult(chooseFile, 1);
            return;
        }
        new DialogPickTrack(this, 0).show();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSetTrack) {
            setTrack();
        } else if (id == R.id.playerLayout) {
            this.listTracksQueue.setVisibility(this.listTracksQueue.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == -1 && data != null) {
                    Uri fileUri = data.getData();
                    String filePath = fileUri.getPath();
                    Logs.debug(filePath);
                }

                break;
        }
    }
}
