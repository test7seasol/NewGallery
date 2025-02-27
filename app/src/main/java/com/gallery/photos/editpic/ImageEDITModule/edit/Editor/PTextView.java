package com.gallery.photos.editpic.ImageEDITModule.edit.Editor;

import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.Sticker;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;

/* loaded from: classes.dex */
public class PTextView extends Sticker {
    private int backgroundAlpha;
    private int backgroundBorder;
    private int backgroundColor;
    private BitmapDrawable backgroundDrawable;
    private final Context context;
    private Drawable drawable;
    private boolean isShowBackground;
    private float maxTextSizePixels;
    private float minTextSizePixels;
    private int paddingHeight;
    private int paddingWidth;
    private Text polishText;
    private StaticLayout staticLayout;
    private String text;
    private Layout.Alignment textAlign;
    private int textAlpha;
    private int textColor;
    private int textHeight;
    private Text.TextShadow textShadow;
    private int textWidth;
    private float lineSpacingExtra = 0.0f;
    private float lineSpacingMultiplier = 1.0f;
    private final TextPaint textPaint = new TextPaint(1);

    public PTextView(Context context, Text text) {
        this.context = context;
        this.polishText = text;
        setTextSize(text.getTextSize()).setTextWidth(text.getTextWidth()).setTextHeight(text.getTextHeight()).setText(text.getText()).setPaddingWidth(SystemUtil.dpToPx(context, text.getPaddingWidth())).setBackgroundBorder(SystemUtil.dpToPx(context, text.getBackgroundBorder())).setTextShadow(text.getTextShadow()).setTextColor(text.getTextColor()).setTextAlpha(text.getTextAlpha()).setBackgroundColor(text.getBackgroundColor()).setBackgroundAlpha(text.getBackgroundAlpha()).setShowBackground(text.isShowBackground()).setTextColor(text.getTextColor()).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + text.getFontName())).setTextAlign(text.getTextAlign()).setTextShare(text.getTextShader()).resizeText();
    }

    private float convertSpToPx(float f) {
        return f * this.context.getResources().getDisplayMetrics().scaledDensity;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public void draw(Canvas canvas) {
        Matrix matrix = getMatrix();
        canvas.save();
        canvas.concat(matrix);
        if (this.isShowBackground) {
            Paint paint = new Paint();
            if (this.backgroundDrawable != null) {
                paint.setShader(new BitmapShader(this.backgroundDrawable.getBitmap(), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR));
                paint.setAlpha(this.backgroundAlpha);
            } else {
                paint.setARGB(this.backgroundAlpha, Color.red(this.backgroundColor), Color.green(this.backgroundColor), Color.blue(this.backgroundColor));
            }
            float f = this.textWidth;
            float f2 = this.textHeight;
            int i = this.backgroundBorder;
            canvas.drawRoundRect(0.0f, 0.0f, f, f2, i, i, paint);
            canvas.restore();
            canvas.save();
            canvas.concat(matrix);
        }
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        canvas.translate(this.paddingWidth, (this.textHeight / 2) - (this.staticLayout.getHeight() / 2));
        this.staticLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        canvas.restore();
    }

    public Text getPolishText() {
        return this.polishText;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public int getAlpha() {
        return this.textPaint.getAlpha();
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public Drawable getDrawable() {
        return this.drawable;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public int getHeight() {
        return this.textHeight;
    }

    public String getText() {
        return this.text;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public int getWidth() {
        return this.textWidth;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public void release() {
        super.release();
        if (this.drawable != null) {
            this.drawable = null;
        }
    }

   /* public PTextView resizeText() {
        String text = getText();
        if (text != null && text.length() > 0) {
            if (this.textShadow != null) {
                this.textPaint.setShadowLayer(r0.getRadius(), this.textShadow.getDx(), this.textShadow.getDy(), this.textShadow.getColorShadow());
            }
            this.textPaint.setTextAlign(Paint.Align.LEFT);
            this.textPaint.setARGB(this.textAlpha, Color.red(this.textColor), Color.green(this.textColor), Color.blue(this.textColor));
            int i = this.textWidth - (this.paddingWidth * 2);
            String str = this.text;
            TextPaint textPaint = this.textPaint;
            if (i <= 0) {
                i = 100;
            }
            this.staticLayout = new StaticLayout(str, textPaint, i, this.textAlign, this.lineSpacingMultiplier, this.lineSpacingExtra, true);
        }
        return this;
    }*/

    public PTextView resizeText() {
        String text2 = getText();
        if (text2 != null && text2.length() > 0) {
            Text.TextShadow textShadow2 = this.textShadow;
            if (textShadow2 != null) {
                this.textPaint.setShadowLayer((float) textShadow2.getRadius(), (float) this.textShadow.getDx(), (float) this.textShadow.getDy(), this.textShadow.getColorShadow());
            }
            this.textPaint.setTextAlign(Paint.Align.LEFT);
            this.textPaint.setARGB(this.textAlpha, Color.red(this.textColor), Color.green(this.textColor), Color.blue(this.textColor));
            int i = this.textWidth - (this.paddingWidth * 2);
            String str = this.text;
            TextPaint textPaint2 = this.textPaint;
            if (i <= 0) {
                i = 100;
            }
            this.staticLayout = new StaticLayout(str, textPaint2, i, this.textAlign, this.lineSpacingMultiplier, this.lineSpacingExtra, true);
        }
        return this;
    }


    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public PTextView setAlpha(int i) {
        this.textPaint.setAlpha(i);
        return this;
    }

    public PTextView setBackgroundAlpha(int i) {
        this.backgroundAlpha = i;
        return this;
    }

    public PTextView setBackgroundBorder(int i) {
        this.backgroundBorder = i;
        return this;
    }

    public PTextView setBackgroundColor(int i) {
        this.backgroundColor = i;
        return this;
    }

    @Override // com.gallery.photos.editphotovideo.sticker.Sticker
    public PTextView setDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public PTextView setPaddingWidth(int i) {
        this.paddingWidth = i;
        return this;
    }

    public PTextView setShowBackground(boolean z) {
        this.isShowBackground = z;
        return this;
    }

    public PTextView setText(String str) {
        this.text = str;
        return this;
    }

    public PTextView setTextAlign(int i) {
        if (i == 2) {
            this.textAlign = Layout.Alignment.ALIGN_NORMAL;
        } else if (i == 3) {
            this.textAlign = Layout.Alignment.ALIGN_OPPOSITE;
        } else if (i == 4) {
            this.textAlign = Layout.Alignment.ALIGN_CENTER;
        }
        return this;
    }

    public PTextView setTextAlpha(int i) {
        this.textAlpha = i;
        return this;
    }

    public PTextView setTextColor(int i) {
        this.textColor = i;
        return this;
    }

    public PTextView setTextHeight(int i) {
        this.textHeight = i;
        return this;
    }

    public PTextView setTextShadow(Text.TextShadow textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public PTextView setTextShare(Shader shader) {
        this.textPaint.setShader(shader);
        return this;
    }

    public PTextView setTextSize(int i) {
        this.textPaint.setTextSize(convertSpToPx(i));
        return this;
    }

    public PTextView setTextWidth(int i) {
        this.textWidth = i;
        return this;
    }

    public PTextView setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        return this;
    }
}
