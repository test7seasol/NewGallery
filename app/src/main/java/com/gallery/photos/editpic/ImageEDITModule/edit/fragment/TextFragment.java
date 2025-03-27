package com.gallery.photos.editpic.ImageEDITModule.edit.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.EditText;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.Text;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.FontAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.ShadowAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.TextBackgroundAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.adapters.TextColorAdapter;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.FontFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.PreferenceUtil;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;
import com.smarteist.autoimageslider.IndicatorView.animation.type.ColorAnimation;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TextFragment extends DialogFragment implements View.OnClickListener, FontAdapter.ItemClickListener, ShadowAdapter.ShadowItemClickListener, TextColorAdapter.ColorListener, TextBackgroundAdapter.BackgroundColorListener {
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String TAG = "TextFragment";
    EditText add_text_edit_text;
    CheckBox checkbox_background;
    private FontAdapter fontAdapter;
    ImageView imageViewColor;
    ImageView imageViewFont;
    ImageView imageViewSet;
    ImageView imageViewShadow;
    ImageView image_view_align_center;
    ImageView image_view_align_left;
    ImageView image_view_align_right;
    ImageView image_view_color;
    ImageView image_view_keyboard;
    ImageView image_view_save_change;
    private InputMethodManager inputMethodManager;
    public LinearLayout linLayoutColors;
    public LinearLayout linearLayoutAdjust;
    ConstraintLayout linear_layout_edit_text_tools;
    LinearLayout linear_layout_preview;
    public Text polishText;
    public RecyclerView recycler_view_background;
    public RecyclerView recycler_view_color;
    RecyclerView recycler_view_fonts;
    RecyclerView recycler_view_shadow;
    RelativeLayout relativeLayoutBg;
    LinearLayout scroll_view_change_font_layout;
    SeekBar seekbar_background_opacity;
    SeekBar seekbar_height;
    SeekBar seekbar_radius;
    SeekBar seekbar_text_size;
    SeekBar seekbar_width;
    private ShadowAdapter shadowAdapter;
    SeekBar textColorOpacity;
    private TextEditor textEditor;
    private List<ImageView> textFunctions;
    TextView textViewColor;
    TextView textViewFont;
    TextView textViewSeekBarBackground;
    TextView textViewSeekBarColor;
    TextView textViewSeekBarHeight;
    TextView textViewSeekBarRadius;
    TextView textViewSeekBarSize;
    TextView textViewSeekBarWith;
    TextView textViewSet;
    TextView textViewShadow;
    TextView text_view_preview_effect;

    public void dismisslayout() {
        dismiss();
    }

    public interface TextEditor {
        void onBackButton();

        void onDone(Text text);
    }

    @Override // com.gallery.photos.editphotovideo.adapters.FontAdapter.ItemClickListener
    public void onItemClick(View view, int i) {
        FontFile.setFontByName(requireContext(), this.text_view_preview_effect, FontFile.getListFonts().get(i));
        this.polishText.setFontName(FontFile.getListFonts().get(i));
        this.polishText.setFontIndex(i);
    }

    public static TextFragment show(AppCompatActivity appCompatActivity, String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_INPUT_TEXT, str);
        bundle.putInt(EXTRA_COLOR_CODE, i);
        TextFragment textFragment = new TextFragment();
        textFragment.setArguments(bundle);
        textFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return textFragment;
    }

    public static TextFragment show(AppCompatActivity appCompatActivity, Text text) {
        TextFragment textFragment = new TextFragment();
        textFragment.setPolishText(text);
        textFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return textFragment;
    }

    @Override // com.gallery.photos.editphotovideo.adapters.ShadowAdapter.ShadowItemClickListener
    public void onShadowItemClick(View view, int i) {
        Text.TextShadow textShadow = Text.getLstTextShadow().get(i);
        this.text_view_preview_effect.setShadowLayer(textShadow.getRadius(), textShadow.getDx(), textShadow.getDy(), textShadow.getColorShadow());
        this.text_view_preview_effect.invalidate();
        this.polishText.setTextShadow(textShadow);
        this.polishText.setTextShadowIndex(i);
    }

    public static TextFragment show(AppCompatActivity appCompatActivity) {
        return show(appCompatActivity, "Test", ContextCompat.getColor(appCompatActivity, R.color.white));
    }

    public void setPolishText(Text text) {
        this.polishText = text;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        return layoutInflater.inflate(R.layout.fragment_text, viewGroup, false);
    }

    public void dismissAndShowSticker() {
        TextEditor textEditor = this.textEditor;
        if (textEditor != null) {
            textEditor.onBackButton();
        }
        dismiss();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.polishText == null) {
            this.polishText = Text.getDefaultProperties();
        }
        this.add_text_edit_text = (EditText) view.findViewById(R.id.add_text_edit_text);
        this.image_view_keyboard = (ImageView) view.findViewById(R.id.image_view_keyboard);
        this.image_view_color = (ImageView) view.findViewById(R.id.image_view_color);
        this.image_view_align_left = (ImageView) view.findViewById(R.id.imageViewAlignLeft);
        this.image_view_align_center = (ImageView) view.findViewById(R.id.imageViewAlignCenter);
        this.image_view_align_right = (ImageView) view.findViewById(R.id.imageViewAlignRight);
        this.textViewFont = (TextView) view.findViewById(R.id.textViewFont);
        this.textViewColor = (TextView) view.findViewById(R.id.textViewColor);
        this.textViewSet = (TextView) view.findViewById(R.id.textViewSet);
        this.textViewShadow = (TextView) view.findViewById(R.id.textViewShadow);
        this.imageViewFont = (ImageView) view.findViewById(R.id.imageViewFont);
        this.imageViewColor = (ImageView) view.findViewById(R.id.imageViewColor);
        this.imageViewSet = (ImageView) view.findViewById(R.id.imageViewSet);
        this.imageViewShadow = (ImageView) view.findViewById(R.id.imageViewShadow);
        this.relativeLayoutBg = (RelativeLayout) view.findViewById(R.id.relativeLayoutBg);
        this.textViewSeekBarSize = (TextView) view.findViewById(R.id.seekbarSize);
        this.textViewSeekBarColor = (TextView) view.findViewById(R.id.seekbarColor);
        this.textViewSeekBarBackground = (TextView) view.findViewById(R.id.seekbarBackground);
        this.textViewSeekBarRadius = (TextView) view.findViewById(R.id.seekbarRadius);
        this.textViewSeekBarWith = (TextView) view.findViewById(R.id.seekbarWith);
        this.textViewSeekBarHeight = (TextView) view.findViewById(R.id.seekbarHeight);
        this.image_view_save_change = (ImageView) view.findViewById(R.id.image_view_save_change);
        this.scroll_view_change_font_layout = (LinearLayout) view.findViewById(R.id.scroll_view_change_font_layout);
        this.linear_layout_edit_text_tools = (ConstraintLayout) view.findViewById(R.id.linear_layout_edit_text_tools);
        this.recycler_view_fonts = (RecyclerView) view.findViewById(R.id.recycler_view_fonts);
        this.recycler_view_shadow = (RecyclerView) view.findViewById(R.id.recycler_view_shadow);
        this.textColorOpacity = (SeekBar) view.findViewById(R.id.seekbar_text_opacity);
        this.text_view_preview_effect = (TextView) view.findViewById(R.id.text_view_preview_effect);
        this.linear_layout_preview = (LinearLayout) view.findViewById(R.id.linear_layout_preview);
        this.checkbox_background = (CheckBox) view.findViewById(R.id.checkbox_background);
        this.seekbar_width = (SeekBar) view.findViewById(R.id.seekbar_width);
        this.seekbar_height = (SeekBar) view.findViewById(R.id.seekbar_height);
        this.seekbar_background_opacity = (SeekBar) view.findViewById(R.id.seekbar_background_opacity);
        this.seekbar_text_size = (SeekBar) view.findViewById(R.id.seekbar_text_size);
        this.seekbar_radius = (SeekBar) view.findViewById(R.id.seekbar_radius);
        this.linLayoutColors = (LinearLayout) view.findViewById(R.id.linLayoutColors);
        this.linearLayoutAdjust = (LinearLayout) view.findViewById(R.id.linearLayoutAdjust);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_color);
        this.recycler_view_color = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), 0, false));
        this.recycler_view_color.setAdapter(new TextColorAdapter(getContext(), this));
        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.recycler_view_bg);
        this.recycler_view_background = recyclerView2;
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), 0, false));
        this.recycler_view_background.setAdapter(new TextBackgroundAdapter(getContext(), this));
        this.add_text_edit_text.setTextFragment(this);
        initAddTextLayout();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
        this.inputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
        setDefaultStyleForEdittext();
