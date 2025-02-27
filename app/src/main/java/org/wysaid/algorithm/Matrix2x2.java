package org.wysaid.algorithm;

/* loaded from: classes4.dex */
public class Matrix2x2 {
    public float[] data;

    protected Matrix2x2() {
        this.data = new float[4];
    }

    protected Matrix2x2(float[] fArr) {
        this.data = fArr;
    }

    public static Matrix2x2 makeIdentity() {
        return new Matrix2x2(new float[]{1.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix2x2 makeRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        return new Matrix2x2(new float[]{cos, sin, -sin, cos});
    }

    protected static float[] _mul(float[] fArr, float[] fArr2) {
        float f = fArr[0] * fArr2[0];
        float f2 = fArr[2];
        float f3 = fArr2[1];
        float f4 = fArr[1] * fArr2[0];
        float f5 = fArr[3];
        float f6 = fArr[0] * fArr2[2];
        float f7 = fArr2[3];
        return new float[]{f + (f2 * f3), f4 + (f3 * f5), f6 + (f2 * f7), (fArr[1] * fArr2[2]) + (f5 * f7)};
    }

    public Matrix2x2 multiply(Matrix2x2 matrix2x2) {
        return new Matrix2x2(_mul(this.data, matrix2x2.data));
    }

    public Matrix2x2 multiplyBy(Matrix2x2 matrix2x2) {
        this.data = _mul(this.data, matrix2x2.data);
        return this;
    }

    public Matrix2x2 clone() {
        return new Matrix2x2((float[]) this.data.clone());
    }
}
