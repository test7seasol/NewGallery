package org.wysaid.myUtils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/* loaded from: classes4.dex */
public class MsgUtil {
    static WeakReference<Context> mContext;
    static Toast mToast;

    public static Toast getCurrentToast() {
        return mToast;
    }

    public static void setCurrentToast(Context context, Toast toast) {
        mContext = new WeakReference<>(context);
        mToast = toast;
    }

    public static void clear() {
        mContext = null;
        mToast = null;
    }

    public static void toastMsg(Context context, String str) {
        toastMsg(context, str, 1);
    }

    public static void toastMsg(Context context, String str, int i) {
        Toast toast;
        WeakReference<Context> weakReference = mContext;
        if (weakReference == null || weakReference.get() != context) {
            if (context == null) {
                mContext = null;
                return;
            }
            WeakReference<Context> weakReference2 = new WeakReference<>(context);
            mContext = weakReference2;
            Toast makeText = Toast.makeText(weakReference2.get(), "", i);
            mToast = makeText;
            makeText.setDuration(i);
        }
        if (mContext.get() == null || (toast = mToast) == null) {
            return;
        }
        toast.setText(str);
        mToast.show();
    }

    public static boolean isDisplaying() {
        Toast toast = mToast;
        return (toast == null || toast.getView() == null || mToast.getView().getWindowVisibility() != 0) ? false : true;
    }
}
