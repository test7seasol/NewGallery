package com.gallery.photos.editpic.callendservice.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.gallery.photos.editpic.callendservice.MainCallActivity;
import com.gallery.photos.editpic.callendservice.model.ContactCDO;
import com.gallery.photos.editpic.databinding.FragmentAfterCallBinding;

public final class AfterCallFragment extends Fragment {
    public static final Companion Companion = new Companion();
    public FragmentAfterCallBinding binding;
    private ContactCDO contact;
    private String contactNumber = "";
    private String contactName = "";
    private String contactID = "";

    public final void setContactNumber(String str) {
        this.contactNumber = str;
    }

    public final void setContactName(String str) {
        this.contactName = str;
    }

    public final void setContactID(String str) {
        this.contactID = str;
    }

    public final void setContact(ContactCDO contactCDO) {
        this.contact = contactCDO;
    }

    public static final class Companion {
        public final AfterCallFragment getInstance(String str, String str2, String str3, ContactCDO contactCDO) {
            AfterCallFragment afterCallFragment = new AfterCallFragment();
            afterCallFragment.setContactNumber(str);
            afterCallFragment.setContactName(str2);
            afterCallFragment.setContactID(str3);
            afterCallFragment.setContact(contactCDO);
            return afterCallFragment;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentAfterCallBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initView();
    }

    public final void initView() {
        bindObjects();
        bindListener();
    }

    public final void bindObjects() {
        // Show help layout if contact ID is not valid
        if (this.contactID == null || this.contactID.isEmpty()) {
            binding.helpLayout.setVisibility(View.VISIBLE);
        }
    }

    public final void bindListener() {
        binding.helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ((MainCallActivity) getActivity()).setSecondPage();
            }
        });
        binding.cvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMessage();
            }
        });
        binding.cvSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMessage();
            }
        });
    }

    public final void sendMessage() {
        try {
            Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + contactNumber));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception unused) {
        }
    }
}
