package com.Zakovskiy.lwaf.wallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.Operation;
import com.Zakovskiy.lwaf.models.enums.OperationType;
import com.Zakovskiy.lwaf.utils.TimeUtils;

import java.util.List;

public class OperationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    public Context context;
    public List<Operation> operations;

    public OperationsAdapter(Context context, List<Operation> operations) {
        this.context = context;
        this.operations = operations;
    }

    @Override
    public int getItemViewType(int position) {
        return operations.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new OperationViewHolder(inflater.inflate(R.layout.item_operation, parent, false));
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shimmer_operation, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OperationViewHolder) {
            Operation operation = operations.get(position);
            OperationViewHolder operationViewHolder = (OperationViewHolder) holder;
            operationViewHolder.bind(operation);
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.bind();
        }
    }

    @Override
    public int getItemCount() {
        return this.operations.size();
    }

    private class OperationViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;
        private TextView date;
        private TextView value;
        private View view;

        public OperationViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ivOperationIcon);
            title = itemView.findViewById(R.id.tvTitle);
            date = itemView.findViewById(R.id.tvDate);
            value = itemView.findViewById(R.id.tvValue);
            this.view = itemView;
        }

        public void bind(Operation operation) {
            OperationType operationType = operation.operationType;
            icon.setImageDrawable(context.getDrawable(operationType.resId));
            title.setText(context.getString(operationType.title));
            date.setText(TimeUtils.getDateAndTime(operation.timeStamp));
            String extAdd = "";
            if(operation.value < 0) {
                value.setTextColor(context.getColor(R.color.red));
            } else {
                extAdd = "+";
                value.setTextColor(context.getColor(R.color.green));
            }
            value.setText(String.format("%s%s", extAdd, operation.value));
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View viewHolder) {
            super(viewHolder);
        }

        public void bind() {

        }
    }
}
