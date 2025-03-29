package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.exifinterface.media.ExifInterface;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.mesh.Enhance;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.mesh.Hips;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.mesh.Refine;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.mesh.Waist;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.effects.resize.Height;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.bodyAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.support.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.tools.bodyModel;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.CapturePhotoUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ResizeImages;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ScaleImage;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.StartPointSeekBar;
import com.gallery.photos.editpic.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.wysaid.nativePort.CGEDeformFilterWrapper;
import org.wysaid.nativePort.CGEImageHandler;
import org.wysaid.view.ImageGLSurfaceView;

/* loaded from: classes.dex */
public class BodyActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, CapturePhotoUtils.PhotoLoadResponse {
    static final int SAVED_PHOTO = 326;
    private static Bitmap mOriginalBitmap;
    private Bundle bundle;
    public ConstraintLayout clConfig;
    public Context context;
    public ImageGLSurfaceView glSurfaceView;
    public ImageView ivBack;
    public ImageView ivCompare;
    public ImageView ivRedo;
    public ImageView ivSave;
    public ImageView ivUndo;
    public bodyAdapter mAdapter;
    private ImageView mCancelButton;
    Canvas mCanvas;
    public Bitmap mCurrentBitmap;
    BackPressed mCurrentInterface;
    public CGEDeformFilterWrapper mDeformWrapper;
    private ImageView mDoneButton;
    public int mIdCurrent;
    public int mIdLast;
    public int mIdRequisite;
    String mImagePath;
    public FrameLayout mLoading;
    private LinearLayout mMenuEnhance;
    public RecyclerView mMenuHome;
    List<bodyModel> mMenuInfo;
    private ConstraintLayout mParent;
    ScaleImage mScaleImage;
    private StartPointSeekBar mSeekbar;
    public float startX;
    public float startY;
    String uri;
    Handler handler = new Handler();
    public boolean isBlocked = true;
    private bodyAdapter.OnItemClickListener mMenuHomeClickListener = new bodyAdapter.OnItemClickListener() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.1
        @Override // com.gallery.photos.editphotovideo.adapters.bodyAdapter.OnItemClickListener
        public void onItemClick(int i) {
            BodyActivity.this.isBlocked = true;
            BodyActivity.this.sendEvent("Tool - open");
            if (i == 0) {
                BodyActivity.this.type = 3;
                BodyActivity.this.mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap = BodyActivity.this.mCurrentBitmap;
                BodyActivity bodyActivity = BodyActivity.this;
                bodyActivity.mCurrentInterface = new Refine(bitmap, bodyActivity, bodyActivity.mScaleImage);
                return;
            }
            if (i == 1) {
                BodyActivity.this.type = 4;
                BodyActivity.this.mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap2 = BodyActivity.this.mCurrentBitmap;
                BodyActivity bodyActivity2 = BodyActivity.this;
                bodyActivity2.mCurrentInterface = new Enhance(bitmap2, bodyActivity2, bodyActivity2.mScaleImage);
                return;
            }
            if (i == 2) {
                BodyActivity.this.type = 5;
                BodyActivity.this.mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap3 = BodyActivity.this.mCurrentBitmap;
                BodyActivity bodyActivity3 = BodyActivity.this;
                bodyActivity3.mCurrentInterface = new Height(bitmap3, bodyActivity3, bodyActivity3.mScaleImage);
                return;
            }
            if (i == 3) {
                BodyActivity.this.type = 6;
                BodyActivity.this.mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap4 = BodyActivity.this.mCurrentBitmap;
                BodyActivity bodyActivity4 = BodyActivity.this;
                bodyActivity4.mCurrentInterface = new Waist(bitmap4, bodyActivity4, bodyActivity4.mScaleImage);
                return;
            }
            if (i == 4) {
                BodyActivity.this.type = 7;
                BodyActivity.this.mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap5 = BodyActivity.this.mCurrentBitmap;
                BodyActivity bodyActivity5 = BodyActivity.this;
                bodyActivity5.mCurrentInterface = new Hips(bitmap5, bodyActivity5, bodyActivity5.mScaleImage);
                return;
            }
            if (i == 6) {
                BodyActivity.this.type = 1;
            } else if (i == 7) {
                BodyActivity.this.type = 2;
            }
        }
    };
    BroadcastReceiver startSaveReceiver = new BroadcastReceiver() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(final Context context, Intent intent) {
            new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.2.1
                @Override // java.lang.Runnable
                public void run() {
                    BodyActivity.this.uri = CapturePhotoUtils.insertImage(BodyActivity.this.getContentResolver(), BodyActivity.this.mCurrentBitmap, "BodyTune_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + ((int) (Math.random() * 100.0d)), "");
                    Intent intent2 = new Intent("photoWasSaved");
                    intent2.putExtra("uri", BodyActivity.this.uri);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
                }
            }).start();
        }
    };
    private int type = 1;

    public interface BackPressed {
        void onBackPressed(boolean z);
    }

    private void navigateToGallery() {
    }

    public static void setFaceBitmap(Bitmap bitmap) {
        mOriginalBitmap = bitmap;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_body);
        this.bundle = new Bundle();
        ArrayList arrayList = new ArrayList();
        this.mMenuInfo = arrayList;
        arrayList.add(new bodyModel(R.drawable.main_menu_icon_refine, getString(R.string.refine)));
        this.mMenuInfo.add(new bodyModel(R.drawable.enhance_big, getString(R.string.enhance)));
        this.mMenuInfo.add(new bodyModel(R.drawable.main_menu_icon_height, getString(R.string.height)));
        this.mMenuInfo.add(new bodyModel(R.drawable.main_menu_icon_waist, getString(R.string.waist)));
        this.mMenuInfo.add(new bodyModel(R.drawable.main_menu_icon_hips, getString(R.string.hips)));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.startSaveReceiver, new IntentFilter("startSaveBitmap"));
        if (bundle == null) {
            for (String str : getFilesDir().list()) {
                if (str.endsWith(".jpg") || str.endsWith(".png")) {
                    deleteFile(str);
                }
            }
            this.context = this;
            ResizeImages resizeImages = new ResizeImages();
            this.mImagePath = getIntent().getStringExtra("path");
            try {
                if (mOriginalBitmap != null) {
                    this.mImagePath = resizeImages.saveBitmap(this.context, Constants.TEMP_FOLDER_NAME, mOriginalBitmap);
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "" + e.getMessage(), 0).show();
                e.printStackTrace();
            }
            new Thread(new AnonymousClass3()).start();
            return;
        }
        this.mIdLast = bundle.getInt("mIdLast");
        int i = bundle.getInt("mIdCurrent");
        this.mIdCurrent = i;
        this.mIdRequisite = i;
        new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.4
            @Override // java.lang.Runnable
            public final void run() {
                BodyActivity.this.lambda$onCreate$0$MainActivity();
            }
        }).start();
    }

    /* renamed from: com.gallery.photos.editphotovideo.activities.BodyActivity$3, reason: invalid class name */
    class AnonymousClass3 implements Runnable {
        AnonymousClass3() {
        }

        @Override // java.lang.Runnable
        public void run() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            try {
                int attributeInt = new ExifInterface(new File(BodyActivity.this.mImagePath).getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                int i = attributeInt == 3 ? 180 : attributeInt == 6 ? 90 : attributeInt == 8 ? 270 : 0;
                Bitmap decodeFile = BitmapFactory.decodeFile(BodyActivity.this.mImagePath, options);
                if (decodeFile == null) {
                    BodyActivity.this.finish();
                    return;
                }
                int width = decodeFile.getWidth();
                int height = decodeFile.getHeight();
                if (Math.max(width, height) <= 1500.0f) {
                    Bitmap unused = BodyActivity.mOriginalBitmap = decodeFile;
                } else {
                    float max = 1500.0f / Math.max(width, height);
                    width = (int) (width * max);
                    height = (int) (height * max);
                    Bitmap unused2 = BodyActivity.mOriginalBitmap = Bitmap.createScaledBitmap(decodeFile, width, height, true);
                    decodeFile.recycle();
                }
                int i2 = width;
                int i3 = height;
                if (i != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(i);
                    Bitmap createBitmap = Bitmap.createBitmap(BodyActivity.mOriginalBitmap, 0, 0, i2, i3, matrix, true);
                    BodyActivity.mOriginalBitmap.recycle();
                    Bitmap unused3 = BodyActivity.mOriginalBitmap = createBitmap;
                }
                if (BodyActivity.mOriginalBitmap != null) {
                    if (!BodyActivity.mOriginalBitmap.isMutable()) {
                        Bitmap copy = BodyActivity.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        BodyActivity.mOriginalBitmap.recycle();
                        Bitmap unused4 = BodyActivity.mOriginalBitmap = copy;
                    }
                    File file = new File(BodyActivity.this.mImagePath);
                    if (file.getParentFile().equals(BodyActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)) && file.delete()) {
                        BodyActivity.this.sendEvent("Camera - Tap");
                    }
                    BodyActivity.this.mCurrentBitmap = BodyActivity.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                FileOutputStream openFileOutput = BodyActivity.this.openFileOutput("original.png", 0);
                                BodyActivity bodyActivity = BodyActivity.this;
                                BodyActivity.mOriginalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, openFileOutput);
                                openFileOutput.close();
                            } catch (Exception e) {
                                Log.d("My", "Error (save Original): " + e.getMessage());
                            }
                        }
                    }).start();
                    BodyActivity.this.handler.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.3.2
                        @Override // java.lang.Runnable
                        public final void run() {
                            AnonymousClass3.this.lambda$run$0$MainActivity$3();
                        }
                    });
                    return;
                }
                BodyActivity.this.finish();
            } catch (IOException | OutOfMemoryError unused5) {
                BodyActivity.this.finish();
            }
        }

        public void lambda$run$0$MainActivity$3() {
            BodyActivity.this.onCreated();
        }
    }

    public void lambda$onCreate$0$MainActivity() {
        int i = 0;
        while (true) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("tool_");
                i++;
                sb.append(i);
                sb.append(".jpg");
                if (!deleteFile(sb.toString())) {
                    deleteFile("tool_" + i + ".png");
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.handler.post(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.5
                    @Override // java.lang.Runnable
                    public void run() {
                        BodyActivity.this.finish();
                    }
                });
                return;
            }
        }
    }

    /* renamed from: com.gallery.photos.editphotovideo.activities.BodyActivity$6, reason: invalid class name */
    class AnonymousClass6 implements Runnable {
        final   Bitmap val$finalDecodeStream;

        AnonymousClass6(Bitmap bitmap) {
            this.val$finalDecodeStream = bitmap;
        }

        @Override // java.lang.Runnable
        public void run() {
            Bitmap unused = BodyActivity.mOriginalBitmap = this.val$finalDecodeStream;
            try {
                BodyActivity.this.mCurrentBitmap = BitmapFactory.decodeStream(new FileInputStream(new File(BodyActivity.this.getFilesDir(), "main_" + BodyActivity.this.mIdCurrent + ".png")));
                if (BodyActivity.this.mCurrentBitmap == null) {
                    BodyActivity.this.mCurrentBitmap = BodyActivity.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    BodyActivity.this.mIdCurrent = 0;
                    BodyActivity.this.mIdRequisite = 0;
                } else if (!BodyActivity.this.mCurrentBitmap.isMutable()) {
                    Bitmap copy = BodyActivity.this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    BodyActivity.this.mCurrentBitmap.recycle();
                    BodyActivity.this.mCurrentBitmap = copy;
                }
            } catch (FileNotFoundException e) {
                BodyActivity.this.mCurrentBitmap = BodyActivity.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                BodyActivity.this.mIdCurrent = 0;
                BodyActivity.this.mIdRequisite = 0;
                e.printStackTrace();
            }
            BodyActivity.this.onCreated();
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("mIdCurrent", this.mIdCurrent);
        bundle.putInt("mIdLast", this.mIdLast);
    }

    public void onCreated() {
        this.mScaleImage = (ScaleImage) findViewById(R.id.mScaleImage);
        this.mLoading = (FrameLayout) findViewById(R.id.loading);
        this.clConfig = (ConstraintLayout) findViewById(R.id.constraint_layout_confirm_blur);
        this.ivSave = (ImageView) findViewById(R.id.imageViewSave);
        this.ivBack = (ImageView) findViewById(R.id.imageViewClose);
        this.ivCompare = (ImageView) findViewById(R.id.imageViewCompare);
        this.mMenuHome = (RecyclerView) findViewById(R.id.menuHome);
        this.ivUndo = (ImageView) findViewById(R.id.imageViewUndo);
        this.ivRedo = (ImageView) findViewById(R.id.imageViewRedo);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.isBlocked = false;
        this.mLoading.setVisibility(View.GONE);
        this.mMenuHome.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        bodyAdapter bodyadapter = new bodyAdapter(this.mMenuInfo, this);
        this.mAdapter = bodyadapter;
        bodyadapter.setOnItemClickListener(this.mMenuHomeClickListener);
        this.mMenuHome.setAdapter(this.mAdapter);
        this.ivSave.setOnClickListener(this);
        this.ivBack.setOnClickListener(this);
        this.ivUndo.setOnClickListener(this);
        this.ivRedo.setOnClickListener(this);
        sendEvent("Page - Edit zone");
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        findViewById(R.id.page).onCancelPendingInputEvents();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
    }

    public void saveEffect(Bitmap bitmap) {
        this.mCurrentBitmap = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(this.mCurrentBitmap);
        this.mCanvas = canvas;
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        addMainState();
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
    }

    public void addMainState() {
        sendEvent("Tool - V");
        int i = this.mIdCurrent + 1;
        this.mIdCurrent = i;
        if (i <= this.mIdLast) {
            while (i <= this.mIdLast) {
                deleteFile("main_" + i + ".png");
                i++;
            }
        }
        int i2 = this.mIdCurrent;
        this.mIdLast = i2;
        this.mIdRequisite = i2;
        final Bitmap copy = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
        BackPressed backPressed = this.mCurrentInterface;
        if (backPressed != null) {
            backPressed.onBackPressed(true);
        }
        final String str = "main_" + this.mIdCurrent + ".png";
        new Thread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.BodyActivity.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    FileOutputStream openFileOutput = BodyActivity.this.openFileOutput(str, 0);
                    copy.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
                    openFileOutput.close();
                    copy.recycle();
                } catch (Exception e) {
                    Log.d("My", "Error (save Bitmap): " + e.getMessage());
                }
            }
        }).start();
    }

    public void sendEvent(String str) {
        this.bundle.putString(str, str);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == SAVED_PHOTO) {
            finish();
        }
    }

    private void close(boolean z) {
        this.mScaleImage.setVisibility(View.VISIBLE);
        this.mIdCurrent = -1;
        if (z) {
            sendEvent("Enhance - V");
        } else {
            sendEvent("Tool - X");
            sendEvent("Enhance - X");
        }
        this.mMenuEnhance.setVisibility(View.VISIBLE);
        this.ivSave.setOnClickListener(this);
        this.ivBack.setOnClickListener(this);
        this.ivCompare.setOnTouchListener(this);
        this.mMenuEnhance.setVisibility(View.GONE);
        findViewById(R.id.constraintLayout).setVisibility(View.GONE);
        this.clConfig.setVisibility(View.VISIBLE);
        findViewById(R.id.menuHome).setVisibility(View.VISIBLE);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.isBlocked) {
            return;
        }
        switch (view.getId()) {
            case R.id.imageViewClose /* 2131362263 */:
                onBackPressed();
                break;
            case R.id.imageViewRedo /* 2131362301 */:
                int i = this.mIdRequisite;
                if (i < this.mIdLast) {
                    int i2 = i + 1;
                    this.mIdRequisite = i2;
                    CapturePhotoUtils.getBitmapFromDisk(i, i2, "main_" + this.mIdRequisite + ".png", this, this);
                    sendEvent("Tool - Forward");
                    break;
                }
                break;
            case R.id.imageViewSave /* 2131362304 */:
                Bitmap bitmap = this.mCurrentBitmap;
                if (bitmap != null) {
                    BitmapTransfer.setBitmap(bitmap);
                }
                Intent intent = new Intent(this, (Class<?>) PhotoEditorActivity.class);
                intent.putExtra("MESSAGE", "done");
                setResult(-1, intent);
                finish();
                break;
            case R.id.imageViewUndo /* 2131362320 */:
                int i3 = this.mIdRequisite;
                if (i3 <= 1) {
                    if (i3 == 1) {
                        this.mIdRequisite = 0;
                        this.mIdCurrent = 0;
                        this.mCurrentBitmap.recycle();
                        Bitmap copy = mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        this.mCurrentBitmap = copy;
                        this.mScaleImage.setImageBitmap(copy);
                        sendEvent("Tool - Back");
                        this.mScaleImage.resetToFitCenter();
                        break;
                    }
                } else {
                    int i4 = i3 - 1;
                    this.mIdRequisite = i4;
                    CapturePhotoUtils.getBitmapFromDisk(i3, i4, "main_" + this.mIdRequisite + ".png", this, this);
                    sendEvent("Tool - Back");
                    break;
                }
                break;
            case R.id.image_view_close /* 2131362342 */:
                close(false);
                break;
            case R.id.image_view_save /* 2131362357 */:
                Toast.makeText(this, "save bitmap", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i <= this.mIdLast; i++) {
            deleteFile("main_" + i + ".png");
        }
        deleteFile("original.png");
        Bitmap bitmap = mOriginalBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        Bitmap bitmap2 = this.mCurrentBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.startSaveReceiver);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.isBlocked) {
            return;
        }
        if (this.clConfig.getVisibility() == 0) {
            super.onBackPressed();
            return;
        }
        BackPressed backPressed = this.mCurrentInterface;
        if (backPressed != null) {
            backPressed.onBackPressed(false);
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mScaleImage.setImageBitmap(mOriginalBitmap);
        } else if (action == 1 || action == 3) {
            this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        }
        return true;
    }

    @Override // com.gallery.photos.editphotovideo.utils.CapturePhotoUtils.PhotoLoadResponse
    public void loadResponse(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            this.mIdRequisite = i;
            return;
        }
        if ((i2 <= i || this.mIdCurrent >= i2) && (i2 >= i || i2 >= this.mIdCurrent)) {
            return;
        }
        this.mCurrentBitmap.recycle();
        if (!bitmap.isMutable()) {
            this.mCurrentBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmap.recycle();
        } else {
            this.mCurrentBitmap = bitmap;
        }
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mIdCurrent = i2;
        this.mIdRequisite = i2;
        this.mScaleImage.resetToFitCenter();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        navigateToGallery();
        return true;
    }

    private void setupNew() {
        this.mCancelButton = (ImageView) findViewById(R.id.image_view_close);
        this.mDoneButton = (ImageView) findViewById(R.id.image_view_save);
        this.mParent = (ConstraintLayout) findViewById(R.id.page);
        this.mMenuEnhance = (LinearLayout) findViewById(R.id.seekbarWithTwoIcon);
        this.mSeekbar = (StartPointSeekBar) findViewById(R.id.SWTI_seekbar);
        this.ivUndo.setOnClickListener(this);
        this.ivRedo.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        ((ImageView) findViewById(R.id.imageViewIcon1)).setImageResource(R.drawable.enhance_small);
        ((ImageView) findViewById(R.id.imageViewIcon2)).setImageResource(R.drawable.enhance_big);
        this.isBlocked = false;
        this.clConfig.setVisibility(View.VISIBLE);
        this.mSeekbar.setAbsoluteMinMaxValue(-50.0d, 50.0d);
        this.mSeekbar.setProgress(0.0d);
        findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
        this.clConfig.setVisibility(View.GONE);
        findViewById(R.id.menuHome).setVisibility(View.GONE);
        this.mMenuEnhance.setVisibility(View.VISIBLE);
        this.mSeekbar.setAbsoluteMinMaxValue(-50.0d, 50.0d);
        this.mSeekbar.setProgress(0.0d);
        this.mSeekbar.setAbsoluteMinMaxValue(-50.0d, 50.0d);
        this.mSeekbar.setProgress(0.0d);
    }

    public void checkNullSix() {
        float width = mOriginalBitmap.getWidth();
        float height = mOriginalBitmap.getHeight();
        float min = Math.min(this.glSurfaceView.getRenderViewport().width / width, this.glSurfaceView.getRenderViewport().height / height);
        if (min < 1.0f) {
            width *= min;
            height *= min;
        }
        CGEDeformFilterWrapper create = CGEDeformFilterWrapper.create((int) width, (int) height, 10.0f);
        this.mDeformWrapper = create;
        create.setUndoSteps(200);
        if (this.mDeformWrapper != null) {
            CGEImageHandler imageHandler = this.glSurfaceView.getImageHandler();
            imageHandler.setFilterWithAddres(this.mDeformWrapper.getNativeAddress());
            imageHandler.processFilters();
        }
    }
}
