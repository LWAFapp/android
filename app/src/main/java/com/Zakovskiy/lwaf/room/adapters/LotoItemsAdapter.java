package com.Zakovskiy.lwaf.room.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.room.models.LotoItem;
import com.Zakovskiy.lwaf.utils.Logs;

import java.util.List;

public class LotoItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<LotoItem> lotoItems;
    private Boolean access = true;

    public LotoItemsAdapter(Context context, List<LotoItem> lotoItems){
        this.context = context;
        this.lotoItems = lotoItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_btn_loto, parent, false);
        return new LotoItemViewHolder(view);
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public LotoItem getItem(int position){
        return this.lotoItems.get(position);
    };

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LotoItem lotoItem = getItem(position);
        LotoItemViewHolder itemHolder = (LotoItemViewHolder) holder;
        itemHolder.bind(lotoItem);
    }

    public void setValid(Integer num) {

    }

    @Override
    public int getItemCount() {
        return this.lotoItems.size();
    }

    private class LotoItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llLotoItem = null;
        private TextView tvLotoItem;

        public LotoItemViewHolder(@NonNull View itemView) {
            super(itemView);
            llLotoItem = itemView.findViewById(R.id.btnLotoItem);
            tvLotoItem = itemView.findViewById(R.id.tvLotoItem);
        }

        public void bind(LotoItem lotoItem) {
            if(lotoItem.valid){
                tvLotoItem.setTextColor(Color.GREEN);
            }
            if(lotoItem.pressed) {
                llLotoItem.setBackgroundColor(context.getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
            }
            tvLotoItem.setText(String.valueOf(lotoItem.number));
            llLotoItem.setOnClickListener((v)->{
                if (!access) return;
                int pressedCount = 0;
                for(int i=0; i < lotoItems.size(); i++) {
                    if(lotoItems.get(i).pressed) {
                        pressedCount++;
                    }
                }
                Logs.info(String.valueOf(pressedCount));
                if(lotoItem.pressed) {
                    lotoItem.pressed = false;
                    llLotoItem.setBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    if(pressedCount < Application.lwafServerConfig.lotoCounts) {
                        if(pressedCount != 0 && pressedCount + 1 > Application.lwafCurrentUser.balance) {
                            new DialogTextBox(context, context.getString(R.string.enough_coins)).show();
                            return;
                        }
                        lotoItem.pressed = true;
                        llLotoItem.setBackgroundColor(context.getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
                    }
                }
            });
        }
    }
}
