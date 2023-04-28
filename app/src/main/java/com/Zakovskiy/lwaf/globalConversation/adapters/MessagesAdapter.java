package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.GlobalMessage;
import com.Zakovskiy.lwaf.models.ShortUser;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter {

    private Context context;
    private List<ShortUser> usernames;
    private List<GlobalMessage> messages;

    public MessagesAdapter(Context context, List<ShortUser> usernames, List<GlobalMessage> messages) {
        super(context, R.layout.item_user_message);
        this.context = context;
        this.usernames = usernames;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_user_message, parent, false);
        TextView username = (TextView) view.findViewById(R.id.username_view);
        username.setText(this.usernames.get(position).toString());
        TextView message = (TextView) view.findViewById(R.id.plain_message_view);
        message.setText(this.messages.get(position).toString());

        return view;
    }
}
