package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.AppCompatEditText;

import com.gallery.photos.editpic.ImageEDITModule.edit.fragment.TextFragment;

/* loaded from: classes.dex */
public class EditText extends AppCompatEditText {
    private TextFragment textFragment;

    public EditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setTextFragment(TextFragment textFragment) {
        this.textFragment = textFragment;
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i == 4) {
            ((InputMethodManager) getContext().getSystemService("input_method")).hideSoftInputFromWindow(getWindowToken(), 0);
            this.textFragment.dismissAndShowSticker();
        }
        return false;
    }
}
