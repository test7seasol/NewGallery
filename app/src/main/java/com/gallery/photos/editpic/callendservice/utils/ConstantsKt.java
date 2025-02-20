package com.gallery.photos.editpic.callendservice.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.gallery.photos.editpic.callendservice.interfaces.OnKeyboardOpenListener;

public final class ConstantsKt {
    public static final void setKeyboardVisibilityListener(Activity activity, final OnKeyboardOpenListener onKeyboardOpenListener) {
        @SuppressLint("ResourceType") final View findViewById = activity.findViewById(16908290);
        findViewById.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;

            @Override
            public void onGlobalLayout() {
                int height = findViewById.getHeight();
                int i = this.mPreviousHeight;
                if (i != 0) {
                    if (i > height) {
                        onKeyboardOpenListener.onKeyBoardIsOpen(true);
                    } else if (i < height) {
                        onKeyboardOpenListener.onKeyBoardIsOpen(false);
                    }
                }
                this.mPreviousHeight = height;
            }
        });
    }
}
