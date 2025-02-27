package com.gallery.photos.editpic.callendservice.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.callendservice.interfaces.OnKeyboardOpenListener;
import com.gallery.photos.editpic.callendservice.utils.ConstantsKt;
import com.gallery.photos.editpic.databinding.FragmentMessageCdoBinding;

public class MessageFragment extends Fragment {
    FragmentMessageCdoBinding binding;
    String contactNumber = "";
    OnKeyboardOpenListener keyboardOpenListener;
    int selectPosition;

    public static MessageFragment getInstance(String str) {
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.contactNumber = str;
        return messageFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentMessageCdoBinding.inflate(layoutInflater, viewGroup, false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.keyboardOpenListener = (OnKeyboardOpenListener) context;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context.toString() + " must implement OnDataPassListener");
        }
    }

    public void initView() {
        this.binding.notTalkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                selectPosition = 0;
                setMesssageSelectLayout();
            }
        });
        this.binding.callLaterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                selectPosition = 1;
                setMesssageSelectLayout();
            }
        });
        this.binding.onWayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                selectPosition = 2;
                setMesssageSelectLayout();
            }
        });
        this.binding.sendNotTalkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMessage(binding.notTalkText.getText().toString());
            }
        });
        this.binding.sendCallLaterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMessage(binding.callLaterText.getText().toString());
            }
        });
        this.binding.sendOnWayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMessage(binding.onWayText.getText().toString());
            }
        });
        this.binding.sendMesssage.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                sendMessage(binding.messageText.getText().toString());
            }
        });
        this.binding.messageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public final void onFocusChange(View view, boolean z) {
                if (z) {
                    selectPosition = 3;
                    setMesssageSelectLayout();
                }

            }
        });
        ConstantsKt.setKeyboardVisibilityListener(requireActivity(), this.keyboardOpenListener);
    }

    public void setMesssageSelectLayout() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            int i = this.selectPosition;
            if (i == 0) {
                this.binding.notTalkText.setTextColor(ContextCompat.getColor(getContext(), R.color.tools_theme));
                this.binding.sendNotTalkText.setVisibility(View.VISIBLE);
                this.binding.selectNotTalk.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                this.binding.callLaterText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendCallLaterText.setVisibility(View.GONE);
                this.binding.selectCallLater.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                this.binding.imgMessage.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.onWayText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendOnWayText.setVisibility(View.GONE);
                this.binding.selectOnWay.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                inputMethodManager.hideSoftInputFromWindow(this.binding.messageText.getWindowToken(), 0);
                this.binding.sendMesssage.setVisibility(View.GONE);
                this.binding.sendMesssage.setVisibility(View.GONE);
            } else if (i == 1) {
                this.binding.callLaterText.setTextColor(ContextCompat.getColor(getContext(), R.color.tools_theme));
                this.binding.sendCallLaterText.setVisibility(View.VISIBLE);
                this.binding.selectCallLater.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                this.binding.onWayText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendOnWayText.setVisibility(View.GONE);
                this.binding.selectOnWay.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                this.binding.imgMessage.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.notTalkText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendNotTalkText.setVisibility(View.GONE);
                this.binding.selectNotTalk.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                this.binding.messageText.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(this.binding.messageText.getWindowToken(), 0);
                this.binding.sendMesssage.setVisibility(View.GONE);
            } else if (i == 2) {
                this.binding.onWayText.setTextColor(ContextCompat.getColor(getContext(), R.color.tools_theme));
                this.binding.sendOnWayText.setVisibility(View.VISIBLE);
                this.binding.selectOnWay.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                this.binding.notTalkText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendNotTalkText.setVisibility(View.GONE);
                this.binding.selectNotTalk.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                this.binding.imgMessage.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.callLaterText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendCallLaterText.setVisibility(View.GONE);
                this.binding.selectCallLater.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                inputMethodManager.hideSoftInputFromWindow(this.binding.messageText.getWindowToken(), 0);
                this.binding.sendMesssage.setVisibility(View.GONE);
                this.binding.sendMesssage.setVisibility(View.GONE);
            } else if (i == 3) {
                this.binding.imgMessage.setColorFilter(ContextCompat.getColor(getContext(), R.color.tools_theme));
                this.binding.onWayText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendOnWayText.setVisibility(View.GONE);
                this.binding.selectOnWay.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                this.binding.notTalkText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendNotTalkText.setVisibility(View.GONE);
                this.binding.selectNotTalk.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                this.binding.callLaterText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                this.binding.sendCallLaterText.setVisibility(View.GONE);
                this.binding.selectCallLater.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                this.binding.sendMesssage.setVisibility(View.VISIBLE);
                this.binding.messageText.requestFocus();
                inputMethodManager.showSoftInput(this.binding.messageText, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String str) {
        try {
            Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception unused) {
        }


    }
}
