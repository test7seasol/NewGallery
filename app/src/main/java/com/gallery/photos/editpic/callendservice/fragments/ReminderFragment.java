package com.gallery.photos.editpic.callendservice.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.callendservice.ReminderReceiver;
import com.gallery.photos.editpic.callendservice.adapter.ReminderAdapter;
import com.gallery.photos.editpic.callendservice.custom.DateTimePicker;
import com.gallery.photos.editpic.callendservice.db.MyDB;
import com.gallery.photos.editpic.callendservice.interfaces.ReminderdeleteClick;
import com.gallery.photos.editpic.callendservice.model.Reminder;
import com.gallery.photos.editpic.callendservice.utils.CDOUtiler;
import com.gallery.photos.editpic.databinding.FragmentReminderCdoBinding;
import com.gallery.photos.editpic.databinding.ListItemReminderColorBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderFragment extends Fragment {
    FragmentReminderCdoBinding binding;
    MyDB databaseHelper;
    ReminderAdapter reminderAdapter;
    ReminderColorAdapter reminderColorAdapter;
    int[] reminderColors;
    ArrayList<Reminder> reminderList;
    long selectTime;
    int selectedColor = 0;
    String contactNumber = "";

    public static ReminderFragment getInstance(String str) {
        ReminderFragment reminderFragment = new ReminderFragment();
        reminderFragment.contactNumber = str;
        return reminderFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentReminderCdoBinding.inflate(layoutInflater, viewGroup, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        try {
            @SuppressLint("Recycle") TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.reminder_colors);
            this.reminderColors = new int[obtainTypedArray.length()];
            for (int i = 0; i < obtainTypedArray.length(); i++) {
                this.reminderColors[i] = obtainTypedArray.getColor(i, 0);
            }
            this.databaseHelper = new MyDB(getContext());
            this.reminderList = new ArrayList<>();
            this.reminderList = this.databaseHelper.getReminderList();
            if (reminderList == null) {
                this.reminderList = new ArrayList<>();
            }
            this.reminderAdapter = new ReminderAdapter(getContext(), this.reminderColors);
            reminderAdapter.setOnCallLogClick(new ReminderdeleteClick() {
                @Override
                public void onDelete(Reminder reminder) {
                    databaseHelper.deleteReminder(reminder);
                    if (reminderAdapter.getItemCount() > 0) {
                        binding.emptyView.setVisibility(View.GONE);
                    } else {
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }
                }
            });
            this.reminderColorAdapter = new ReminderColorAdapter(getContext());
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void initView() {
        try {
            this.binding.reminderListView.setLayoutManager(new LinearLayoutManager(getContext()));
            this.binding.reminderListView.setAdapter(this.reminderAdapter);
            this.reminderAdapter.setReminderList(this.reminderList);
            if (!this.reminderList.isEmpty()) {
                this.binding.emptyView.setVisibility(View.GONE);
            } else {
                this.binding.emptyView.setVisibility(View.VISIBLE);
            }
            this.binding.reminderColorListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            this.binding.reminderColorListView.setAdapter(this.reminderColorAdapter);
            this.binding.createReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    binding.setReminderLayout.setVisibility(View.VISIBLE);
                    binding.emptyView.setVisibility(View.GONE);
                    binding.createReminder.setVisibility(View.GONE);
                    binding.reminderListView.setVisibility(View.GONE);
                    selectedColor = 0;
                    reminderColorAdapter.notifyDataSetChanged();
                }
            });

            this.binding.timePicker.setOnDateChangeListener(new DateTimePicker.OnDateChangeListener() {
                @Override
                public final void vor(long j) {
                    selectTime = j;
                }
            });

            this.binding.timePicker.setDate(Calendar.getInstance().getTimeInMillis());
            this.binding.cancelReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    try {
                        CDOUtiler.hideKeyboard(requireActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    binding.setReminderLayout.setVisibility(View.GONE);
                    binding.createReminder.setVisibility(View.VISIBLE);
                    binding.reminderListView.setVisibility(View.VISIBLE);
                    if (!reminderList.isEmpty()) {
                        binding.emptyView.setVisibility(View.GONE);
                    } else {
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }
                }
            });
            this.binding.saveReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    try {
                        CDOUtiler.hideKeyboard(requireActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (selectTime == 0) {
                            selectTime = binding.timePicker.getDate();
                        }
                        Reminder reminder = new Reminder();
                        reminder.setTitle(binding.reminderTitle.getText().toString());
                        reminder.setTime(selectTime);
                        reminder.setColor(reminderColors[selectedColor]);
                        reminder.setMobileNumber(contactNumber);
                        reminder.setId((int) databaseHelper.addReminder(reminder));
                        reminderAdapter.addnewItem(reminder);
                        reminderList.add(reminder);
                        binding.setReminderLayout.setVisibility(View.GONE);
                        binding.createReminder.setVisibility(View.VISIBLE);
                        binding.reminderListView.setVisibility(View.VISIBLE);
                        binding.reminderTitle.setText("");
                        setReminder(reminder);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setReminder(Reminder reminder) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(this.selectTime);
            calendar.set(13, 0);
            if (reminder != null) {
                Intent intent = new Intent(getContext(), ReminderReceiver.class);
                intent.putExtra("extra_reminder_name", reminder.getTitle());
                intent.putExtra("extra_reminder_id", reminder.getId());
                ((AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE)).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(getContext(), reminder.getId(), intent, 201326592));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ReminderColorAdapter extends RecyclerView.Adapter<ReminderColorAdapter.ReminderColorViewHolder> {
        Context context;

        public ReminderColorAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ReminderColorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ReminderColorViewHolder(ListItemReminderColorBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ReminderColorViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.binding.frameColorItem.setImageTintList(ColorStateList.valueOf(reminderColors[position]));
            if (position == selectedColor) {
                viewHolder.binding.frameSelectedBack.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.frameSelectedBack.setVisibility(View.GONE);
            }

            viewHolder.binding.frameMain.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public final void onClick(View view) {

                    selectedColor = position;
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            int[] iArr = reminderColors;
            if (iArr != null) {
                return iArr.length;
            }
            return 0;
        }


        public class ReminderColorViewHolder extends RecyclerView.ViewHolder {
            ListItemReminderColorBinding binding;

            @SuppressLint({"NotifyDataSetChanged"})
            public ReminderColorViewHolder(ListItemReminderColorBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

        }
    }
}
