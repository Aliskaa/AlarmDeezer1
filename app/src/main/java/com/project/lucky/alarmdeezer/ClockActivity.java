package com.project.lucky.alarmdeezer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by lucky on 16/10/16.
 */
public class ClockActivity extends BaseActivity {

    private TimePicker timePicker;
    private EditText editText;
    private static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
    private static int snooze = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);

        findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent;
                intent = new Intent(ClockActivity.this, HomeActivity.class);
                intent.putExtra("timeHour", timeHour);
                intent.putExtra("timeMinute", timeMinute);
                intent.putExtra("snooze", editText.getText());
                startActivity(intent);
                ClockActivity.this.finish();
            }
        });

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timeHour = hourOfDay;
                timeMinute = minute;
            }
        });

        editText = (EditText) findViewById(R.id.snooze);


    }

}
