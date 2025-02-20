package com.gallery.photos.editpic.callendservice.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.callendservice.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class DateTimePicker extends FrameLayout {
    private Context context;
    private OnDateChangeListener dateChangeListener;
    private List<String> dateList, hoursList, minutesList;
    private int daysForward;
    private WheelPicker datePicker, hourPicker, minutesPicker;
    private int itemsDistanceBottomDate, itemsDistanceBottomHours, itemsDistanceBottomMinutes, itemsDistanceDate, itemsDistanceHours, itemsDistanceMinutes, itemsDistanceTopDate, itemsDistanceTopHours, itemsDistanceTopMinutes;
    private ConstraintLayout layout;
    private long selectedDate;

    public interface OnDateChangeListener {
        void vor(long j);
    }

    public void vibrate() {
    }

    public DateTimePicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.itemsDistanceDate = 86;
        this.itemsDistanceTopDate = 86;
        this.itemsDistanceBottomDate = -86;
        this.itemsDistanceHours = 86;
        this.itemsDistanceTopHours = 86;
        this.itemsDistanceBottomHours = -86;
        this.itemsDistanceMinutes = 86;
        this.itemsDistanceTopMinutes = 86;
        this.itemsDistanceBottomMinutes = -86;
        this.selectedDate = 0L;
        this.daysForward = 30;
        this.context = context;
        init();
    }

    @SuppressLint("DefaultLocale")
    private void init() {
        Log.e("DateTimePicker", "init: ");
        setLayoutParams(new LayoutParams(-1, -2));
        this.layout = (ConstraintLayout) View.inflate(getContext(), R.layout.layout_time_date_picker, null);
        this.datePicker = (WheelPicker) this.layout.findViewById(R.id.date_picker);
        this.hourPicker = (WheelPicker) this.layout.findViewById(R.id.hour_picker);
        this.minutesPicker = (WheelPicker) this.layout.findViewById(R.id.minutes_picker);
        this.datePicker.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrollStateChanged(int i) {
            }

            @Override
            public void onWheelScrolled(int i) {
                if (i <= itemsDistanceTopDate) {
                    if (i < itemsDistanceBottomDate) {
                        vibrate();
                        itemsDistanceTopDate -= itemsDistanceDate;
                        itemsDistanceBottomDate -= itemsDistanceDate;
                        return;
                    }
                    return;
                }
                vibrate();
                itemsDistanceTopDate += itemsDistanceDate;
                itemsDistanceBottomDate += itemsDistanceDate;
            }

            @Override
            public void onWheelSelected(int i) {
                if (dateChangeListener != null) {
                    dateChangeListener.vor(getDate());
                }
            }
        });
        this.hourPicker.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrollStateChanged(int i) {
            }

            @Override
            public void onWheelScrolled(int i) {
                if (i <= itemsDistanceTopHours) {
                    if (i < itemsDistanceBottomHours) {
                        vibrate();
                        itemsDistanceTopHours -= itemsDistanceHours;
                        itemsDistanceBottomHours -= itemsDistanceHours;
                        return;
                    }
                    return;
                }
                vibrate();
                itemsDistanceTopHours += itemsDistanceHours;
                itemsDistanceBottomHours += itemsDistanceHours;
            }

            @Override
            public void onWheelSelected(int i) {
                if (dateChangeListener != null) {
                    dateChangeListener.vor(getDate());
                }
            }
        });
        this.minutesPicker.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrollStateChanged(int i) {
            }

            @Override
            public void onWheelScrolled(int i) {
                if (i <= itemsDistanceTopMinutes) {
                    if (i < itemsDistanceBottomMinutes) {
                        vibrate();
                        itemsDistanceTopMinutes -= itemsDistanceMinutes;
                        itemsDistanceBottomMinutes -= itemsDistanceMinutes;
                        return;
                    }
                    return;
                }
                vibrate();
                itemsDistanceTopMinutes += itemsDistanceMinutes;
                itemsDistanceBottomMinutes += itemsDistanceMinutes;
            }

            @Override
            public void onWheelSelected(int i) {
                if (dateChangeListener != null) {
                    dateChangeListener.vor(getDate());
                }
            }
        });
        this.dateList = addDatesToList();
        this.hoursList = Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        this.minutesList = new ArrayList();
        for (int i = 0; i < 60; i += 5) {
            this.minutesList.add(String.format("%02d", Integer.valueOf(i)));
        }
        this.datePicker.setData(this.dateList);
        this.hourPicker.setData(this.hoursList);
        this.minutesPicker.setData(this.minutesList);
        addView(this.layout, new LayoutParams(-1, -2));
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("DateTimePicker", "onGlobalLayout: setData " + selectedDate);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (selectedDate > 0) {
                    setDate(selectedDate);
                }
            }
        });
    }

    public void setDaysForward(int i) {
        this.daysForward = i;
        init();
    }

    public List<String> addDatesToList() {
        ArrayList<String> arrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < this.daysForward - 2; i++) {
            arrayList.add(Utils.getPrettyDate(this.context, calendar.getTimeInMillis()));
            calendar.add(6, 1);
        }
        return arrayList;
    }

    public long getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, this.hourPicker.getCurrentItemPosition());
        calendar.set(12, this.minutesPicker.getCurrentItemPosition() * 5);
        calendar.roll(6, this.datePicker.getCurrentItemPosition());
        calendar.set(13, 0);
        return calendar.getTimeInMillis();
    }

    public void setDate(long j) {
        int indexOf;
        this.selectedDate = j;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        Calendar calendar2 = Calendar.getInstance();
        int i = calendar.get(11);
        int i2 = calendar.get(12) / 5;
        if (calendar.get(6) == calendar2.get(6)) {
            indexOf = 0;
        } else {
            indexOf = calendar.get(6) - 1 == calendar2.get(6) ? 1 : this.datePicker.getData().indexOf(Utils.getPrettyDate(this.context, calendar.getTimeInMillis()));
        }
        Log.e("DateTimePicker", "setDate: " + i + ", " + i2 + ", " + indexOf);
        this.hourPicker.setSelectedItemPosition(i, true);
        this.minutesPicker.setSelectedItemPosition(i2, true);
        this.datePicker.setSelectedItemPosition(indexOf, true);
    }

    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
        this.dateChangeListener = onDateChangeListener;
    }
}
