package com.gallery.photos.editpic.callendservice.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gallery.photos.editpic.callendservice.fragments.AfterCallFragment;
import com.gallery.photos.editpic.callendservice.fragments.MessageFragment;
import com.gallery.photos.editpic.callendservice.fragments.MoreOptionFragment;
import com.gallery.photos.editpic.callendservice.fragments.ReminderFragment;
import com.gallery.photos.editpic.callendservice.model.ContactCDO;

public class CallerScreenAdapter extends FragmentStateAdapter {
    public final int PAGE_COUNT, PAGE_HOME, PAGE_MESSAGE, PAGE_MORE_OPTION, PAGE_REMINDER;
    ContactCDO contact;
    String contactID, contactName, contactNumber;
    FragmentActivity fragmentActivity;

    @Override
    public int getItemCount() {
        return 4;
    }

    public CallerScreenAdapter(FragmentActivity fragmentActivity, String str) {
        super(fragmentActivity);
        this.PAGE_COUNT = 4;
        this.PAGE_HOME = 0;
        this.PAGE_MESSAGE = 1;
        this.PAGE_REMINDER = 2;
        this.PAGE_MORE_OPTION = 3;
        this.contactNumber = str;
        this.fragmentActivity = fragmentActivity;
    }

    public void setContactData(String str, String str2, ContactCDO contactCDO) {
        this.contactName = str;
        this.contactID = str2;
        this.contact = contactCDO;
    }

    @Override
    public Fragment createFragment(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        this.fragmentActivity.getSupportFragmentManager();
                        MoreOptionFragment companion = MoreOptionFragment.Companion.getInstance(this.contactID, this.contactNumber);
                        Bundle bundle = new Bundle();
                        bundle.putString("contactNumber", this.contactNumber);
                        bundle.putString("contactName", this.contactName);
                        bundle.putString("contactID", this.contactID);
                        companion.setArguments(bundle);
                        return companion;
                    }
                    return new MessageFragment();
                }
                return ReminderFragment.getInstance(this.contactNumber);
            }
            return MessageFragment.getInstance(this.contactNumber);
        }
        return AfterCallFragment.Companion.getInstance(this.contactNumber, this.contactName, this.contactID, this.contact);
    }
}
