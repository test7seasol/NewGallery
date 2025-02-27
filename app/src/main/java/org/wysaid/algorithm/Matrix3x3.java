package org.wysaid.algorithm;

/* loaded from: classes4.dex */
public class Matrix3x3 {
    public float[] data;

    Matrix3x3() {
        this.data = new float[9];
    }

    Matrix3x3(float[] fArr) {
        this.data = fArr;
    }

    public static Matrix3x3 makeIdentity() {
        return new Matrix3x3(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix3x3 makeRotation(float f, float f2, float f3, float f4) {
        float normalizeScaling = AlgorithmUtil.getNormalizeScaling(f2, f3, f4);
        float f5 = f2 * normalizeScaling;
        float f6 = f3 * normalizeScaling;
        float f7 = f4 * normalizeScaling;
        double d = f;
        float cos = (float) Math.cos(d);
        float f8 = 1.0f - cos;
        float sin = (float) Math.sin(d);
        float f9 = f8 * f5;
        float f10 = f9 * f6;
        float f11 = f7 * sin;
        float f12 = f9 * f7;
        float f13 = f6 * sin;
        float f14 = f8 * f6;
        float f15 = f14 * f7;
        float f16 = f5 * sin;
        return new Matrix3x3(new float[]{(f9 * f5) + cos, f10 + f11, f12 - f13, f10 - f11, (f6 * f14) + cos, f15 + f16, f12 + f13, f15 - f16, cos + (f8 * f7 * f7)});
    }

    public static Matrix3x3 makeXRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        return new Matrix3x3(new float[]{1.0f, 0.0f, 0.0f, 0.0f, cos, sin, 0.0f, -sin, cos});
    }

    public static Matrix3x3 makeYRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        return new Matrix3x3(new float[]{cos, 0.0f, -sin, 0.0f, 1.0f, 0.0f, sin, 0.0f, cos});
    }

    public static Matrix3x3 makeZRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        return new Matrix3x3(new float[]{cos, sin, 0.0f, -sin, cos, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    protected static float[] _mul(float[] fArr, float[] fArr2) {
        float f = fArr[0] * fArr2[0];
        float f2 = fArr[3];
        float f3 = fArr2[1];
        float f4 = fArr[6];
        float f5 = fArr2[2];
        float f6 = fArr[1];
        float f7 = fArr2[0];
        float f8 = fArr[4];
        float f9 = fArr[7];
        float f10 = fArr[2] * f7;
        float f11 = fArr[5];
        float f12 = f10 + (fArr2[1] * f11);
        float f13 = fArr[8];
        float f14 = fArr[0];
        float f15 = fArr2[3] * f14;
        float f16 = fArr2[4];
        float f17 = f15 + (f2 * f16);
        float f18 = fArr2[5];
        float f19 = fArr[1];
        float f20 = fArr2[3];
        float f21 = fArr[2];
        float f22 = f14 * fArr2[6];
        float f23 = fArr[3];
        float f24 = fArr2[7];
        float f25 = f22 + (f23 * f24);
        float f26 = fArr2[8];
        float f27 = fArr2[6];
        return new float[]{f + (f2 * f3) + (f4 * f5), (f6 * f7) + (f3 * f8) + (f9 * f5), f12 + (f5 * f13), f17 + (f4 * f18), (f19 * f20) + (f8 * f16) + (f9 * f18), (f20 * f21) + (f11 * fArr2[4]) + (f18 * f13), f25 + (f4 * f26), (f19 * f27) + (fArr[4] * f24) + (f9 * f26), (f21 * f27) + (fArr[5] * fArr2[7]) + (f13 * f26)};
    }

    public Matrix3x3 multiply(Matrix3x3 matrix3x3) {
        return new Matrix3x3(_mul(this.data, matrix3x3.data));
    }

    public Matrix3x3 multiplyBy(Matrix3x3 matrix3x3) {
        this.data = _mul(this.data, matrix3x3.data);
        return this;
    }

    public Matrix3x3 clone() {
        return new Matrix3x3((float[]) this.data.clone());
    }
}
