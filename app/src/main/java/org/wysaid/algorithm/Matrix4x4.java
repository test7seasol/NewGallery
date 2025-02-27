package org.wysaid.algorithm;

/* loaded from: classes4.dex */
public class Matrix4x4 {
    public float[] data;

    Matrix4x4() {
        this.data = new float[16];
    }

    Matrix4x4(float[] fArr) {
        this.data = fArr;
    }

    public static Matrix4x4 makeIdentity() {
        return new Matrix4x4(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix4x4 makeRotation(float f, float f2, float f3, float f4) {
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
        return new Matrix4x4(new float[]{(f9 * f5) + cos, f10 + f11, f12 - f13, 0.0f, f10 - f11, (f6 * f14) + cos, f15 + f16, 0.0f, f12 + f13, f15 - f16, cos + (f8 * f7 * f7), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix4x4 makeXRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        return new Matrix4x4(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, cos, sin, 0.0f, 0.0f, -sin, cos, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix4x4 makeYRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        return new Matrix4x4(new float[]{cos, 0.0f, -sin, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, sin, 0.0f, cos, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix4x4 makeZRotation(float f) {
        double d = f;
        float cos = (float) Math.cos(d);
        float sin = (float) Math.sin(d);
        return new Matrix4x4(new float[]{cos, sin, 0.0f, 0.0f, -sin, cos, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix4x4 makeTranslation(float f, float f2, float f3) {
        return new Matrix4x4(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, f, f2, f3, 1.0f});
    }

    public static Matrix4x4 makeScaling(float f, float f2, float f3) {
        return new Matrix4x4(new float[]{f, 0.0f, 0.0f, 0.0f, 0.0f, f2, 0.0f, 0.0f, 0.0f, 0.0f, f3, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f});
    }

    public static Matrix4x4 makePerspective(float f, float f2, float f3, float f4) {
        float tan = 1.0f / ((float) Math.tan(f / 2.0f));
        float f5 = f3 - f4;
        return new Matrix4x4(new float[]{tan / f2, 0.0f, 0.0f, 0.0f, 0.0f, tan, 0.0f, 0.0f, 0.0f, 0.0f, (f4 + f3) / f5, -1.0f, 0.0f, 0.0f, ((f4 * 2.0f) * f3) / f5, 0.0f});
    }

    public static Matrix4x4 makeFrustum(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = f2 + f;
        float f8 = f2 - f;
        float f9 = f4 - f3;
        float f10 = f6 - f5;
        float f11 = 2.0f * f5;
        return new Matrix4x4(new float[]{f11 / f8, 0.0f, 0.0f, 0.0f, 0.0f, f11 / f9, 0.0f, 0.0f, f7 / f8, (f4 + f3) / f9, (-(f6 + f5)) / f10, -1.0f, 0.0f, 0.0f, ((f6 * (-2.0f)) * f5) / f10, 0.0f});
    }

    public static Matrix4x4 makeOrtho(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = f2 + f;
        float f8 = f2 - f;
        float f9 = f4 - f3;
        float f10 = f4 + f3;
        float f11 = f6 + f5;
        float f12 = f6 - f5;
        return new Matrix4x4(new float[]{2.0f / f8, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f / f9, 0.0f, 0.0f, 0.0f, 0.0f, (-2.0f) / f12, 0.0f, (-f7) / f8, (-f10) / f9, (-f11) / f12, 1.0f});
    }

    protected static float[] _mul(float[] fArr, float[] fArr2) {
        float f = fArr[0] * fArr2[0];
        float f2 = fArr[4];
        float f3 = fArr2[1];
        float f4 = fArr[8];
        float f5 = fArr2[2];
        float f6 = fArr[12];
        float f7 = fArr2[3];
        float f8 = fArr[1];
        float f9 = fArr2[0];
        float f10 = fArr[5];
        float f11 = fArr[9];
        float f12 = fArr[13];
        float f13 = fArr[2] * f9;
        float f14 = fArr[6];
        float f15 = fArr2[1];
        float f16 = fArr[10];
        float f17 = fArr[14];
        float f18 = fArr[3] * f9;
        float f19 = fArr[7];
        float f20 = fArr[11];
        float f21 = f18 + (f15 * f19) + (fArr2[2] * f20);
        float f22 = fArr[15];
        float f23 = fArr[0];
        float f24 = fArr2[4] * f23;
        float f25 = fArr2[5];
        float f26 = f24 + (f2 * f25);
        float f27 = fArr2[6];
        float f28 = f26 + (f4 * f27);
        float f29 = fArr2[7];
        float f30 = fArr[1];
        float f31 = fArr2[4];
        float f32 = fArr[2];
        float f33 = fArr2[5];
        float f34 = fArr[3];
        float f35 = fArr2[8] * f23;
        float f36 = fArr[4];
        float f37 = fArr2[9];
        float f38 = fArr2[10];
        float f39 = f35 + (f36 * f37) + (f4 * f38);
        float f40 = fArr2[11];
        float f41 = fArr2[8];
        float f42 = fArr[5];
        float f43 = fArr[6];
        float f44 = fArr2[9];
        float f45 = fArr[7];
        float f46 = f23 * fArr2[12];
        float f47 = fArr2[13];
        float f48 = f46 + (f36 * f47);
        float f49 = fArr[8];
        float f50 = fArr2[14];
        float f51 = f48 + (f49 * f50);
        float f52 = fArr2[15];
        float f53 = fArr2[12];
        float f54 = fArr2[13];
        return new float[]{f + (f2 * f3) + (f4 * f5) + (f6 * f7), (f8 * f9) + (f3 * f10) + (f11 * f5) + (f12 * f7), f13 + (f14 * f15) + (f5 * f16) + (f17 * f7), f21 + (f7 * f22), f28 + (f6 * f29), (f30 * f31) + (f10 * f25) + (f11 * f27) + (f12 * f29), (f32 * f31) + (f14 * f33) + (f27 * f16) + (f17 * f29), (f31 * f34) + (f19 * f33) + (fArr2[6] * f20) + (f29 * f22), f39 + (f6 * f40), (f30 * f41) + (f37 * f42) + (f11 * f38) + (f12 * f40), (f32 * f41) + (f43 * f44) + (f16 * f38) + (f17 * f40), (f41 * f34) + (f44 * f45) + (f20 * fArr2[10]) + (f40 * f22), f51 + (f6 * f52), (f30 * f53) + (f42 * f47) + (fArr[9] * f50) + (f12 * f52), (f32 * f53) + (f43 * f54) + (fArr[10] * f50) + (f17 * f52), (f34 * f53) + (f45 * f54) + (fArr[11] * fArr2[14]) + (f22 * f52)};
    }

    public Matrix4x4 multiply(Matrix4x4 matrix4x4) {
        return new Matrix4x4(_mul(this.data, matrix4x4.data));
    }

    public Matrix4x4 multiplyBy(Matrix4x4 matrix4x4) {
        this.data = _mul(this.data, matrix4x4.data);
        return this;
    }

    public Matrix4x4 clone() {
        return new Matrix4x4((float[]) this.data.clone());
    }
}
