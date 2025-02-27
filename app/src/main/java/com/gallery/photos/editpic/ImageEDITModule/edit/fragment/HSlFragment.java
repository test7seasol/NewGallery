package com.gallery.photos.editpic.ImageEDITModule.edit.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import com.gallery.photos.editpic.R;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import org.wysaid.nativePort.CGENativeLibrary;

/* loaded from: classes.dex */
public class HSlFragment extends DialogFragment {
    private static final String TAG = "HSlFragment";
    private Bitmap bitmap;
    private RadioGroup colorselection;
    public ImageView image_view_ratio;
    public OnFilterSavePhoto onFilterSavePhoto;
    private IndicatorSeekBar seekbarIntensityHue;
    private IndicatorSeekBar seekbarIntensityLightness;
    private IndicatorSeekBar seekbarIntensitySaturation;
    private Bitmap tempbitmap;
    private Bitmap tempbitmap2;

    public interface OnFilterSavePhoto {
        void onSaveFilter(Bitmap bitmap);
    }

    public void setOnFilterSavePhoto(OnFilterSavePhoto onFilterSavePhoto) {
        this.onFilterSavePhoto = onFilterSavePhoto;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static HSlFragment show(AppCompatActivity appCompatActivity, OnFilterSavePhoto onFilterSavePhoto, Bitmap bitmap) {
        HSlFragment hSlFragment = new HSlFragment();
        hSlFragment.setBitmap(bitmap);
        hSlFragment.setOnFilterSavePhoto(onFilterSavePhoto);
        hSlFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return hSlFragment;
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
        View inflate = layoutInflater.inflate(R.layout.fragment_hsl, viewGroup, false);
        this.seekbarIntensityHue = (IndicatorSeekBar) inflate.findViewById(R.id.hue);
        this.seekbarIntensitySaturation = (IndicatorSeekBar) inflate.findViewById(R.id.sat);
        this.seekbarIntensityLightness = (IndicatorSeekBar) inflate.findViewById(R.id.light);
        this.colorselection = (RadioGroup) inflate.findViewById(R.id.colorselection);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.imageViewRatio);
        this.image_view_ratio = imageView;
        imageView.setImageBitmap(this.bitmap);
        inflate.findViewById(R.id.imageViewCloseRatio).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.HSlFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HSlFragment.this.dismiss();
            }
        });
        inflate.findViewById(R.id.imageViewSaveRatio).setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.fragment.HSlFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HSlFragment.this.onFilterSavePhoto.onSaveFilter(((BitmapDrawable) HSlFragment.this.image_view_ratio.getDrawable()).getBitmap());
                HSlFragment.this.dismiss();
            }
        });
        this.seekbarIntensityHue.setOnSeekChangeListener(new OnSeekChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.HSlFragment.3
            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onSeeking(SeekParams seekParams) {
                String str;
                if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.red) {
                    if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.green) {
                        if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.blue) {
                            if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.mergenta) {
                                if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.yellow) {
                                    if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.cyan) {
                                        if (HSlFragment.this.colorselection.getCheckedRadioButtonId() == R.id.white) {
                                            str = "@selcolor white(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                        } else {
                                            str = "";
                                        }
                                    } else {
                                        str = "@selcolor cyan(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                    }
                                } else {
                                    str = "@selcolor yellow(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                }
                            } else {
                                str = "@selcolor magenta(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                            }
                        } else {
                            str = "@selcolor blue(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                        }
                    } else {
                        str = "@selcolor green(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                    }
                } else {
                    str = "@selcolor red(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                }
                if (HSlFragment.this.tempbitmap == null) {
                    HSlFragment hSlFragment = HSlFragment.this;
                    hSlFragment.tempbitmap = hSlFragment.getBitmapFromView(hSlFragment.image_view_ratio);
                }
                HSlFragment hSlFragment2 = HSlFragment.this;
                hSlFragment2.tempbitmap2 = CGENativeLibrary.filterImage_MultipleEffects(hSlFragment2.tempbitmap, str, HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 200.0f);
                HSlFragment.this.image_view_ratio.setImageBitmap(HSlFragment.this.tempbitmap2);
            }
        });
        this.seekbarIntensitySaturation.setOnSeekChangeListener(new OnSeekChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.HSlFragment.4
            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onSeeking(SeekParams seekParams) {
                String str;
                if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.red) {
                    if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.green) {
                        if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.blue) {
                            if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.mergenta) {
                                if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.yellow) {
                                    if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.cyan) {
                                        if (HSlFragment.this.colorselection.getCheckedRadioButtonId() == R.id.white) {
                                            str = "@selcolor white(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                        } else {
                                            str = "";
                                        }
                                    } else {
                                        str = "@selcolor cyan(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                    }
                                } else {
                                    str = "@selcolor yellow(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                }
                            } else {
                                str = "@selcolor magenta(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                            }
                        } else {
                            str = "@selcolor blue(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                        }
                    } else {
                        str = "@selcolor green(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                    }
                } else {
                    str = "@selcolor red(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                }
                if (HSlFragment.this.tempbitmap == null) {
                    HSlFragment hSlFragment = HSlFragment.this;
                    hSlFragment.tempbitmap = hSlFragment.getBitmapFromView(hSlFragment.image_view_ratio);
                }
                HSlFragment hSlFragment2 = HSlFragment.this;
                hSlFragment2.tempbitmap2 = CGENativeLibrary.filterImage_MultipleEffects(hSlFragment2.tempbitmap, str, HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 200.0f);
                HSlFragment.this.image_view_ratio.setImageBitmap(HSlFragment.this.tempbitmap2);
            }
        });
        this.colorselection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.HSlFragment.5
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                HSlFragment.this.tempbitmap = null;
            }
        });
        this.seekbarIntensityLightness.setOnSeekChangeListener(new OnSeekChangeListener() { // from class: com.gallery.photos.editphotovideo.fragment.HSlFragment.6
            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override // com.warkiz.widget.OnSeekChangeListener
            public void onSeeking(SeekParams seekParams) {
                String str;
                if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.red) {
                    if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.green) {
                        if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.blue) {
                            if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.mergenta) {
                                if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.yellow) {
                                    if (HSlFragment.this.colorselection.getCheckedRadioButtonId() != R.id.cyan) {
                                        if (HSlFragment.this.colorselection.getCheckedRadioButtonId() == R.id.white) {
                                            str = "@selcolor white(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                        } else {
                                            str = "";
                                        }
                                    } else {
                                        str = "@selcolor cyan(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                    }
                                } else {
                                    str = "@selcolor yellow(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                                }
                            } else {
                                str = "@selcolor magenta(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                            }
                        } else {
                            str = "@selcolor blue(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                        }
                    } else {
                        str = "@selcolor green(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                    }
                } else {
                    str = "@selcolor red(" + (HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensitySaturation.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + "," + (HSlFragment.this.seekbarIntensityLightness.getProgressFloat() / 100.0f) + ")";
                }
                if (HSlFragment.this.tempbitmap == null) {
                    HSlFragment hSlFragment = HSlFragment.this;
                    hSlFragment.tempbitmap = hSlFragment.getBitmapFromView(hSlFragment.image_view_ratio);
                }
                HSlFragment hSlFragment2 = HSlFragment.this;
                hSlFragment2.tempbitmap2 = CGENativeLibrary.filterImage_MultipleEffects(hSlFragment2.tempbitmap, str, HSlFragment.this.seekbarIntensityHue.getProgressFloat() / 200.0f);
                HSlFragment.this.image_view_ratio.setImageBitmap(HSlFragment.this.tempbitmap2);
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

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.bitmap = null;
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(createBitmap));
        return createBitmap;
    }
}
