package com.Zakovskiy.lwaf.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.utils.ImageUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAvatar extends CircleImageView {
    private Boolean drawRedCircle = false;

    public UserAvatar(Context context) {
        this(context, null);
    }

    public UserAvatar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserAvatar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        this.setBorderColor(getResources().getColor(R.color.blue));
    }

    public void setUser(ShortUser user) {
        setUser(user, getContext());
    }
    public void setUser(ShortUser user, Context context) {
        if (user.avatar != null && !user.avatar.isEmpty()) {
            ImageUtils.loadImage(context, user.avatar, this, true, true);
        } else {
            post(() -> {
                char firstChar = user.nickname.charAt(0);
                ColorGenerator generator = ColorGenerator.DEFAULT; // выберите любой генератор цвета
                int color = generator.getColor(user.nickname); // получаем цвет на основе имени пользователя
                int width = getWidth();
                int newSize = (int) (width * 2.5);
                int fontSize = newSize / 2;
                TextDrawable drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.WHITE)
                        .fontSize(fontSize)
                        .height(newSize)
                        .bold()
                        .width(newSize)
                        .endConfig()
                        .buildRound(String.valueOf(firstChar), color);
                this.setImageDrawable(drawable);
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.setBorderColor(getContext().getColor(user.sex.color));
        }
    }

    public void drawCircle() {
        drawRedCircle = true;
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // рисуем сначала View
        super.dispatchDraw(canvas);

        if (drawRedCircle) {
            Paint paint = new Paint();
            Logs.info("dr");
            int radius = 10; // радиус круга в пикселях
            int padding = 5; // отступ круга от верхнего правого угла View в пикселях
            int x = getWidth() - radius - padding; // координата x центра круга
            int y = radius + padding; // координата y центра круга

            // цвет круга - красный
            paint.setColor(Color.RED);

            // рисуем круг
            canvas.drawCircle(x, y, radius, paint);
        }
    }
}
