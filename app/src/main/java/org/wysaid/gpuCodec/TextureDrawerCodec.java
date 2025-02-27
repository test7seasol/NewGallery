package org.wysaid.gpuCodec;

import org.wysaid.common.TextureDrawer;

/* loaded from: classes4.dex */
public class TextureDrawerCodec extends TextureDrawer {
    public static final String COLOR_CONVERSION_NAME = "colorConversion";
    static final float[] MATRIX_YUV2RGB = {1.0f, 1.0f, 1.0f, 0.0f, -0.18732f, 1.8556f, 1.57481f, -0.46813f, 0.0f};
    static final float[] MATRIX_RGB2YUV = {0.21260133f, -0.11457283f, 0.49999598f, 0.7152003f, -0.38542804f, -0.4541502f, 0.07219838f, 0.5000009f, -0.04584577f};
}
