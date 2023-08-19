package com.Zakovskiy.lwaf.adminPanel.dialogs.reports.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.adminPanel.dialogs.clients.adapters.AdminClientsAdapter;
import com.Zakovskiy.lwaf.models.AdminReport;
import com.Zakovskiy.lwaf.models.Client;
import com.Zakovskiy.lwaf.models.enums.ReportStatus;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.utils.TimeUtils;
import com.Zakovskiy.lwaf.widgets.UserAvatar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdminReportsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AdminReport> adminReports;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Context context;
    private FragmentManager fragmentManager;

    public AdminReportsAdapter(Context context, FragmentManager fragmentManager, List<AdminReport> adminReports) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.adminReports = adminReports;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_admin_report, parent, false);
        return new ReportsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdminReport adminReport = this.adminReports.get(position);
        ReportsViewHolder textHolder = (ReportsViewHolder) holder;
        textHolder.bind(adminReport);
    }

    @Override
    public int getItemCount() {
        return this.adminReports.size();
    }

    private class ReportsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvFromNickname;
        private TextView tvToNickname;
        private UserAvatar uaFrom;
        private UserAvatar uaTo;
        private CardView cvStatus;
        private LinearLayout llTo;
        private LinearLayout llFrom;
        private ImageView ivCloseReport;
        private TextView tvTimeCreate;
        private TextView tvTimeClose;


        public ReportsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvContent = itemView.findViewById(R.id.tvContent);
            this.tvToNickname = itemView.findViewById(R.id.tvNicknameTo);
            this.tvFromNickname = itemView.findViewById(R.id.tvNicknameFrom);
            this.uaFrom = itemView.findViewById(R.id.uaFrom);
            this.uaTo = itemView.findViewById(R.id.uaTo);
            this.cvStatus = itemView.findViewById(R.id.cvStatus);
            this.llTo = itemView.findViewById(R.id.llTo);
            this.llFrom = itemView.findViewById(R.id.llFrom);
            this.ivCloseReport = itemView.findViewById(R.id.ivCloseReport);
            this.tvTimeClose = itemView.findViewById(R.id.tvTimeClose);
            this.tvTimeCreate = itemView.findViewById(R.id.tvTimeCreate);
        }

        public void bind(AdminReport adminReport) {
            this.cvStatus.setCardBackgroundColor(context.getColor(adminReport.status.color));
            this.llTo.setOnClickListener((view)->{
                ProfileDialogFragment.newInstance(context, adminReport.toUser.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.llFrom.setOnClickListener((view)->{
                ProfileDialogFragment.newInstance(context, adminReport.fromUser.userId).show(fragmentManager, "ProfileDialogFragment");
            });
            this.tvTimeCreate.setText(TimeUtils.getDateAndTime(adminReport.timeCreate*1000));
            if(adminReport.status == ReportStatus.OPEN) {
                this.ivCloseReport.setVisibility(View.VISIBLE);
                this.ivCloseReport.setOnClickListener((view) -> {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ADMIN_CLOSE_REPORT);
                    data.put(PacketDataKeys.REPORT_ID, adminReport.reportId);
                    socketHelper.sendData(new JSONObject(data));
                });
            } else if(adminReport.timeClose > 0) {
                this.tvTimeClose.setVisibility(View.VISIBLE);
                this.tvTimeClose.setText(String.format("%s: %s", context.getString(R.string.closed), TimeUtils.getDateAndTime(adminReport.timeClose*1000)));
            }
            ((TextView)this.cvStatus.findViewById(R.id.tvStatus)).setText(context.getString(adminReport.status.title));
            this.uaFrom.setUser(adminReport.fromUser);
            this.tvToNickname.setText(adminReport.toUser.nickname);
            this.tvFromNickname.setText(adminReport.fromUser.nickname);
            this.uaTo.setUser(adminReport.toUser);
            this.tvTitle.setText(context.getString(adminReport.type.title));
            this.tvContent.setText(adminReport.content);
        }
    }
}
