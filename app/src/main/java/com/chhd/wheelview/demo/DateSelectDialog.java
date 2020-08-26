package com.chhd.wheelview.demo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.andy.wheelview.base.WheelItemView;
import com.andy.wheelview.base.WheelView;
import com.andy.wheelview.dialog.DateItem;
import com.andy.wheelview.dialog.DateTimeWheelDialog;
import com.blankj.utilcode.util.SizeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;

public class DateSelectDialog extends AppCompatDialogFragment {

    public static DateSelectDialog newInstance() {
        DateSelectDialog dialog = new DateSelectDialog();
        Bundle args = new Bundle();
        dialog.setArguments(args);
        return dialog;
    }

    public static final int SHOW_YEAR = 0;
    public static final int SHOW_YEAR_MONTH = 1;
    public static final int SHOW_YEAR_MONTH_DAY = 2;
    public static final int SHOW_YEAR_MONTH_DAY_HOUR = 3;
    public static final int SHOW_YEAR_MONTH_DAY_HOUR_MINUTE = 4;

    @IntDef({
            SHOW_YEAR,
            SHOW_YEAR_MONTH,
            SHOW_YEAR_MONTH_DAY,
            SHOW_YEAR_MONTH_DAY_HOUR,
            SHOW_YEAR_MONTH_DAY_HOUR_MINUTE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShowConfig {
    }

    private final String TAG = "DateTimeWheelDialog";
    private final int MIN_MONTH = 1;
    private final int MAX_MONTH = 12;
    private final int MIN_DAY = 1;
    private final int MIN_HOUR = 0;
    private final int MAX_HOUR = 23;
    private final int MIN_MINUTE = 0;
    private final int MAX_MINUTE = 59;

    private WheelItemView yearWheelItemView;
    private WheelItemView monthWheelItemView;
    private WheelItemView dayWheelItemView;

    private DateItem[] yearItems;
    private DateItem[] monthItems;
    private DateItem[] dayItems;
    private DateItem[] hourItems;
    private DateItem[] minuteItems;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private Calendar selectedCalendar = Calendar.getInstance();
    private DateTimeWheelDialog.OnClickCallBack cancelCallBack = null;
    private DateTimeWheelDialog.OnClickCallBack okCallBack = null;

    private int showCount = 5;
    private int itemVerticalSpace = 32;
    private boolean isViewInitialized = false;
    private boolean keepLastSelected = false;
    private int showConfig = SHOW_YEAR_MONTH_DAY_HOUR_MINUTE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_date_select, container, false);
    }

