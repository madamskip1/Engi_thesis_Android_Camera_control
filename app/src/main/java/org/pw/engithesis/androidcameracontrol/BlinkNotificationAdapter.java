package org.pw.engithesis.androidcameracontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BlinkNotificationAdapter {
    static int counter = 0;
    private final LayoutInflater layoutInflater;
    private final AppCompatActivity ctx;

    public BlinkNotificationAdapter(AppCompatActivity ctx) {
        this.ctx = ctx;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addBox() {
        LinearLayout blinkMsgBox = (LinearLayout) ctx.findViewById(R.id.blink_msg_box);
        Button button = prepareButton();
        blinkMsgBox.addView(button);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                blinkMsgBox.removeView(button);
            }
        }, 5000);
    }

    public Button prepareButton() {
        Button button = new Button(ctx);
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("'['HH:mm:ss, dd-MM-yyyy']'");
        String buttonText = "Mrugnięto o " + dateFormat.format(date);
        button.setText(buttonText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200);
        params.weight = 1f;
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);

        if (counter % 2 == 0) {
            button.setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.holo_blue_light));
        } else {
            button.setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.holo_green_dark));
        }

        return button;
    }
}
