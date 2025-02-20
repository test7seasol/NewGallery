package com.gallery.photos.editpic.callendservice.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.gallery.photos.editpic.databinding.FragmentMoreOptionBinding;


public final class MoreOptionFragment extends Fragment {
    public static final Companion Companion = new Companion();
    private FragmentMoreOptionBinding binding;
    private String contactID = "";
    private String contactNumber = "";

    public final void setContactID(String str) {
        this.contactID = str;
    }

    public final void setContactNumber(String str) {
        this.contactNumber = str;
    }

    public static final class Companion {

        public final MoreOptionFragment getInstance(String str, String str2) {
            MoreOptionFragment moreOptionFragment = new MoreOptionFragment();
            if (str == null) {
                str = "";
            }
            moreOptionFragment.setContactID(str);
            if (str2 == null) {
                str2 = "";
            }
            moreOptionFragment.setContactNumber(str2);
            return moreOptionFragment;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentMoreOptionBinding.inflate(inflater, viewGroup, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        if (contactID == null) {
            binding.editContact.setVisibility(View.VISIBLE);
        } else {
            binding.editContact.setVisibility(View.GONE);
        }

        binding.editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                addContact();
            }
        });

        binding.messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMessage();
            }
        });

        binding.calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                openCalendar();
            }
        });

        binding.sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMail();
            }
        });

        binding.web.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                openBrowser();
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

    public final void sendMail() {
        Intent intent = new Intent("android.intent.action.SENDTO");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra("android.intent.extra.EMAIL", "");
        intent.putExtra("android.intent.extra.SUBJECT", "");
        try {
            startActivity(intent);
            finishActivity();
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private final void openCalendar() {
        try {
            Intent intent = new Intent("android.intent.action.INSERT");
            intent.setType("vnd.android.cursor.item/event");
            startActivity(intent);
            finishActivity();
        } catch (Exception e) {
        }
    }

    private final void openBrowser() {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("http://www.google.com"));
            startActivity(intent);
            finishActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void addContact() {

        if (contactID == null) {
            try {
                Intent intent = new Intent("android.intent.action.INSERT");
                intent.setType("vnd.android.cursor.dir/raw_contact");
                intent.putExtra("phone", isEmptyVal(this.contactNumber) ? "" : this.contactNumber);
                intent.putExtra("finishActivityOnSaveCompleted", true);
                startActivity(intent);
                finishActivity();
            } catch (Exception e) {
            }
        }
    }

    public boolean isEmptyVal(String str) {
        return str == null || str.isEmpty() || str.trim().equals("null") || str.trim().isEmpty();
    }

    private final void finishActivity() {
        try {
            requireActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
