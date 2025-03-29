package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.NeonAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.WingsAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.LayoutItemListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.MultiTouchListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.WingsItemListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.support.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ImageUtils;
import com.gallery.photos.editpic.R;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class NeonActivity extends BaseActivity implements LayoutItemListener, WingsItemListener {
    private static Bitmap faceBitmap;
    public static ImageView imageViewFont;
    public static Bitmap resultBmp;

    private Context context;
    private Bitmap foreground;
    private ImageView imageViewBack;
    private ImageView imageViewBackground;
    private ImageView imageViewCover;
    private ImageView imageViewFrame;
    private ImageView imageViewShape;
    private ImageView imageViewSpiral;
    private ImageView imageViewWing;
    private NeonAdapter neonAdapter;
    private RecyclerView recyclerViewNeon;
    private RecyclerView recyclerViewWing;
    private RelativeLayout relativeLayoutRootView;
    private SeekBar seekBarOpacity;
    private Bitmap selectedBitmap;
    private TextView textViewFrame;
    private TextView textViewShape;
    private TextView textViewSpiral;
    private TextView textViewWing;
    private WingsAdapter wingsAdapter;
    private int neonCount = 10;
    private int frameCount = 10;
    private int shapeCount = 10;
    private int wingCount = 8;
    boolean isFirst = true;
    private ArrayList<String> neonEffectList = new ArrayList<>();
    private ArrayList<String> shapeEffectList = new ArrayList<>();
    private ArrayList<String> frameEffectList = new ArrayList<>();
    private ArrayList<String> wingsEffectList = new ArrayList<>();
    public int count = 0;


    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_neon);
        this.context = this;
        this.selectedBitmap = faceBitmap;


        new Handler().postDelayed(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.1
            @Override // java.lang.Runnable
            public void run() {
                NeonActivity.this.imageViewCover.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (!NeonActivity.this.isFirst || NeonActivity.this.selectedBitmap == null) {
                            return;
                        }
                        NeonActivity.this.isFirst = false;
                        NeonActivity.this.initBitmap();
                    }
                });
            }
        }, 1000L);
        this.neonEffectList.add("none");
        for (int i = 1; i <= this.neonCount; i++) {
            this.neonEffectList.add("b_" + i);
        }
        this.shapeEffectList.add("none");
        for (int i2 = 1; i2 <= this.shapeCount; i2++) {
            this.shapeEffectList.add("shape_" + i2);
        }
        this.frameEffectList.add("none");
        for (int i3 = 1; i3 <= this.frameCount; i3++) {
            this.frameEffectList.add("f_" + i3);
        }
        this.wingsEffectList.add("none");
        for (int i4 = 1; i4 <= this.wingCount; i4++) {
            this.wingsEffectList.add("wing_" + i4);
        }
        initViews();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBitmap() {
        ImageView imageView;
        Bitmap bitmap = faceBitmap;
        if (bitmap != null) {
            this.selectedBitmap = ImageUtils.getBitmapResize(this.context, bitmap, this.imageViewCover.getWidth(), this.imageViewCover.getHeight());
            this.relativeLayoutRootView.setLayoutParams(new LinearLayout.LayoutParams(this.selectedBitmap.getWidth(), this.selectedBitmap.getHeight()));
            Bitmap bitmap2 = this.selectedBitmap;
            if (bitmap2 != null && (imageView = this.imageViewBackground) != null) {
                imageView.setImageBitmap(bitmap2);
            }
            setStart();
        }
    }

    public void initViews() {
        this.relativeLayoutRootView = (RelativeLayout) findViewById(R.id.mContentRootView);
        imageViewFont = (ImageView) findViewById(R.id.imageViewFont);
        this.imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        this.imageViewBackground = (ImageView) findViewById(R.id.imageViewBackground);
        this.imageViewCover = (ImageView) findViewById(R.id.imageViewCover);
        this.textViewSpiral = (TextView) findViewById(R.id.textViewNeon);
        this.textViewShape = (TextView) findViewById(R.id.textViewShape);
        this.textViewFrame = (TextView) findViewById(R.id.textViewFrame);
        this.textViewWing = (TextView) findViewById(R.id.textViewWing);
        this.imageViewSpiral = (ImageView) findViewById(R.id.imageViewNeon);
        this.imageViewShape = (ImageView) findViewById(R.id.imageViewShape);
        this.imageViewFrame = (ImageView) findViewById(R.id.imageViewFrame);
        this.imageViewWing = (ImageView) findViewById(R.id.imageViewWing);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLine);
        this.recyclerViewNeon = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false));
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewWings);
        this.recyclerViewWing = recyclerView2;
        recyclerView2.setLayoutManager(new LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false));
        seAdapterList();
        seAdapterWingList();
        this.seekBarOpacity = (SeekBar) findViewById(R.id.seekbarOpacity);
        this.imageViewBackground.setRotationY(0.0f);
        this.neonAdapter.addData(this.neonEffectList);
        this.imageViewCover.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.2
            @Override // java.lang.Runnable
            public void run() {
                NeonActivity.this.initBitmap();
            }
        });
        this.seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (NeonActivity.this.imageViewBack == null || NeonActivity.imageViewFont == null) {
                    return;
                }
                float f = i * 0.01f;
                NeonActivity.this.imageViewBack.setAlpha(f);
                NeonActivity.imageViewFont.setAlpha(f);
            }
        });
        findViewById(R.id.imageViewCloseNeon).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NeonActivity.this.onBackPressed();
            }
        });
        findViewById(R.id.imageViewSaveNeon).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {

                new saveFile().execute(new String[0]);

            }
        });
        findViewById(R.id.linearLayoutEraser).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EraserBgActivity.b = NeonActivity.this.foreground;
                Intent intent = new Intent(NeonActivity.this, (Class<?>) EraserBgActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_NEON);
                NeonActivity.this.startActivityForResult(intent, 1024);
            }
        });
        findViewById(R.id.linearLayoutNeon).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NeonActivity.this.neonAdapter.addData(NeonActivity.this.neonEffectList);
                NeonActivity.this.imageViewSpiral.setColorFilter(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.imageViewShape.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewFrame.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewWing.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewSpiral.setTextColor(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.textViewShape.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewFrame.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewWing.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.recyclerViewNeon.setVisibility(View.VISIBLE);
                NeonActivity.this.recyclerViewWing.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.linearLayoutShape).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NeonActivity.this.neonAdapter.addData(NeonActivity.this.shapeEffectList);
                NeonActivity.this.imageViewSpiral.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewShape.setColorFilter(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.imageViewFrame.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewWing.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewSpiral.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewShape.setTextColor(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.textViewFrame.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewWing.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.recyclerViewNeon.setVisibility(View.VISIBLE);
                NeonActivity.this.recyclerViewWing.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.linearLayoutFrame).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NeonActivity.this.neonAdapter.addData(NeonActivity.this.frameEffectList);
                NeonActivity.this.imageViewSpiral.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewShape.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewFrame.setColorFilter(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.imageViewWing.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewSpiral.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewShape.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewFrame.setTextColor(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.textViewWing.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.recyclerViewNeon.setVisibility(View.VISIBLE);
                NeonActivity.this.recyclerViewWing.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.linearLayoutWing).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NeonActivity.this.wingsAdapter.addData(NeonActivity.this.wingsEffectList);
                NeonActivity.this.imageViewSpiral.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewShape.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewFrame.setColorFilter(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.imageViewWing.setColorFilter(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.textViewSpiral.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewShape.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewFrame.setTextColor(NeonActivity.this.getResources().getColor(R.color.iconColor));
                NeonActivity.this.textViewWing.setTextColor(NeonActivity.this.getResources().getColor(R.color.mainColor));
                NeonActivity.this.recyclerViewNeon.setVisibility(View.GONE);
                NeonActivity.this.recyclerViewWing.setVisibility(View.VISIBLE);
            }
        });
    }

    public void seAdapterList() {
        NeonAdapter neonAdapter = new NeonAdapter(this.context);
        this.neonAdapter = neonAdapter;
        neonAdapter.setLayoutItenListener(this);
        this.recyclerViewNeon.setAdapter(this.neonAdapter);
        this.neonAdapter.addData(this.neonEffectList);
    }

    public void seAdapterWingList() {
        WingsAdapter wingsAdapter = new WingsAdapter(this.context);
        this.wingsAdapter = wingsAdapter;
        wingsAdapter.setMenuItemClickLister(this);
        this.recyclerViewWing.setAdapter(this.wingsAdapter);
        this.wingsAdapter.addData(this.wingsEffectList);
    }

    @Override // com.gallery.photos.editphotovideo.listener.LayoutItemListener
    public void onLayoutListClick(View view, int i) {
        if (i != 0) {
            Bitmap bitmapFromAsset = ImageUtils.getBitmapFromAsset(this.context, "spiral/back/" + this.neonAdapter.getItemList().get(i) + "_back.png");
            Bitmap bitmapFromAsset2 = ImageUtils.getBitmapFromAsset(this.context, "spiral/front/" + this.neonAdapter.getItemList().get(i) + "_front.png");
            this.imageViewBack.setImageBitmap(bitmapFromAsset);
            imageViewFont.setImageBitmap(bitmapFromAsset2);
        } else {
            this.imageViewBack.setImageResource(0);
            imageViewFont.setImageResource(0);
        }
        this.imageViewBack.setOnTouchListener(new MultiTouchListener(this, true));
    }

    @Override // com.gallery.photos.editphotovideo.listener.WingsItemListener
    public void onWingListClick(View view, int i) {
        if (i != 0) {
            Context context = this.context;
            StringBuilder sb = new StringBuilder("wing/");
            sb.append(this.wingsAdapter.getItemList().get(i));
            this.wingsAdapter.getItemList().get(i).startsWith("b");
            sb.append(".webp");
            this.imageViewBack.setImageBitmap(ImageUtils.getBitmapFromAsset(context, sb.toString()));
            imageViewFont.setImageResource(0);
        } else {
            this.imageViewBack.setImageResource(0);
            imageViewFont.setImageResource(0);
        }
        this.imageViewBack.setOnTouchListener(new MultiTouchListener(this, true));
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        Bitmap bitmap;
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 1024 && (bitmap = resultBmp) != null) {
            this.foreground = bitmap;
            this.imageViewCover.setImageBitmap(bitmap);
        }
    }

    /* JADX WARN: Type inference failed for: r9v0, types: [com.gallery.photos.editphotovideo.activities.NeonActivity$11] */
    public void setStart() {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(5000L, 1000L) { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.11
            @Override // android.os.CountDownTimer
            public void onFinish() {
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                NeonActivity.this.count++;
                if (progressBar.getProgress() <= 90) {
                    progressBar.setProgress(NeonActivity.this.count * 5);
                }
            }
        }.start();
       /* new MLCropAsyncTask(new MLOnCropTaskCompleted() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.12
            @Override // com.gallery.photos.editphotovideo.crop.MLOnCropTaskCompleted
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
                NeonActivity.this.selectedBitmap.getWidth();
                NeonActivity.this.selectedBitmap.getHeight();
                int width = NeonActivity.this.selectedBitmap.getWidth();
                int height = NeonActivity.this.selectedBitmap.getHeight();
                int i3 = width * height;
                NeonActivity.this.selectedBitmap.getPixels(new int[i3], 0, width, 0, 0, width, height);
                int[] iArr = new int[i3];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
                NeonActivity neonActivity = NeonActivity.this;
                neonActivity.foreground = ImageUtils.getMask(neonActivity, neonActivity.selectedBitmap, createBitmap, width, height);
                NeonActivity.this.foreground = Bitmap.createScaledBitmap(bitmap, NeonActivity.this.foreground.getWidth(), NeonActivity.this.foreground.getHeight(), false);
                NeonActivity.this.runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.NeonActivity.12.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (Palette.from(NeonActivity.this.foreground).generate().getDominantSwatch() == null) {
                            Toast.makeText(NeonActivity.this, NeonActivity.this.getString(R.string.txt_not_detect_human), 0).show();
                        }
                        BitmapTransfer.bitmap = NeonActivity.this.foreground;
                        NeonActivity.this.imageViewCover.setImageBitmap(BitmapTransfer.bitmap);
                    }
                });
            }
        }, this, progressBar).execute(new Void[0]);*/
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
            NeonActivity.this.relativeLayoutRootView.setDrawingCacheEnabled(true);
            Bitmap bitmapFromView = getBitmapFromView(NeonActivity.this.relativeLayoutRootView);
            NeonActivity.this.relativeLayoutRootView.setDrawingCacheEnabled(false);
            return bitmapFromView;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                BitmapTransfer.setBitmap(bitmap);
            }
            Intent intent = new Intent(NeonActivity.this, (Class<?>) PhotoEditorActivity.class);
            intent.putExtra("MESSAGE", "done");
            NeonActivity.this.setResult(-1, intent);
            NeonActivity.this.startActivity(intent);
            NeonActivity.this.finish();
        }
    }


}
