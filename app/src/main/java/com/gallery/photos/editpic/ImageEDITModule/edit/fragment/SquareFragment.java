package com.gallery.photos.editpic.ImageEDITModule.edit.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.SquareAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.FilterFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.StickerFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.square.SquareView;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.SplashSticker;
import com.gallery.photos.editpic.R;


public class SquareFragment extends DialogFragment implements SquareAdapter.SplashChangeListener {
    private static final String TAG = "SquareFragment";
    private Bitmap BlurBitmap;
    public Bitmap bitmap;
    public SplashDialogListener blurSquareBgListener;
    private FrameLayout frameLayout;
    private ImageView imageViewBackground;
    public boolean isSplashView;
    private SplashSticker polishSplashSticker;
    public SquareView polishSplashView;
    public RecyclerView recyclerViewBlur;
    private SeekBar seekbar_brush;

    public interface SplashDialogListener {
        void onSaveBlurBackground(Bitmap bitmap);
    }

    public void setPolishSplashView(boolean z) {
        this.isSplashView = z;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static SquareFragment show(AppCompatActivity appCompatActivity, Bitmap bitmap, Bitmap bitmap2, Bitmap bitmap3, SplashDialogListener splashDialogListener, boolean z) {
        SquareFragment squareFragment = new SquareFragment();
        squareFragment.setBitmap(bitmap);
        squareFragment.setBlurBitmap(bitmap3);
        squareFragment.setBlurSquareBgListener(splashDialogListener);
        squareFragment.setPolishSplashView(z);
        squareFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return squareFragment;
    }

    public void setBlurBitmap(Bitmap bitmap) {
        this.BlurBitmap = bitmap;
    }

    public void setBlurSquareBgListener(SplashDialogListener splashDialogListener) {
        this.blurSquareBgListener = splashDialogListener;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_square, viewGroup, false);
        this.imageViewBackground = (ImageView) inflate.findViewById(R.id.image_view_background);
        this.polishSplashView = (SquareView) inflate.findViewById(R.id.splash_view);
        this.frameLayout = (FrameLayout) inflate.findViewById(R.id.frame_layout);
        this.imageViewBackground.setImageBitmap(this.bitmap);
        if (this.isSplashView) {
            this.polishSplashView.setImageBitmap(this.BlurBitmap);
        }
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recycler_view_splash);
        this.recyclerViewBlur = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recyclerViewBlur.setHasFixedSize(true);
        this.recyclerViewBlur.setAdapter(new SquareAdapter(getContext(), this, this.isSplashView));
        if (this.isSplashView) {
            SplashSticker splashSticker = new SplashSticker(StickerFile.loadBitmapFromAssets(getContext(), "blur/image_mask_1.webp"), StickerFile.loadBitmapFromAssets(getContext(), "blur/image_frame_1.webp"));
            this.polishSplashSticker = splashSticker;
            this.polishSplashView.addSticker(splashSticker);
        }
        SeekBar seekBar = (SeekBar) inflate.findViewById(R.id.seekbar_brush);
        this.seekbar_brush = seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.album.photomanager.fragment.SquareFragment.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                SquareFragment.this.new LoadBlurBitmap(i).execute(new Void[0]);
            }
        });
        this.polishSplashView.refreshDrawableState();
        this.polishSplashView.setLayerType(2, null);
        inflate.findViewById(R.id.image_view_save).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.album.photomanager.fragment.SquareFragment.2
            @Override
            public void onClick(View view) {
                SquareFragment.this.blurSquareBgListener.onSaveBlurBackground(SquareFragment.this.polishSplashView.getBitmap(SquareFragment.this.bitmap));
                SquareFragment.this.dismiss();
            }
        });
        inflate.findViewById(R.id.image_view_close).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.album.photomanager.fragment.SquareFragment.3
            @Override
            public void onClick(View view) {
                SquareFragment.this.dismiss();
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

    class LoadBlurBitmap extends AsyncTask<Void, Bitmap, Bitmap> {
        private float intensity;

        @Override // android.os.AsyncTask
        public void onPreExecute() {
        }

        public LoadBlurBitmap(float f) {
            this.intensity = f;
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Void... voidArr) {
            return FilterFile.getBlurImageFromBitmap(SquareFragment.this.bitmap, this.intensity);
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            SquareFragment.this.polishSplashView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.polishSplashView.getSticker().release();
        Bitmap bitmap = this.BlurBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.BlurBitmap = null;
        this.bitmap = null;
    }

    @Override // com.gallery.album.photomanager.adapters.SquareAdapter.SplashChangeListener
    public void onSelected(SplashSticker splashSticker) {
        this.polishSplashView.addSticker(splashSticker);
    }
}
