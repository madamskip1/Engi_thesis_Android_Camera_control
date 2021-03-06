package org.pw.engithesis.androidcameracontrol.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.pw.engithesis.androidcameracontrol.FaceAnalyser;
import org.pw.engithesis.androidcameracontrol.R;
import org.pw.engithesis.androidcameracontrol.interfaces.Observer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BlinkActivity extends AppCompatActivity implements Observer {

    private int buttonsCounter = 0;
    private LinearLayout blinkMsgBox;

    @Override
    public void update() {
        addBox();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.main_activity_btn_text_blink);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blink);
        blinkMsgBox = findViewById(R.id.blink_msg_box);

        FaceAnalyser faceAnalyser = new FaceAnalyser(this);
        faceAnalyser.attachObserverToBlinkDetector(this);
        faceAnalyser.start();
    }

    private void addBox() {
        Button button = createButton();
        blinkMsgBox.addView(button);
        new Handler(Looper.getMainLooper()).postDelayed(() -> blinkMsgBox.removeView(button), 5000);
    }

    private Button createButton() {
        Button button = new Button(this);
        String buttonText = "Mrugnięto o " + getTime() + ".";
        button.setText(buttonText);
        button.setBackgroundColor(ContextCompat.getColor(this, getButtonColor(buttonsCounter)));
        buttonsCounter++;

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(10, 10, 10, 20);
        button.setLayoutParams(buttonLayoutParams);

        return button;
    }

    private String getTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("'['HH:mm:ss:SSS']'", new Locale("pl", "PL"));

        return dateFormat.format(date);
    }

    private int getButtonColor(int buttonsCounter) {
        return (buttonsCounter % 2 == 0) ? android.R.color.holo_blue_light : android.R.color.holo_green_dark;
    }
}