package com.Zakovskiy.lwaf.profileDialog.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.LastTrack;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LastTracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LastTrack> lastTrackList;
    private Context context;

    public LastTracksAdapter(Context context, List<LastTrack> lastTracks) {
        this.context = context;
        this.lastTrackList = lastTracks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new LastTrackViewHolder(inflater.inflate(R.layout.item_last_track, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LastTrack lastTrack = getItem(position);
        LastTrackViewHolder lastTrackViewHolder = (LastTrackViewHolder) holder;
        lastTrackViewHolder.bind(lastTrack);
    }

    public LastTrack getItem(int position) {
        return this.lastTrackList.get(position);
    }

    @Override
    public int getItemCount() {
        return lastTrackList.size();
    }

    private class LastTrackViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private MaterialTextView materialTextView;

        public LastTrackViewHolder(@NonNull View itemView) {
            super(itemView);
            Logs.info("lta");
            imageView = itemView.findViewById(R.id.lastTrackIcon);
            materialTextView = itemView.findViewById(R.id.lastTrackTitle);
        }

        public void bind(LastTrack lastTrack) {
            if(!lastTrack.icon.isEmpty()) {
                ImageUtils.loadImage(context, lastTrack.icon, imageView, true, true);
            }
            materialTextView.setText(Html.fromHtml(lastTrack.title));
        }
    }
}
