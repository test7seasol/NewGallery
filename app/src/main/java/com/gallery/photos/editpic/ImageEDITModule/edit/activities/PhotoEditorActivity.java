package com.gallery.photos.editpic.ImageEDITModule.edit.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.OnPhotoEditorListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.OnSaveBitmap;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.PTextView;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.PhotoEditor;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.PhotoEditorView;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.Text;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.ViewType;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.AdjustAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.FilterAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.OverlayAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.RecyclerTabLayout;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.StickerAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.StickerTabAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.ToolsAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.ToolsEffectAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.constants.StoreManager;
import com.gallery.photos.editpic.ImageEDITModule.edit.event.DeleteIconEvent;
import com.gallery.photos.editpic.ImageEDITModule.edit.event.ZoomIconEvent;
import com.gallery.photos.editpic.ImageEDITModule.edit.fragment.CropperFragment;
import com.gallery.photos.editpic.ImageEDITModule.edit.fragment.HSlFragment;
import com.gallery.photos.editpic.ImageEDITModule.edit.fragment.RatioFragment;
import com.gallery.photos.editpic.ImageEDITModule.edit.fragment.SplashFragment;
import com.gallery.photos.editpic.ImageEDITModule.edit.fragment.SquareFragment;
import com.gallery.photos.editpic.ImageEDITModule.edit.fragment.TextFragment;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.AdjustListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.FilterListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.OverlayListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.picker.PermissionsUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.picker.PhotoPicker;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.FilterFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.OverlayFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.StickerFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.BitmapStickerIcon;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.DrawableSticker;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.Sticker;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.StickerView;
import com.gallery.photos.editpic.ImageEDITModule.edit.support.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.tools.ToolEditor;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.BitmapTransfer;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.ColorSlider;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.FilterUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.PreferenceUtil;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SaveFileUtils;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.myadsworld.MyAddPrefs;
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass;
import com.google.android.material.tabs.TabLayout;
import com.hold1.keyboardheightprovider.KeyboardHeightProvider;

import org.wysaid.myUtils.MsgUtil;
import org.wysaid.nativePort.CGENativeLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

/* loaded from: classes.dex */
public class PhotoEditorActivity extends BaseActivity implements OnPhotoEditorListener, View.OnClickListener, SplashFragment.SplashSaturationBackgrundListener, HSlFragment.OnFilterSavePhoto, StickerAdapter.OnClickStickerListener, CropperFragment.OnCropPhoto, RatioFragment.RatioSaveListener, SquareFragment.SplashDialogListener, ToolsAdapter.OnItemSelected, ToolsEffectAdapter.OnItemEffectSelected, FilterListener, OverlayListener, AdjustListener {
    private static final String TAG = "PhotoEditorActivity";

    private TextView addNewText;
    private ConstraintLayout constraint_layout_adjust;
    private ConstraintLayout constraint_layout_brush;
    public ConstraintLayout constraint_layout_confirmP;
    public ConstraintLayout constraint_layout_filter;
    public ConstraintLayout constraint_layout_overlay;
    private ConstraintLayout constraint_layout_root_view;
    private ConstraintLayout constraint_layout_sticker;
    private ConstraintLayout constraint_save_control;
    private Guideline guideline;
    private Guideline guidelinePaint;
    public ImageView imageViewAddSticker;
    ImageView imageViewEraser;
    ImageView imageViewNeon;
    ImageView imageViewPaint;
    private ImageView image_view_compare_adjust;
    public ImageView image_view_compare_filter;
    public ImageView image_view_compare_overlay;
    private KeyboardHeightProvider keyboardHeightProvider;
    public LinearLayout linLayoutColor;
    private LinearLayout linearLayoutEraser;
    private LinearLayout linearLayoutSize;
    public LinearLayout linear_layout_wrapper_sticker_list;
    public AdjustAdapter mAdjustAdapter;
    public PhotoEditor photoEditor;
    public PhotoEditorView photo_editor_view;
    private RecyclerView recycler_view_adjust;
    public RecyclerView recycler_view_filter;
    public RecyclerView recycler_view_overlay;
    public RecyclerView recycler_view_tools;
    public RecyclerView recycler_view_tools_effect;
    private RelativeLayout relativeLayoutSaveSticker;
    private RelativeLayout relativeLayoutSaveText;
    private RelativeLayout relativeLayoutText;
    private RelativeLayout relative_layout_loading;
    public RelativeLayout relative_layout_wrapper_photo;
    public SeekBar seekbarStickerAlpha;
    private SeekBar seekbar_adjust;
    private SeekBar seekbar_brush_size;
    private SeekBar seekbar_erase_size;
    public SeekBar seekbar_filter;
    public SeekBar seekbar_overlay;
    private Animation slideDownAnimation;
    private Animation slideUpAnimation;
    TabLayout tabLayout;
    public TextFragment.TextEditor textEditor;
    public TextFragment textEditorDialogFragment;
    private TextView textViewEraerValue;
    TextView textViewEraser;
    TextView textViewNeon;
    TextView textViewPaint;
    private TextView textViewSizeValue;
    public ToolEditor currentMode = ToolEditor.NONE;
    public ArrayList lstBitmapWithFilter = new ArrayList();
    public List<Bitmap> lstBitmapWithOverlay = new ArrayList();
    public List<Bitmap> lstBitmapWithGradient = new ArrayList();
    public List<Bitmap> lstBitmapWithLight = new ArrayList();
    public List<Bitmap> lstBitmapWithDust = new ArrayList();
    public List<Bitmap> lstBitmapWithMask = new ArrayList();
    private final ToolsAdapter mEditingToolsAdapter = new ToolsAdapter(this);
    private final ToolsEffectAdapter toolsEffectAdapter = new ToolsEffectAdapter(this);
    public CGENativeLibrary.LoadImageCallback mLoadImageCallback = new CGENativeLibrary.LoadImageCallback() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.1
        @Override // org.wysaid.nativePort.CGENativeLibrary.LoadImageCallback
        public Bitmap loadImage(String str, Object obj) {
            try {
                return BitmapFactory.decodeStream(PhotoEditorActivity.this.getAssets().open(str));
            } catch (IOException unused) {
                return null;
            }
        }

