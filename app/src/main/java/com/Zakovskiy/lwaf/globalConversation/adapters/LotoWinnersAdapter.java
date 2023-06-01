package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.LotoWinner;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.StringUtils;

import java.util.List;

public class LotoWinnersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String message;
    private FragmentManager fragmentManager;
    private List<LotoWinner> llw;

    public LotoWinnersAdapter(Context context, List<LotoWinner> lotoWinnerList, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.llw = lotoWinnerList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.item_loto_winner, parent, false);
        return new LWViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LotoWinner lw = this.llw.get(position);
        LWViewHolder lwvh = (LWViewHolder) holder;
        lwvh.bind(lw);
    }

    @Override
    public int getItemCount() {
        return this.llw.size();
    }

    class LWViewHolder extends RecyclerView.ViewHolder {
        private TextView numbersView;
        private TextView winView;
        public LWViewHolder(@NonNull View itemView) {
            super(itemView);
            numbersView = itemView.findViewById(R.id.lotoWinnerNumbers);
            winView = itemView.findViewById(R.id.lotoWinnerSize);
        }

        public void bind(LotoWinner lw) {
            this.numbersView.setText(lw.user.nickname + " - " + StringUtils.join_l(lw.lotoNumbers, ", "));
            this.winView.setText(String.valueOf(lw.winSize));
        }
    }
}
