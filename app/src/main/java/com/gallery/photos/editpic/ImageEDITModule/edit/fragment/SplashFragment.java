package com.gallery.photos.editpic.ImageEDITModule.edit.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.SplashSquareView;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.SquareAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.StickerFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.SplashSticker;
import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class SplashFragment extends DialogFragment implements SquareAdapter.SplashChangeListener {
    private static final String TAG = "SplashFragment";
    private Bitmap SaturationBitmap;
    public Bitmap bitmap;
    private FrameLayout frame_layout;
    private ImageView image_view_background;
    public boolean isSplashView;
    private SplashSticker polishSplashSticker;
    public SplashSquareView polishSplashView;
    public RecyclerView recycler_view_splash;
    public SplashSaturationBackgrundListener splashSaturationBackgrundListener;
    private ViewGroup viewGroup;

    public interface SplashSaturationBackgrundListener {
        void onSaveSplashBackground(Bitmap bitmap);
    }

    public void setPolishSplashView(boolean z) {
        this.isSplashView = z;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static SplashFragment show(AppCompatActivity appCompatActivity, Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, SplashSaturationBackgrundListener splashSaturationBackgrundListener, boolean z) {
        SplashFragment splashFragment = new SplashFragment();
        splashFragment.setBitmap(bitmap);
        splashFragment.setSaturationBitmap(bitmap3);
        splashFragment.setSplashSaturationBackgrundListener(splashSaturationBackgrundListener);
        splashFragment.setPolishSplashView(z);
        splashFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return splashFragment;
    }

    public void setSaturationBitmap(Bitmap bitmap) {
        this.SaturationBitmap = bitmap;
    }

    public void setSplashSaturationBackgrundListener(SplashSaturationBackgrundListener splashSaturationBackgrundListener) {
        this.splashSaturationBackgrundListener = splashSaturationBackgrundListener;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//        getDialog().getWindow().requestFeature(1);
//        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_splash, viewGroup, false);
        this.viewGroup = viewGroup;
        this.image_view_background = (ImageView) inflate.findViewById(R.id.imageViewBackground);
        this.polishSplashView = (SplashSquareView) inflate.findViewById(R.id.splashView);
        this.frame_layout = (FrameLayout) inflate.findViewById(R.id.frame_layout);
        this.image_view_background.setImageBitmap(this.bitmap);
        if (this.isSplashView) {
            this.polishSplashView.setImageBitmap(this.SaturationBitmap);
        }
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerViewSplashSquare);
        this.recycler_view_splash = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recycler_view_splash.setHasFixedSize(true);
        this.recycler_view_splash.setAdapter(new SquareAdapter(getContext(), this, this.isSplashView));
        if (this.isSplashView) {
            SplashSticker splashSticker = new SplashSticker(StickerFile.loadBitmapFromAssets(getContext(), "blur/image_mask_1.webp"), StickerFile.loadBitmapFromAssets(getContext(), "blur/image_frame_1.webp"));
            this.polishSplashSticker = splashSticker;
            this.polishSplashView.addSticker(splashSticker);
        }
        this.polishSplashView.refreshDrawableState();
        this.polishSplashView.setLayerType(2, null);
        inflate.findViewById(R.id.imageViewSaveSplash).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.SplashFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashFragment.this.splashSaturationBackgrundListener.onSaveSplashBackground(SplashFragment.this.polishSplashView.getBitmap(SplashFragment.this.bitmap));
                SplashFragment.this.dismiss();
            }
        });
        inflate.findViewById(R.id.imageViewCloseSplash).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.SplashFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashFragment.this.dismiss();
            }
        });
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.polishSplashView.getSticker().release();
        Bitmap bitmap = this.SaturationBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.SaturationBitmap = null;
        this.bitmap = null;
    }

    @Override // com.gallery.photos.editphotovideo.adapters.SquareAdapter.SplashChangeListener
    public void onSelected(SplashSticker splashSticker) {
        this.polishSplashView.addSticker(splashSticker);
    }
}
