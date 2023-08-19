package com.Zakovskiy.lwaf.room.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.models.ModelTrackResponse;
import com.Zakovskiy.lwaf.models.FavoriteTrack;
import com.Zakovskiy.lwaf.models.LastTrack;
import com.Zakovskiy.lwaf.models.Track;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.profileDialog.adapters.LastTracksAdapter;
import com.Zakovskiy.lwaf.room.DialogPickTrack;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TracksResponseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public Context context;
    private DialogPickTrack dialogPickTrack;
    public List<ModelTrackResponse> tracks;
    private SocketHelper socketHelper;
    private Integer type = 0;

    public TracksResponseAdapter(DialogPickTrack dialog, SocketHelper socketHelper, List<ModelTrackResponse> tracks, Integer type) {
        this.context = dialog.getContext();
        this.dialogPickTrack = dialog;
        this.type = type;
        this.tracks = tracks;
        this.socketHelper = socketHelper;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new TracksResponseViewHolder(inflater.inflate(R.layout.item_track_in_list, parent, false));
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.shimmer_track_in_list, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TracksResponseAdapter.TracksResponseViewHolder) {
            ModelTrackResponse track = getItem(position);
            TracksResponseAdapter.TracksResponseViewHolder lastTrackViewHolder = (TracksResponseAdapter.TracksResponseViewHolder) holder;
            lastTrackViewHolder.bind(track);
        } else {
            TracksResponseAdapter.LoadingViewHolder loadingViewHolder = (TracksResponseAdapter.LoadingViewHolder) holder;
            loadingViewHolder.bind();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return tracks.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public ModelTrackResponse getItem(int position) {
        return this.tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }

    private class TracksResponseViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView icon;
        private TextView title;
        private TextView duration;
        private View view;


        public TracksResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.playerTrackIcon);
            title = itemView.findViewById(R.id.trackTitle);
            duration = itemView.findViewById(R.id.trackDuration);
            this.view = itemView;
        }

        public void bind(ModelTrackResponse track) {
            this.view.setOnClickListener((v)->{
                HashMap<String, Object> data = new HashMap<>();
                if(type == 0 || type == 4 || type == 5) {
                    data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_TRACK_ADD);
                    Track newAddTrack = new Track();
                    newAddTrack.key = String.format("%s_%s", track.ownerId, track.id);
                    newAddTrack.title = track.title;
                    newAddTrack.artist = track.artist;
                    newAddTrack.duration = track.duration;
                    newAddTrack.icon = track.album.thumb.photo_1200;
                    data.put(PacketDataKeys.TRACK, newAddTrack);
                    data.put(PacketDataKeys.SKIP, type == 4);
                } else if(type == 1 || type == 2) {
                    data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.SET_FAVORITE_TRACK);
                    FavoriteTrack favoriteTrack = new FavoriteTrack();
                    favoriteTrack.trackId = track.id;
                    favoriteTrack.ownerId = track.ownerId;
                    favoriteTrack.title = track.title;
                    favoriteTrack.artist = track.artist;
                    favoriteTrack.duration = track.duration;
                    favoriteTrack.icon = track.album.thumb.photo_1200;
                    data.put(PacketDataKeys.FAVORITE_TRACK, favoriteTrack);
                } else if(type == 3) {
                    data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_REPLACE_TRACK);
                    Track newReplacedrack = new Track();
                    newReplacedrack.key = String.format("%s_%s", track.ownerId, track.id);
                    newReplacedrack.title = track.title;
                    newReplacedrack.artist = track.artist;
                    newReplacedrack.duration = track.duration;
                    newReplacedrack.icon = track.album.thumb.photo_1200;
                    data.put(PacketDataKeys.TRACK, newReplacedrack);
                }
                socketHelper.sendData(JsonUtils.convertObjectToJsonString(data));
                dialogPickTrack.dismiss();
            });
            this.title.setText(Html.fromHtml(String.format("<b>%s</b> - %s", track.artist, track.title)));
            Integer durationMinutes = (Integer) track.duration / 60;
            Integer durationSeconds = (Integer) track.duration % 60;
            this.duration.setText(String.format("%02d:%02d", durationMinutes, durationSeconds));
            String urlIcon = track.album.thumb.photo_1200;
            ImageUtils.loadImage(context, urlIcon.isEmpty() ? R.drawable.without_preview : urlIcon, this.icon);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View viewHolder) {
            super(viewHolder);
        }

        public void bind() {

        }
    }
}