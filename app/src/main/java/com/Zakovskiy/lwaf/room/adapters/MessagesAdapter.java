package com.Zakovskiy.lwaf.room.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.menuDialog.MenuDialogFragment;
import com.Zakovskiy.lwaf.models.MessageGlobal;
import com.Zakovskiy.lwaf.models.MessageRoom;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends ArrayAdapter<MessageRoom> {

    private Context context;
    private List<MessageRoom> messages;
    private
    FragmentManager fragmentManager;

    public MessagesAdapter(Context context,
                           FragmentManager fragmentManager, List<MessageRoom> messages) {
        super(context, R.layout.item_user_message, messages);
        this.context = context;
        this.messages = messages;
        this.fragmentManager = fragmentManager;
    }

    public int getCount() {
        return this.messages.size();
    }

    @Override
    public MessageRoom getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MessageRoom messageGlobal = getItem(position);
        Logs.info("MessagesAdapter RUNNING");
        View view = new View(this.context);
        if (messageGlobal.type == MessageType.TEXT) {
            view = inflater.inflate(R.layout.item_user_message, parent, false);
            TextView username = (TextView) view.findViewById(R.id.username_view);
            username.setText(messageGlobal.user.nickname);
            TextView message = (TextView) view.findViewById(R.id.plain_message_view);
            message.setText(messageGlobal.message);
            TextView date = (TextView) view.findViewById(R.id.message_datetime);
            date.setText((String) TimeUtils.getDateAndTime(messageGlobal.timeSend*1000)); //Moscow time
        } else if (messageGlobal.type == MessageType.JOIN) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setText(String.format("%s %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.join)));

        } else if (messageGlobal.type == MessageType.LEFT) {
            view = inflater.inflate(R.layout.item_system_message, parent, false);
            TextView message = (TextView) view.findViewById(R.id.message_view);
            message.setTextColor(0xFFFF0000);
            message.setText(String.format("%s %s %s",
                    this.context.getString(R.string.user),
                    messageGlobal.user.nickname,
                    this.context.getString(R.string.left)));
        }

        view.setOnClickListener(v -> {
            List<MenuButton> menuButtons = new ArrayList<>();
            menuButtons.add(new MenuButton("bebra", "#FFFFFF", (View vi)->{
                Logs.debug("he");
            }));
            menuButtons.add(new MenuButton("bebrishe", "#F34E6E", (View vi)->{
                Logs.debug("he");
            }));
            MenuDialogFragment.newInstance(context, menuButtons).show(fragmentManager.beginTransaction(), "MenuDialog");
        });

        return view;
    }
}
