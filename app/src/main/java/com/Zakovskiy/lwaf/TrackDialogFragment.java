package com.Zakovskiy.lwaf;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.Zakovskiy.lwaf.models.Track;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.TimeUtils;

import org.w3c.dom.Text;

public class TrackDialogFragment extends DialogFragment {
    private Context context;
    private Track track;
    private long timestamp;
    private SeekBar seekBar;
    private TextView currentTime;
    public TrackDialogFragment(Context context, Track track) {
        super();
        this.context = context;
        this.track = track;
        this.timestamp = track.timeStamp;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.dark_transparent)));
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceBundle) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(1);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_fragment_track);
        TextView whoSet = dialog.findViewById(R.id.whoSetTrack);
        whoSet.setText(Html.fromHtml(String.format("%s %s", context.getString(R.string.Set), "<b>"+this.track.user.nickname+"</b>")));
        ImageView trackIcon = dialog.findViewById(R.id.trackIcon);
        if (this.track.icon.equals("")) {
            trackIcon.setImageResource(R.drawable.without_preview);
        } else {
            ImageUtils.loadImage(this.context, this.track.icon, trackIcon, false, false);
        }
        TextView author = dialog.findViewById(R.id.tvTrackAuthor);
        author.setText(this.track.artist);
        author.setSelected(true);
        TextView title = dialog.findViewById(R.id.tvTrackTitle);
        title.setText(this.track.title);
        title.setSelected(true);
        this.seekBar = dialog.findViewById(R.id.seekBar);
        TextView allTime = dialog.findViewById(R.id.allTime);
        allTime.setText(TimeUtils.secondsToDur(this.track.duration));
        this.currentTime = dialog.findViewById(R.id.momentTime);
        this.seekBar.setMax(this.track.duration);
        this.seekBar.setOnTouchListener((view, motionEvent) -> true);
        repeat();


        return dialog;
    }

    private void repeat() {
        this.seekBar.postDelayed(
                () -> {
                    int alreadyPlayed = (int) (System.currentTimeMillis()/1000 - timestamp);
                    if (alreadyPlayed >= this.track.duration) {
                        this.dismiss();
                        return;
                    }
                    this.currentTime.setText(TimeUtils.secondsToDur(alreadyPlayed));
                    this.seekBar.setProgress(alreadyPlayed);

                    repeat();
                }, 1000
        );
    }
}
