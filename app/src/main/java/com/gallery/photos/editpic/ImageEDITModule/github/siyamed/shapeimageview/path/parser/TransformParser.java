package com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.path.parser;

import android.graphics.Matrix;
import android.util.Log;

/* loaded from: classes.dex */
class TransformParser {
    private static final String TAG = "SvgToPath";

    TransformParser() {
    }

    static Matrix parseTransform(String str) {
        int i;
        Matrix matrix = new Matrix();
        while (true) {
            parseTransformItem(str, matrix);
            int indexOf = str.indexOf(")");
            if (indexOf <= 0 || str.length() <= (i = indexOf + 1)) {
                break;
            }
            str = str.substring(i).replaceFirst("[\\s,]*", "");
        }
        return matrix;
    }

    private static void parseTransformItem(String str, Matrix matrix) {
        float f;
        float f2 = 0.0f;
        if (str.startsWith("matrix(")) {
            NumberParse parseNumbers = NumberParse.parseNumbers(str.substring(7));
            if (parseNumbers.numbers.size() == 6) {
                Matrix matrix2 = new Matrix();
                matrix2.setValues(new float[]{parseNumbers.numbers.get(0).floatValue(), parseNumbers.numbers.get(2).floatValue(), parseNumbers.numbers.get(4).floatValue(), parseNumbers.numbers.get(1).floatValue(), parseNumbers.numbers.get(3).floatValue(), parseNumbers.numbers.get(5).floatValue(), 0.0f, 0.0f, 1.0f});
                matrix.preConcat(matrix2);
            }
        } else if (str.startsWith("translate(")) {
            NumberParse parseNumbers2 = NumberParse.parseNumbers(str.substring(10));
            if (parseNumbers2.numbers.size() > 0) {
                float floatValue = parseNumbers2.numbers.get(0).floatValue();
                if (parseNumbers2.numbers.size() > 1) {
                    f2 = parseNumbers2.numbers.get(1).floatValue();
                }
                matrix.preTranslate(floatValue, f2);
            }
        } else if (str.startsWith("scale(")) {
            NumberParse parseNumbers3 = NumberParse.parseNumbers(str.substring(6));
            if (parseNumbers3.numbers.size() > 0) {
                float floatValue2 = parseNumbers3.numbers.get(0).floatValue();
                matrix.preScale(floatValue2, parseNumbers3.numbers.size() > 1 ? parseNumbers3.numbers.get(1).floatValue() : floatValue2);
            }
        } else if (str.startsWith("skewX(")) {
            NumberParse parseNumbers4 = NumberParse.parseNumbers(str.substring(6));
            if (parseNumbers4.numbers.size() > 0) {
                matrix.preSkew((float) Math.tan((double) parseNumbers4.numbers.get(0).floatValue()), 0.0f);
            }
        } else if (str.startsWith("skewY(")) {
            NumberParse parseNumbers5 = NumberParse.parseNumbers(str.substring(6));
            if (parseNumbers5.numbers.size() > 0) {
                matrix.preSkew(0.0f, (float) Math.tan((double) parseNumbers5.numbers.get(0).floatValue()));
            }
        } else if (str.startsWith("rotate(")) {
            NumberParse parseNumbers6 = NumberParse.parseNumbers(str.substring(7));
            if (parseNumbers6.numbers.size() > 0) {
                float floatValue3 = parseNumbers6.numbers.get(0).floatValue();
                if (parseNumbers6.numbers.size() > 2) {
                    f2 = parseNumbers6.numbers.get(1).floatValue();
                    f = parseNumbers6.numbers.get(2).floatValue();
                } else {
                    f = 0.0f;
                }
                matrix.preTranslate(f2, f);
                matrix.preRotate(floatValue3);
                matrix.preTranslate(-f2, -f);
            }
        } else {
            Log.w(TAG, "Invalid transform (" + str + ")");
        }
    }
}
