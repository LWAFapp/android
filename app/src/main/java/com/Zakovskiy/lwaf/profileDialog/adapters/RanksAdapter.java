package com.Zakovskiy.lwaf.profileDialog.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.LastTrack;
import com.Zakovskiy.lwaf.models.Rank;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RanksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Rank> ranksList;
    private Context context;

    public RanksAdapter(Context context, List<Rank> ranks) {
        this.context = context;
        this.ranksList = ranks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new RanksAdapter.RankViewHolder(inflater.inflate(R.layout.item_rank, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Rank rank = getItem(position);
        RanksAdapter.RankViewHolder rankViewHolder = (RanksAdapter.RankViewHolder) holder;
        rankViewHolder.bind(rank);
    }

    public Rank getItem(int position) {
        return this.ranksList.get(position);
    }

    @Override
    public int getItemCount() {
        return ranksList.size();
    }

    private class RankViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private MaterialTextView materialTextView;
        private CardView cardView;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rankIcon);
            materialTextView = itemView.findViewById(R.id.rankTitle);
            cardView = itemView.findViewById(R.id.cvRank);
        }

        public void bind(Rank rank) {
            ImageUtils.loadImage(context, rank.getIcon(), imageView, true, true);
            materialTextView.setText(Html.fromHtml(rank.title));
            cardView.setCardBackgroundColor(Color.parseColor(rank.backgroundColor));
            materialTextView.setTextColor(Color.parseColor(rank.textColor));
        }
    }
}