    public void show(FragmentManager manager) {
        super.show(manager, getClass().getName());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        setShowCount(7);
        setItemVerticalSpace(SizeUtils.dp2px(24));

        int lineColor = Color.parseColor("#605b81e6");
        int textColor = Color.parseColor("#5b81e6");

        isViewInitialized = true;
        LinearLayout lyPickerContainer = getView().findViewById(R.id.wheel_container);
        //year
        yearWheelItemView = new WheelItemView(lyPickerContainer.getContext());
        yearWheelItemView.setItemVerticalSpace(itemVerticalSpace);
        yearWheelItemView.setShowCount(showCount);
        yearWheelItemView.setMaskLineColor(lineColor);
        yearWheelItemView.setTextColor(textColor);
        yearWheelItemView.getWheelView().setHorizontalOffset(SizeUtils.dp2px(15));
        lyPickerContainer.addView(yearWheelItemView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        //month
        monthWheelItemView = new WheelItemView(lyPickerContainer.getContext());
        monthWheelItemView.setItemVerticalSpace(itemVerticalSpace);
        monthWheelItemView.setShowCount(showCount);
        monthWheelItemView.setMaskLineColor(lineColor);
        monthWheelItemView.setTextColor(textColor);
        lyPickerContainer.addView(monthWheelItemView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        //day
        dayWheelItemView = new WheelItemView(lyPickerContainer.getContext());
        dayWheelItemView.setItemVerticalSpace(itemVerticalSpace);
        dayWheelItemView.setShowCount(showCount);
        dayWheelItemView.setMaskLineColor(lineColor);
        dayWheelItemView.setTextColor(textColor);
        dayWheelItemView.getWheelView().setHorizontalOffset(-SizeUtils.dp2px(15));
        lyPickerContainer.addView(dayWheelItemView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        configShowUI();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date startDate = calendar.getTime();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2020);
        Date endDate = calendar.getTime();

        setDateArea(startDate, endDate, true);

        Calendar current = Calendar.getInstance();
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);
        updateSelectedDate(new Date(current.getTimeInMillis()));
    }

    public void setDateArea(@NonNull Date startDate, @NonNull Date endDate, boolean keepLastSelected) {
        if (startDate.after(endDate))
            throw new IllegalArgumentException("start date should be before end date");
        startCalendar.setTime(startDate);
        endCalendar.setTime(endDate);
        selectedCalendar.setTimeInMillis(startDate.getTime());
        this.keepLastSelected = keepLastSelected;
        initAreaDate();
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public void setItemVerticalSpace(int itemVerticalSpace) {
        this.itemVerticalSpace = itemVerticalSpace;
    }

    private void initAreaDate() {
        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        int endMonth = endCalendar.get(Calendar.MONTH) + 1;
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startCalendar.get(Calendar.MINUTE);
        int endMinute = endCalendar.get(Calendar.MINUTE);

        yearItems = updateItems(DateItem.TYPE_YEAR, startYear, endYear);
        monthItems = updateItems(DateItem.TYPE_MONTH, startMonth, endMonth);
        int dayActualMaximum = startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayItems = updateItems(DateItem.TYPE_DAY, startDay, endDay);
        hourItems = updateItems(DateItem.TYPE_HOUR, startHour, endHour);
        minuteItems = updateItems(DateItem.TYPE_MINUTE, startMinute, endMinute);
        yearWheelItemView.setItems(yearItems);
        monthWheelItemView.setItems(monthItems);
        dayWheelItemView.setItems(dayItems);
    }

    private DateItem[] updateItems(@DateItem.DateType int type, int startValue, int endValue) {
        int index = -1;
        DateItem[] items = new DateItem[endValue - startValue + 1];
        for (int i = startValue; i <= endValue; i++) {
            index++;
            items[index] = new DateItem(type, i);
        }
        return items;
    }

    public void configShowUI() {
        yearWheelItemView.setTotalOffsetX(0);
        monthWheelItemView.setTotalOffsetX(0);
        dayWheelItemView.setTotalOffsetX(0);

        yearWheelItemView.setVisibility(View.VISIBLE);
        monthWheelItemView.setVisibility(View.VISIBLE);
        dayWheelItemView.setVisibility(View.VISIBLE);
    }

    public void updateSelectedDate(@NonNull Date selectedDate) {
        if (selectedDate.before(startCalendar.getTime()) || selectedDate.after(endCalendar.getTime()))
            throw new IllegalArgumentException("selected date must be between start date and end date");
        selectedCalendar.setTime(selectedDate);
        initSelectedDate();
        initOnScrollListener();
    }

    private void initSelectedDate() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);
        int index = findSelectedIndexByValue(yearItems, year);
        yearWheelItemView.setSelectedIndex(index, false);
        index = findSelectedIndexByValue(monthItems, month + 1);
        monthWheelItemView.setSelectedIndex(index, false);
        index = findSelectedIndexByValue(dayItems, day);
        dayWheelItemView.setSelectedIndex(index, false);
    }