        @Override // org.wysaid.nativePort.CGENativeLibrary.LoadImageCallback
        public void loadImageOK(Bitmap bitmap, Object obj) {
            bitmap.recycle();
        }
    };
    View.OnTouchListener onCompareTouchListener = new View.OnTouchListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda8
        @Override // android.view.View.OnTouchListener
        public final boolean onTouch(View view, MotionEvent motionEvent) {
            return PhotoEditorActivity.this.m266xf4e98f23(view, motionEvent);
        }
    };
    FEATURES selectedFeatures = FEATURES.COLOR;
    private ColorSlider.OnColorSelectedListener mListener = new ColorSlider.OnColorSelectedListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.19
        @Override // com.gallery.photos.editphotovideo.utils.ColorSlider.OnColorSelectedListener
        public void onColorChanged(int i, int i2) {
            PhotoEditorActivity.this.updateView(i2);
        }
    };

    enum FEATURES {
        COLOR,
        NEON,
        BODY,
        DRIP,
        WING,
        //        BG_CHANGE,
        ART,
        DOUBLE,
        BORDER,
        LIGHT,
        DUST,
        MASK,
        GRADIENT,
        EFFECT,
        OVERLAY
    }

    /* renamed from: lambda$new$0$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    boolean m266xf4e98f23(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.photo_editor_view.getGLSurfaceView().setAlpha(0.0f);
            return true;
        }
        if (action != 1) {
            return true;
        }
        this.photo_editor_view.getGLSurfaceView().setAlpha(1.0f);
        return false;
    }

    private void applyStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(android.R.color.black, getTheme())); // Set black status bar
        window.getDecorView().setSystemUiVisibility(0); // Ensures white text/icons
        window.setNavigationBarColor(getResources().getColor(android.R.color.black, getTheme())); // Set black navigation bar
    }


    @SuppressLint("MissingInflatedId")
    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        makeFullScreen();
        setContentView(R.layout.activity_photo_editor);

        applyStatusBarColor();

        MyAllAdCommonClass.showAdmobBanner(
                PhotoEditorActivity.this,
                findViewById(R.id.banner_container),
                findViewById(R.id.shimmer_container_banner),
                false,
                new MyAddPrefs(PhotoEditorActivity.this).getAdmBannerId()
        );

        MyAllAdCommonClass.load_Admob_Interstial(this);

        initViews();
        this.slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        this.slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        CGENativeLibrary.setLoadImageCallback(this.mLoadImageCallback, null);
        if (Build.VERSION.SDK_INT < 26) {
            getWindow().setSoftInputMode(48);
        }
        this.recycler_view_tools.setLayoutManager(new LinearLayoutManager(this, 0, false));
        this.recycler_view_tools.setAdapter(this.mEditingToolsAdapter);
//        this.recycler_view_tools.setHasFixedSize(true);
        this.recycler_view_tools_effect.setLayoutManager(new LinearLayoutManager(this, 0, false));
        this.recycler_view_tools_effect.setAdapter(this.toolsEffectAdapter);
//        this.recycler_view_tools_effect.setHasFixedSize(true);
        this.recycler_view_filter.setLayoutManager(new LinearLayoutManager(this, 0, false));
//        this.recycler_view_filter.setHasFixedSize(true);
        this.recycler_view_overlay.setLayoutManager(new LinearLayoutManager(this, 0, false));
//        this.recycler_view_overlay.setHasFixedSize(true);
        new LinearLayoutManager(this, 0, false);
        this.recycler_view_adjust.setLayoutManager(new LinearLayoutManager(this, 0, false));
//        this.recycler_view_adjust.setHasFixedSize(true);
        AdjustAdapter adjustAdapter = new AdjustAdapter(getApplicationContext(), this);
        this.mAdjustAdapter = adjustAdapter;
        this.recycler_view_adjust.setAdapter(adjustAdapter);
        PhotoEditor build2 = new PhotoEditor.Builder(this, this.photo_editor_view).setPinchTextScalable(true).build();
        this.photoEditor = build2;
        build2.setOnPhotoEditorListener(this);
        toogleDrawBottomToolbar(false);
        PreferenceUtil.setHeightOfKeyboard(getApplicationContext(), 0);
        KeyboardHeightProvider keyboardHeightProvider = new KeyboardHeightProvider(this);
        this.keyboardHeightProvider = keyboardHeightProvider;
        keyboardHeightProvider.addKeyboardListener(new KeyboardHeightProvider.KeyboardListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda10
            @Override // com.hold1.keyboardheightprovider.KeyboardHeightProvider.KeyboardListener
            public final void onHeightChanged(int i) {
                PhotoEditorActivity.this.m268xf99e559a(i);
            }
        });
        ColorSlider colorSlider = (ColorSlider) findViewById(R.id.color_slider);
        colorSlider.setSelectorColor(-1);
        colorSlider.setListener(this.mListener);
        updateView(colorSlider.getSelectedColor());
        this.photoEditor.setBrushDrawingMode(false);
        findViewById(R.id.imageViewColorPicker).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new AmbilWarnaDialog(PhotoEditorActivity.this, Color.parseColor("#0090FF"), true, new AmbilWarnaDialog.OnAmbilWarnaListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.2.1
                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    @Override // yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                        PhotoEditorActivity.this.updateView(i);
                    }
                }).show();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString(PhotoPicker.KEY_SELECTED_PHOTOS) != null) {
            new OnLoadBitmapFromUri().execute(extras.getString(PhotoPicker.KEY_SELECTED_PHOTOS));
        } else if (extras != null && extras.getString("MESSAGE").equals("done") && BitmapTransfer.bitmap != null) {
            new loadBitmap().execute(BitmapTransfer.bitmap);
        }

        findViewById(R.id.imageViewSavePaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoading(true);
                constraint_layout_confirmP.setVisibility(View.GONE);
                runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoEditorActivity.this.m267x53538b3();
                    }
                });
                slideDownSaveView();
                currentMode = ToolEditor.NONE;
            }
        });


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        this.tabLayout = tabLayout;
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.overlay)));
        TabLayout tabLayout2 = this.tabLayout;
        tabLayout2.addTab(tabLayout2.newTab().setText(getResources().getString(R.string.light)));
        TabLayout tabLayout3 = this.tabLayout;
        tabLayout3.addTab(tabLayout3.newTab().setText(getResources().getString(R.string.dust)));
        TabLayout tabLayout4 = this.tabLayout;
        tabLayout4.addTab(tabLayout4.newTab().setText(getResources().getString(R.string.gradient)));
        TabLayout tabLayout5 = this.tabLayout;
        tabLayout5.addTab(tabLayout5.newTab().setText(getResources().getString(R.string.mask)));
        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.3
            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    PhotoEditorActivity.this.selectedFeatures = FEATURES.EFFECT;
                    PhotoEditorActivity.this.new LoadOverlayBitmap().execute(new Void[0]);
                    return;
                }
                if (position == 1) {
                    PhotoEditorActivity.this.selectedFeatures = FEATURES.LIGHT;
                    PhotoEditorActivity.this.new LoadOverlayBitmap().execute(new Void[0]);
                    return;
                }
                if (position == 2) {
                    PhotoEditorActivity.this.selectedFeatures = FEATURES.DUST;
                    PhotoEditorActivity.this.new LoadOverlayBitmap().execute(new Void[0]);
                } else if (position == 3) {
                    PhotoEditorActivity.this.selectedFeatures = FEATURES.GRADIENT;
                    PhotoEditorActivity.this.new LoadOverlayBitmap().execute(new Void[0]);
                } else {
                    if (position != 4) {
                        return;
                    }
                    PhotoEditorActivity.this.selectedFeatures = FEATURES.MASK;
                    PhotoEditorActivity.this.new LoadOverlayBitmap().execute(new Void[0]);
                }
            }
        });
    }

    /* renamed from: lambda$onCreate$2$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    void m268xf99e559a(int i) {
        if (i <= 0) {
            PreferenceUtil.setHeightOfNotch(getApplicationContext(), -i);
            return;
        }
        TextFragment textFragment = this.textEditorDialogFragment;
        if (textFragment != null) {
            textFragment.updateAddTextBottomToolbarHeight(PreferenceUtil.getHeightOfNotch(getApplicationContext()) + i);
            PreferenceUtil.setHeightOfKeyboard(getApplicationContext(), i + PreferenceUtil.getHeightOfNotch(getApplicationContext()));
        }
    }

    private void toogleDrawBottomToolbar(boolean z) {
        this.imageViewEraser.setVisibility(!z ? 8 : 0);
    }

    public void iEraserBrush() {
        this.linLayoutColor.setVisibility(View.GONE);
        this.linearLayoutSize.setVisibility(View.GONE);
        this.linearLayoutEraser.setVisibility(View.VISIBLE);
        this.imageViewEraser.setColorFilter(ContextCompat.getColor(this, R.color.mainColor));
        this.imageViewPaint.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewNeon.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.textViewEraser.setTextColor(ContextCompat.getColor(this, R.color.mainColor));
        this.textViewPaint.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.textViewNeon.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.photoEditor.brushEraser();
        this.seekbar_erase_size.setProgress(20);
    }

    public void iNeonBrush() {
        this.selectedFeatures = FEATURES.NEON;
        this.linLayoutColor.setVisibility(View.VISIBLE);
        this.linearLayoutSize.setVisibility(View.VISIBLE);
        this.linearLayoutEraser.setVisibility(View.GONE);
        this.imageViewEraser.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewPaint.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewNeon.setColorFilter(ContextCompat.getColor(this, R.color.mainColor));
        this.textViewEraser.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.textViewPaint.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.textViewNeon.setTextColor(ContextCompat.getColor(this, R.color.mainColor));
        this.photoEditor.setBrushMode(2);
        this.photoEditor.setBrushDrawingMode(true);
        this.seekbar_brush_size.setProgress(20);
    }

    public void iColorBrush() {
        this.selectedFeatures = FEATURES.COLOR;
        this.linLayoutColor.setVisibility(View.VISIBLE);
        this.linearLayoutSize.setVisibility(View.VISIBLE);
        this.linearLayoutEraser.setVisibility(View.GONE);
        this.imageViewEraser.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewNeon.setColorFilter(ContextCompat.getColor(this, R.color.white));
        this.imageViewPaint.setColorFilter(ContextCompat.getColor(this, R.color.mainColor));
        this.textViewEraser.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.textViewPaint.setTextColor(ContextCompat.getColor(this, R.color.mainColor));
        this.textViewNeon.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.photoEditor.setBrushMode(1);
        this.photoEditor.setBrushDrawingMode(true);
        this.seekbar_brush_size.setProgress(20);
    }

    private void initViews() {
        this.relative_layout_loading = (RelativeLayout) findViewById(R.id.relative_layout_loading);
        this.linLayoutColor = (LinearLayout) findViewById(R.id.linLayoutColor);
        this.relative_layout_loading.setVisibility(View.VISIBLE);
        this.linear_layout_wrapper_sticker_list = (LinearLayout) findViewById(R.id.linear_layout_wrapper_sticker_list);
        PhotoEditorView photoEditorView = (PhotoEditorView) findViewById(R.id.photo_editor_view);
        this.photo_editor_view = photoEditorView;
        photoEditorView.setVisibility(View.INVISIBLE);
        this.recycler_view_tools = (RecyclerView) findViewById(R.id.recycler_view_tools);
        this.recycler_view_tools_effect = (RecyclerView) findViewById(R.id.recycler_view_tools_effect);
        this.linearLayoutEraser = (LinearLayout) findViewById(R.id.linearLayoutEraser);
        this.linearLayoutSize = (LinearLayout) findViewById(R.id.linearLayoutSize);
        this.guidelinePaint = (Guideline) findViewById(R.id.guidelinePaint);
        this.textViewSizeValue = (TextView) findViewById(R.id.textViewSizeValue);
        this.textViewEraerValue = (TextView) findViewById(R.id.textViewEraserValue);
        this.guideline = (Guideline) findViewById(R.id.guideline);
        this.recycler_view_filter = (RecyclerView) findViewById(R.id.recycler_view_filter);
        this.recycler_view_overlay = (RecyclerView) findViewById(R.id.recycler_view_overlay);
        this.recycler_view_adjust = (RecyclerView) findViewById(R.id.recycler_view_adjust);
        this.constraint_layout_root_view = (ConstraintLayout) findViewById(R.id.constraint_layout_root_view);
        this.constraint_layout_filter = (ConstraintLayout) findViewById(R.id.constraint_layout_filter);
        this.constraint_layout_confirmP = (ConstraintLayout) findViewById(R.id.constraint_layout_confirmP);
        this.constraint_layout_overlay = (ConstraintLayout) findViewById(R.id.constraint_layout_overlay);
        this.constraint_layout_adjust = (ConstraintLayout) findViewById(R.id.constraint_layout_adjust);
        this.constraint_layout_sticker = (ConstraintLayout) findViewById(R.id.constraint_layout_sticker);
        this.relativeLayoutSaveSticker = (RelativeLayout) findViewById(R.id.relativeLayoutSaveSticker);
        this.relativeLayoutSaveText = (RelativeLayout) findViewById(R.id.relativeLayoutSaveText);
        ViewPager viewPager = (ViewPager) findViewById(R.id.sticker_viewpaper);
        this.seekbar_filter = (SeekBar) findViewById(R.id.seekbar_filter);
        this.textViewPaint = (TextView) findViewById(R.id.textViewPaint);
        this.textViewNeon = (TextView) findViewById(R.id.textViewNeon);
        this.textViewEraser = (TextView) findViewById(R.id.textViewEraser);
        this.seekbar_overlay = (SeekBar) findViewById(R.id.seekbar_overlay);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbarStickerAlpha);
        this.seekbarStickerAlpha = seekBar;
        seekBar.setVisibility(View.GONE);
        this.constraint_layout_brush = (ConstraintLayout) findViewById(R.id.constraintLayoutPaint);
        this.relative_layout_wrapper_photo = (RelativeLayout) findViewById(R.id.relative_layout_wrapper_photo);
        this.imageViewPaint = (ImageView) findViewById(R.id.imageViewPaint);
        this.imageViewNeon = (ImageView) findViewById(R.id.imageViewNeon);
        this.imageViewEraser = (ImageView) findViewById(R.id.imageViewEraser);
        this.seekbar_brush_size = (SeekBar) findViewById(R.id.seekbarBrushSize);
        this.seekbar_erase_size = (SeekBar) findViewById(R.id.seekbarEraserSize);
        TextView textView = (TextView) findViewById(R.id.imageViewSaveFinal);
        this.constraint_save_control = (ConstraintLayout) findViewById(R.id.constraint_save_control);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoEditorActivity.this.m262x7974d804(view);
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.image_view_compare_adjust);
        this.image_view_compare_adjust = imageView;
        imageView.setOnTouchListener(this.onCompareTouchListener);
        this.image_view_compare_adjust.setVisibility(View.GONE);
        ImageView imageView2 = (ImageView) findViewById(R.id.image_view_compare_filter);
        this.image_view_compare_filter = imageView2;
        imageView2.setOnTouchListener(this.onCompareTouchListener);
        this.image_view_compare_filter.setVisibility(View.GONE);
        ImageView imageView3 = (ImageView) findViewById(R.id.image_view_compare_overlay);
        this.image_view_compare_overlay = imageView3;
        imageView3.setOnTouchListener(this.onCompareTouchListener);
        this.image_view_compare_overlay.setVisibility(View.GONE);
        findViewById(R.id.image_view_exit).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoEditorActivity.this.m263x9f08e105(view);
            }
        });
        findViewById(R.id.linearLayoutPaint).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotoEditorActivity.this.iColorBrush();
            }
        });
        findViewById(R.id.linearLayoutNeon).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotoEditorActivity.this.iNeonBrush();
            }
        });
        findViewById(R.id.linearLayoutEraserBtn).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhotoEditorActivity.this.iEraserBrush();
            }
        });
        this.seekbar_erase_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.8
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                PhotoEditorActivity.this.photoEditor.setBrushEraserSize(i);
                PhotoEditorActivity.this.textViewEraerValue.setText(String.valueOf(i));
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
                PhotoEditorActivity.this.photoEditor.brushEraser();
            }
        });
        this.seekbar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.9
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                PhotoEditorActivity.this.photoEditor.setBrushSize(i + 10);
                PhotoEditorActivity.this.textViewSizeValue.setText(String.valueOf(i));
            }
        });
        this.seekbarStickerAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.10
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                Sticker currentSticker = PhotoEditorActivity.this.photo_editor_view.getCurrentSticker();
                if (currentSticker != null) {
                    currentSticker.setAlpha(i);
                }
            }
        });
        ImageView imageView4 = (ImageView) findViewById(R.id.imageViewAddSticker);
        this.imageViewAddSticker = imageView4;
        imageView4.setVisibility(View.GONE);
        this.imageViewAddSticker.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoEditorActivity.this.m264xc49cea06(view);
            }
        });
        this.addNewText = (TextView) findViewById(R.id.addNewText);
        this.relativeLayoutText = (RelativeLayout) findViewById(R.id.relativeLayoutText);
        this.addNewText.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoEditorActivity.this.m265xea30f307(view);
            }
        });
        SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekbar_adjust);
        this.seekbar_adjust = seekBar2;
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.11
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar3) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar3) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar3, int i, boolean z) {
                PhotoEditorActivity.this.mAdjustAdapter.getCurrentAdjustModel().setSeekBarIntensity(PhotoEditorActivity.this.photoEditor, i / seekBar3.getMax(), true);
            }
        });


        BitmapStickerIcon bitmapStickerIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_close), 0, BitmapStickerIcon.REMOVE);
        bitmapStickerIcon.setIconEvent(new DeleteIconEvent());
//        BitmapStickerIcon bitmapStickerIcon2 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_scale), 3, BitmapStickerIcon.ZOOM);
//        bitmapStickerIcon2.setIconEvent(new ZoomIconEvent());
//        BitmapStickerIcon bitmapStickerIcon3 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_flip), 1, BitmapStickerIcon.FLIP);
//        bitmapStickerIcon3.setIconEvent(new FlipHorizontallyEvent());
//        BitmapStickerIcon bitmapStickerIcon4 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_rotate), 3, BitmapStickerIcon.ROTATE);
//        bitmapStickerIcon4.setIconEvent(new ZoomIconEvent());
//        BitmapStickerIcon bitmapStickerIcon5 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_edit), 1, BitmapStickerIcon.EDIT);
//        bitmapStickerIcon5.setIconEvent(new EditTextIconEvent());
        BitmapStickerIcon bitmapStickerIcon6 = new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_outline_scale), 1, BitmapStickerIcon.ALIGN_HORIZONTALLY);
        bitmapStickerIcon6.setIconEvent(new ZoomIconEvent());


        this.photo_editor_view.setIcons(Arrays.asList(bitmapStickerIcon/*, bitmapStickerIcon2, bitmapStickerIcon3, bitmapStickerIcon5, bitmapStickerIcon4*/, bitmapStickerIcon6));
        this.photo_editor_view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.photo_editor_view.setLocked(false);
        this.photo_editor_view.setConstrained(true);
        this.photo_editor_view.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.12
            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerDragFinished(Sticker sticker) {
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerFlipped(Sticker sticker) {
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerTouchedDown(Sticker sticker) {
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerZoomFinished(Sticker sticker) {
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onTouchDownForBeauty(float f, float f2) {
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onTouchDragForBeauty(float f, float f2) {
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onTouchUpForBeauty(float f, float f2) {
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerAdded(Sticker sticker) {
                PhotoEditorActivity.this.seekbarStickerAlpha.setVisibility(View.VISIBLE);
                PhotoEditorActivity.this.seekbarStickerAlpha.setProgress(sticker.getAlpha());
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerClicked(Sticker sticker) {
                if (sticker instanceof PTextView) {
                    ((PTextView) sticker).setTextColor(-65536);
                    PhotoEditorActivity.this.photo_editor_view.replace(sticker);
                    PhotoEditorActivity.this.photo_editor_view.invalidate();
                }
                PhotoEditorActivity.this.seekbarStickerAlpha.setVisibility(View.VISIBLE);
                PhotoEditorActivity.this.seekbarStickerAlpha.setProgress(sticker.getAlpha());
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerDeleted(Sticker sticker) {
                PhotoEditorActivity.this.seekbarStickerAlpha.setVisibility(View.GONE);
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerTouchOutside() {
                PhotoEditorActivity.this.seekbarStickerAlpha.setVisibility(View.GONE);
            }

            @Override
            // com.gallery.photos.editphotovideo.sticker.StickerView.OnStickerOperationListener
            public void onStickerDoubleTapped(Sticker sticker) {
                if (sticker instanceof PTextView) {
                    sticker.setShow(false);
                    PhotoEditorActivity.this.photo_editor_view.setHandlingSticker(null);
                    PhotoEditorActivity photoEditorActivity = PhotoEditorActivity.this;
                    photoEditorActivity.textEditorDialogFragment = TextFragment.show(photoEditorActivity, ((PTextView) sticker).getPolishText());
                    PhotoEditorActivity.this.textEditor = new TextFragment.TextEditor() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.12.1
                        @Override
                        // com.gallery.photos.editphotovideo.fragment.TextFragment.TextEditor
                        public void onDone(Text text) {
                            PhotoEditorActivity.this.photo_editor_view.getStickers().remove(PhotoEditorActivity.this.photo_editor_view.getLastHandlingSticker());
                            PhotoEditorActivity.this.photo_editor_view.addSticker(new PTextView(PhotoEditorActivity.this, text));
                        }

                        @Override
                        // com.gallery.photos.editphotovideo.fragment.TextFragment.TextEditor
                        public void onBackButton() {
                            PhotoEditorActivity.this.photo_editor_view.showLastHandlingSticker();
                        }
                    };
                    PhotoEditorActivity.this.textEditorDialogFragment.setOnTextEditorListener(PhotoEditorActivity.this.textEditor);
                }
            }
        });
        this.seekbar_filter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.13
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar3) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar3) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar3, int i, boolean z) {
                PhotoEditorActivity.this.photo_editor_view.setFilterIntensity(i / 100.0f);
            }
        });
        this.seekbar_overlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.14
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar3) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar3) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar3, int i, boolean z) {
                PhotoEditorActivity.this.photo_editor_view.setFilterIntensity(i / 100.0f);
            }
        });
        getWindowManager().getDefaultDisplay().getSize(new Point());
        viewPager.setAdapter(new PagerAdapter() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.15
            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                return 15;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public boolean isViewFromObject(View view, Object obj) {
                return view.equals(obj);
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                viewGroup.removeView((View) obj);
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public Object instantiateItem(ViewGroup viewGroup, int i) {
                View inflate = LayoutInflater.from(PhotoEditorActivity.this.getBaseContext()).inflate(R.layout.sticker_list, (ViewGroup) null, false);
                RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recycler_view_sticker);
//                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(PhotoEditorActivity.this.getApplicationContext(), 6));
                switch (i) {
                    case 0:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.amojiList(), i, PhotoEditorActivity.this));
                        break;
                    case 1:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.chickenList(), i, PhotoEditorActivity.this));
                        break;
                    case 2:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.childList(), i, PhotoEditorActivity.this));
                        break;
                    case 3:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.christmasList(), i, PhotoEditorActivity.this));
                        break;
                    case 4:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.cuteList(), i, PhotoEditorActivity.this));
                        break;
                    case 5:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.emojList(), i, PhotoEditorActivity.this));
                        break;
                    case 6:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.emojiList(), i, PhotoEditorActivity.this));
                        break;
                    case 7:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.fruitList(), i, PhotoEditorActivity.this));
                        break;
                    case 8:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.heartList(), i, PhotoEditorActivity.this));
                        break;
                    case 9:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.lovedayList(), i, PhotoEditorActivity.this));
                        break;
                    case 10:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.plantList(), i, PhotoEditorActivity.this));
                        break;
                    case 11:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.stickerList(), i, PhotoEditorActivity.this));
                        break;
                    case 12:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.sweetList(), i, PhotoEditorActivity.this));
                        break;
                    case 13:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.textcolorList(), i, PhotoEditorActivity.this));
                        break;
                    case 14:
                        recyclerView.setAdapter(new StickerAdapter(PhotoEditorActivity.this.getApplicationContext(), StickerFile.textneonList(), i, PhotoEditorActivity.this));
                        break;
                }
                viewGroup.addView(inflate);
                return inflate;
            }
        });
        RecyclerTabLayout recyclerTabLayout = (RecyclerTabLayout) findViewById(R.id.recycler_tab_layout);
        recyclerTabLayout.setUpWithAdapter(new StickerTabAdapter(viewPager, getApplicationContext()));
        recyclerTabLayout.setPositionThreshold(0.5f);
        recyclerTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.BackgroundColor));
    }

    private SharedPreferences sharedPreferences;
    private boolean isAdShownThisSession;

    void m262x7974d804(View view) {
        sharedPreferences = getSharedPreferences("SplashPrefs", MODE_PRIVATE);
        isAdShownThisSession = sharedPreferences.getBoolean("isAdShownThisSession", false);

        if (!isAdShownThisSession) {
            MyAllAdCommonClass.AdShowdialogFirstActivityQue(this, new MyAllAdCommonClass.MyListener() {
                @Override
                public void callback() {
                    sharedPreferences.edit().putBoolean("isAdShownThisSession", true).apply(); // Store that ad was shown

                    SaveView1();
                }
            });
        } else {
            SaveView1();
        }


    }

    /* renamed from: lambda$initViews$4$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    void m263x9f08e105(View view) {
        onBackPressed();
    }

    /* renamed from: lambda$initViews$5$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    void m264xc49cea06(View view) {
        this.imageViewAddSticker.setVisibility(View.GONE);
        slideUp(this.linear_layout_wrapper_sticker_list);
    }

    /* renamed from: lambda$initViews$6$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    void m265xea30f307(View view) {
        this.photo_editor_view.setHandlingSticker(null);
        openTextFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SaveView1() {
        if (PermissionsUtils.checkWriteStoragePermission(this)) {
            new SaveBitmap().execute(new Void[0]);
        }
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        this.slideUpAnimation = loadAnimation;
        view.startAnimation(loadAnimation);
        this.slideUpAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.16
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    public void slideDown(View view) {
        view.setVisibility(View.GONE);
        view.startAnimation(this.slideDownAnimation);
        this.slideDownAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.17
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    // com.gallery.photos.editphotovideo.activities.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override // com.gallery.photos.editphotovideo.Editor.OnPhotoEditorListener
    public void onAddViewListener(ViewType viewType, int i) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + i + "]");
    }

    @Override // com.gallery.photos.editphotovideo.Editor.OnPhotoEditorListener
    public void onRemoveViewListener(int i) {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + i + "]");
    }

    @Override // com.gallery.photos.editphotovideo.Editor.OnPhotoEditorListener
    public void onRemoveViewListener(ViewType viewType, int i) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + i + "]");
    }

    @Override // com.gallery.photos.editphotovideo.Editor.OnPhotoEditorListener
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override // com.gallery.photos.editphotovideo.Editor.OnPhotoEditorListener
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeSticker /* 2131362001 */:

                slideDown(this.constraint_layout_sticker);
                slideUp(this.recycler_view_tools);

            case R.id.imageViewClosePaint /* 2131362269 */:
//                slideDown(this.constraint_layout_brush);
//                slideUp(this.recycler_view_tools);

            case R.id.image_view_close_adjust /* 2131362343 */:
                Log.e("TAGee", "onClick:image_view_close_adjust ");
             /*   new SaveFilterAsBitmap().execute(new Void[0]);
                this.image_view_compare_adjust.setVisibility(View.GONE);
                slideDown(this.constraint_layout_adjust);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                setGuideLine();
                updateLayout();*/
//                this.currentMode = ToolEditor.NONE;

            case R.id.image_view_close_filter /* 2131362344 */:

                slideDown(this.constraint_layout_filter);
                slideUp(this.recycler_view_tools);

            case R.id.image_view_close_overlay /* 2131362345 */:
                slideDown(this.constraint_layout_overlay);
                slideUp(this.recycler_view_tools);

            case R.id.image_view_close_text /* 2131362346 */:
                slideDownSaveView();
                onBackPressed();
                break;
            case R.id.imageViewPaintRedo /* 2131362295 */:
                this.photoEditor.redoBrush();
                break;
            case R.id.imageViewPaintUndo /* 2131362296 */:
                this.photoEditor.undoBrush();
                break;
            case R.id.imageViewRedo /* 2131362301 */:
                setRedo();
                break;
            case R.id.imageViewSavePaint /* 2131362311 */:
                mLoading(true);
                this.constraint_layout_confirmP.setVisibility(View.GONE);
                runOnUiThread(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoEditorActivity.this.m267x53538b3();
                    }
                });
                slideDownSaveView();
                this.currentMode = ToolEditor.NONE;
                break;
            case R.id.imageViewUndo /* 2131362320 */:
                setUndo();
                break;
            case R.id.image_view_save_adjust /* 2131362358 */:
                new SaveFilterAsBitmap().execute(new Void[0]);
                this.image_view_compare_adjust.setVisibility(View.GONE);
                slideDown(this.constraint_layout_adjust);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                setGuideLine();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
                break;
            case R.id.image_view_save_filter /* 2131362360 */:
                new SaveFilterAsBitmap().execute(new Void[0]);
                this.image_view_compare_filter.setVisibility(View.GONE);
                slideDown(this.constraint_layout_filter);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                setGuideLine();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
                break;
            case R.id.image_view_save_overlay /* 2131362361 */:
                new SaveFilterAsBitmap().execute(new Void[0]);
                slideDown(this.constraint_layout_overlay);
                slideUp(this.recycler_view_tools);
                this.image_view_compare_overlay.setVisibility(View.GONE);
                slideDownSaveView();
                setGuideLine();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
                break;
            case R.id.image_view_save_text /* 2131362362 */:
                this.photo_editor_view.setHandlingSticker(null);
                this.photo_editor_view.setLocked(true);
                this.relativeLayoutSaveText.setVisibility(View.GONE);
                if (!this.photo_editor_view.getStickers().isEmpty()) {
                    new SaveStickerAsBitmap().execute(new Void[0]);
                }
                setGuideLine();
                slideDown(this.relativeLayoutText);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                updateLayout();
                this.currentMode = ToolEditor.NONE;
                break;
            case R.id.saveSticker /* 2131362792 */:
                this.photo_editor_view.setHandlingSticker(null);
                this.photo_editor_view.setLocked(true);
                this.relativeLayoutSaveSticker.setVisibility(View.GONE);
                this.imageViewAddSticker.setVisibility(View.GONE);
                if (!this.photo_editor_view.getStickers().isEmpty()) {
                    new SaveStickerAsBitmap().execute(new Void[0]);
                }
                updateLayout();
                setGuideLine();
                slideDown(this.linear_layout_wrapper_sticker_list);
                slideDown(this.constraint_layout_sticker);
                slideDown(this.relativeLayoutSaveSticker);
                slideUp(this.recycler_view_tools);
                slideDownSaveView();
                this.currentMode = ToolEditor.NONE;
                break;
        }
    }

    /* renamed from: lambda$onClick$7$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    void m267x53538b3() {
        this.photoEditor.setBrushDrawingMode(false);
        this.imageViewEraser.setVisibility(View.GONE);
        slideDown(this.constraint_layout_brush);
        slideUp(this.recycler_view_tools);
        setGuideLine();
        this.photo_editor_view.setImageSource(this.photoEditor.getBrushDrawingView().getDrawBitmap(this.photo_editor_view.getCurrentBitmap()));
        this.photoEditor.clearBrushAllViews();
        mLoading(false);
        updateLayout();
    }

    private void setUndo() {
        this.photo_editor_view.undo();
    }

    private void setRedo() {
        this.photo_editor_view.redo();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.keyboardHeightProvider.onPause();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.keyboardHeightProvider.onResume();
    }

    public void openTextFragment() {
        this.textEditorDialogFragment = TextFragment.show(this);
        TextFragment.TextEditor textEditor = new TextFragment.TextEditor() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity.18
            @Override // com.gallery.photos.editphotovideo.fragment.TextFragment.TextEditor
            public void onDone(Text text) {
                PhotoEditorActivity.this.photo_editor_view.addSticker(new PTextView(PhotoEditorActivity.this.getApplicationContext(), text));
            }

            @Override // com.gallery.photos.editphotovideo.fragment.TextFragment.TextEditor
            public void onBackButton() {
                if (PhotoEditorActivity.this.photo_editor_view.getStickers().isEmpty()) {
                    PhotoEditorActivity.this.onBackPressed();
                }
            }
        };
        this.textEditor = textEditor;
        this.textEditorDialogFragment.setOnTextEditorListener(textEditor);
    }

    @Override // com.gallery.photos.editphotovideo.adapters.ToolsAdapter.OnItemSelected
    public void onToolSelected(ToolEditor toolEditor) {
        this.currentMode = toolEditor;
//        switch (AnonymousClass21.$SwitchMap$com$artRoom$photo$editor$tools$ToolEditor[toolEditor.ordinal()]) {
        switch (toolEditor) {
            case PAINT:
                Log.e(TAG, "onToolSelected: filter");
                iColorBrush();
                updateLayout();
                this.photoEditor.setBrushDrawingMode(true);
                this.constraint_layout_confirmP.setVisibility(View.VISIBLE);
                slideDown(this.recycler_view_tools);
                slideUp(this.constraint_layout_brush);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                slideUpSaveControl();
                toogleDrawBottomToolbar(true);
                setGuideLinePaint();
                this.photoEditor.setBrushMode(1);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(this.constraint_layout_root_view);
                constraintSet.connect(this.constraint_layout_confirmP.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
                constraintSet.connect(this.constraint_layout_confirmP.getId(), 4, this.guidelinePaint.getId(), 3, 0);
                constraintSet.connect(this.constraint_layout_confirmP.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
                constraintSet.applyTo(this.constraint_layout_root_view);
                break;
            case TEXT:
                Log.e(TAG, "onToolSelected: TEXT");

                slideUpSaveView();
                this.photo_editor_view.setLocked(false);
                openTextFragment();
                slideDown(this.recycler_view_tools);
                slideUp(this.relativeLayoutSaveText);
                this.relativeLayoutText.setVisibility(View.VISIBLE);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                setGuideLine();
                break;
            case E_TOOLS:
                Log.e(TAG, "onToolSelected: E_TOOLS");
                slideUp(this.recycler_view_tools_effect);
               /* slideUpSaveView();
                updateLayout();
                this.image_view_compare_adjust.setVisibility(View.VISIBLE);
                AdjustAdapter adjustAdapter = new AdjustAdapter(getApplicationContext(), this);
                this.mAdjustAdapter = adjustAdapter;
                this.recycler_view_adjust.setAdapter(adjustAdapter);
                this.mAdjustAdapter.setSelectedAdjust(0);
                this.photoEditor.setAdjustFilter(this.mAdjustAdapter.getFilterConfig());
                ConstraintSet constraintSet2 = new ConstraintSet();
                constraintSet2.clone(this.constraint_layout_root_view);
                constraintSet2.connect(this.relative_layout_wrapper_photo.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
                constraintSet2.connect(this.relative_layout_wrapper_photo.getId(), 4, this.constraint_layout_adjust.getId(), 3, 0);
                constraintSet2.connect(this.relative_layout_wrapper_photo.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
                constraintSet2.applyTo(this.constraint_layout_root_view);
                slideUp(this.constraint_layout_adjust);
                slideDown(this.recycler_view_tools);
                this.recycler_view_tools_effect.setVisibility(View.GONE);*/
                break;
            case FILTER:
              /*  Log.e(TAG, "onToolSelected: FILTER");
                slideUpSaveView();
                new LoadFilterBitmap().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);*/
                break;
           /* case HSL:
                Log.e(TAG, "onToolSelected: HSL" );

                this.constraint_layout_sticker.setVisibility(View.VISIBLE);
                this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
                updateLayout();
                slideUpSaveView();
                this.photo_editor_view.setLocked(false);
                slideDown(this.recycler_view_tools);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                slideUp(this.constraint_layout_sticker);
                slideUp(this.relativeLayoutSaveSticker);
                setGuideLine();
                break;*/

            case STICKER:
                Log.e(TAG, "onToolSelected: HSL");

                this.constraint_layout_sticker.setVisibility(View.VISIBLE);
                this.linear_layout_wrapper_sticker_list.setVisibility(View.VISIBLE);
                updateLayout();
                slideUpSaveView();
                this.photo_editor_view.setLocked(false);
                slideDown(this.recycler_view_tools);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                slideUp(this.constraint_layout_sticker);
                slideUp(this.relativeLayoutSaveSticker);
                setGuideLine();
                break;


            case EFFECT:
                Log.e(TAG, "onToolSelected: EFFECT");

                slideUp(this.recycler_view_tools_effect);
                break;
            case ADJUST:
                Log.e(TAG, "onToolSelected: ADJUST");