//        this.inputMethodManager.toggleSoftInput(2, 0);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                        linear_layout_edit_text_tools.getLayoutParams();
                params.bottomMargin = 0;
                linear_layout_edit_text_tools.setLayoutParams(params);
            }
        });
        showKeyboard();

        this.recycler_view_fonts.setLayoutManager(new GridLayoutManager(getContext(), 5));
        FontAdapter fontAdapter = new FontAdapter(getContext(), FontFile.getListFonts());
        this.fontAdapter = fontAdapter;
        fontAdapter.setClickListener(this);
        this.recycler_view_fonts.setAdapter(this.fontAdapter);
        this.recycler_view_shadow.setLayoutManager(new GridLayoutManager(getContext(), 5));
        ShadowAdapter shadowAdapter = new ShadowAdapter(getContext(), Text.getLstTextShadow());
        this.shadowAdapter = shadowAdapter;
        shadowAdapter.setClickListener(this);
        this.recycler_view_shadow.setAdapter(this.shadowAdapter);
        view.findViewById(R.id.linearLayoutFont).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                TextFragment.this.recycler_view_fonts.setVisibility(View.VISIBLE);
                TextFragment.this.recycler_view_shadow.setVisibility(View.GONE);
                TextFragment.this.linearLayoutAdjust.setVisibility(View.GONE);
                TextFragment.this.linLayoutColors.setVisibility(View.GONE);
                TextFragment.this.imageViewFont.setColorFilter(TextFragment.this.getResources().getColor(R.color.mainColor));
                TextFragment.this.imageViewShadow.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewSet.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewColor.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewFont.setTextColor(TextFragment.this.getResources().getColor(R.color.mainColor));
                TextFragment.this.textViewShadow.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewSet.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewColor.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
            }
        });
        view.findViewById(R.id.linearLayoutShadow).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                TextFragment.this.recycler_view_fonts.setVisibility(View.GONE);
                TextFragment.this.recycler_view_shadow.setVisibility(View.VISIBLE);
                TextFragment.this.linearLayoutAdjust.setVisibility(View.GONE);
                TextFragment.this.linLayoutColors.setVisibility(View.GONE);
                TextFragment.this.imageViewFont.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewShadow.setColorFilter(TextFragment.this.getResources().getColor(R.color.mainColor));
                TextFragment.this.imageViewSet.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewColor.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewFont.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewShadow.setTextColor(TextFragment.this.getResources().getColor(R.color.mainColor));
                TextFragment.this.textViewSet.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewColor.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
            }
        });
        view.findViewById(R.id.linearLayoutSet).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                TextFragment.this.recycler_view_fonts.setVisibility(View.GONE);
                TextFragment.this.recycler_view_shadow.setVisibility(View.GONE);
                TextFragment.this.linearLayoutAdjust.setVisibility(View.VISIBLE);
                TextFragment.this.linLayoutColors.setVisibility(View.GONE);
                TextFragment.this.imageViewFont.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewShadow.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewSet.setColorFilter(TextFragment.this.getResources().getColor(R.color.mainColor));
                TextFragment.this.imageViewColor.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewFont.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewShadow.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewSet.setTextColor(TextFragment.this.getResources().getColor(R.color.mainColor));
                TextFragment.this.textViewColor.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
            }
        });
        view.findViewById(R.id.linearLayoutColor).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                TextFragment.this.recycler_view_fonts.setVisibility(View.GONE);
                TextFragment.this.recycler_view_shadow.setVisibility(View.GONE);
                TextFragment.this.linearLayoutAdjust.setVisibility(View.GONE);
                TextFragment.this.linLayoutColors.setVisibility(View.VISIBLE);
                TextFragment.this.imageViewFont.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewShadow.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewSet.setColorFilter(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.imageViewColor.setColorFilter(TextFragment.this.getResources().getColor(R.color.mainColor));
                TextFragment.this.textViewFont.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewShadow.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewSet.setTextColor(TextFragment.this.getResources().getColor(R.color.iconColor));
                TextFragment.this.textViewColor.setTextColor(TextFragment.this.getResources().getColor(R.color.mainColor));
            }
        });
        this.textColorOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.5
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextFragment.this.textViewSeekBarColor.setText(String.valueOf(i));
                int i2 = 255 - i;
                TextFragment.this.polishText.setTextAlpha(i2);
                TextFragment.this.text_view_preview_effect.setTextColor(Color.argb(i2, Color.red(TextFragment.this.polishText.getTextColor()), Color.green(TextFragment.this.polishText.getTextColor()), Color.blue(TextFragment.this.polishText.getTextColor())));
            }
        });
        this.add_text_edit_text.addTextChangedListener(new TextWatcher() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.6
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                TextFragment.this.text_view_preview_effect.setText(charSequence.toString());
                TextFragment.this.polishText.setText(charSequence.toString());
            }
        });
        this.checkbox_background.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (!z) {
                    TextFragment.this.polishText.setShowBackground(false);
                    TextFragment.this.text_view_preview_effect.setBackgroundResource(0);
                    TextFragment.this.text_view_preview_effect.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                } else if (TextFragment.this.checkbox_background.isPressed() || TextFragment.this.polishText.isShowBackground()) {
                    TextFragment.this.polishText.setShowBackground(true);
                    TextFragment.this.initPreviewText();
                } else {
                    TextFragment.this.checkbox_background.setChecked(false);
                    TextFragment.this.polishText.setShowBackground(false);
                    TextFragment.this.initPreviewText();
                }
            }
        });
        this.seekbar_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.8
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextFragment.this.textViewSeekBarWith.setText(String.valueOf(i));
                TextFragment.this.text_view_preview_effect.setPadding(SystemUtil.dpToPx(TextFragment.this.requireContext(), i), TextFragment.this.text_view_preview_effect.getPaddingTop(), SystemUtil.dpToPx(TextFragment.this.getContext(), i), TextFragment.this.text_view_preview_effect.getPaddingBottom());
                TextFragment.this.polishText.setPaddingWidth(i);
            }
        });
        this.seekbar_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.9
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextFragment.this.textViewSeekBarHeight.setText(String.valueOf(i));
                TextFragment.this.text_view_preview_effect.setPadding(TextFragment.this.text_view_preview_effect.getPaddingLeft(), SystemUtil.dpToPx(TextFragment.this.requireContext(), i), TextFragment.this.text_view_preview_effect.getPaddingRight(), SystemUtil.dpToPx(TextFragment.this.getContext(), i));
                TextFragment.this.polishText.setPaddingHeight(i);
            }
        });
        this.seekbar_background_opacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.10
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextFragment.this.textViewSeekBarBackground.setText(String.valueOf(i));
                TextFragment.this.polishText.setBackgroundAlpha(255 - i);
                int red = Color.red(TextFragment.this.polishText.getBackgroundColor());
                int green = Color.green(TextFragment.this.polishText.getBackgroundColor());
                int blue = Color.blue(TextFragment.this.polishText.getBackgroundColor());
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setColor(Color.argb(TextFragment.this.polishText.getBackgroundAlpha(), red, green, blue));
                gradientDrawable.setCornerRadius(SystemUtil.dpToPx(TextFragment.this.requireContext(), TextFragment.this.polishText.getBackgroundBorder()));
                TextFragment.this.text_view_preview_effect.setBackground(gradientDrawable);
            }
        });
        this.seekbar_text_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.11
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextFragment.this.textViewSeekBarSize.setText(String.valueOf(i));
                if (i < 15) {
                    i = 15;
                }
                TextFragment.this.text_view_preview_effect.setTextSize(i);
                TextFragment.this.polishText.setTextSize(i);
            }
        });
        this.seekbar_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.12
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextFragment.this.textViewSeekBarRadius.setText(String.valueOf(i));
                TextFragment.this.polishText.setBackgroundBorder(i);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius(SystemUtil.dpToPx(TextFragment.this.requireContext(), i));
                gradientDrawable.setColor(Color.argb(TextFragment.this.polishText.getBackgroundAlpha(), Color.red(TextFragment.this.polishText.getBackgroundColor()), Color.green(TextFragment.this.polishText.getBackgroundColor()), Color.blue(TextFragment.this.polishText.getBackgroundColor())));
                TextFragment.this.text_view_preview_effect.setBackground(gradientDrawable);
            }
        });
        if (PreferenceUtil.getKeyboard(requireContext()) > 0) {
            updateAddTextBottomToolbarHeight(PreferenceUtil.getKeyboard(getContext()));
        }
        initPreviewText();
    }

    public void initPreviewText() {
        if (this.polishText.isShowBackground()) {
            if (this.polishText.getBackgroundColor() != 0) {
                this.text_view_preview_effect.setBackgroundColor(this.polishText.getBackgroundColor());
            }
            if (this.polishText.getBackgroundAlpha() < 255) {
                this.text_view_preview_effect.setBackgroundColor(Color.argb(this.polishText.getBackgroundAlpha(), Color.red(this.polishText.getBackgroundColor()), Color.green(this.polishText.getBackgroundColor()), Color.blue(this.polishText.getBackgroundColor())));
            }
            if (this.polishText.getBackgroundBorder() > 0) {
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius(SystemUtil.dpToPx(requireContext(), this.polishText.getBackgroundBorder()));
                gradientDrawable.setColor(Color.argb(this.polishText.getBackgroundAlpha(), Color.red(this.polishText.getBackgroundColor()), Color.green(this.polishText.getBackgroundColor()), Color.blue(this.polishText.getBackgroundColor())));
                this.text_view_preview_effect.setBackground(gradientDrawable);
            }
        }
        if (this.polishText.getPaddingHeight() > 0) {
            TextView textView = this.text_view_preview_effect;
            textView.setPadding(textView.getPaddingLeft(), this.polishText.getPaddingHeight(), this.text_view_preview_effect.getPaddingRight(), this.polishText.getPaddingHeight());
            this.seekbar_height.setProgress(this.polishText.getPaddingHeight());
        }
        if (this.polishText.getPaddingWidth() > 0) {
            this.text_view_preview_effect.setPadding(this.polishText.getPaddingWidth(), this.text_view_preview_effect.getPaddingTop(), this.polishText.getPaddingWidth(), this.text_view_preview_effect.getPaddingBottom());
            this.seekbar_width.setProgress(this.polishText.getPaddingWidth());
        }
        if (this.polishText.getText() != null) {
            this.text_view_preview_effect.setText(this.polishText.getText());
            this.add_text_edit_text.setText(this.polishText.getText());
        }
        if (this.polishText.getTextShader() != null) {
            this.text_view_preview_effect.setLayerType(1, null);
            this.text_view_preview_effect.getPaint().setShader(this.polishText.getTextShader());
        }
        if (this.polishText.getTextAlign() == 4) {
            this.image_view_align_center.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_center_select));
        } else if (this.polishText.getTextAlign() == 3) {
            this.image_view_align_right.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_right));
        } else if (this.polishText.getTextAlign() == 2) {
            this.image_view_align_left.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_left));
        }
        this.text_view_preview_effect.setPadding(SystemUtil.dpToPx(getContext(), this.polishText.getPaddingWidth()), this.text_view_preview_effect.getPaddingTop(), SystemUtil.dpToPx(getContext(), this.polishText.getPaddingWidth()), this.text_view_preview_effect.getPaddingBottom());
        this.text_view_preview_effect.setTextColor(this.polishText.getTextColor());
        this.text_view_preview_effect.setTextAlignment(this.polishText.getTextAlign());
        this.text_view_preview_effect.setTextSize(this.polishText.getTextSize());
        FontFile.setFontByName(getContext(), this.text_view_preview_effect, this.polishText.getFontName());
        this.text_view_preview_effect.invalidate();
    }

    private void setDefaultStyleForEdittext() {
        this.add_text_edit_text.requestFocus();
        this.add_text_edit_text.setTextSize(20.0f);
        this.add_text_edit_text.setTextAlignment(4);
        this.add_text_edit_text.setTextColor(Color.parseColor(ColorAnimation.DEFAULT_SELECTED_COLOR));
    }

    private void initAddTextLayout() {
        this.textFunctions = getTextFunctions();
        this.image_view_keyboard.setOnClickListener(this);
        this.image_view_color.setOnClickListener(this);
        this.image_view_save_change.setOnClickListener(this);
        this.image_view_align_left.setOnClickListener(this);
        this.image_view_align_center.setOnClickListener(this);
        this.image_view_align_right.setOnClickListener(this);
        this.scroll_view_change_font_layout.setVisibility(View.GONE);
        this.seekbar_width.setProgress(this.polishText.getPaddingWidth());
    }

    @Override // com.gallery.photos.editphotovideo.adapters.TextColorAdapter.ColorListener
    public void onColorSelected(int i, TextColorAdapter.SquareView squareView) {
        if (squareView.isColor) {
            this.text_view_preview_effect.setTextColor(squareView.drawableId);
            this.polishText.setTextColor(squareView.drawableId);
            this.text_view_preview_effect.getPaint().setShader(null);
            this.polishText.setTextShader(null);
            return;
        }
        this.text_view_preview_effect.setTextColor(squareView.drawableId);
        this.polishText.setTextColor(squareView.drawableId);
        this.text_view_preview_effect.getPaint().setShader(null);
        this.polishText.setTextShader(null);
    }

    @Override // com.gallery.photos.editphotovideo.adapters.TextBackgroundAdapter.BackgroundColorListener
    public void onBackgroundColorSelected(int i, TextBackgroundAdapter.SquareView squareView) {
        if (squareView.isColor) {
            this.text_view_preview_effect.setBackgroundColor(squareView.drawableId);
            this.polishText.setBackgroundColor(squareView.drawableId);
            this.seekbar_radius.setEnabled(true);
            this.polishText.setShowBackground(true);
            if (!this.checkbox_background.isChecked()) {
                this.checkbox_background.setChecked(true);
            }
            int red = Color.red(this.polishText.getBackgroundColor());
            int green = Color.green(this.polishText.getBackgroundColor());
            int blue = Color.blue(this.polishText.getBackgroundColor());
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.argb(this.polishText.getBackgroundAlpha(), red, green, blue));
            gradientDrawable.setCornerRadius(SystemUtil.dpToPx(requireContext(), this.polishText.getBackgroundBorder()));
            this.text_view_preview_effect.setBackground(gradientDrawable);
            return;
        }
        this.text_view_preview_effect.setBackgroundColor(squareView.drawableId);
        this.polishText.setBackgroundColor(squareView.drawableId);
        this.seekbar_radius.setEnabled(true);
        this.polishText.setShowBackground(true);
        if (!this.checkbox_background.isChecked()) {
            this.checkbox_background.setChecked(true);
        }
        int red2 = Color.red(this.polishText.getBackgroundColor());
        int green2 = Color.green(this.polishText.getBackgroundColor());
        int blue2 = Color.blue(this.polishText.getBackgroundColor());
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.argb(this.polishText.getBackgroundAlpha(), red2, green2, blue2));
        gradientDrawable2.setCornerRadius(SystemUtil.dpToPx(requireContext(), this.polishText.getBackgroundBorder()));
        this.text_view_preview_effect.setBackground(gradientDrawable2);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ViewCompat.setOnApplyWindowInsetsListener(getDialog().getWindow().getDecorView(), new OnApplyWindowInsetsListener() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.13
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return ViewCompat.onApplyWindowInsets(TextFragment.this.getDialog().getWindow().getDecorView(), windowInsetsCompat.inset(windowInsetsCompat.getSystemWindowInsetLeft(), 0, windowInsetsCompat.getSystemWindowInsetRight(), windowInsetsCompat.getSystemWindowInsetBottom()));
            }
        });
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

    public void updateAddTextBottomToolbarHeight(final int i) {
        new Handler().post(new Runnable() { // from class: com.gallery.photos.editphotovideo.fragment.TextFragment.14
            @Override // java.lang.Runnable
            public void run() {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) TextFragment.this.linear_layout_edit_text_tools.getLayoutParams();
                layoutParams.bottomMargin = i;
                TextFragment.this.linear_layout_edit_text_tools.setLayoutParams(layoutParams);
                TextFragment.this.linear_layout_edit_text_tools.invalidate();
                TextFragment.this.scroll_view_change_font_layout.invalidate();
                Log.i("HIHIH", i + "");
            }
        });
    }

    public void setOnTextEditorListener(TextEditor textEditor) {
        this.textEditor = textEditor;
    }

    @SuppressLint("WrongConstant")
    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageViewAlignCenter) { /* 2131362253 */
            if (this.polishText.getTextAlign() == 2 || this.polishText.getTextAlign() == 3) {
                this.polishText.setTextAlign(4);
                this.image_view_align_center.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_center_select));
                this.image_view_align_left.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_left));
                this.image_view_align_right.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_right));
            }
            this.text_view_preview_effect.setTextAlignment(this.polishText.getTextAlign());
            this.text_view_preview_effect.setText(this.text_view_preview_effect.getText().toString().trim() + " ");
            TextView textView = this.text_view_preview_effect;
            textView.setText(textView.getText().toString().trim());
        } else if (id == R.id.image_view_keyboard) {
           /* this.image_view_keyboard.setColorFilter(getResources().getColor(R.color.hovercolor));
            this.image_view_color.setColorFilter(getResources().getColor(R.color.default_icon_color));
            toggleTextEditEditable(true);
            binding.addTextEditText.setVisibility(View.VISIBLE);
            binding.addTextEditText.requestFocus();
            this.scroll_view_change_font_layout.setVisibility(View.GONE);
            this.linear_layout_edit_text_tools.invalidate();
            this.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/

            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    view.getWindowVisibleDisplayFrame(r);

                    int screenHeight = view.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    // If more than 15% of the screen height is covered by keyboard
                    if (keypadHeight > screenHeight * 0.15) {
                        // Keyboard is visible
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                                linear_layout_edit_text_tools.getLayoutParams();
                        params.bottomMargin = keypadHeight;
                        linear_layout_edit_text_tools.setLayoutParams(params);
                    } else {
                        // Keyboard is hidden
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                                linear_layout_edit_text_tools.getLayoutParams();
                        params.bottomMargin = 0;
                        linear_layout_edit_text_tools.setLayoutParams(params);
                    }
                }
            });
            showKeyboard();
        } else if (id == R.id.imageViewAlignLeft) { /* 2131362254 */
            if (this.polishText.getTextAlign() == 3 || this.polishText.getTextAlign() == 4) {
                this.polishText.setTextAlign(2);
                this.image_view_align_left.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_left_select));
                this.image_view_align_center.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_center));
                this.image_view_align_right.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_right));
            }
            this.text_view_preview_effect.setTextAlignment(this.polishText.getTextAlign());
            this.text_view_preview_effect.setText(this.text_view_preview_effect.getText().toString().trim() + " ");
            TextView textView2 = this.text_view_preview_effect;
            textView2.setText(textView2.getText().toString().trim());
        } else if (id == R.id.imageViewAlignRight) { /* 2131362255 */
            if (this.polishText.getTextAlign() == 4 || this.polishText.getTextAlign() == 2) {
                this.polishText.setTextAlign(3);
                this.image_view_align_left.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_left));
                this.image_view_align_center.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_center));
                this.image_view_align_right.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_format_align_right_select));
            }
            this.text_view_preview_effect.setTextAlignment(this.polishText.getTextAlign());
            this.text_view_preview_effect.setText(this.text_view_preview_effect.getText().toString().trim() + " ");
            TextView textView3 = this.text_view_preview_effect;
            textView3.setText(textView3.getText().toString().trim());
        } else if (id == R.id.image_view_color) { /* 2131362348 */
            this.image_view_keyboard.setColorFilter(getResources().getColor(R.color.white));
            this.image_view_color.setColorFilter(getResources().getColor(R.color.mainColor));
            this.inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            this.scroll_view_change_font_layout.setVisibility(View.VISIBLE);
            toggleTextEditEditable(false);
            this.add_text_edit_text.setVisibility(View.GONE);
            this.seekbar_background_opacity.setProgress(255 - this.polishText.getBackgroundAlpha());
            this.seekbar_text_size.setProgress(this.polishText.getTextSize());
            this.seekbar_radius.setProgress(this.polishText.getBackgroundBorder());
            this.seekbar_width.setProgress(this.polishText.getPaddingWidth());
            this.seekbar_height.setProgress(this.polishText.getPaddingHeight());
            this.textColorOpacity.setProgress(255 - this.polishText.getTextAlpha());
            this.shadowAdapter.setSelectedItem(this.polishText.getFontIndex());
            this.fontAdapter.setSelectedItem(this.polishText.getFontIndex());
            this.checkbox_background.setChecked(this.polishText.isShowBackground());
            this.checkbox_background.setChecked(this.polishText.isShowBackground());
        } else if (id == R.id.image_view_keyboard) { /* 2131362354 */
            this.image_view_keyboard.setColorFilter(getResources().getColor(R.color.mainColor));
            this.image_view_color.setColorFilter(getResources().getColor(R.color.white));
            toggleTextEditEditable(true);
            this.add_text_edit_text.setVisibility(View.VISIBLE);
            this.add_text_edit_text.requestFocus();
            this.scroll_view_change_font_layout.setVisibility(View.GONE);
            this.linear_layout_edit_text_tools.invalidate();
            this.inputMethodManager.toggleSoftInput(2, 0);
        } else if (id == R.id.image_view_save_change) { /* 2131362359 */
            if (this.polishText.getText() == null || this.polishText.getText().length() == 0) {
                this.inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                this.textEditor.onBackButton();
                dismiss();
            } else {
                this.polishText.setTextWidth(this.text_view_preview_effect.getMeasuredWidth());
                this.polishText.setTextHeight(this.text_view_preview_effect.getMeasuredHeight());
                this.inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                this.textEditor.onDone(this.polishText);
                dismiss();
            }
        }
    }

    private void toggleTextEditEditable(boolean z) {
        this.add_text_edit_text.setFocusable(z);
        this.add_text_edit_text.setFocusableInTouchMode(z);
        this.add_text_edit_text.setClickable(z);
    }

    private void showKeyboard() {

        this.image_view_keyboard.setColorFilter(getResources().getColor(R.color.appcolor));
        this.image_view_color.setColorFilter(getResources().getColor(R.color.iconColor));
        toggleTextEditEditable(true);
        add_text_edit_text.setVisibility(View.VISIBLE);
        add_text_edit_text.requestFocus();
        this.scroll_view_change_font_layout.setVisibility(View.GONE);
        this.linear_layout_edit_text_tools.invalidate();
        // Show keyboard without forcing layout changes
        add_text_edit_text.postDelayed(() -> {
            inputMethodManager.showSoftInput(add_text_edit_text, InputMethodManager.SHOW_IMPLICIT);
        }, 100);
    }

    private List<ImageView> getTextFunctions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.image_view_keyboard);
        arrayList.add(this.image_view_color);
        arrayList.add(this.image_view_save_change);
        return arrayList;
    }
}