    private void initOnScrollListener() {
        yearWheelItemView.setOnSelectedListener(new WheelView.OnSelectedListener() {
            @Override
            public void onSelected(Context context, int selectedIndex) {
                selectedCalendar.set(Calendar.YEAR, yearItems[selectedIndex].getValue());
                if (showConfig > SHOW_YEAR)
                    onYearChanged();
            }
        });
        monthWheelItemView.setOnSelectedListener(new WheelView.OnSelectedListener() {
            @Override
            public void onSelected(Context context, int selectedIndex) {
                selectedCalendar.set(Calendar.MONTH, monthItems[selectedIndex].getValue() - 1);
                if (showConfig > SHOW_YEAR_MONTH)
                    onMonthChanged();
            }
        });
        dayWheelItemView.setOnSelectedListener(new WheelView.OnSelectedListener() {
            @Override
            public void onSelected(Context context, int selectedIndex) {
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayItems[selectedIndex].getValue());
                if (showConfig > SHOW_YEAR_MONTH_DAY)
                    onDayChanged();
            }
        });
    }

    private void onYearChanged() {
        //update month list
        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        int endMonth = endCalendar.get(Calendar.MONTH) + 1;
        int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
        int tempIndex = -1;
        int lastSelectedIndex = -1;
        int startValue, endValue;
        if (isSameValue(selectedYear, startYear)) {
            startValue = startMonth;
            endValue = MAX_MONTH;
        } else if (isSameValue(selectedYear, endYear)) {
            startValue = MIN_MONTH;
            endValue = endMonth;
        } else {
            startValue = MIN_MONTH;
            endValue = MAX_MONTH;
        }
        monthItems = new DateItem[endValue - startValue + 1];
        for (int i = startValue; i <= endValue; i++) {
            tempIndex++;
            monthItems[tempIndex] = new DateItem(DateItem.TYPE_MONTH, i);
            if (isSameValue(selectedMonth, i)) {
                lastSelectedIndex = tempIndex;
            }
        }
        int newSelectedIndex = keepLastSelected ? (lastSelectedIndex == -1 ? 0 : lastSelectedIndex) : 0;
        monthWheelItemView.setItems(monthItems);
        monthWheelItemView.setSelectedIndex(newSelectedIndex);
    }

    private void onMonthChanged() {
        //update day list
        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        int endMonth = endCalendar.get(Calendar.MONTH) + 1;
        int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);
        int tempIndex = -1;
        int lastSelectedIndex = -1;
        int startValue, endValue;
        if (isSameValue(selectedYear, startYear) && isSameValue(selectedMonth, startMonth)) {
            startValue = startDay;
            endValue = selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else if (isSameValue(selectedYear, endYear) && isSameValue(selectedMonth, endMonth)) {
            startValue = MIN_DAY;
            endValue = endDay;
        } else {
            startValue = MIN_DAY;
            endValue = selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        dayItems = new DateItem[endValue - startValue + 1];
        for (int i = startValue; i <= endValue; i++) {
            tempIndex++;
            dayItems[tempIndex] = new DateItem(DateItem.TYPE_DAY, i);
            if (isSameValue(selectedDay, i)) {
                lastSelectedIndex = tempIndex;
            }
        }
        int newSelectedIndex = keepLastSelected ? (lastSelectedIndex == -1 ? 0 : lastSelectedIndex) : 0;
        dayWheelItemView.setItems(dayItems);
        dayWheelItemView.setSelectedIndex(newSelectedIndex);
    }

    private void onDayChanged() {
        //update hour list
        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        int endMonth = endCalendar.get(Calendar.MONTH) + 1;
        int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int selectedHour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int tempIndex = -1;
        int lastSelectedIndex = -1;
        int startValue, endValue;
        if (isSameValue(selectedYear, startYear) && isSameValue(selectedMonth, startMonth) && isSameValue(selectedDay, startDay)) {
            startValue = startHour;
            endValue = MAX_HOUR;
        } else if (isSameValue(selectedYear, endYear) && isSameValue(selectedMonth, endMonth) && isSameValue(selectedDay, endDay)) {
            startValue = MIN_HOUR;
            endValue = endHour;
        } else {
            startValue = MIN_HOUR;
            endValue = MAX_HOUR;
        }
        hourItems = new DateItem[endValue - startValue + 1];
        for (int i = startValue; i <= endValue; i++) {
            tempIndex++;
            hourItems[tempIndex] = new DateItem(DateItem.TYPE_HOUR, i);
            if (isSameValue(selectedHour, i)) {
                lastSelectedIndex = tempIndex;
            }
        }
        int newSelectedIndex = keepLastSelected ? (lastSelectedIndex == -1 ? 0 : lastSelectedIndex) : 0;
    }

    private void onHourChanged() {
        //update minute list
        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        int endMonth = endCalendar.get(Calendar.MONTH) + 1;
        int selectedMonth = selectedCalendar.get(Calendar.MONTH) + 1;
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        int selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int selectedHour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startCalendar.get(Calendar.MINUTE);
        int endMinute = endCalendar.get(Calendar.MINUTE);
        int selectedMinute = selectedCalendar.get(Calendar.MINUTE);
        int tempIndex = -1;
        int lastSelectedIndex = -1;
        int startValue, endValue;
        if (isSameValue(selectedYear, startYear) && isSameValue(selectedMonth, startMonth) && isSameValue(selectedDay, startDay) && isSameValue(selectedHour, startHour)) {
            startValue = startMinute;
            endValue = MAX_MINUTE;
        } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
            startValue = MIN_MINUTE;
            endValue = endMinute;
        } else {
            startValue = MIN_MINUTE;
            endValue = MAX_MINUTE;
        }
        minuteItems = new DateItem[endValue - startValue + 1];
        for (int i = startValue; i <= endValue; i++) {
            tempIndex++;
            minuteItems[tempIndex] = new DateItem(DateItem.TYPE_MINUTE, i);
            if (isSameValue(selectedMinute, i)) {
                lastSelectedIndex = tempIndex;
            }
        }
        int newSelectedIndex = keepLastSelected ? (lastSelectedIndex == -1 ? 0 : lastSelectedIndex) : 0;
    }

    private int findSelectedIndexByValue(DateItem[] items, int value) {
        int selectedIndex = 0;
        for (int i = 0; i < items.length; i++) {
            if (isSameValue(value, items[i].getValue())) {
                selectedIndex = i;
                break;
            }
        }
        return selectedIndex;
    }

    private boolean isSameValue(int value1, int value2) {
        return value1 == value2;
    }
}