//                slideUp(this.recycler_view_tools_effect);


                Log.e(TAG, "onToolSelected: E_TOOLS");
                slideUpSaveView();
                updateLayout();
                this.image_view_compare_adjust.setVisibility(View.VISIBLE);
                AdjustAdapter adjustAdapter = new AdjustAdapter(getApplicationContext(), this);
                this.mAdjustAdapter = adjustAdapter;
                this.recycler_view_adjust.setAdapter(adjustAdapter);
                this.mAdjustAdapter.setSelectedAdjust(0);
                this.photoEditor.setAdjustFilter(this.mAdjustAdapter.getFilterConfig());
                ConstraintSet constraintSet2 = new ConstraintSet();
                constraintSet2.clone(this.constraint_layout_root_view);
                constraintSet2.connect(this.relative_layout_wrapper_photo.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
                constraintSet2.connect(this.relative_layout_wrapper_photo.getId(), 4, this.constraint_layout_adjust.getId(), 3, 0);
                constraintSet2.connect(this.relative_layout_wrapper_photo.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
                constraintSet2.applyTo(this.constraint_layout_root_view);
                slideUp(this.constraint_layout_adjust);
                slideDown(this.recycler_view_tools);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
            case RATIO:
                Log.e(TAG, "onToolSelected: STICKER");

                new ShowRatioFragment().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
//            case SQUARE:
//                new ShowSplashFragment(true).execute(new Void[0]);
//                this.recycler_view_tools_effect.setVisibility(View.GONE);
//                break;
            case CROP:
                Log.e(TAG, "onToolSelected: MIRROR");

                CropperFragment.show(this, this, this.photo_editor_view.getCurrentBitmap());
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
//            case PAINT:
//                new openShapeFragment().execute(new Void[0]);
//                this.recycler_view_tools_effect.setVisibility(View.GONE);
//                break;
            case MIRROR:
                Log.e(TAG, "onToolSelected: RATIO");

                BitmapTransfer.bitmap = this.photo_editor_view.getCurrentBitmap();
                startActivityForResult(new Intent(this, (Class<?>) MirrorActivity.class), 900);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
            case SQUARE:
                Log.e(TAG, "onToolSelected: SQUARE");

                new openSplashFragment(true).execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
           /* case SPLASHING:
                Log.e(TAG, "onToolSelected: SPLASHING" );

                HSlFragment.show(this, this, this.photo_editor_view.getCurrentBitmap());
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;*/

            /*case HSL:
                Log.e(TAG, "onToolSelected: HSL");

                HSlFragment.show(this, this, this.photo_editor_view.getCurrentBitmap());
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;*/
        }
        this.photo_editor_view.setHandlingSticker(null);
    }


    @Override // com.gallery.photos.editphotovideo.adapters.ToolsEffectAdapter.OnItemEffectSelected
    public void onToolEffectSelected(ToolEditor toolEditor) {
        this.currentMode = toolEditor;
        switch (toolEditor) {
            case EFFECT:
                slideUpSaveView();
                this.selectedFeatures = FEATURES.OVERLAY;
                new LoadOverlayBitmap().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
            case SPLASH:
                BitmapTransfer.bitmap = this.photo_editor_view.getCurrentBitmap();
                startActivityForResult(new Intent(this, (Class<?>) SplashActivity.class), 900);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
            case BLUR:
                BitmapTransfer.bitmap = this.photo_editor_view.getCurrentBitmap();
                startActivityForResult(new Intent(this, (Class<?>) BlurActivity.class), 900);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
            case BODY:
                this.selectedFeatures = FEATURES.BODY;
                new dripEffect().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
            case NEON:
                this.selectedFeatures = FEATURES.WING;
                new dripEffect().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
           /* case BG_CHANGE:
                this.selectedFeatures = FEATURES.BG_CHANGE;
                new dripEffect().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;*/
            case FRAME:
                this.selectedFeatures = FEATURES.BORDER;
                new dripEffect().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;
           /* case DOUBLE:
                this.selectedFeatures = FEATURES.DOUBLE;
                new dripEffect().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;*/
          /*  case ART:
                this.selectedFeatures = FEATURES.ART;
                new dripEffect().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;*/
          /*  case DRIP:
                this.selectedFeatures = FEATURES.DRIP;
                new dripEffect().execute(new Void[0]);
                this.recycler_view_tools_effect.setVisibility(View.GONE);
                break;*/
        }
        this.photo_editor_view.setHandlingSticker(null);
    }

    public void setGuideLine() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraint_layout_root_view);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 4, this.guideline.getId(), 3, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
        constraintSet.applyTo(this.constraint_layout_root_view);
    }

    public void setGuideLinePaint() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.constraint_layout_root_view);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 1, this.constraint_layout_root_view.getId(), 1, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 4, this.guidelinePaint.getId(), 3, 0);
        constraintSet.connect(this.relative_layout_wrapper_photo.getId(), 2, this.constraint_layout_root_view.getId(), 2, 0);
        constraintSet.applyTo(this.constraint_layout_root_view);
    }

    public void slideUpSaveView() {
        this.constraint_save_control.setVisibility(View.GONE);
    }

    public void slideUpSaveControl() {
        this.constraint_save_control.setVisibility(View.GONE);
    }

    public void slideDownSaveControl() {
        this.constraint_save_control.setVisibility(View.VISIBLE);
    }

    public void slideDownSaveView() {
        this.constraint_save_control.setVisibility(View.VISIBLE);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        this.recycler_view_tools_effect.setVisibility(View.GONE);
        if (constraint_layout_adjust.getVisibility() == View.VISIBLE) {
            constraint_layout_adjust.setVisibility(View.GONE);
            slideDown(constraint_layout_adjust);
            slideUp(recycler_view_tools);
        } else {
            // Adjust module is already closed, so exit activity
            Log.e("TAGed", "onBackPressed: 0 "); // Second log when exiting

            finish();// Finish the activity
        }

//      onBackPressed();
    }

    private void setOnBackPressDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        getWindow().setLayout(-1, -1);
        attributes.gravity = 80;
        window.setAttributes(attributes);
        dialog.setContentView(R.layout.dialog_exit);
        TextView textView = (TextView) dialog.findViewById(R.id.textViewCancel);
        TextView textView2 = (TextView) dialog.findViewById(R.id.textViewDiscard);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                dialog.dismiss();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoEditorActivity.this.m269x47925749(dialog, view);
            }
        });
        dialog.show();
    }

    /* renamed from: lambda$setOnBackPressDialog$9$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    void m269x47925749(Dialog dialog, View view) {
        dialog.dismiss();
        this.currentMode = null;
        finish();
        finish();
    }

    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.gallery.photos.editphotovideo.listener.AdjustListener
    public void onAdjustSelected(AdjustAdapter.AdjustModel adjustModel) {
        Log.d("XXXXXXXX", "onAdjustSelected " + adjustModel.seekbarIntensity + " " + this.seekbar_adjust.getMax());
        this.seekbar_adjust.setProgress((int) (adjustModel.seekbarIntensity * ((float) this.seekbar_adjust.getMax())));
    }

    @Override // com.gallery.photos.editphotovideo.adapters.StickerAdapter.OnClickStickerListener
    public void addSticker(int i, Bitmap bitmap) {
        this.photo_editor_view.addSticker(new DrawableSticker(new BitmapDrawable(getResources(), bitmap)));
        slideDown(this.linear_layout_wrapper_sticker_list);
        this.imageViewAddSticker.setVisibility(View.VISIBLE);
    }

    @Override // com.gallery.photos.editphotovideo.fragment.CropperFragment.OnCropPhoto
    public void finishCrop(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
        updateLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateView(int i) {
        if (this.selectedFeatures == FEATURES.COLOR) {
            this.photoEditor.setBrushColor(i);
        } else if (this.selectedFeatures == FEATURES.NEON) {
            this.photoEditor.setBrushColor(i);
        }
    }

    @Override
    // com.gallery.photos.editphotovideo.fragment.SplashFragment.SplashSaturationBackgrundListener
    public void onSaveSplashBackground(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
    }

    @Override // com.gallery.photos.editphotovideo.fragment.RatioFragment.RatioSaveListener
    public void ratioSavedBitmap(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
        updateLayout();
    }

    public void onBeautySave(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
    }

    @Override // com.gallery.photos.editphotovideo.fragment.SquareFragment.SplashDialogListener
    public void onSaveBlurBackground(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
    }

    @Override // com.gallery.photos.editphotovideo.fragment.HSlFragment.OnFilterSavePhoto
    public void onSaveFilter(Bitmap bitmap) {
        this.photo_editor_view.setImageSource(bitmap);
        this.currentMode = ToolEditor.NONE;
    }


    @Override // com.gallery.photos.editphotovideo.listener.OverlayListener
    public void onOverlaySelected(int i, String str) {
        this.photoEditor.setFilterEffect(str);
        this.seekbar_overlay.setProgress(70);
        if (this.currentMode == ToolEditor.EFFECT) {
            this.photo_editor_view.getGLSurfaceView().setFilterIntensity(0.7f);
        }
    }

    @Override // com.gallery.photos.editphotovideo.listener.FilterListener
    public void onFilterSelected(int i, String str) {
        this.photoEditor.setFilterEffect(str);
        this.seekbar_filter.setProgress(50);
        if (this.currentMode == ToolEditor.EFFECT) {
            this.photo_editor_view.getGLSurfaceView().setFilterIntensity(0.7f);
        }
    }

    class LoadFilterBitmap extends AsyncTask<Void, Void, Void> {
        LoadFilterBitmap() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            PhotoEditorActivity.this.lstBitmapWithFilter.clear();
            PhotoEditorActivity.this.lstBitmapWithFilter.addAll(FilterFile.getListBitmapFilter(ThumbnailUtils.extractThumbnail(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap(), 100, 100)));
            Log.d("XXXXXXXX", "LoadFilterBitmap " + PhotoEditorActivity.this.lstBitmapWithFilter.size());
            return null;
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Void r9) {
            RecyclerView recyclerView = PhotoEditorActivity.this.recycler_view_filter;
            ArrayList arrayList = PhotoEditorActivity.this.lstBitmapWithFilter;
            PhotoEditorActivity photoEditorActivity = PhotoEditorActivity.this;
            recyclerView.setAdapter(new FilterAdapter(arrayList, photoEditorActivity, photoEditorActivity.getApplicationContext(), Arrays.asList(FilterFile.FILTERS)));
            PhotoEditorActivity photoEditorActivity2 = PhotoEditorActivity.this;
            photoEditorActivity2.slideDown(photoEditorActivity2.recycler_view_tools);
            PhotoEditorActivity photoEditorActivity3 = PhotoEditorActivity.this;
            photoEditorActivity3.slideUp(photoEditorActivity3.constraint_layout_filter);
            PhotoEditorActivity.this.image_view_compare_filter.setVisibility(View.VISIBLE);
            PhotoEditorActivity.this.seekbar_filter.setProgress(100);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(PhotoEditorActivity.this.constraint_layout_root_view);
            constraintSet.connect(PhotoEditorActivity.this.relative_layout_wrapper_photo.getId(), 1, PhotoEditorActivity.this.constraint_layout_root_view.getId(), 1, 0);
            constraintSet.connect(PhotoEditorActivity.this.relative_layout_wrapper_photo.getId(), 4, PhotoEditorActivity.this.constraint_layout_filter.getId(), 3, 0);
            constraintSet.connect(PhotoEditorActivity.this.relative_layout_wrapper_photo.getId(), 2, PhotoEditorActivity.this.constraint_layout_root_view.getId(), 2, 0);
            constraintSet.applyTo(PhotoEditorActivity.this.constraint_layout_root_view);
            PhotoEditorActivity.this.mLoading(false);
            PhotoEditorActivity.this.updateLayout();
        }
    }

    class ShowRatioFragment extends AsyncTask<Void, Bitmap, Bitmap> {
        ShowRatioFragment() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Void... voidArr) {
            return FilterFile.getBlurImageFromBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap(), 5.0f);
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            PhotoEditorActivity.this.mLoading(false);
            PhotoEditorActivity photoEditorActivity = PhotoEditorActivity.this;
            RatioFragment.show(photoEditorActivity, photoEditorActivity, photoEditorActivity.photo_editor_view.getCurrentBitmap(), bitmap);
        }
    }

    class LoadOverlayBitmap extends AsyncTask<Void, Void, Void> {
        LoadOverlayBitmap() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        protected Void doInBackground(Void... voids) {
            try {
                // Get the current bitmap safely
                Bitmap currentBitmap = PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap();

                // Check if bitmap exists and isn't recycled
                if (currentBitmap == null || currentBitmap.isRecycled()) {
                    Log.e("PhotoEditor", "Current bitmap is null or recycled");
                    return null;
                }

                // Create a safe copy of the bitmap for processing
                Bitmap safeBitmap = currentBitmap.copy(currentBitmap.getConfig(), true);
                if (safeBitmap == null) {
                    Log.e("PhotoEditor", "Failed to create bitmap copy");
                    return null;
                }

                // Create thumbnail from the safe copy
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(safeBitmap, 100, 100);

                // Process based on selected feature
                switch (PhotoEditorActivity.this.selectedFeatures) {
                    case OVERLAY:
                        PhotoEditorActivity.this.lstBitmapWithOverlay.clear();
                        PhotoEditorActivity.this.lstBitmapWithOverlay.addAll(
                                OverlayFile.getListBitmapOverlayEffect(thumbnail));
                        break;

                    case LIGHT:
                        PhotoEditorActivity.this.lstBitmapWithLight.clear();
                        PhotoEditorActivity.this.lstBitmapWithLight.addAll(
                                OverlayFile.getListBitmapLightEffect(thumbnail));
                        break;

                    case DUST:
                        PhotoEditorActivity.this.lstBitmapWithDust.clear();
                        PhotoEditorActivity.this.lstBitmapWithDust.addAll(
                                OverlayFile.getListBitmapDustEffect(thumbnail));
                        break;

                    case MASK:
                        PhotoEditorActivity.this.lstBitmapWithMask.clear();
                        PhotoEditorActivity.this.lstBitmapWithMask.addAll(
                                OverlayFile.getListBitmapMaskEffect(thumbnail));
                        break;

                    case GRADIENT:
                        PhotoEditorActivity.this.lstBitmapWithGradient.clear();
                        PhotoEditorActivity.this.lstBitmapWithGradient.addAll(
                                OverlayFile.getListBitmapGradientEffect(thumbnail));
                        break;

                    case EFFECT:
                        PhotoEditorActivity.this.lstBitmapWithOverlay.clear();
                        PhotoEditorActivity.this.lstBitmapWithOverlay.addAll(
                                OverlayFile.getListBitmapOverlayEffect(thumbnail));
                        break;
                }

                // Clean up
                if (!safeBitmap.isRecycled()) {
                    safeBitmap.recycle();
                }
                if (thumbnail != null && !thumbnail.isRecycled()) {
                    thumbnail.recycle();
                }

            } catch (Exception e) {
                Log.e("PhotoEditor", "Error in doInBackground", e);
            }
            return null;
        }
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r9) {

            try {

            if (PhotoEditorActivity.this.selectedFeatures == FEATURES.OVERLAY) {
                RecyclerView recyclerView = PhotoEditorActivity.this.recycler_view_overlay;
                List<Bitmap> list = PhotoEditorActivity.this.lstBitmapWithOverlay;
                PhotoEditorActivity photoEditorActivity = PhotoEditorActivity.this;
                recyclerView.setAdapter(new OverlayAdapter(list, photoEditorActivity, photoEditorActivity.getApplicationContext(), Arrays.asList(OverlayFile.OVERLAY_EFFECTS)));
                PhotoEditorActivity photoEditorActivity2 = PhotoEditorActivity.this;
                photoEditorActivity2.slideDown(photoEditorActivity2.recycler_view_tools);
                PhotoEditorActivity photoEditorActivity3 = PhotoEditorActivity.this;
                photoEditorActivity3.slideUp(photoEditorActivity3.constraint_layout_overlay);
                PhotoEditorActivity.this.image_view_compare_overlay.setVisibility(View.VISIBLE);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(PhotoEditorActivity.this.constraint_layout_root_view);
                constraintSet.connect(PhotoEditorActivity.this.relative_layout_wrapper_photo.getId(), 1, PhotoEditorActivity.this.constraint_layout_root_view.getId(), 1, 0);
                constraintSet.connect(PhotoEditorActivity.this.relative_layout_wrapper_photo.getId(), 4, PhotoEditorActivity.this.constraint_layout_overlay.getId(), 3, 0);
                constraintSet.connect(PhotoEditorActivity.this.relative_layout_wrapper_photo.getId(), 2, PhotoEditorActivity.this.constraint_layout_root_view.getId(), 2, 0);
                constraintSet.applyTo(PhotoEditorActivity.this.constraint_layout_root_view);
                PhotoEditorActivity.this.updateLayout();
            } else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.LIGHT) {
                RecyclerView recyclerView2 = PhotoEditorActivity.this.recycler_view_overlay;
                List<Bitmap> list2 = PhotoEditorActivity.this.lstBitmapWithLight;
                PhotoEditorActivity photoEditorActivity4 = PhotoEditorActivity.this;
                recyclerView2.setAdapter(new OverlayAdapter(list2, photoEditorActivity4, photoEditorActivity4.getApplicationContext(), Arrays.asList(OverlayFile.LIGHT_EFFECTS)));
            } else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.DUST) {
                RecyclerView recyclerView3 = PhotoEditorActivity.this.recycler_view_overlay;
                List<Bitmap> list3 = PhotoEditorActivity.this.lstBitmapWithDust;
                PhotoEditorActivity photoEditorActivity5 = PhotoEditorActivity.this;
                recyclerView3.setAdapter(new OverlayAdapter(list3, photoEditorActivity5, photoEditorActivity5.getApplicationContext(), Arrays.asList(OverlayFile.DUST_EFFECTS)));
            } else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.MASK) {
                RecyclerView recyclerView4 = PhotoEditorActivity.this.recycler_view_overlay;
                List<Bitmap> list4 = PhotoEditorActivity.this.lstBitmapWithMask;
                PhotoEditorActivity photoEditorActivity6 = PhotoEditorActivity.this;
                recyclerView4.setAdapter(new OverlayAdapter(list4, photoEditorActivity6, photoEditorActivity6.getApplicationContext(), Arrays.asList(OverlayFile.MASK_EFFECTS)));
            } else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.GRADIENT) {
                RecyclerView recyclerView5 = PhotoEditorActivity.this.recycler_view_overlay;
                List<Bitmap> list5 = PhotoEditorActivity.this.lstBitmapWithGradient;
                PhotoEditorActivity photoEditorActivity7 = PhotoEditorActivity.this;
                recyclerView5.setAdapter(new OverlayAdapter(list5, photoEditorActivity7, photoEditorActivity7.getApplicationContext(), Arrays.asList(OverlayFile.GRADIENT_EFFECTS)));
            } else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.EFFECT) {
                RecyclerView recyclerView6 = PhotoEditorActivity.this.recycler_view_overlay;
                List<Bitmap> list6 = PhotoEditorActivity.this.lstBitmapWithOverlay;
                PhotoEditorActivity photoEditorActivity8 = PhotoEditorActivity.this;
                recyclerView6.setAdapter(new OverlayAdapter(list6, photoEditorActivity8, photoEditorActivity8.getApplicationContext(), Arrays.asList(OverlayFile.OVERLAY_EFFECTS)));
            }
            PhotoEditorActivity.this.mLoading(false);
            PhotoEditorActivity.this.seekbar_overlay.setProgress(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class openSplashFragment extends AsyncTask<Void, List<Bitmap>, List<Bitmap>> {
        boolean isSplashSquared;

        public openSplashFragment(boolean z) {
            this.isSplashSquared = z;
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public List<Bitmap> doInBackground(Void... voidArr) {
            Bitmap currentBitmap = PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap();

            try {
                // Add null check for currentBitmap
                if (currentBitmap == null || currentBitmap.isRecycled()) {
                    Log.e("PhotoEditor", "Current bitmap is null or recycled");
                    return null;
                }
                ArrayList arrayList = new ArrayList();
                arrayList.add(currentBitmap);
                if (this.isSplashSquared) {
                    arrayList.add(FilterUtils.getBlackAndWhiteImageFromBitmap(currentBitmap));
                }
                return arrayList;
            } catch (Exception e) {
                Log.e("PhotoEditor", "Background processing failed", e);
                return null;
            }
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(List<Bitmap> list) {
            if (list == null && list.isEmpty()) {
                Toast.makeText(PhotoEditorActivity.this, "Failed to process image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (this.isSplashSquared) {
                SplashFragment.show(PhotoEditorActivity.this, list.get(0), null, list.get(1), PhotoEditorActivity.this, true);
            }
            PhotoEditorActivity.this.mLoading(false);
        }
    }

    class dripEffect extends AsyncTask<Void, Void, Void> {
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            return null;
        }

        dripEffect() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Void r4) {
            StoreManager.setCurrentCroppedBitmap(PhotoEditorActivity.this, null);
            StoreManager.setCurrentCroppedMaskBitmap(PhotoEditorActivity.this, null);
           /* if (PhotoEditorActivity.this.selectedFeatures == FEATURES.DRIP) {
                DripActivity.setFaceBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity photoEditorActivity = PhotoEditorActivity.this;
                StoreManager.setCurrentOriginalBitmap(photoEditorActivity, photoEditorActivity.photo_editor_view.getCurrentBitmap());
                Intent intent = new Intent(PhotoEditorActivity.this, (Class<?>) DripActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_TOOL);
                PhotoEditorActivity.this.startActivityForResult(intent, 900);
            } else */
            if (PhotoEditorActivity.this.selectedFeatures == FEATURES.ART) {
                PortraitActivity.setFaceBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity photoEditorActivity2 = PhotoEditorActivity.this;
                StoreManager.setCurrentOriginalBitmap(photoEditorActivity2, photoEditorActivity2.photo_editor_view.getCurrentBitmap());
                Intent intent2 = new Intent(PhotoEditorActivity.this, (Class<?>) PortraitActivity.class);
                intent2.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_TOOL);
                PhotoEditorActivity.this.startActivityForResult(intent2, 900);
            } else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.WING) {
                NeonActivity.setFaceBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity photoEditorActivity3 = PhotoEditorActivity.this;
                StoreManager.setCurrentOriginalBitmap(photoEditorActivity3, photoEditorActivity3.photo_editor_view.getCurrentBitmap());
                Intent intent3 = new Intent(PhotoEditorActivity.this, (Class<?>) NeonActivity.class);
                intent3.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_TOOL);
                PhotoEditorActivity.this.startActivityForResult(intent3, 900);
            } else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.BORDER) {
                BorderActivity.setFaceBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity photoEditorActivity4 = PhotoEditorActivity.this;
                StoreManager.setCurrentOriginalBitmap(photoEditorActivity4, photoEditorActivity4.photo_editor_view.getCurrentBitmap());
                Intent intent4 = new Intent(PhotoEditorActivity.this, (Class<?>) BorderActivity.class);
                intent4.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_TOOL);
                PhotoEditorActivity.this.startActivityForResult(intent4, 900);
            }
            /*else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.DOUBLE) {
                DoubleActivity.setFaceBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity photoEditorActivity5 = PhotoEditorActivity.this;
                StoreManager.setCurrentOriginalBitmap(photoEditorActivity5, photoEditorActivity5.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity.this.startActivityForResult(new Intent(PhotoEditorActivity.this, (Class<?>) DoubleActivity.class), 900);
            } */
           /* else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.BG_CHANGE) {
                RemoveBgActivity.setFaceBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity photoEditorActivity6 = PhotoEditorActivity.this;
                StoreManager.setCurrentOriginalBitmap(photoEditorActivity6, photoEditorActivity6.photo_editor_view.getCurrentBitmap());
                Intent intent5 = new Intent(PhotoEditorActivity.this, (Class<?>) RemoveBgActivity.class);
                intent5.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_TOOL_CHANGE);
                PhotoEditorActivity.this.startActivityForResult(intent5, 900);
            } */
            else if (PhotoEditorActivity.this.selectedFeatures == FEATURES.BODY) {
                BodyActivity.setFaceBitmap(PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap());
                PhotoEditorActivity photoEditorActivity7 = PhotoEditorActivity.this;
                StoreManager.setCurrentOriginalBitmap(photoEditorActivity7, photoEditorActivity7.photo_editor_view.getCurrentBitmap());
                Intent intent6 = new Intent(PhotoEditorActivity.this, (Class<?>) BodyActivity.class);
                intent6.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_TOOL);
                PhotoEditorActivity.this.startActivityForResult(intent6, 900);
            }
            PhotoEditorActivity.this.overridePendingTransition(R.anim.enter, R.anim.exit);
            PhotoEditorActivity.this.mLoading(false);
        }
    }


    class ShowSplashFragment extends AsyncTask<Void, List<Bitmap>, List<Bitmap>> {
        boolean isSplashSquared;

        public ShowSplashFragment(boolean z) {
            this.isSplashSquared = z;
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public List<Bitmap> doInBackground(Void... voidArr) {
            Bitmap currentBitmap = PhotoEditorActivity.this.photo_editor_view.getCurrentBitmap();
            ArrayList arrayList = new ArrayList();
            arrayList.add(currentBitmap);
            if (this.isSplashSquared) {
                arrayList.add(FilterFile.getBlurImageFromBitmap(currentBitmap, 2.5f));
            }
            return arrayList;
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(List<Bitmap> list) {
            if (this.isSplashSquared) {
                SquareFragment.show(PhotoEditorActivity.this, list.get(0), null, list.get(1), PhotoEditorActivity.this, true);
            }
            PhotoEditorActivity.this.mLoading(false);
        }
    }

    class SaveFilterAsBitmap extends AsyncTask<Void, Void, Bitmap> {
        SaveFilterAsBitmap() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        void lambda$doInBackground$0(Bitmap[] bitmapArr, Bitmap bitmap) {
            bitmapArr[0] = bitmap;
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Void... voidArr) {
            final Bitmap[] bitmapArr = {null};
            PhotoEditorActivity.this.photo_editor_view.saveGLSurfaceViewAsBitmap(new OnSaveBitmap() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$SaveFilterAsBitmap$$ExternalSyntheticLambda0
                @Override // com.gallery.photos.editphotovideo.Editor.OnSaveBitmap
                public final void onBitmapReady(Bitmap bitmap) {
                    lambda$doInBackground$0(bitmapArr, bitmap);
                }
            });
            while (true) {
                Bitmap bitmap = bitmapArr[0];
                if (bitmap != null) {
                    return bitmap;
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            PhotoEditorActivity.this.photo_editor_view.setImageSource(bitmap);
            PhotoEditorActivity.this.photo_editor_view.setFilterEffect("");
            PhotoEditorActivity.this.mLoading(false);
        }
    }

    class SaveStickerAsBitmap extends AsyncTask<Void, Void, Bitmap> {
        SaveStickerAsBitmap() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.photo_editor_view.getGLSurfaceView().setAlpha(0.0f);
            PhotoEditorActivity.this.mLoading(true);
        }

        void lambda$doInBackground$0(Bitmap[] bitmapArr, Bitmap bitmap) {
            bitmapArr[0] = bitmap;
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Void... voidArr) {
            final Bitmap[] bitmapArr = {null};
            while (true) {
                Bitmap bitmap = bitmapArr[0];
                if (bitmap != null) {
                    return bitmap;
                }
                try {
                    PhotoEditorActivity.this.photoEditor.saveStickerAsBitmap(new OnSaveBitmap() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$SaveStickerAsBitmap$$ExternalSyntheticLambda0
                        @Override // com.gallery.photos.editphotovideo.Editor.OnSaveBitmap
                        public final void onBitmapReady(Bitmap bitmap2) {
                            lambda$doInBackground$0(bitmapArr, bitmap2);
                        }
                    });
                    while (bitmapArr[0] == null) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception unused) {
                }
            }
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            PhotoEditorActivity.this.photo_editor_view.setImageSource(bitmap);
            PhotoEditorActivity.this.photo_editor_view.getStickers().clear();
            PhotoEditorActivity.this.photo_editor_view.getGLSurfaceView().setAlpha(1.0f);
            PhotoEditorActivity.this.mLoading(false);
            PhotoEditorActivity.this.updateLayout();
        }
    }

    @Override
    // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 123) {
            if (i != 900 || intent == null || !intent.getStringExtra("MESSAGE").equals("done") || BitmapTransfer.bitmap == null) {
                return;
            }
            new loadBitmap().execute(BitmapTransfer.bitmap);
            return;
        }
        if (i2 == -1) {
            try {
                InputStream openInputStream = getContentResolver().openInputStream(intent.getData());
                Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream);
                float width = decodeStream.getWidth();
                float height = decodeStream.getHeight();
                float max = Math.max(width / 1280.0f, height / 1280.0f);
                if (max > 1.0f) {
                    decodeStream = Bitmap.createScaledBitmap(decodeStream, (int) (width / max), (int) (height / max), false);
                }
                if (SystemUtil.rotateBitmap(decodeStream, new ExifInterface(openInputStream).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) != decodeStream) {
                    decodeStream.recycle();
                    decodeStream = null;
                }
                this.photo_editor_view.setImageSource(decodeStream);
                updateLayout();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                MsgUtil.toastMsg(this, "Error: Can not open image");
                return;
            }
        }
        finish();
    }

    @Override // com.gallery.photos.editphotovideo.activities.BaseActivity
    public void isPermissionGranted(boolean z, String str) {
        if (z) {
            new SaveBitmap().execute(new Void[0]);
        }
    }

    class loadBitmap extends AsyncTask<Bitmap, Bitmap, Bitmap> {
        loadBitmap() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(Bitmap... bitmapArr) {
            try {
                Bitmap bitmap = bitmapArr[0];
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();
                float max = Math.max(width / 1280.0f, height / 1280.0f);
                return max > 1.0f ? Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false) : bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            try {

            PhotoEditorActivity.this.photo_editor_view.setImageSource(bitmap);
            PhotoEditorActivity.this.updateLayout();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class OnLoadBitmapFromUri extends AsyncTask<String, Bitmap, Bitmap> {
        OnLoadBitmapFromUri() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public Bitmap doInBackground(String... strArr) {
            try {
                Uri fromFile = Uri.fromFile(new File(strArr[0]));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(PhotoEditorActivity.this.getContentResolver(), fromFile);
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();
                float max = Math.max(width / 1280.0f, height / 1280.0f);
                if (max > 1.0f) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false);
                }
                Bitmap rotateBitmap = SystemUtil.rotateBitmap(bitmap, new ExifInterface(PhotoEditorActivity.this.getContentResolver().openInputStream(fromFile)).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1));
                if (rotateBitmap != bitmap) {
                    bitmap.recycle();
                }
                return rotateBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override // android.os.AsyncTask
        public void onPostExecute(Bitmap bitmap) {
            if (bitmap == null || bitmap.isRecycled()) {
                Toast.makeText(PhotoEditorActivity.this,
                        "Failed to load image", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
            PhotoEditorActivity.this.photo_editor_view.setImageSource(bitmap);
            PhotoEditorActivity.this.updateLayout();
            } catch (Exception e) {
                Log.e("PhotoEditor", "Error setting image", e);
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    public void updateLayout() {
        this.photo_editor_view.postDelayed(new Runnable() { // from class: com.gallery.photos.editphotovideo.activities.PhotoEditorActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                PhotoEditorActivity.this.m270x573c1047();
            }
        }, 300L);
    }

    /* renamed from: lambda$updateLayout$10$com-artRoom-photo-editor-activities-PhotoEditorActivity, reason: not valid java name */
    void m270x573c1047() {
        try {
            Display defaultDisplay = getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            int i = point.x;
            int height = this.relative_layout_wrapper_photo.getHeight();
            int i2 = this.photo_editor_view.getGLSurfaceView().getRenderViewport().width;
            float f = this.photo_editor_view.getGLSurfaceView().getRenderViewport().height;
            float f2 = i2;
            if (((int) ((i * f) / f2)) <= height) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
                layoutParams.addRule(13);
                this.photo_editor_view.setLayoutParams(layoutParams);
                this.photo_editor_view.setVisibility(View.VISIBLE);
            } else {
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams((int) ((height * f2) / f), -1);
                layoutParams2.addRule(13);
                this.photo_editor_view.setLayoutParams(layoutParams2);
                this.photo_editor_view.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLoading(false);
    }

    class SaveBitmap extends AsyncTask<Void, String, String> {
        SaveBitmap() {
        }

        @Override // android.os.AsyncTask
        public void onPreExecute() {
            PhotoEditorActivity.this.mLoading(true);
        }

        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            try {
                PhotoEditorActivity photoEditorActivity = PhotoEditorActivity.this;
                return SaveFileUtils.saveBitmapFileRemoveBg(photoEditorActivity, photoEditorActivity.photo_editor_view.getCurrentBitmap(), new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()), null).getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            PhotoEditorActivity.this.mLoading(false);
            if (str == null) {
                Toast.makeText(PhotoEditorActivity.this.getApplicationContext(), "Oop! Something went wrong", 1).show();
                return;
            }
            finish();
           /* Intent intent = new Intent(PhotoEditorActivity.this, (Class<?>) ShareActivity.class);
            intent.putExtra("path", str);
            PhotoEditorActivity.this.startActivity(intent);*/
        }
    }

    public void mLoading(boolean z) {
        if (z) {
            getWindow().setFlags(16, 16);
            this.relative_layout_loading.setVisibility(View.VISIBLE);
        } else {
            getWindow().clearFlags(16);
            this.relative_layout_loading.setVisibility(View.GONE);
        }
    }


}
