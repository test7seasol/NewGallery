package com.gallery.photos.editpic.ImageEDITModule.edit.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.AspectAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.ColorRatioAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.GradientRatioAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.FilterFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;
import com.steelkiwi.cropiwa.AspectRatio;

/* loaded from: classes.dex */
public class RatioFragment extends DialogFragment implements AspectAdapter.OnNewSelectedListener, GradientRatioAdapter.BackgroundInstaListener, ColorRatioAdapter.BackgroundColorListener {
    private static final String TAG = "RatioFragment";
    private AspectRatio aspectRatio;
    private Bitmap bitmap;
    private Bitmap blurBitmap;
    private ConstraintLayout constraint_layout_ratio;
    public FrameLayout frame_layout_wrapper;
    ImageView imageViewBorder;
    ImageView imageViewColor;
    ImageView imageViewCrop;
    ImageView imageViewGradient;
    private ImageView image_view_blur;
    public ImageView image_view_ratio;
    public RatioSaveListener ratioSaveListener;
    public RecyclerView recycler_view_background;
    public RecyclerView recycler_view_color;
    public RecyclerView recycler_view_ratio;
    private RelativeLayout relative_layout_loading;
    FEATURES selectedFeatures = FEATURES.COLOR;
    TextView textViewColor;
    TextView textViewDg;
    TextView textViewFrame;
    TextView textViewRatio;

    enum FEATURES {
        COLOR, BORDER
    }

