package com.gallery.photos.editpic.callendservice.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.callendservice.interfaces.ReminderdeleteClick;
import com.gallery.photos.editpic.callendservice.model.Reminder;
import com.gallery.photos.editpic.databinding.ItemReminderHistoryNewBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public final class ReminderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Reminder> callLogList;
    private final Context context;
    private ReminderdeleteClick onCallLogClick;
    private int[] reminderColors;

    public ReminderAdapter(Context context, int[] reminderColors) {
        this.context = context;
        this.reminderColors = reminderColors;
        this.callLogList = new ArrayList<>();
    }

    public final void setOnCallLogClick(ReminderdeleteClick onCallLogClick) {
        this.onCallLogClick = onCallLogClick;
    }

    @SuppressLint("NotifyDataSetChanged")
    public final void setReminderList(ArrayList<Reminder> callLogList) {
        ArrayList<Reminder> arrayList = new ArrayList<>();
        this.callLogList = arrayList;
        arrayList.addAll(callLogList);
        notifyDataSetChanged();
    }

    public final void addnewItem(Reminder reminder) {
        this.callLogList.add(reminder);
        notifyItemInserted(this.callLogList.size() - 1);
    }

    public static final class CallLogListHolder extends RecyclerView.ViewHolder {
        private final ItemReminderHistoryNewBinding mBinding;

        public CallLogListHolder(ItemReminderHistoryNewBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        ItemReminderHistoryNewBinding inflate = ItemReminderHistoryNewBinding.inflate(LayoutInflater.from(this.context), parent, false);
        return new CallLogListHolder(inflate);
    }

    @Override
    public int getItemCount() {
        return this.callLogList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        final CallLogListHolder callLogListHolder = (CallLogListHolder) holder;
        Reminder reminder2 = this.callLogList.get(i);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("E,MMM dd");
        String str = simpleDateFormat2.format(Calendar.getInstance().getTime()).toString();
        Date date = new Date(reminder2.getTime());
        String title = reminder2.getTitle();
        if (!title.isEmpty()) {
            callLogListHolder.mBinding.reminderDetails.setText(reminder2.getTitle());
        }
        callLogListHolder.mBinding.txtTime.setText(simpleDateFormat.format(date).toString());
        String str2 = simpleDateFormat2.format(date).toString();
        if (str.equals(str2)) {
            str2 = "Today";
        }
        callLogListHolder.mBinding.txtDay.setText(str2);
        callLogListHolder.mBinding.frameColorItem.setImageTintList(ColorStateList.valueOf(reminder2.getColor()));
        callLogListHolder.mBinding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {

                if (callLogListHolder.getAdapterPosition() == -1 || callLogList.size() <= callLogListHolder.getAdapterPosition()) {
                    return;
                }
                Reminder reminder = callLogList.get(callLogListHolder.getAdapterPosition());
                callLogList.remove(callLogListHolder.getAdapterPosition());
                notifyItemRemoved(callLogListHolder.getAdapterPosition());
                if (onCallLogClick != null) {
                    onCallLogClick.onDelete(reminder);
                }

            }
        });
    }
}
