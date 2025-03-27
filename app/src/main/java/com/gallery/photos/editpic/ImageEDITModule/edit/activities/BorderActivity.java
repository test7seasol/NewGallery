package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.DripView;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.FrameAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.PixLabAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.ProfileAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.LayoutItemListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.ProfileItemListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.ScaleTouchListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.WingsItemListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ColorSlider;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.DripFrameLayout;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.DripUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ImageUtils;
import com.gallery.photos.editpic.R;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

/* loaded from: classes.dex */
public class BorderActivity extends BaseActivity implements LayoutItemListener, WingsItemListener, ProfileItemListener {
    private static Bitmap faceBitmap;

    private DripView dripViewCover;
    private DripView dripViewFrame;
    private DripView dripViewPixLab;
    private DripView dripViewProfile;
    private FrameAdapter frameAdapter;
    private DripFrameLayout frameLayoutBackground;
    private ImageView imageViewColor;
    private ImageView imageViewFrame;
    private ImageView imageViewPixLab;
    private ImageView imageViewProfile;
    private LinearLayout linLayoutColor;
    private PixLabAdapter pixLabAdapter;
    private ProfileAdapter profileAdapter;
    private RecyclerView recyclerViewFrame;
    private RecyclerView recyclerViewPixLab;
    private RecyclerView recyclerViewProfile;
    private Bitmap selectedBitmap;
    private TextView textViewColor;
    private TextView textViewFrame;
    private TextView textViewPixLab;
    private TextView textViewProfile;
    private Bitmap mainBitmap = null;
    private Bitmap OverLayBackground = null;
    private boolean isFirst = true;
    private ArrayList<String> profileEffectList = new ArrayList<>();
    private ArrayList<String> frameList = new ArrayList<>();
    private ArrayList<String> dripEffectList = new ArrayList<>();
    private ColorSlider.OnColorSelectedListener mListener = new ColorSlider.OnColorSelectedListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.10
        @Override // com.gallery.photos.editphotovideo.utils.ColorSlider.OnColorSelectedListener
        public void onColorChanged(int i, int i2) {
            BorderActivity.this.updateView(i2);
        }
    };


    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_border);

        this.dripViewFrame = (DripView) findViewById(R.id.dripViewFrame);
        this.dripViewCover = (DripView) findViewById(R.id.dripViewImage);
        this.dripViewProfile = (DripView) findViewById(R.id.dripViewProfile);
        this.dripViewPixLab = (DripView) findViewById(R.id.dripViewPixLab);
        this.recyclerViewFrame = (RecyclerView) findViewById(R.id.recyclerViewFrame);
        this.recyclerViewPixLab = (RecyclerView) findViewById(R.id.recyclerViewPixLab);
        this.linLayoutColor = (LinearLayout) findViewById(R.id.linLayoutColor);
        this.recyclerViewProfile = (RecyclerView) findViewById(R.id.recyclerViewProfile);
        this.textViewFrame = (TextView) findViewById(R.id.textViewFrame);
        this.textViewColor = (TextView) findViewById(R.id.textViewColor);
        this.textViewPixLab = (TextView) findViewById(R.id.textViewPixlab);
        this.textViewProfile = (TextView) findViewById(R.id.textViewProfile);
        this.imageViewFrame = (ImageView) findViewById(R.id.imageViewFrame);
        this.imageViewColor = (ImageView) findViewById(R.id.imageViewColor);
        this.imageViewPixLab = (ImageView) findViewById(R.id.imageViewPixlab);
        this.imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        this.dripViewCover.setOnTouchListener(new ScaleTouchListener(this, true));
        this.frameLayoutBackground = (DripFrameLayout) findViewById(R.id.frameLayoutBackground);
        new Handler().postDelayed(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.1
            @Override // java.lang.Runnable
            public void run() {
                BorderActivity.this.dripViewCover.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (BorderActivity.this.isFirst) {
                            BorderActivity.this.isFirst = false;
                            BorderActivity.this.initBitmap();
                        }
                    }
                });
            }
        }, 1000L);
        findViewById(R.id.linearLayoutFrame).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BorderActivity.this.imageViewFrame.setColorFilter(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.imageViewColor.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewPixLab.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewProfile.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewFrame.setTextColor(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.textViewColor.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewPixLab.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewProfile.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.dripViewPixLab.setVisibility(View.GONE);
                BorderActivity.this.dripViewFrame.setVisibility(View.VISIBLE);
                BorderActivity.this.recyclerViewFrame.setVisibility(View.VISIBLE);
                BorderActivity.this.linLayoutColor.setVisibility(View.GONE);
                BorderActivity.this.recyclerViewPixLab.setVisibility(View.GONE);
                BorderActivity.this.recyclerViewProfile.setVisibility(View.GONE);
                BorderActivity.this.dripViewProfile.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.linearLayoutColor).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BorderActivity.this.imageViewFrame.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewPixLab.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewProfile.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewColor.setColorFilter(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.textViewFrame.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewPixLab.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewProfile.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewColor.setTextColor(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.recyclerViewFrame.setVisibility(View.GONE);
                BorderActivity.this.linLayoutColor.setVisibility(View.VISIBLE);
                BorderActivity.this.recyclerViewPixLab.setVisibility(View.GONE);
                BorderActivity.this.recyclerViewProfile.setVisibility(View.GONE);
                BorderActivity.this.dripViewPixLab.setVisibility(View.VISIBLE);
                BorderActivity.this.dripViewFrame.setVisibility(View.GONE);
                BorderActivity.this.dripViewProfile.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.linearLayoutProfile).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BorderActivity.this.imageViewFrame.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewPixLab.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewProfile.setColorFilter(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.imageViewColor.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewFrame.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewPixLab.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewProfile.setTextColor(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.textViewColor.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.recyclerViewFrame.setVisibility(View.GONE);
                BorderActivity.this.linLayoutColor.setVisibility(View.GONE);
                BorderActivity.this.recyclerViewPixLab.setVisibility(View.GONE);
                BorderActivity.this.recyclerViewProfile.setVisibility(View.VISIBLE);
                BorderActivity.this.dripViewPixLab.setVisibility(View.GONE);
                BorderActivity.this.dripViewFrame.setVisibility(View.GONE);
                BorderActivity.this.dripViewProfile.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.linearLayoutPixlab).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BorderActivity.this.imageViewFrame.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewPixLab.setColorFilter(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.imageViewProfile.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.imageViewColor.setColorFilter(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewFrame.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewPixLab.setTextColor(BorderActivity.this.getResources().getColor(R.color.mainColor));
                BorderActivity.this.textViewProfile.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.textViewColor.setTextColor(BorderActivity.this.getResources().getColor(R.color.iconColor));
                BorderActivity.this.recyclerViewFrame.setVisibility(View.GONE);
                BorderActivity.this.linLayoutColor.setVisibility(View.GONE);
                BorderActivity.this.recyclerViewPixLab.setVisibility(View.VISIBLE);
                BorderActivity.this.recyclerViewProfile.setVisibility(View.GONE);
                BorderActivity.this.dripViewPixLab.setVisibility(View.VISIBLE);
                BorderActivity.this.dripViewFrame.setVisibility(View.GONE);
                BorderActivity.this.dripViewProfile.setVisibility(View.GONE);
            }
        });
        ColorSlider colorSlider = (ColorSlider) findViewById(R.id.color_slider);
        colorSlider.setSelectorColor(-1);
        colorSlider.setListener(this.mListener);
        updateView(colorSlider.getSelectedColor());
        findViewById(R.id.imageViewColorPicker).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new AmbilWarnaDialog(BorderActivity.this, Color.parseColor("#0090FF"), true, new AmbilWarnaDialog.OnAmbilWarnaListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.6.1
                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                        BorderActivity.this.updateView(i);
                    }
                }).show();
            }
        });
        findViewById(R.id.imageViewCloseSplash).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BorderActivity.this.onBackPressed();
            }
        });
        findViewById(R.id.imageViewSaveSplash).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new saveFile().execute(new String[0]);
            }
        });
        for (int i = 1; i <= 15; i++) {
            this.frameList.add("frame_" + i);
        }
        for (int i2 = 1; i2 <= 10; i2++) {
            this.profileEffectList.add("frame_" + i2);
        }
        for (int i3 = 1; i3 <= 10; i3++) {
            this.dripEffectList.add("style_" + i3);
        }
        this.recyclerViewFrame.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerViewPixLab.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerViewProfile.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        setFrameList();
        setProfileList();
        setPixLabList();
        this.dripViewCover.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BorderActivity.9
            @Override // java.lang.Runnable
            public void run() {
                try {
                    BorderActivity.this.initBitmap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateView(int i) {
        this.dripViewPixLab.setColorFilter(i);
    }

    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBitmap() {
        // Create a defensive copy if needed
        Bitmap sourceBitmap = faceBitmap;

        if (sourceBitmap != null && !sourceBitmap.isRecycled()) {
            try {
                Bitmap bitmapResize = ImageUtils.getBitmapResize(this, sourceBitmap, 1024, 1024);
                this.selectedBitmap = bitmapResize;
                this.dripViewCover.setImageBitmap(bitmapResize);
            } catch (Exception e) {
                Log.e("BorderActivity", "Error resizing bitmap", e);
                // Handle error case (e.g., show placeholder image)
            }
        } else {
            Log.w("BorderActivity", "Source bitmap is null or recycled");
            // Handle null/recycled case (e.g., show placeholder image)
        }
    }
    @Override // com.gallery.photos.editphotovideo.listener.LayoutItemListener
    public void onLayoutListClick(View view, int i) {
        Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(this, "frame/" + this.frameAdapter.getItemList().get(i) + ".png");
        if (!"none".equals(this.frameAdapter.getItemList().get(i))) {
            this.OverLayBackground = bitmapFromAsset;
            this.dripViewFrame.setImageBitmap(bitmapFromAsset);
        } else {
            this.OverLayBackground = null;
        }
    }

    @Override // com.gallery.photos.editphotovideo.listener.ProfileItemListener
    public void onProfileListClick(View view, int i) {
        Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(this, "card/" + this.frameAdapter.getItemList().get(i) + ".webp");
        if (!"none".equals(this.frameAdapter.getItemList().get(i))) {
            this.OverLayBackground = bitmapFromAsset;
            this.dripViewProfile.setImageBitmap(bitmapFromAsset);
        } else {
            this.OverLayBackground = null;
        }
    }

    @Override // com.gallery.photos.editphotovideo.listener.WingsItemListener
    public void onWingListClick(View view, int i) {
        Bitmap bitmapFromAsset = DripUtils.getBitmapFromAsset(this, "pixlab/" + this.pixLabAdapter.getItemList().get(i) + ".webp");
        if (!"none".equals(this.pixLabAdapter.getItemList().get(i))) {
            this.OverLayBackground = bitmapFromAsset;
            this.dripViewPixLab.setImageBitmap(bitmapFromAsset);
        } else {
            this.OverLayBackground = null;
        }
    }

    public void setFrameList() {
        FrameAdapter frameAdapter = new FrameAdapter(this);
        this.frameAdapter = frameAdapter;
        frameAdapter.setClickListener(this);
        this.recyclerViewFrame.setAdapter(this.frameAdapter);
        this.frameAdapter.addData(this.frameList);
    }

    public void setProfileList() {
        ProfileAdapter profileAdapter = new ProfileAdapter(this);
        this.profileAdapter = profileAdapter;
        profileAdapter.setClickListener(this);
        this.recyclerViewProfile.setAdapter(this.profileAdapter);
        this.profileAdapter.addData(this.profileEffectList);
    }

    public void setPixLabList() {
        PixLabAdapter pixLabAdapter = new PixLabAdapter(this);
        this.pixLabAdapter = pixLabAdapter;
        pixLabAdapter.setClickListener(this);
        this.recyclerViewPixLab.setAdapter(this.pixLabAdapter);
        this.pixLabAdapter.addData(this.dripEffectList);
    }

    private class saveFile extends AsyncTask<String, Bitmap, Bitmap> {
        private saveFile() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public Bitmap getBitmapFromView(View view) {
            Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            return createBitmap;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Bitmap doInBackground(String... strArr) {
            BorderActivity.this.frameLayoutBackground.setDrawingCacheEnabled(true);
            try {
                Bitmap bitmapFromView = getBitmapFromView(BorderActivity.this.frameLayoutBackground);
                BorderActivity.this.frameLayoutBackground.setDrawingCacheEnabled(false);
                return bitmapFromView;
            } catch (Exception unused) {
                BorderActivity.this.frameLayoutBackground.setDrawingCacheEnabled(false);
                return null;
            } catch (Throwable th) {
                BorderActivity.this.frameLayoutBackground.setDrawingCacheEnabled(false);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                BitmapTransfer.bitmap = bitmap;
            }
            Intent intent = new Intent(BorderActivity.this, (Class<?>) PhotoEditorActivity.class);
            intent.putExtra("MESSAGE", "done");
            BorderActivity.this.setResult(-1, intent);
            BorderActivity.this.startActivity(intent);
            BorderActivity.this.finish();
        }
    }
}
