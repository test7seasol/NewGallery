package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.BgAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.LayoutItemListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.MultiTouchListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.support.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ColorFilterGenerator;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ImageUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SaveFileUtils;
import com.gallery.photos.editpic.R;
import com.google.android.material.tabs.TabLayout;
import com.quarkworks.roundedframelayout.RoundedFrameLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class RemoveBgActivity extends AppCompatActivity implements LayoutItemListener {
    private static int IMAGE_GALLERY_REQUEST = 20;
    public static Bitmap bgCircleBit = null;
    private static int curBgType = 1;
    public static Bitmap eraserResultBmp;
    public static Bitmap faceBitmap;
    public static Bitmap faceBitmap2;

    private BgAdapter bgAdapter;
    private Bitmap bitmap;
    private Bitmap foreground;
    private RelativeLayout frameLayoutContent;
    public int height;
    private ImageView imageViewBg;
    private ImageView ivBGCover;
    private ImageView ivBackground;
    private ImageView ivBrightness;
    private ImageView ivContrast;
    private ImageView ivExposure;
    private ImageView ivHue;
    private ImageView ivMain;
    private ImageView ivSaturation;
    public LinearLayout llAdjust;
    public LinearLayout llBg;
    public LinearLayout llBrightness;
    public LinearLayout llContrast;
    public LinearLayout llExposure;
    public LinearLayout llHue;
    public LinearLayout llSaturation;
    private Context mContext;
    private String openFrom;
    RecyclerView recyclerViewStyle;
    RoundedFrameLayout rfChange;
    public SeekBar sBSmooth;
    public SeekBar sbAdjust;
    private Bitmap selectedBit;
    TabLayout tabLayout;
    private TextView tvBrightness;
    private TextView tvContrast;
    private TextView tvExposure;
    private TextView tvHue;
    private TextView tvSaturation;
    public View vBrightness;
    public View vContrast;
    public View vExposure;
    public View vHue;
    public View vLine;
    public View vSaturation;
    public int width;
    boolean isFirstTime = true;
    public int count = 0;
    int beach = 6;
    int city = 7;
    int desert = 6;
    int color = 30;
    int forest = 6;
    int garden = 8;
    int gradient = 11;
    int nature = 15;
    int places = 14;
    int road = 9;
    int spiral = 7;
    int gate = 7;
    private ArrayList<String> beachList = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> desertList = new ArrayList<>();
    private ArrayList<String> colorList = new ArrayList<>();
    private ArrayList<String> forestList = new ArrayList<>();
    private ArrayList<String> gardenList = new ArrayList<>();
    private ArrayList<String> gradientList = new ArrayList<>();
    private ArrayList<String> natureList = new ArrayList<>();
    private ArrayList<String> placesList = new ArrayList<>();
    private ArrayList<String> roadList = new ArrayList<>();
    private ArrayList<String> spiralList = new ArrayList<>();
    private ArrayList<String> gateList = new ArrayList<>();
    MODULE selectedFeatures = MODULE.COVER;

    enum MODULE {
        COVER,
        BACKGROUND
    }


    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_remove_bg);
        this.openFrom = getIntent().getStringExtra(Constants.KEY_OPEN_FROM);
        this.mContext = this;
        this.selectedBit = faceBitmap;

        new Handler().postDelayed(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.1
            @Override // java.lang.Runnable
            public void run() {
                RemoveBgActivity.this.ivBackground.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!RemoveBgActivity.this.isFirstTime || RemoveBgActivity.this.selectedBit == null) {
                            return;
                        }
                        RemoveBgActivity.this.isFirstTime = false;
                        RemoveBgActivity.this.initBMPNew();
                    }
                });
            }
        }, 1000L);
        for (int i = 1; i <= this.beach; i++) {
            this.beachList.add("beach_" + i);
        }
        for (int i2 = 1; i2 <= this.city; i2++) {
            this.cityList.add("city_" + i2);
        }
        for (int i3 = 1; i3 <= this.desert; i3++) {
            this.desertList.add("desert_" + i3);
        }
        for (int i4 = 1; i4 <= this.color; i4++) {
            this.colorList.add("color_" + i4);
        }
        for (int i5 = 1; i5 <= this.forest; i5++) {
            this.forestList.add("forest_" + i5);
        }
        for (int i6 = 1; i6 <= this.garden; i6++) {
            this.gardenList.add("garden_" + i6);
        }
        for (int i7 = 1; i7 <= this.gradient; i7++) {
            this.gradientList.add("gradient_" + i7);
        }
        for (int i8 = 1; i8 <= this.nature; i8++) {
            this.natureList.add("nature_" + i8);
        }
        for (int i9 = 1; i9 <= this.places; i9++) {
            this.placesList.add("places_" + i9);
        }
        for (int i10 = 1; i10 <= this.road; i10++) {
            this.roadList.add("road_" + i10);
        }
        for (int i11 = 1; i11 <= this.spiral; i11++) {
            this.spiralList.add("spiral_" + i11);
        }
        for (int i12 = 1; i12 <= this.gate; i12++) {
            this.gateList.add("gate_" + i12);
        }
        Init();
        this.tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        this.vLine = findViewById(R.id.vLine);
        this.ivMain = (ImageView) findViewById(R.id.imageViewWings);
        this.rfChange = (RoundedFrameLayout) findViewById(R.id.refresh_button_container);
        this.llBg = (LinearLayout) findViewById(R.id.linLayoutBg);
        this.ivMain.setVisibility(View.GONE);
        this.ivBackground.setOnTouchListener(new MultiTouchListener(this, true));
        findViewById(R.id.imageViewCloseWings).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.onBackPressed();
            }
        });
        this.ivBGCover = (ImageView) findViewById(R.id.imageViewBackgroundCover);
        this.imageViewBg = (ImageView) findViewById(R.id.imageViewBg);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarSmooth);
        this.sBSmooth = seekBar;
        processingBitmap_Blur(this.foreground, seekBar.getProgress());
        this.sBSmooth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i13, boolean z) {
                if (i13 == 0) {
                    RemoveBgActivity.faceBitmap = RemoveBgActivity.faceBitmap2;
                    RemoveBgActivity.faceBitmap2 = RemoveBgActivity.this.foreground;
                    RemoveBgActivity.this.ivBackground.setImageBitmap(RemoveBgActivity.faceBitmap2);
                } else {
                    RemoveBgActivity removeBgActivity = RemoveBgActivity.this;
                    removeBgActivity.processingBitmap_Blur(removeBgActivity.foreground, i13);
                }
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i13 = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
        this.height = i13 - ImageUtils.dpToPx((Context) this, 120.0f);
        curBgType = 1;
        if (this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_HOME_REMOVE)) {
            this.tabLayout.setVisibility(View.GONE);
            this.llBg.setVisibility(View.GONE);
            this.rfChange.setVisibility(View.VISIBLE);
            this.ivMain.setVisibility(View.GONE);
            this.ivBGCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg2, this.width, this.height));
        } else if (this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_TOOL_CHANGE)) {
            this.tabLayout.setVisibility(View.VISIBLE);
            this.llBg.setVisibility(View.VISIBLE);
            this.rfChange.setVisibility(View.GONE);
            this.ivMain.setVisibility(View.VISIBLE);
            this.ivMain.setImageResource(R.drawable.beach_1);
        }
        findViewById(R.id.imageViewSaveWings).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.saveEffect();
                if (RemoveBgActivity.this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_TOOL_CHANGE)) {

                    new saveFile().execute(new String[0]);

                }
                if (RemoveBgActivity.this.openFrom.equalsIgnoreCase(Constants.VALUE_OPEN_FROM_HOME_REMOVE)) {

                    RemoveBgActivity.this.new SaveBitmapWithoutBg().execute(new Void[0]);

                }
            }
        });
        findViewById(R.id.linearLayoutCurrentImage).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EraserBgActivity.b = RemoveBgActivity.this.foreground;
                Intent intent = new Intent(RemoveBgActivity.this, (Class<?>) EraserBgActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_REMOVE_BG);
                RemoveBgActivity.this.startActivityForResult(intent, 1024);
            }
        });
        findViewById(R.id.linearLayoutOriginalImage).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EraserBgActivity.b = RemoveBgActivity.faceBitmap;
                Intent intent = new Intent(RemoveBgActivity.this, (Class<?>) EraserBgActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_REMOVE_BG);
                RemoveBgActivity.this.startActivityForResult(intent, 1024);
            }
        });
        findViewById(R.id.imageViewBg).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.changeBG();
                RemoveBgActivity.this.ivMain.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.imageViewPicker).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.selectedFeatures = MODULE.BACKGROUND;
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                RemoveBgActivity.this.startActivityForResult(intent, RemoveBgActivity.IMAGE_GALLERY_REQUEST);
            }
        });
        TabLayout tabLayout = this.tabLayout;
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.beach)));
        TabLayout tabLayout2 = this.tabLayout;
        tabLayout2.addTab(tabLayout2.newTab().setText(getResources().getString(R.string.City)));
        TabLayout tabLayout3 = this.tabLayout;
        tabLayout3.addTab(tabLayout3.newTab().setText(getResources().getString(R.string.Desert)));
        TabLayout tabLayout4 = this.tabLayout;
        tabLayout4.addTab(tabLayout4.newTab().setText(getResources().getString(R.string.color)));
        TabLayout tabLayout5 = this.tabLayout;
        tabLayout5.addTab(tabLayout5.newTab().setText(getResources().getString(R.string.Forest)));
        TabLayout tabLayout6 = this.tabLayout;
        tabLayout6.addTab(tabLayout6.newTab().setText(getResources().getString(R.string.Garden)));
        TabLayout tabLayout7 = this.tabLayout;
        tabLayout7.addTab(tabLayout7.newTab().setText(getResources().getString(R.string.Degrade)));
        TabLayout tabLayout8 = this.tabLayout;
        tabLayout8.addTab(tabLayout8.newTab().setText(getResources().getString(R.string.Nature)));
        TabLayout tabLayout9 = this.tabLayout;
        tabLayout9.addTab(tabLayout9.newTab().setText(getResources().getString(R.string.Places)));
        TabLayout tabLayout10 = this.tabLayout;
        tabLayout10.addTab(tabLayout10.newTab().setText(getResources().getString(R.string.Road)));
        TabLayout tabLayout11 = this.tabLayout;
        tabLayout11.addTab(tabLayout11.newTab().setText(getResources().getString(R.string.Spiral)));
        TabLayout tabLayout12 = this.tabLayout;
        tabLayout12.addTab(tabLayout12.newTab().setText(getResources().getString(R.string.gate)));
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.9
            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.beachList);
                        break;
                    case 1:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.cityList);
                        break;
                    case 2:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.desertList);
                        break;
                    case 3:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.colorList);
                        break;
                    case 4:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.forestList);
                        break;
                    case 5:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.gardenList);
                        break;
                    case 6:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.gradientList);
                        break;
                    case 7:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.natureList);
                        break;
                    case 8:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.placesList);
                        break;
                    case 9:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.roadList);
                        break;
                    case 10:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.spiralList);
                        break;
                    case 11:
                        RemoveBgActivity.this.bgAdapter.addData(RemoveBgActivity.this.gateList);
                        break;
                }
            }
        });
        setAdjustment();
        saveEffect();
        this.vBrightness.setVisibility(View.VISIBLE);
        adjustSeekbarMaxAndProgress();
        if (this.sbAdjust.getVisibility() != View.VISIBLE) {
            this.sbAdjust.setVisibility(View.VISIBLE);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewStyle);
        this.recyclerViewStyle = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext, 0, false));
        BgAdapter bgAdapter = new BgAdapter(this.mContext);
        this.bgAdapter = bgAdapter;
        bgAdapter.setMenuItemClickLister(this);
        this.recyclerViewStyle.setAdapter(this.bgAdapter);
        this.bgAdapter.addData(this.beachList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBMPNew() {
        Bitmap bitmap = faceBitmap;
        if (bitmap != null) {
            this.selectedBit = ImageUtils.getBitmapResize(this.mContext, bitmap, this.ivBackground.getWidth(), this.ivBackground.getHeight());
            setStart();
        }
    }

    private void Init() {
        this.ivBackground = (ImageView) findViewById(R.id.imageViewBackground);
        this.frameLayoutContent = (RelativeLayout) findViewById(R.id.mContentRootView);
        this.ivBackground.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.10
            @Override // java.lang.Runnable
            public void run() {
                RemoveBgActivity.this.initBMPNew();
            }
        });
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 1024) {
            Bitmap bitmap = eraserResultBmp;
            if (bitmap != null) {
                this.foreground = bitmap;
                this.ivBackground.setImageBitmap(bitmap);
                return;
            }
            return;
        }
        if (i2 == -1 && this.selectedFeatures == MODULE.BACKGROUND) {
            try {
                this.ivMain.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(intent.getData())));
                this.ivMain.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, getResources().getString(R.string.Something_went_wrong), 1).show();
            }
        }
    }

    @Override // com.gallery.photos.editphotovideo.listener.LayoutItemListener
    public void onLayoutListClick(View view, int i) {
        this.ivMain.setImageBitmap(ImageUtils.getBitmapFromAsset(this, "background/" + this.bgAdapter.getItemList().get(i) + ".webp"));
    }

    /* JADX WARN: Type inference failed for: r9v0, types: [com.gallery.photos.editphotovideo.activities.RemoveBgActivity$11] */
    public void setStart() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(5000L, 1000L) { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.11
            @Override // android.os.CountDownTimer
            public void onFinish() {
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                RemoveBgActivity.this.count++;
                if (progressBar.getProgress() <= 90) {
                    progressBar.setProgress(RemoveBgActivity.this.count * 5);
                }
            }
        }.start();
       /* new MLCropAsyncTask(new MLOnCropTaskCompleted() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.12
            @Override // com.gallery.photos.editphotovideo.crop.MLOnCropTaskCompleted
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
                RemoveBgActivity.this.selectedBit.getWidth();
                RemoveBgActivity.this.selectedBit.getHeight();
                int width = RemoveBgActivity.this.selectedBit.getWidth();
                int height = RemoveBgActivity.this.selectedBit.getHeight();
                int i3 = width * height;
                RemoveBgActivity.this.selectedBit.getPixels(new int[i3], 0, width, 0, 0, width, height);
                int[] iArr = new int[i3];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
                RemoveBgActivity removeBgActivity = RemoveBgActivity.this;
                removeBgActivity.foreground = ImageUtils.getMask(removeBgActivity, removeBgActivity.selectedBit, createBitmap, width, height);
                RemoveBgActivity.this.foreground = Bitmap.createScaledBitmap(bitmap, RemoveBgActivity.this.foreground.getWidth(), RemoveBgActivity.this.foreground.getHeight(), false);
                RemoveBgActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Palette.from(RemoveBgActivity.this.foreground).generate().getDominantSwatch() == null) {
                            Toast.makeText(RemoveBgActivity.this, RemoveBgActivity.this.getString(R.string.txt_not_detect_human), 0).show();
                        }
                        BitmapTransfer.bitmap = RemoveBgActivity.this.foreground;
                        RemoveBgActivity.this.ivBackground.setImageBitmap(BitmapTransfer.bitmap);
                    }
                });
            }
        }, this, progressBar).execute(new Void[0]);*/
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeBG() {
        int i = curBgType;
        if (i == 1) {
            curBgType = 2;
            this.ivBGCover.setImageBitmap(null);
            this.ivBGCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg1, this.width, this.height));
            this.imageViewBg.setImageResource(R.drawable.tbg1);
            return;
        }
        if (i == 2) {
            curBgType = 3;
            this.ivBGCover.setImageBitmap(null);
            this.ivBGCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg, this.width, this.height));
            this.imageViewBg.setImageResource(R.drawable.tbg);
            return;
        }
        if (i == 3) {
            curBgType = 4;
            this.ivBGCover.setImageBitmap(null);
            this.ivBGCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg3, this.width, this.height));
            this.imageViewBg.setImageResource(R.drawable.tbg3);
            return;
        }
        if (i == 4) {
            curBgType = 5;
            this.ivBGCover.setImageBitmap(null);
            this.ivBGCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg4, this.width, this.height));
            this.imageViewBg.setImageResource(R.drawable.tbg4);
            return;
        }
        if (i == 5) {
            curBgType = 6;
            this.ivBGCover.setImageBitmap(null);
            this.ivBGCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg5, this.width, this.height));
            this.imageViewBg.setImageResource(R.drawable.tbg5);
            return;
        }
        if (i == 6) {
            curBgType = 1;
            this.ivBGCover.setImageBitmap(null);
            this.ivBGCover.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.tbg2, this.width, this.height));
            this.imageViewBg.setImageResource(R.drawable.tbg2);
        }
    }

    private void setOnBackPressDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        getWindow().setLayout(-1, -1);
        attributes.gravity = Gravity.BOTTOM;
        window.setAttributes(attributes);
        dialog.setContentView(R.layout.dialog_exit);
        TextView textView = (TextView) dialog.findViewById(R.id.textViewCancel);
        TextView textView2 = (TextView) dialog.findViewById(R.id.textViewDiscard);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                dialog.dismiss();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RemoveBgActivity.this.m271x1b9facc4(dialog, view);
            }
        });
        dialog.show();
    }

    /* renamed from: lambda$setOnBackPressDialog$2$com-artRoom-photo-editor-activities-RemoveBgActivity, reason: not valid java name */
      void m271x1b9facc4(Dialog dialog, View view) {
        dialog.dismiss();
        finish();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        setOnBackPressDialog();
    }

    class SaveBitmapWithoutBg extends AsyncTask<Void, String, String> {
        @Override // android.os.AsyncTask
        public void onPreExecute() {
        }

        SaveBitmapWithoutBg() {
        }

        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            try {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) RemoveBgActivity.this.ivBackground.getDrawable();
                RemoveBgActivity.this.bitmap = bitmapDrawable.getBitmap();
                RemoveBgActivity removeBgActivity = RemoveBgActivity.this;
                return SaveFileUtils.saveBitmapFileRemove(removeBgActivity, removeBgActivity.bitmap, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()), null).getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            if (str == null) {
                Toast.makeText(RemoveBgActivity.this.getApplicationContext(), R.string.Something_went_wrong, 1).show();
                return;
            }

            finish();
            /*Intent intent = new Intent(RemoveBgActivity.this, (Class<?>) ShareActivity.class);
            intent.putExtra("path", str);
            RemoveBgActivity.this.startActivity(intent);*/
        }
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
            RemoveBgActivity.this.frameLayoutContent.setDrawingCacheEnabled(true);
            try {
                Bitmap bitmapFromView = getBitmapFromView(RemoveBgActivity.this.frameLayoutContent);
                RemoveBgActivity.this.frameLayoutContent.setDrawingCacheEnabled(false);
                return bitmapFromView;
            } catch (Exception unused) {
                RemoveBgActivity.this.frameLayoutContent.setDrawingCacheEnabled(false);
                return null;
            } catch (Throwable th) {
                RemoveBgActivity.this.frameLayoutContent.setDrawingCacheEnabled(false);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                BitmapTransfer.setBitmap(bitmap);
            }
            Intent intent = new Intent(RemoveBgActivity.this, (Class<?>) PhotoEditorActivity.class);
            intent.putExtra("MESSAGE", "done");
            RemoveBgActivity.this.setResult(-1, intent);
            RemoveBgActivity.this.finish();
        }
    }

    public void setAdjustment() {
        this.llAdjust = (LinearLayout) findViewById(R.id.linearLayoutAdjust);
        this.ivBrightness = (ImageView) findViewById(R.id.img_brightness);
        this.ivContrast = (ImageView) findViewById(R.id.img_contrast);
        this.ivSaturation = (ImageView) findViewById(R.id.img_saturation);
        this.ivExposure = (ImageView) findViewById(R.id.img_exposure);
        this.ivHue = (ImageView) findViewById(R.id.img_hue);
        this.tvBrightness = (TextView) findViewById(R.id.textViewBrightness);
        this.tvContrast = (TextView) findViewById(R.id.textViewContrast);
        this.tvSaturation = (TextView) findViewById(R.id.textViewSaturation);
        this.tvExposure = (TextView) findViewById(R.id.textViewExposure);
        this.tvHue = (TextView) findViewById(R.id.textViewHue);
        this.sbAdjust = (SeekBar) findViewById(R.id.seekbar_adjust);
        this.llBrightness = (LinearLayout) findViewById(R.id.ll_img_brightness);
        this.llContrast = (LinearLayout) findViewById(R.id.ll_img_contrast);
        this.llSaturation = (LinearLayout) findViewById(R.id.ll_img_saturation);
        this.llExposure = (LinearLayout) findViewById(R.id.ll_img_exposure);
        this.llHue = (LinearLayout) findViewById(R.id.ll_img_hue);
        this.vBrightness = findViewById(R.id.indicator_brightness);
        this.vContrast = findViewById(R.id.indicator_contrast);
        this.vSaturation = findViewById(R.id.indicator_saturation);
        this.vExposure = findViewById(R.id.indicator_exposure);
        this.vHue = findViewById(R.id.indicator_hue);
        this.sbAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.13
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (RemoveBgActivity.this.vBrightness.getVisibility() == View.VISIBLE) {
                    RemoveBgActivity.this.ivBackground.setColorFilter(ColorFilterGenerator.adjustBrightness(i != 100 ? i - 100 : 0));
                    return;
                }
                if (RemoveBgActivity.this.vContrast.getVisibility() == 0) {
                    RemoveBgActivity.this.ivBackground.setColorFilter(ColorFilterGenerator.adjustContrast(i != 100 ? i - 100 : 0));
                    return;
                }
                if (RemoveBgActivity.this.vSaturation.getVisibility() == 0) {
                    RemoveBgActivity.this.ivBackground.setColorFilter(ColorFilterGenerator.adjustSaturation(i != 100 ? i - 100 : 0));
                } else if (RemoveBgActivity.this.vExposure.getVisibility() == 0) {
                    RemoveBgActivity.this.ivBackground.setColorFilter(ColorFilterGenerator.adjustExposure(i != 100 ? i - 100 : 0));
                } else if (RemoveBgActivity.this.vHue.getVisibility() == 0) {
                    RemoveBgActivity.this.ivBackground.setColorFilter(ColorFilterGenerator.adjustHue(i != 100 ? i - 100 : 0));
                }
            }
        });
        this.llBrightness.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.saveEffect();
                RemoveBgActivity.this.resetMainIndicatorView();
                RemoveBgActivity.this.vBrightness.setVisibility(View.VISIBLE);
                RemoveBgActivity.this.adjustSeekbarMaxAndProgress();
                if (RemoveBgActivity.this.sbAdjust.getVisibility() != 0) {
                    RemoveBgActivity.this.sbAdjust.setVisibility(View.VISIBLE);
                }
            }
        });
        this.llContrast.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.15
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.saveEffect();
                RemoveBgActivity.this.resetMainIndicatorView();
                RemoveBgActivity.this.vContrast.setVisibility(View.VISIBLE);
                RemoveBgActivity.this.adjustSeekbarMaxAndProgress();
                if (RemoveBgActivity.this.sbAdjust.getVisibility() != 0) {
                    RemoveBgActivity.this.sbAdjust.setVisibility(View.VISIBLE);
                }
            }
        });
        this.llSaturation.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.saveEffect();
                RemoveBgActivity.this.resetMainIndicatorView();
                RemoveBgActivity.this.vSaturation.setVisibility(View.VISIBLE);
                RemoveBgActivity.this.adjustSeekbarMaxAndProgress();
                if (RemoveBgActivity.this.sbAdjust.getVisibility() != 0) {
                    RemoveBgActivity.this.sbAdjust.setVisibility(View.VISIBLE);
                }
            }
        });
        this.llExposure.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.17
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.saveEffect();
                RemoveBgActivity.this.resetMainIndicatorView();
                RemoveBgActivity.this.vExposure.setVisibility(View.VISIBLE);
                RemoveBgActivity.this.adjustSeekbarMaxAndProgress();
                if (RemoveBgActivity.this.sbAdjust.getVisibility() != 0) {
                    RemoveBgActivity.this.sbAdjust.setVisibility(View.VISIBLE);
                }
            }
        });
        this.llHue.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.18
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemoveBgActivity.this.saveEffect();
                RemoveBgActivity.this.resetMainIndicatorView();
                RemoveBgActivity.this.vHue.setVisibility(View.VISIBLE);
                RemoveBgActivity.this.adjustSeekbarMaxAndProgress();
                if (RemoveBgActivity.this.sbAdjust.getVisibility() != 0) {
                    RemoveBgActivity.this.sbAdjust.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void saveEffect() {
        if (this.vBrightness.getVisibility() == 0) {
            Bitmap cacheBitMap = getCacheBitMap(this.ivBackground);
            this.ivBackground.setColorFilter((ColorFilter) null);
            this.ivBackground.setImageBitmap(cacheBitMap);
            return;
        }
        if (this.vContrast.getVisibility() == 0) {
            Bitmap cacheBitMap2 = getCacheBitMap(this.ivBackground);
            this.ivBackground.setColorFilter((ColorFilter) null);
            this.ivBackground.setImageBitmap(cacheBitMap2);
            return;
        }
        if (this.vSaturation.getVisibility() == 0) {
            Bitmap cacheBitMap3 = getCacheBitMap(this.ivBackground);
            this.ivBackground.setColorFilter((ColorFilter) null);
            this.ivBackground.setImageBitmap(cacheBitMap3);
            return;
        }
        if (this.vExposure.getVisibility() == 0) {
            Bitmap cacheBitMap4 = getCacheBitMap(this.ivBackground);
            this.ivBackground.setColorFilter((ColorFilter) null);
            this.ivBackground.setImageBitmap(cacheBitMap4);
            return;
        }
        if (this.vHue.getVisibility() == 0) {
            Bitmap cacheBitMap5 = getCacheBitMap(this.ivBackground);
            this.ivBackground.setColorFilter((ColorFilter) null);
            this.ivBackground.setImageBitmap(cacheBitMap5);
        }
    }

    public void resetMainIndicatorView() {
        this.vBrightness.setVisibility(View.INVISIBLE);
        this.vContrast.setVisibility(View.INVISIBLE);
        this.vExposure.setVisibility(View.INVISIBLE);
        this.vHue.setVisibility(View.INVISIBLE);
        this.vSaturation.setVisibility(View.INVISIBLE);
    }

    public void adjustSeekbarMaxAndProgress() {
        if (this.vBrightness.getVisibility() == 0) {
            this.sbAdjust.setMax(200);
            this.sbAdjust.setProgress(100);
            this.ivBrightness.setColorFilter(getResources().getColor(R.color.mainColor));
            this.ivContrast.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivSaturation.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivExposure.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivHue.setColorFilter(getResources().getColor(R.color.iconColor));
            this.tvBrightness.setTextColor(getResources().getColor(R.color.mainColor));
            this.tvContrast.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvSaturation.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvExposure.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvHue.setTextColor(getResources().getColor(R.color.iconColor));
            return;
        }
        if (this.vContrast.getVisibility() == 0) {
            this.sbAdjust.setMax(200);
            this.sbAdjust.setProgress(100);
            this.ivBrightness.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivContrast.setColorFilter(getResources().getColor(R.color.mainColor));
            this.ivSaturation.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivExposure.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivHue.setColorFilter(getResources().getColor(R.color.iconColor));
            this.tvBrightness.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvContrast.setTextColor(getResources().getColor(R.color.mainColor));
            this.tvSaturation.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvExposure.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvHue.setTextColor(getResources().getColor(R.color.iconColor));
            return;
        }
        if (this.vSaturation.getVisibility() == 0) {
            this.sbAdjust.setMax(200);
            this.sbAdjust.setProgress(100);
            this.ivBrightness.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivContrast.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivSaturation.setColorFilter(getResources().getColor(R.color.mainColor));
            this.ivExposure.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivHue.setColorFilter(getResources().getColor(R.color.iconColor));
            this.tvBrightness.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvContrast.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvSaturation.setTextColor(getResources().getColor(R.color.mainColor));
            this.tvExposure.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvHue.setTextColor(getResources().getColor(R.color.iconColor));
            return;
        }
        if (this.vExposure.getVisibility() == 0) {
            this.sbAdjust.setMax(200);
            this.sbAdjust.setProgress(100);
            this.ivBrightness.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivContrast.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivSaturation.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivExposure.setColorFilter(getResources().getColor(R.color.mainColor));
            this.ivHue.setColorFilter(getResources().getColor(R.color.iconColor));
            this.tvBrightness.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvContrast.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvSaturation.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvExposure.setTextColor(getResources().getColor(R.color.mainColor));
            this.tvHue.setTextColor(getResources().getColor(R.color.iconColor));
            return;
        }
        if (this.vHue.getVisibility() == 0) {
            this.sbAdjust.setMax(200);
            this.sbAdjust.setProgress(100);
            this.ivBrightness.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivContrast.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivSaturation.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivExposure.setColorFilter(getResources().getColor(R.color.iconColor));
            this.ivHue.setColorFilter(getResources().getColor(R.color.mainColor));
            this.tvBrightness.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvContrast.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvSaturation.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvExposure.setTextColor(getResources().getColor(R.color.iconColor));
            this.tvHue.setTextColor(getResources().getColor(R.color.mainColor));
        }
    }

    public static Bitmap getCacheBitMap(ImageView imageView) {
        try {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap createBitmap = Bitmap.createBitmap(imageView.getDrawingCache());
            imageView.destroyDrawingCache();
            imageView.setDrawingCacheEnabled(false);
            return createBitmap;
        } catch (Exception unused) {
            return null;
        }
    }

    public void processingBitmap_Blur(final Bitmap bitmap, final int i) {
        final ProgressDialog show = ProgressDialog.show(this, "", getResources().getString(R.string.Processing_image), true);
        show.setCancelable(false);
        final Bitmap[] bitmapArr = new Bitmap[1];
        new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.19
            @Override // java.lang.Runnable
            public void run() {
                bitmapArr[0] = RemoveBgActivity.this.processBlurEffect(bitmap, i);
                show.dismiss();
            }
        }).start();
        show.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.gallery.photos.editphotovideo.activities.RemoveBgActivity.20
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                ImageView imageView = RemoveBgActivity.this.ivBackground;
                Bitmap bitmap2 = bitmapArr[0];
                RemoveBgActivity.faceBitmap2 = bitmap2;
                imageView.setImageBitmap(bitmap2);
            }
        });
    }

    public Bitmap processBlurEffect(Bitmap bitmap, int i) {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(this.foreground, bitmap.getWidth(), bitmap.getHeight(), false);
            Bitmap[] blurAlpha = getBlurAlpha(bitmap, i);
            Canvas canvas = new Canvas();
            canvas.setBitmap(createBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawBitmap(blurAlpha[1], 0.0f, 0.0f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(createScaledBitmap, 0.0f, 0.0f, paint);
            Bitmap createBitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Canvas canvas2 = new Canvas();
            canvas2.setBitmap(createBitmap2);
            Paint paint2 = new Paint();
            paint2.setAntiAlias(true);
            canvas2.drawBitmap(blurAlpha[0], 0.0f, 0.0f, paint2);
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas2.drawBitmap(createBitmap, 0.0f, 0.0f, paint2);
            return createBitmap2;
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap[] getBlurAlpha(Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Bitmap extractAlpha = bitmap.extractAlpha();
        Canvas canvas = new Canvas();
        canvas.setBitmap(createBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setMaskFilter(new BlurMaskFilter(i, BlurMaskFilter.Blur.NORMAL));
        canvas.drawBitmap(extractAlpha, 0.0f, 0.0f, paint);
        return new Bitmap[]{getResizedAlpha(createBitmap, i), getResizedAlphaInc(createBitmap, i)};
    }

    public Bitmap getResizedAlpha(Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int i2 = i * 2;
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() - i2, bitmap.getHeight() - i2, true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(createBitmap);
        float f = i;
        canvas.drawBitmap(createScaledBitmap, f, f, (Paint) null);
        return createBitmap;
    }

    public Bitmap getResizedAlphaInc(Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int i2 = i * 2;
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() + i2, bitmap.getHeight() + i2, true);
        Canvas canvas = new Canvas();
        canvas.setBitmap(createBitmap);
        float f = -i;
        canvas.drawBitmap(createScaledBitmap, f, f, (Paint) null);
        return createBitmap;
    }


}