    public interface RatioSaveListener {
        void ratioSavedBitmap(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static RatioFragment show(AppCompatActivity appCompatActivity, RatioSaveListener ratioSaveListener, Bitmap bitmap, Bitmap bitmap2) {
        RatioFragment ratioFragment = new RatioFragment();
        ratioFragment.setBitmap(bitmap);
        ratioFragment.setBlurBitmap(bitmap2);
        ratioFragment.setRatioSaveListener(ratioSaveListener);
        ratioFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return ratioFragment;
    }

    public void setBlurBitmap(Bitmap bitmap) {
        this.blurBitmap = bitmap;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void setRatioSaveListener(RatioSaveListener ratioSaveListener) {
        this.ratioSaveListener = ratioSaveListener;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_ratio, viewGroup, false);
        AspectAdapter aspectAdapter = new AspectAdapter(true);
        aspectAdapter.setListener(this);
        RelativeLayout relativeLayout = (RelativeLayout) inflate.findViewById(R.id.relative_layout_loading);
        this.relative_layout_loading = relativeLayout;
        relativeLayout.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recycler_view_ratio);
        this.recycler_view_ratio = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recycler_view_ratio.setAdapter(aspectAdapter);
        this.aspectRatio = new AspectRatio(1, 1);
        RecyclerView recyclerView2 = (RecyclerView) inflate.findViewById(R.id.recycler_view_background);
        this.recycler_view_background = recyclerView2;
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recycler_view_background.setAdapter(new GradientRatioAdapter(getContext(), this));
        RecyclerView recyclerView3 = (RecyclerView) inflate.findViewById(R.id.recycler_view_color);
        this.recycler_view_color = recyclerView3;
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        this.recycler_view_color.setAdapter(new ColorRatioAdapter(getContext(), this));
        this.imageViewCrop = (ImageView) inflate.findViewById(R.id.imageViewRatio);
        this.imageViewGradient = (ImageView) inflate.findViewById(R.id.imageViewDegrade);
        this.imageViewBorder = (ImageView) inflate.findViewById(R.id.imageViewBorder);
        this.imageViewColor = (ImageView) inflate.findViewById(R.id.imageViewColor);
        this.textViewColor = (TextView) inflate.findViewById(R.id.textViewColor);
        this.textViewDg = (TextView) inflate.findViewById(R.id.textViewDegrade);
        this.textViewFrame = (TextView) inflate.findViewById(R.id.textViewBorder);
        this.textViewRatio = (TextView) inflate.findViewById(R.id.textViewRatio);
        inflate.findViewById(R.id.linearLayoutRatio).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.RatioFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RatioFragment.this.recycler_view_ratio.setVisibility(View.VISIBLE);
                RatioFragment.this.recycler_view_background.setVisibility(View.GONE);
                RatioFragment.this.recycler_view_color.setVisibility(View.GONE);
                RatioFragment.this.imageViewCrop.setColorFilter(RatioFragment.this.getResources().getColor(R.color.mainColor));
                RatioFragment.this.imageViewGradient.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewBorder.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewColor.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewColor.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewDg.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewFrame.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewRatio.setTextColor(RatioFragment.this.getResources().getColor(R.color.mainColor));
            }
        });
        inflate.findViewById(R.id.linearLayoutDegrade).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.RatioFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RatioFragment.this.recycler_view_ratio.setVisibility(View.GONE);
                RatioFragment.this.recycler_view_background.setVisibility(View.VISIBLE);
                RatioFragment.this.recycler_view_color.setVisibility(View.GONE);
                RatioFragment.this.imageViewCrop.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewGradient.setColorFilter(RatioFragment.this.getResources().getColor(R.color.mainColor));
                RatioFragment.this.imageViewBorder.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewColor.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewColor.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewDg.setTextColor(RatioFragment.this.getResources().getColor(R.color.mainColor));
                RatioFragment.this.textViewFrame.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewRatio.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
            }
        });
        inflate.findViewById(R.id.linearLayoutBorder).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.RatioFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RatioFragment.this.selectedFeatures = FEATURES.BORDER;
                RatioFragment.this.recycler_view_color.setVisibility(View.VISIBLE);
                RatioFragment.this.recycler_view_ratio.setVisibility(View.GONE);
                RatioFragment.this.recycler_view_background.setVisibility(View.GONE);
                RatioFragment.this.imageViewCrop.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewGradient.setColorFilter(RatioFragment.this.getResources().getColor(R.color.black));
                RatioFragment.this.imageViewBorder.setColorFilter(RatioFragment.this.getResources().getColor(R.color.mainColor));
                RatioFragment.this.imageViewColor.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewColor.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewDg.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewFrame.setTextColor(RatioFragment.this.getResources().getColor(R.color.mainColor));
                RatioFragment.this.textViewRatio.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
            }
        });
        inflate.findViewById(R.id.linearLayoutColor).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.RatioFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RatioFragment.this.selectedFeatures = FEATURES.COLOR;
                RatioFragment.this.recycler_view_color.setVisibility(View.VISIBLE);
                RatioFragment.this.recycler_view_ratio.setVisibility(View.GONE);
                RatioFragment.this.recycler_view_background.setVisibility(View.GONE);
                RatioFragment.this.imageViewCrop.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewGradient.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewBorder.setColorFilter(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.imageViewColor.setColorFilter(RatioFragment.this.getResources().getColor(R.color.mainColor));
                RatioFragment.this.textViewColor.setTextColor(RatioFragment.this.getResources().getColor(R.color.mainColor));
                RatioFragment.this.textViewDg.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewFrame.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
                RatioFragment.this.textViewRatio.setTextColor(RatioFragment.this.getResources().getColor(R.color.iconColor));
            }
        });
        ((SeekBar) inflate.findViewById(R.id.seekbarPadding)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.RatioFragment.5
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                int dpToPx = SystemUtil.dpToPx(RatioFragment.this.getContext(), i);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) RatioFragment.this.image_view_ratio.getLayoutParams();
                layoutParams.setMargins(dpToPx, dpToPx, dpToPx, dpToPx);
                RatioFragment.this.image_view_ratio.setLayoutParams(layoutParams);
            }
        });
        ImageView imageView = (ImageView) inflate.findViewById(R.id.image_view_ratio);
        this.image_view_ratio = imageView;
        imageView.setImageBitmap(this.bitmap);
        this.image_view_ratio.setAdjustViewBounds(true);
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.constraint_layout_ratio = (ConstraintLayout) inflate.findViewById(R.id.constraint_layout_ratio);
        ImageView imageView2 = (ImageView) inflate.findViewById(R.id.image_view_blur);
        this.image_view_blur = imageView2;
        imageView2.setImageBitmap(this.blurBitmap);
        FrameLayout frameLayout = (FrameLayout) inflate.findViewById(R.id.frame_layout_wrapper);
        this.frame_layout_wrapper = frameLayout;
        frameLayout.setLayoutParams(new ConstraintLayout.LayoutParams(point.x, point.x));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraint_layout_ratio);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 3, this.constraint_layout_ratio.getId(), 3, 0);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 1, this.constraint_layout_ratio.getId(), 1, 0);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 4, this.constraint_layout_ratio.getId(), 4, 0);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 2, this.constraint_layout_ratio.getId(), 2, 0);
        constraintSet.applyTo(this.constraint_layout_ratio);
        inflate.findViewById(R.id.image_view_close).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.RatioFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RatioFragment.this.dismiss();
            }
        });
        inflate.findViewById(R.id.image_view_save).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.RatioFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                SaveRatioView saveRatioView = RatioFragment.this.new SaveRatioView();
                RatioFragment ratioFragment = RatioFragment.this;
                saveRatioView.execute(ratioFragment.getBitmapFromView(ratioFragment.frame_layout_wrapper));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
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

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
    }

    private int[] calculateWidthAndHeight(AspectRatio aspectRatio, Point point) {
        int height = this.constraint_layout_ratio.getHeight();
        if (aspectRatio.getHeight() > aspectRatio.getWidth()) {
            int ratio = (int) (aspectRatio.getRatio() * height);
            if (ratio < point.x) {
                return new int[]{ratio, height};
            }
            return new int[]{point.x, (int) (point.x / aspectRatio.getRatio())};
        }
        int ratio2 = (int) (point.x / aspectRatio.getRatio());
        if (ratio2 > height) {
            return new int[]{(int) (height * aspectRatio.getRatio()), height};
        }
        return new int[]{point.x, ratio2};
    }

    @Override // com.gallery.photos.editphotovideo.adapters.AspectAdapter.OnNewSelectedListener
    public void onNewAspectRatioSelected(AspectRatio aspectRatio) {
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.aspectRatio = aspectRatio;
        int[] calculateWidthAndHeight = calculateWidthAndHeight(aspectRatio, point);
        this.frame_layout_wrapper.setLayoutParams(new ConstraintLayout.LayoutParams(calculateWidthAndHeight[0], calculateWidthAndHeight[1]));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraint_layout_ratio);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 3, this.constraint_layout_ratio.getId(), 3, 0);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 1, this.constraint_layout_ratio.getId(), 1, 0);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 4, this.constraint_layout_ratio.getId(), 4, 0);
        constraintSet.connect(this.frame_layout_wrapper.getId(), 2, this.constraint_layout_ratio.getId(), 2, 0);
        constraintSet.applyTo(this.constraint_layout_ratio);
    }

    class SaveRatioView extends AsyncTask<Bitmap, Bitmap, Bitmap> {
        SaveRatioView() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            RatioFragment.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap cloneBitmap = FilterFile.cloneBitmap(bitmapArr[0]);
            bitmapArr[0].recycle();
            bitmapArr[0] = null;
            return cloneBitmap;
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            RatioFragment.this.mLoading(false);
            RatioFragment.this.ratioSaveListener.ratioSavedBitmap(bitmap);
            RatioFragment.this.dismiss();
        }
    }

    @Override
    // com.gallery.photos.editphotovideo.adapters.ColorRatioAdapter.BackgroundColorListener
    public void onBackgroundColorSelected(int i, ColorRatioAdapter.SquareView squareView) {
        if (this.selectedFeatures == FEATURES.COLOR) {
            if (squareView.isColor) {
                this.frame_layout_wrapper.setBackgroundColor(squareView.drawableId);
                this.image_view_blur.setVisibility(View.GONE);
            } else if (squareView.text.equals("None")) {
                this.image_view_blur.setVisibility(View.VISIBLE);
            } else {
                this.frame_layout_wrapper.setBackgroundResource(squareView.drawableId);
                this.image_view_blur.setVisibility(View.GONE);
            }
            this.frame_layout_wrapper.invalidate();
            return;
        }
        if (this.selectedFeatures == FEATURES.BORDER) {
            if (squareView.isColor) {
                this.image_view_ratio.setBackgroundColor(squareView.drawableId);
                int dpToPx = SystemUtil.dpToPx(getContext(), 3);
                this.image_view_ratio.setPadding(dpToPx, dpToPx, dpToPx, dpToPx);
            } else if (squareView.text.equals("None")) {
                this.image_view_ratio.setPadding(0, 0, 0, 0);
                this.frame_layout_wrapper.invalidate();
            } else {
                int dpToPx2 = SystemUtil.dpToPx(getContext(), 3);
                this.image_view_ratio.setPadding(dpToPx2, dpToPx2, dpToPx2, dpToPx2);
                this.image_view_ratio.setBackgroundColor(squareView.drawableId);
            }
        }
    }

    @Override
    // com.gallery.photos.editphotovideo.adapters.GradientRatioAdapter.BackgroundInstaListener
    public void onBackgroundSelected(int i, GradientRatioAdapter.SquareView squareView) {
        if (squareView.isGradient) {
            this.frame_layout_wrapper.setBackgroundColor(squareView.drawableId);
        } else if (squareView.text.equals("None")) {
            this.image_view_blur.setVisibility(View.VISIBLE);
        } else {
            this.frame_layout_wrapper.setBackgroundResource(squareView.drawableId);
            this.image_view_blur.setVisibility(View.GONE);
        }
        this.frame_layout_wrapper.invalidate();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        Bitmap bitmap = this.blurBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.blurBitmap = null;
        }
        this.bitmap = null;
    }

    private boolean isConnectedNetwork() {
        boolean z = false;
        boolean z2 = false;
        for (NetworkInfo networkInfo : ((ConnectivityManager) getActivity().getSystemService("connectivity")).getAllNetworkInfo()) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI") && networkInfo.isConnected()) {
                z2 = true;
            }
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE") && networkInfo.isConnected()) {
                z = true;
            }
        }
        return z || z2;
    }

    public Bitmap getBitmapFromView(View view) {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            return createBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public void mLoading(boolean z) {
        if (z) {
            getActivity().getWindow().setFlags(16, 16);
            this.relative_layout_loading.setVisibility(View.VISIBLE);
        } else {
            getActivity().getWindow().clearFlags(16);
            this.relative_layout_loading.setVisibility(View.GONE);
        }
    }
}
