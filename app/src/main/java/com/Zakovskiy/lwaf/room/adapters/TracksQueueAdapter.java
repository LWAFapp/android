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

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.menuDialog.MenuDialogFragment;
import com.Zakovskiy.lwaf.models.Track;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TracksQueueAdapter extends ArrayAdapter<Track> {

    private Context context;
    private List<Track> tracks;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private ABCActivity ac;

    public TracksQueueAdapter(@NonNull Context context, List<Track> tracks, ABCActivity ac) {
        super(context, R.layout.item_track_in_queue, tracks);
        this.context = context;
        this.tracks = tracks;
        this.ac = ac;
    }

    public int getCount() {
        return this.tracks.size();
    }

    @Override
    public Track getItem(int position) {
        return this.tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Track track = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_track_in_queue, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.playerTrackIcon);
            holder.title = convertView.findViewById(R.id.trackTitle);
            holder.duration = convertView.findViewById(R.id.trackDuration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.findViewById(R.id.trackInQueueDelete).setOnClickListener(v -> {

        });
        convertView.findViewById(R.id.trackInQueueEdit).setOnClickListener(v -> {

        });
        holder.title.setText(Html.fromHtml(track.title));
        Integer durationMinutes = (Integer) track.duration / 60;
        Integer durationSeconds = (Integer) track.duration % 60;
        holder.duration.setText(String.format("%02d:%02d", durationMinutes, durationSeconds));
        ImageUtils.loadImage(this.context, track.icon.isEmpty() ? R.drawable.without_preview : track.icon, holder.icon);


        return convertView;
    }

    static class ViewHolder {
        private TextView title;
        private TextView duration;
        private CircleImageView icon;
    }
}
