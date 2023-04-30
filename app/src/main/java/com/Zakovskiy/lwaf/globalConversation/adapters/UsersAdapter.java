package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.utils.Logs;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<ShortUser> {

    public Context context;
    public List<ShortUser> users;

    public UsersAdapter(Context context, List<ShortUser> users) {
        super(context, R.layout.item_user_in_listview, users);
        this.context = context;
        this.users = users;
    }
    public int getCount() {
        return this.users.size();
    }

    @Override
    public ShortUser getItem(int position) {
        return this.users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ShortUser shortUser = getItem(position);
        Logs.info("MessagesAdapter RUNNING");
        View view = new View(this.context);
        view = inflater.inflate(R.layout.item_user_in_listview, parent, false);
        TextView username = (TextView) view.findViewById(R.id.username);
        ImageView sexView = (ImageView) view.findViewById(R.id.sexShape);
        username.setText(shortUser.nickname);
        if (shortUser.sex == Sex.FEMALE) {
            sexView.setImageDrawable(this.context.getResources().getDrawable(R.drawable.sex_white));
        }
        return view;
    }
}
