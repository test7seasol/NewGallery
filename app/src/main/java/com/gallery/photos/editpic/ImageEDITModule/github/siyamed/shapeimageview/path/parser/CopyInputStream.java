package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
class CopyInputStream {
    private static final String TAG = SvgToPath.TAG;
    private ByteArrayOutputStream _copy;
    private final InputStream _is;

    public CopyInputStream(InputStream inputStream) {
        this._is = inputStream;
        try {
            copy();
        } catch (IOException e) {
            Log.w(TAG, "IOException in CopyInputStream " + e.toString());
        }
    }

    private void copy() throws IOException {
        this._copy = new ByteArrayOutputStream();
        byte[] bArr = new byte[256];
        while (true) {
            int read = this._is.read(bArr);
            if (-1 != read) {
                this._copy.write(bArr, 0, read);
            } else {
                this._copy.flush();
                return;
            }
        }
    }

    public ByteArrayInputStream getCopy() {
        return new ByteArrayInputStream(this._copy.toByteArray());
    }
}
