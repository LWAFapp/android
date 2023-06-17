package com.Zakovskiy.lwaf.room.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Range;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.TrackDialogFragment;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.Track;
import com.Zakovskiy.lwaf.models.enums.TrackReactionsType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.RoomActivity;
import com.Zakovskiy.lwaf.utils.ArrayUtils;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

import org.json.JSONObject;

import java.awt.font.NumericShaper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayedTrackView extends LinearLayout implements View.OnClickListener {

    private CircleImageView icon;
    private Context context;
    private TextView title;
    private LinearLayout reactionLike;
    private LinearLayout reactionDislike;
    private LinearLayout reactionSuperlike;
    private Button buttonSetTrack;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private RoomActivity room;
    private Track track;

    public PlayedTrackView(Context context) {
        super(context);
        initView(context);
    }

    public PlayedTrackView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public PlayedTrackView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public void setRoom(RoomActivity room) {
        this.room = room;
    }

    public void setTitle(String title) {
        this.title.setText(Html.fromHtml(title));
    }

    public void setIcon(Object url) {
        ImageUtils.loadImage(this.getContext(), url, this.icon);
    }
    public void setTrack(Track track) {this.track = track;}

    public void startAnimation() {
        RotateAnimation rotate = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(6000);
        rotate.setRepeatCount(Animation.INFINITE);
        this.icon.startAnimation(rotate);

//        Animation anim = AnimationUtils.loadAnimation(this.context, R.anim.anim_track_text_motion);
//        anim.setDuration(6000);
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                title.animate().translationX(0);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        this.title.startAnimation(anim);
        this.title.setSelected(true);
    }

    public void stopAnimation() {
        this.icon.clearAnimation();
        this.title.clearAnimation();
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_played_track, this);
        this.context = context;
        this.icon = findViewById(R.id.playerTrackIcon);
        this.title = findViewById(R.id.playerTrackTitle);
        this.reactionDislike = findViewById(R.id.trackReactionDislike);
        this.reactionLike = findViewById(R.id.trackReactionLike);
        this.reactionSuperlike = findViewById(R.id.trackReactionSuperlike);
        this.reactionSuperlike.setOnClickListener(this);
        this.reactionLike.setOnClickListener(this);
        this.reactionDislike.setOnClickListener(this);
        this.buttonSetTrack = findViewById(R.id.btnSetTrack);
        this.buttonSetTrack.setOnClickListener(this);
        this.icon.setOnClickListener(this);
        Logs.debug(Application.lwafServerConfig.toString());
        Logs.debug(Application.lwafServerConfig.colors.setSuperLikeOnTrack);
        ((TextView) this.reactionSuperlike.findViewById(R.id.trackReactionRockText)).setTextColor(Color.parseColor(Application.lwafServerConfig.colors.setSuperLikeOnTrack));
    }

    public void resetReactions(Track track) {
        ((TextView) this.reactionDislike.findViewById(R.id.trackReactionDislikeText)).setText(String.valueOf(track.dislikes));
        ((TextView) this.reactionLike.findViewById(R.id.trackReactionLikeText)).setText(String.valueOf(track.likes));
        ((TextView) this.reactionSuperlike.findViewById(R.id.trackReactionRockText)).setText(String.valueOf(track.superLikes));
        if (track.user.userId.equals(Application.lwafCurrentUser.userId)) {
            this.buttonSetTrack.setVisibility(INVISIBLE);
            return;
        }
        this.buttonSetTrack.setVisibility(VISIBLE);
    }

    private void setReaction(TrackReactionsType type) {
        HashMap<String, Object> dataMessage = new HashMap<>();
        dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_TRACK_SET_REACTION);
        dataMessage.put(PacketDataKeys.REACTION_TYPE, type.ordinal());
        this.socketHelper.sendData(new JSONObject(dataMessage));
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.trackReactionLike) {
            setReaction(TrackReactionsType.LIKE);
        } else if (itemId == R.id.trackReactionDislike) {
            setReaction(TrackReactionsType.DISLIKE);
        } else if (itemId == R.id.trackReactionSuperlike) {
            setReaction(TrackReactionsType.SUPER_LIKE);
        } else if (itemId == R.id.btnSetTrack) {
            this.room.setTrack();
        } else if (itemId == R.id.playerTrackIcon) {
            new TrackDialogFragment(context, this.track).show(room.getSupportFragmentManager(), "TrackDialogFragment");
        }
    }
}
