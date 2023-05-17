package com.Zakovskiy.lwaf.profileDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.LWAFApi;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.menuDialog.MenuDialogFragment;
import com.Zakovskiy.lwaf.menuDialog.adapters.MenuButtonAdapter;
import com.Zakovskiy.lwaf.models.FavoriteTrack;
import com.Zakovskiy.lwaf.models.LastTrack;
import com.Zakovskiy.lwaf.models.Rank;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.models.enums.FriendType;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.profileDialog.adapters.LastTracksAdapter;
import com.Zakovskiy.lwaf.profileDialog.adapters.RanksAdapter;
import com.Zakovskiy.lwaf.room.DialogPickTrack;
import com.Zakovskiy.lwaf.utils.FileUtils;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileDialogFragment extends DialogFragment implements SocketHelper.SocketListener {
    private List<MenuButton> buttons;
    private Context context;
    private String userId;
    private List<MenuButton> menuButtons;
    private MenuButtonAdapter menuButtonAdapter;
    private TextView tvUsername;
    private TextView tvLastSeen;
    private EditText tvAbout;
    private TextView tvNotHaveFavoriteTrack;
    private TextView tvFavoriteTrack;
    private TextView tvBalance;

    private RecyclerView rvLastTracks;
    private RecyclerView rvRanks;

    private float newScale;

    private User user;

    private LinearLayout llStatistics;
    private LinearLayout llFavoriteTrack;

    private ScrollView svContent;
    private ActionMenuItemView menuItemMore;

    private Button btnAddFavoriteTrack;
    private UserAvatar civAvatar;
    private CircleImageView civFavoriteTrack;
    private List<LastTrack> lastTracks = new ArrayList<>();
    private List<Rank> rankList = new ArrayList<>();
    private LastTracksAdapter lastTracksAdapter;
    private RanksAdapter ranksAdapter;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private CardView cvLastTracks;
    private Boolean changedAbout;
    private Handler handler = new Handler();
    private Runnable runnableAvatarScroll = new Runnable() {
        @Override
        public void run() {
            civAvatar.setScaleX(newScale);
            civAvatar.setScaleY(newScale);
        }
    };

    public ProfileDialogFragment() {}

    public static ProfileDialogFragment newInstance(Context context, String userId) {
        ProfileDialogFragment fragment = new ProfileDialogFragment();
        fragment.userId = userId;
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GET_USER);
        data.put(PacketDataKeys.USER_TYPE, PacketDataKeys.USER_ID);
        data.put(PacketDataKeys.CONTENT, this.userId);
        this.socketHelper.sendData(new JSONObject(data));
        Dialog dialog = getDialog();
        if(dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public void onStop() {
        socketHelper.unsubscribe(this);
        Logs.info("STOP");
        if(isSelf() && changedAbout) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.CHANGE_ABOUT);
            data.put(PacketDataKeys.ABOUT, tvAbout.getText().toString());
            socketHelper.sendData(JsonUtils.convertObjectToJsonString(data));
        }
        super.onStop();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_profile);
        this.tvUsername = dialog.findViewById(R.id.profileUsername);
        this.llStatistics = dialog.findViewById(R.id.layoutStatisticks);
        this.tvLastSeen = dialog.findViewById(R.id.profileLastSeen);
        this.civAvatar = dialog.findViewById(R.id.avatarProfile);
        this.tvAbout = dialog.findViewById(R.id.textAbout);
        this.tvNotHaveFavoriteTrack = dialog.findViewById(R.id.textNotHaveFavoriteTrack);
        this.llFavoriteTrack = dialog.findViewById(R.id.layoutFavoriteTrack);
        this.btnAddFavoriteTrack = dialog.findViewById(R.id.btnAddFavoriteTrack);
        this.tvFavoriteTrack = dialog.findViewById(R.id.textFavoriteTrack);
        this.civFavoriteTrack = dialog.findViewById(R.id.iconFavoriteTrack);
        this.rvLastTracks = dialog.findViewById(R.id.rvLastTracks);
        this.rvRanks = dialog.findViewById(R.id.rvRanks);
        this.cvLastTracks = dialog.findViewById(R.id.cardViewLastTracks);
        this.svContent = dialog.findViewById(R.id.profileScrollContent);
        this.menuItemMore = dialog.findViewById(R.id.more);
        this.tvBalance = dialog.findViewById(R.id.tvBalance);
        ranksAdapter = new RanksAdapter(context, rankList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvRanks.setLayoutManager(linearLayoutManager);
        rvRanks.setAdapter(ranksAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.svContent.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY > oldScrollY) {  // скролл вниз
                    newScale = Math.max(civAvatar.getScaleX() - 0.01f, 0.5f);  // уменьшаем масштаб на 0.01, но не меньше 0.5
                } else if (scrollY < oldScrollY) {  // скролл вверх
                    newScale = Math.min(civAvatar.getScaleX() + 0.01f, 1.0f);  // увеличиваем масштаб на 0.01, но не больше 1
                    if (scrollY == 0) {  // достигнут верх экрана
                        newScale = 1.0f;  // возвращаем масштаб к начальному значению
                    }
                } else {
                    return;
                }

                handler.removeCallbacks(runnableAvatarScroll);
                handler.postDelayed(runnableAvatarScroll, 8);
            });
        }
        return dialog;
    }

    private View.OnClickListener onClickAddFavoriteTrack = v -> {
        new DialogPickTrack(context, 1).show();
    };

    private View.OnClickListener onClickFavorite = v -> {
        new DialogPickTrack(context, 2).show();
    };

    public void changeAvatar () {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        chooseFile = Intent.createChooser(chooseFile, getString(R.string.pick_avatar));
        startActivityForResult(chooseFile, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver contentResolver = getContext().getContentResolver();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Application.lwafServerConfig.restHost).build();
            LWAFApi lwafApi = retrofit.create(LWAFApi.class);
            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(uri);
            } catch (FileNotFoundException e) {
                new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            RequestBody requestBody = null;
            try {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), FileUtils.getBytes(inputStream));
            } catch (IOException e) {
                new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", FileUtils.getFileName(getContext(), uri), requestBody);
            lwafApi.uploadPhoto(Application.lwafCurrentUser.accessToken, filePart).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String responseString = null;
                    try {
                        responseString = response.body().string();
                        Logs.debug(responseString);
                        JsonNode json = JsonUtils.convertJsonStringToJsonNode(responseString);
                        if (json.has("error")) {
                            new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
                        } else {
                            civAvatar.setImageURI(uri);
                        }
                    } catch (IOException e) {
                        new DialogTextBox(getContext(), getString(R.string.error_upload_photo)).show();
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

    private void changeFavoriteTracks(FavoriteTrack favoriteTrack) {
        this.tvNotHaveFavoriteTrack.setVisibility(View.GONE);
        this.llFavoriteTrack.setVisibility(View.GONE);
        this.btnAddFavoriteTrack.setVisibility(View.GONE);
        if(favoriteTrack == null || favoriteTrack.ownerId == 0) {
            if(!isSelf()) {
                this.tvNotHaveFavoriteTrack.setVisibility(View.VISIBLE);
            } else {
                this.btnAddFavoriteTrack.setVisibility(View.VISIBLE);
            }
        } else {
            if(isSelf()) {
                this.llFavoriteTrack.setOnClickListener(onClickFavorite);
            }
            ImageUtils.loadImage(context, favoriteTrack.icon.isEmpty() ? R.drawable.without_preview : favoriteTrack.icon, this.civFavoriteTrack, true, true);
            tvFavoriteTrack.setText(Html.fromHtml(favoriteTrack.title));
            this.llFavoriteTrack.setVisibility(View.VISIBLE);
        }
    }

    private boolean isSelf() {
        return this.user.userId.equals(Application.lwafCurrentUser.userId);
    }

    private void changeLastTracks(List<LastTrack> list) {
        lastTracks.clear();
        lastTracks.addAll(list);
        lastTracksAdapter.notifyDataSetChanged();
    }

    private void changeRanks(List<Rank> list) {
        rankList.clear();
        rankList.addAll(list);
        ranksAdapter.notifyDataSetChanged();
    }

    private void bind(User user) {
        this.user = user;
        lastTracksAdapter = new LastTracksAdapter(context, lastTracks, isSelf());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvLastTracks.setLayoutManager(linearLayoutManager);
        rvLastTracks.setAdapter(lastTracksAdapter);
        this.btnAddFavoriteTrack.setOnClickListener(onClickAddFavoriteTrack);
        this.tvUsername.setText(user.nickname);
        if(!user.hidenBalance || isSelf()) {
            tvBalance.setVisibility(View.VISIBLE);
            tvBalance.setText(String.valueOf(user.balance));
        }
        this.tvAbout.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changedAbout = true;
            }
        });
        this.menuItemMore.setOnClickListener((v)->{
            List<MenuButton> menuButtons = new ArrayList<>();
            if (!isSelf()) {
                menuButtons.add(new MenuButton(getString(user.friendType.title), "#FFFFFF", (vb) -> {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(PacketDataKeys.TYPE_EVENT, user.friendType.packetType);
                    if(user.friendType == FriendType.ADD_FRIEND) {
                        data.put(PacketDataKeys.USER_ID, user.userId);
                    } else {
                        data.put(PacketDataKeys.FRIEND_ID, user.friendId);
                    }
                    this.socketHelper.sendData(new JSONObject(data));
                }));
                menuButtons.add(new MenuButton(getString(R.string.report), "#E10F4A", (vb) -> {
                    Logs.debug("Report success");
                }));
            }
            MenuDialogFragment.newInstance(context, menuButtons).show(getFragmentManager(), "MenuButtons");
            Logs.debug("press");
        });
        ((TextView)this.llStatistics.findViewById(R.id.textTracks)).setText(String.valueOf(user.tracks));
        ((TextView)this.llStatistics.findViewById(R.id.textDislikes)).setText(String.valueOf(user.dislikes));
        ((TextView)this.llStatistics.findViewById(R.id.textSuperlikes)).setText(String.valueOf(user.superLikes));
        ((TextView)this.llStatistics.findViewById(R.id.textLikes)).setText(String.valueOf(user.likes));
        Long currentTimeStamp = System.currentTimeMillis() / 1000;
        if((currentTimeStamp - user.lastSeen) > Application.lwafServerConfig.onlineTime) {
            this.tvLastSeen.setText(TimeUtils.getTime(user.lastSeen*1000, "dd.MM.yyyy HH:mm"));
        }
        if(user.lastTracks.size() > 0 || isSelf()) {
            changeLastTracks(user.lastTracks);
        } else {
            cvLastTracks.setVisibility(View.GONE);
        }
        if(user.ranks.size() > 0) {
            rvRanks.setVisibility(View.VISIBLE);
            changeRanks(user.ranks);
        }
        changeFavoriteTracks(user.favoriteTrack);
        if(!isSelf()){
            this.tvAbout.setEnabled(false);
            if(user.avatar != null && !user.avatar.isEmpty()) {
                this.civAvatar.setOnClickListener((v) -> {
                    new AvatarDialogFragment(context, user.avatar).show(getFragmentManager(), "AvatarDialogFragment");
                });
            }
        } else {
            this.civAvatar.setOnClickListener((v) -> {
                if(user.avatar == null || user.avatar.isEmpty()) {
                    changeAvatar();
                    return;
                }
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.pm_avatar, popupMenu.getMenu());
                MenuItem menuOpenAvatar = popupMenu.getMenu().findItem(R.id.pm_openAvatar);
                MenuItem menuRemoveAvatar = popupMenu.getMenu().findItem(R.id.pm_removeAvatar);
                MenuItem menuSetAvatar = popupMenu.getMenu().findItem(R.id.pm_setAvatar);
                menuRemoveAvatar.setOnMenuItemClickListener((mv)->{
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.REMOVE_AVATAR);
                    this.socketHelper.sendData(new JSONObject(data));
                    return false;
                });
                menuOpenAvatar.setOnMenuItemClickListener((mv)->{
                    new AvatarDialogFragment(context, user.avatar).show(getFragmentManager(), "AvatarDialogFragment");
                    return false;
                });
                menuSetAvatar.setOnMenuItemClickListener((mv)->{
                    changeAvatar();
                    return false;
                });
                popupMenu.show();
            });
        }
        if(user.about != null && !user.about.isEmpty()) {
            this.tvAbout.setText(user.about);
        }

        this.civAvatar.setUser(user);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(JsonNode json) {
        if(json.has(PacketDataKeys.TYPE_EVENT)) {
            String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
            if(typeEvent.equals(PacketDataKeys.GET_USER)) {
                User user = (User) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.USER), User.class);
                getActivity().runOnUiThread(()->bind(user));
            } else if(typeEvent.equals(PacketDataKeys.REMOVE_FAVORITE_TRACK)) {
                getActivity().runOnUiThread(()->changeFavoriteTracks(null));
            } else if (typeEvent.equals(PacketDataKeys.SET_FAVORITE_TRACK)) {
                FavoriteTrack favoriteTrack = JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.FAVORITE_TRACK), FavoriteTrack.class);
                getActivity().runOnUiThread(()->changeFavoriteTracks(favoriteTrack));
            } else if(typeEvent.equals(PacketDataKeys.FRIEND_ADD_TO_FRIENDSHIP_LIST)) {
                user.friendId = json.get(PacketDataKeys.FRIEND_ID).asText();
                user.friendType = FriendType.CANCEL_REQUEST;
            } else if(typeEvent.equals(PacketDataKeys.FRIEND_DELETE_FRIENDSHIP)) {
                user.friendType = FriendType.ADD_FRIEND;
            } else if(typeEvent.equals(PacketDataKeys.FRIEND_ACCEPT_FRIENDSHIP)) {
                user.friendType = FriendType.REMOVE_FRIEND;
            } else if(typeEvent.equals(PacketDataKeys.REMOVE_AVATAR)) {
                user.avatar = "";
                civAvatar.post(()-> {
                    civAvatar.setUser(user);
                });
            }
        }
    }

    @Override
    public void onReceiveError(String str) {

    }
}
