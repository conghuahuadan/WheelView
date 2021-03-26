package com.chhd.wheelview.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.andy.wheelview.dialog.DateTimeWheelDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// https://github.com/JustinRoom/WheelViewDemo
public class MainActivity extends AppCompatActivity {

    public static long toLong(String from, String fromPattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(fromPattern, Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            return format.parse(from).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectDialog dialog = DateSelectDialog.newInstance(null);
                dialog.setOnDateSelectedCallback(new DateSelectDialog.OnDateSelectedCallback() {
                    @Override
                    public void onDateSelected(long millis) {

                    }
                });
                dialog.show(getSupportFragmentManager());
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY_HOUR_MINUTE);
            }
        });
    }

    private DateTimeWheelDialog createDialog(int type) {
        Calendar curCal = Calendar.getInstance();
        curCal.set(Calendar.YEAR, 2020);
        curCal.set(Calendar.MONTH, 0);
        curCal.set(Calendar.DAY_OF_MONTH, 1);
        curCal.set(Calendar.HOUR_OF_DAY, 0);
        curCal.set(Calendar.MINUTE, 0);
        curCal.set(Calendar.SECOND, 0);
        curCal.set(Calendar.MILLISECOND, 0);
        Date startDate = curCal.getTime();

        Calendar endCal = Calendar.getInstance();
        Date endDate = endCal.getTime();

        DateTimeWheelDialog dialog = new DateTimeWheelDialog(this);
        dialog.setShowCount(7);
        dialog.setItemVerticalSpace(30);
        dialog.show();
        dialog.setTitle("选择时间");
        int config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY_HOUR_MINUTE;
        switch (type) {
            case 0:
                config = DateTimeWheelDialog.SHOW_YEAR;
                break;
            case 1:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH;
                break;
            case 2:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY;
                break;
            case 3:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY_HOUR;
                break;
            case 4:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY_HOUR_MINUTE;
                break;
        }
        dialog.configShowUI(config);
        dialog.setCancelButton("取消", null);
        dialog.setOKButton("确定", new DateTimeWheelDialog.OnClickCallBack() {
            @Override
            public boolean callBack(View v, @NonNull Date selectedDate) {
                return false;
            }
        });
        dialog.setDateArea(startDate, endDate, true);
        dialog.updateSelectedDate(endDate);
        return dialog;
    }
}