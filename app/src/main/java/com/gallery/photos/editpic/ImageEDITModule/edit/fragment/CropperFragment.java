package com.gallery.photos.editpic.ImageEDITModule.edit.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.AspectAdapter;
import com.gallery.photos.editpic.ImageEDITModule.github.flipzeus.FlipDirection;
import com.gallery.photos.editpic.ImageEDITModule.github.flipzeus.ImageFlipper;
import com.gallery.photos.editpic.R;

import com.isseiaoki.simplecropview.CropImageView;
import com.steelkiwi.cropiwa.AspectRatio;

/* loaded from: classes.dex */
public class CropperFragment extends DialogFragment implements AspectAdapter.OnNewSelectedListener {
    private static final String TAG = "CropFragment";
    private Bitmap bitmap;
    public CropImageView crop_image_view;
    public OnCropPhoto onCropPhoto;
    private RelativeLayout relative_layout_loading;

    public interface OnCropPhoto {
        void finishCrop(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static CropperFragment show(AppCompatActivity appCompatActivity, OnCropPhoto onCropPhoto, Bitmap bitmap) {
        CropperFragment cropperFragment = new CropperFragment();
        cropperFragment.setBitmap(bitmap);
        cropperFragment.setOnCropPhoto(onCropPhoto);
        cropperFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return cropperFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void setOnCropPhoto(OnCropPhoto onCropPhoto) {
        this.onCropPhoto = onCropPhoto;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
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
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.fragment_cropper, viewGroup, false);
        AspectAdapter aspectAdapter = new AspectAdapter();
        aspectAdapter.setListener(this);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recycler_view_ratio);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(aspectAdapter);
        CropImageView cropImageView = (CropImageView) inflate.findViewById(R.id.crop_image_view);
        this.crop_image_view = cropImageView;
        cropImageView.setCropMode(CropImageView.CropMode.FREE);
        inflate.findViewById(R.id.linearLayoutRotateLeft).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.CropperFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CropperFragment.this.crop_image_view.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
        });
        inflate.findViewById(R.id.linearLayoutRotateRight).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.CropperFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CropperFragment.this.crop_image_view.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });
        inflate.findViewById(R.id.linearLayoutFlipV).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.CropperFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ImageFlipper.flip(CropperFragment.this.crop_image_view, FlipDirection.VERTICAL);
            }
        });
        inflate.findViewById(R.id.linearLayoutFlipH).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.CropperFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ImageFlipper.flip(CropperFragment.this.crop_image_view, FlipDirection.HORIZONTAL);
            }
        });
        inflate.findViewById(R.id.imageViewSaveCrop).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.CropperFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CropperFragment.this.new OnSaveCrop().execute(new Void[0]);
            }
        });
        RelativeLayout relativeLayout = (RelativeLayout) inflate.findViewById(R.id.relative_layout_loading);
        this.relative_layout_loading = relativeLayout;
        relativeLayout.setVisibility(View.GONE);
        inflate.findViewById(R.id.imageViewCloseCrop).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.CropperFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CropperFragment.this.dismiss();
            }
        });
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        CropImageView cropImageView = (CropImageView) view.findViewById(R.id.crop_image_view);
        this.crop_image_view = cropImageView;
        cropImageView.setImageBitmap(this.bitmap);
    }

    @Override // com.gallery.photos.editphotovideo.adapters.AspectAdapter.OnNewSelectedListener
    public void onNewAspectRatioSelected(AspectRatio aspectRatio) {
        if (aspectRatio.getWidth() == 10 && aspectRatio.getHeight() == 10) {
            this.crop_image_view.setCropMode(CropImageView.CropMode.FREE);
        } else {
            this.crop_image_view.setCustomRatio(aspectRatio.getWidth(), aspectRatio.getHeight());
        }
    }

    class OnSaveCrop extends AsyncTask<Void, Bitmap, Bitmap> {
        OnSaveCrop() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            CropperFragment.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Void... voidArr) {
            return CropperFragment.this.crop_image_view.getCroppedBitmap();
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            CropperFragment.this.mLoading(false);
            CropperFragment.this.onCropPhoto.finishCrop(bitmap);
            CropperFragment.this.dismiss();
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
