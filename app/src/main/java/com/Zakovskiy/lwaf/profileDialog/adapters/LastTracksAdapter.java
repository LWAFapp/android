package com.Zakovskiy.lwaf.profileDialog.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.FavoriteTrack;
import com.Zakovskiy.lwaf.models.LastTrack;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LastTracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private List<LastTrack> lastTrackList;
    private Context context;
    private Boolean self;

    public LastTracksAdapter(Context context, List<LastTrack> lastTracks, Boolean self) {
        this.context = context;
        this.lastTrackList = lastTracks;
        this.self = self;
    }

    @Override
    public int getItemViewType(int position) {
        return lastTrackList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new LastTrackViewHolder(inflater.inflate(R.layout.item_last_track, parent, false));
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shimmer_last_track, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LastTrackViewHolder) {
            LastTrack lastTrack = getItem(position);
            LastTrackViewHolder lastTrackViewHolder = (LastTrackViewHolder) holder;
            lastTrackViewHolder.bind(lastTrack);
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.bind();
        }
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
        private View item;

        public LastTrackViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.lastTrackIcon);
            materialTextView = itemView.findViewById(R.id.lastTrackTitle);
            item = itemView;
        }

        public void bind(LastTrack lastTrack) {
            if(!lastTrack.icon.isEmpty()) {
                ImageUtils.loadImage(context, lastTrack.icon, imageView, true, true);
            }
            materialTextView.setText(Html.fromHtml(String.format("<b>%s</b> - %s", lastTrack.artist, lastTrack.title)));
            item.setOnClickListener((v)->{
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.pm_last_track, popupMenu.getMenu());
                MenuItem menuItemSetFavorite = popupMenu.getMenu().findItem(R.id.pm_setLastTrackFavorite);
                MenuItem menuItemOpen = popupMenu.getMenu().findItem(R.id.pm_openLastTrack);
                if(self) {
                    menuItemSetFavorite.setVisible(true);
                    menuItemSetFavorite.setOnMenuItemClickListener((MenuItem mv)->{
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.SET_FAVORITE_TRACK);
                        FavoriteTrack favoriteTrack = new FavoriteTrack();
                        favoriteTrack.trackId = lastTrack.trackId;
                        favoriteTrack.ownerId = lastTrack.ownerId;
                        favoriteTrack.title = lastTrack.title;
                        favoriteTrack.duration = lastTrack.duration;
                        favoriteTrack.icon = lastTrack.icon;
                        data.put(PacketDataKeys.FAVORITE_TRACK, favoriteTrack);
                        socketHelper.sendData(JsonUtils.convertObjectToJsonString(data));
                        return false;
                    });
                }
                popupMenu.show();
            });
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ShimmerFrameLayout shimmerLayout;

        public LoadingViewHolder(View view) {
            super(view);
            shimmerLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmerLastTrack);
        }

        public void bind() {
            shimmerLayout.startShimmer();
        }
    }
}
