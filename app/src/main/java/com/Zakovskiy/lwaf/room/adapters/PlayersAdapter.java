package com.Zakovskiy.lwaf.room.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.Player;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.widgets.UserAvatar;

import java.util.List;

public class PlayersAdapter extends ArrayAdapter<Player> {

    public Context context;
    public List<Player> players;
    public FragmentManager fragmentManager;

    public PlayersAdapter(Context context, FragmentManager fragmentManager, List<Player> players) {
        super(context, R.layout.item_user_in_listview, players);
        this.context = context;
        this.players = players;
        this.fragmentManager = fragmentManager;
    }

    public int getCount() {
        return this.players.size();
    }

    @Override
    public Player getItem(int position) {
        return this.players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Player player = getItem(position);
        View view = inflater.inflate(R.layout.item_user_in_listview, parent, false);
        view.setOnClickListener((v)->{
            ProfileDialogFragment.newInstance(context, player.userId).show(fragmentManager, "ProfileDialogFragment");
        });
        TextView username = view.findViewById(R.id.username);
        UserAvatar avatarImage = view.findViewById(R.id.circleImageView2);
        if (player.superLikesSize != null &&player.superLikesSize > 0) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(player.nickname + "    " + String.valueOf(player.superLikesSize));
            Bitmap sl = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock);
            sl = Bitmap.createScaledBitmap(sl, 150, 150, true);
            int len = String.valueOf(player.superLikesSize).length();
            ssb.setSpan(new ImageSpan(sl), ssb.length() - len - 1, ssb.length() - len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            username.setText(ssb, TextView.BufferType.SPANNABLE);
        } else {
            username.setText(player.nickname);
        }
        avatarImage.setUser(player);
        return view;
    }
}