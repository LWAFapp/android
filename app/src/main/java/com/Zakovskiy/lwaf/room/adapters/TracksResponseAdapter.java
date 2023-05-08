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

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.api.models.ModelTrackResponse;
import com.Zakovskiy.lwaf.models.FavoriteTrack;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.DialogPickTrack;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TracksResponseAdapter extends ArrayAdapter<ModelTrackResponse> {

    public Context context;
    private DialogPickTrack dialogPickTrack;
    public List<ModelTrackResponse> tracks;
    private SocketHelper socketHelper;
    private Integer type = 0;

    public TracksResponseAdapter(DialogPickTrack dialog, SocketHelper socketHelper, List<ModelTrackResponse> tracks, Integer type) {
        super(dialog.getContext(), R.layout.item_track_in_list, tracks);
        this.context = dialog.getContext();
        this.dialogPickTrack = dialog;
        this.type = type;
        this.tracks = tracks;
        this.socketHelper = socketHelper;
    }

    public int getCount() {
        return this.tracks.size();
    }

    @Override
    public ModelTrackResponse getItem(int position) {
        return this.tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ModelTrackResponse track = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_track_in_list, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.playerTrackIcon);
            holder.title = convertView.findViewById(R.id.trackTitle);
            holder.duration = convertView.findViewById(R.id.trackDuration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String title = String.format("<b>%s</b> - %s", track.artist, track.title);
        convertView.setOnClickListener((v)->{
            HashMap<String, Object> data = new HashMap<>();
            if(type == 0) {
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_TRACK_ADD);
                data.put(PacketDataKeys.KEY, String.format("%s\n%s\n%s\n%s\n%s", track.id, track.ownerId, title, track.duration, track.album.thumb.photo_1200));
            } else if(type == 1 || type == 2) {
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.SET_FAVORITE_TRACK);
                FavoriteTrack favoriteTrack = new FavoriteTrack();
                favoriteTrack.trackId = track.id;
                favoriteTrack.ownerId = track.ownerId;
                favoriteTrack.title = title;
                favoriteTrack.duration = track.duration;
                favoriteTrack.icon = track.album.thumb.photo_1200;
                data.put(PacketDataKeys.FAVORITE_TRACK, favoriteTrack);
            }
            this.socketHelper.sendData(JsonUtils.convertObjectToJsonString(data));
            this.dialogPickTrack.dismiss();
        });
        holder.title.setText(Html.fromHtml(title));
        Integer durationMinutes = (Integer) track.duration / 60;
        Integer durationSeconds = (Integer) track.duration % 60;
        holder.duration.setText(String.format("%02d:%02d", durationMinutes, durationSeconds));
        String urlIcon = track.album.thumb.photo_1200;
        ImageUtils.loadImage(this.context, urlIcon.isEmpty() ? R.drawable.without_preview : urlIcon, holder.icon);
        return convertView;
    }

    static class ViewHolder {
        private TextView title;
        private TextView duration;
        private CircleImageView icon;
    }
}