package com.Zakovskiy.lwaf.ratings.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.Rank;
import com.Zakovskiy.lwaf.models.RatingUser;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.profileDialog.adapters.RanksAdapter;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RatingUser> ratingsUserList;
    private Context context;
    private FragmentManager fragmentManager;

    public RatingsAdapter(Context context, FragmentManager fragmentManager, List<RatingUser> ratingsUsers) {
        this.context = context;
        this.ratingsUserList = ratingsUsers;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new RatingsAdapter.RankViewHolder(inflater.inflate(R.layout.item_rating_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RatingUser rank = getItem(position);
        RatingsAdapter.RankViewHolder rankViewHolder = (RatingsAdapter.RankViewHolder) holder;
        rankViewHolder.bind(rank, position);
    }

    public RatingUser getItem(int position) {
        return this.ratingsUserList.get(position);
    }

    @Override
    public int getItemCount() {
        return ratingsUserList.size();
    }

    private class RankViewHolder extends RecyclerView.ViewHolder {
        private UserAvatar uaAvatar;
        private TextView tvNickname;
        private TextView tvValue;
        private MaterialButton btnPosition;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvNickname = itemView.findViewById(R.id.tvNickname);
            this.tvValue = itemView.findViewById(R.id.tvValue);
            this.uaAvatar = itemView.findViewById(R.id.uaAvatar);
            this.btnPosition = itemView.findViewById(R.id.btnPosition);
        }

        public void bind(RatingUser ratingUser, int position) {
            this.uaAvatar.setUser((ShortUser) ratingUser);
            this.uaAvatar.setOnClickListener((view)->{
                ProfileDialogFragment.newInstance(context, ratingUser.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.tvNickname.setText(ratingUser.nickname);
            this.tvValue.setText(String.format("%s %s", ratingUser.value, context.getString(ratingUser.ratingsType.stringId)));
            this.btnPosition.setText(String.valueOf(position + 1));
        }
    }
}