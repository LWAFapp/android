package com.Zakovskiy.lwaf.globalConversation.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.profileDialog.ProfileDialogFragment;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends ArrayAdapter<ShortUser> {

    public Context context;
    public List<ShortUser> users;
    public FragmentManager fragmentManager;

    public UsersAdapter(Context context, FragmentManager fragmentManager, List<ShortUser> users) {
        super(context, R.layout.item_user_in_listview, users);
        this.context = context;
        this.users = users;
        this.fragmentManager = fragmentManager;
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
        View view = inflater.inflate(R.layout.item_user_in_listview, parent, false);
        view.setOnClickListener((v)->{
            ProfileDialogFragment.newInstance(context, shortUser.userId).show(fragmentManager, "ProfileDialogFragment");
        });
        TextView username = view.findViewById(R.id.username);
        CircleImageView avatarImage = view.findViewById(R.id.circleImageView2);
        username.setText(shortUser.nickname);
        if (shortUser.sex == Sex.FEMALE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                avatarImage.setBorderColor(context.getColor(R.color.red));
            }
        }
        if (shortUser.avatar != null) {
            ImageUtils.loadImage(context, shortUser.avatar, avatarImage, true, true);
        } else {
            char firstChar = shortUser.nickname.charAt(0);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(shortUser.nickname); // получаем цвет на основе имени пользователя
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .fontSize(50)
                    .height(100)
                    .width(100)
                    .endConfig()
                    .buildRound(String.valueOf(firstChar), color);
            avatarImage.setImageDrawable(drawable);
        }
        return view;
    }
}
