package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.PhotoEditor;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.AdjustListener;
import com.gallery.photos.editpic.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class AdjustAdapter extends RecyclerView.Adapter<AdjustAdapter.ViewHolder> {
    public AdjustListener adjustListener;
    public List<AdjustModel> adjustModelList;
    private Context context;
    public String ADJUST = " @adjust brightness 0 @adjust contrast 1 @adjust saturation 1 @adjust sharpen 0 @adjust exposure 0 @adjust hue 0 ";
    public int selectedFilterIndex = 0;

    public class AdjustModel {
        String code;
        Drawable icon;
        public int index;
        public float intensity;
        public float maxValue;
        public float minValue;
        public String name;
        public float originValue;
        public float seekbarIntensity = 0.5f;

        AdjustModel(int i, String str, String str2, Drawable drawable, float f, float f2, float f3) {
            this.index = i;
            this.name = str;
            this.code = str2;
            this.icon = drawable;
            this.minValue = f;
            this.originValue = f2;
            this.maxValue = f3;
        }

        public void setSeekBarIntensity(PhotoEditor photoEditor, float progress, boolean z) {
            if (photoEditor != null) {
                this.seekbarIntensity = progress;
                intensity = calcIntensity(progress);
                photoEditor.setFilterIntensityForIndex(intensity, this.index, z);
            }
        }

        public float calcIntensity(float progress) {
            // Ensure progress is between 0 and 1
            progress = Math.max(0f, Math.min(1f, progress));
            // Linear mapping from [0,1] to [minValue, maxValue]
            return minValue + (maxValue - minValue) * progress;
            // OR if you need a more complex mapping:
            // return minValue + (originValue - minValue)  progress  2f;  // For first half
            // return originValue + (maxValue - originValue)  (progress - 0.5f)  2f; // For second half
        }
    }

    public AdjustAdapter(Context context, AdjustListener adjustListener) {
        this.context = context;
        this.adjustListener = adjustListener;
        init();
    }

    public void setSelectedAdjust(int i) {
        this.adjustListener.onAdjustSelected(this.adjustModelList.get(i));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_adjust, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.text_view_adjust_name.setText(this.adjustModelList.get(i).name);
        ImageView imageView = viewHolder.image_view_adjust_icon;
        int i2 = this.selectedFilterIndex;
        imageView.setImageDrawable(this.adjustModelList.get(i).icon);
        if (this.selectedFilterIndex == i) {
            viewHolder.image_view_adjust_icon.setColorFilter(this.context.getResources().getColor(R.color.mainColor));
            viewHolder.text_view_adjust_name.setTextColor(this.context.getResources().getColor(R.color.mainColor));
        } else {
            viewHolder.image_view_adjust_icon.setColorFilter(this.context.getResources().getColor(R.color.iconColor));
            viewHolder.text_view_adjust_name.setTextColor(this.context.getResources().getColor(R.color.iconColor));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.adjustModelList.size();
    }

    public String getFilterConfig() {
        return MessageFormat.format(this.ADJUST, this.adjustModelList.get(0).originValue + "", this.adjustModelList.get(1).originValue + "", this.adjustModelList.get(2).originValue + "", this.adjustModelList.get(3).originValue + "", this.adjustModelList.get(4).originValue + "", Float.valueOf(this.adjustModelList.get(5).originValue));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view_adjust_icon;
        RelativeLayout relativeLayoutEdit;
        TextView text_view_adjust_name;

        ViewHolder(View view) {
            super(view);
            this.image_view_adjust_icon = (ImageView) view.findViewById(R.id.image_view_adjust_icon);
            this.text_view_adjust_name = (TextView) view.findViewById(R.id.text_view_adjust_name);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutEdit);
            this.relativeLayoutEdit = relativeLayout;
            relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.adapters.AdjustAdapter.ViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    AdjustAdapter.this.selectedFilterIndex = ViewHolder.this.getLayoutPosition();
                    AdjustAdapter.this.adjustListener.onAdjustSelected(AdjustAdapter.this.adjustModelList.get(AdjustAdapter.this.selectedFilterIndex));
                    AdjustAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    public AdjustModel getCurrentAdjustModel() {
        return this.adjustModelList.get(this.selectedFilterIndex);
    }

    private void init() {
        ArrayList arrayList = new ArrayList();
        this.adjustModelList = arrayList;
        arrayList.add(new AdjustModel(0, this.context.getString(R.string.brightness), "brightness", this.context.getDrawable(R.drawable.ic_brightness), -1.0f, 0.0f, 1.0f));
        this.adjustModelList.add(new AdjustModel(1, this.context.getString(R.string.contrast), "contrast", this.context.getDrawable(R.drawable.ic_contrast), 0.1f, 1.0f, 3.0f));
        this.adjustModelList.add(new AdjustModel(2, this.context.getString(R.string.saturation), "saturation", this.context.getDrawable(R.drawable.ic_saturation), 0.0f, 1.0f, 3.0f));
        this.adjustModelList.add(new AdjustModel(5, this.context.getString(R.string.hue), "hue", this.context.getDrawable(R.drawable.ic_hue), -2.0f, 0.0f, 2.0f));
        this.adjustModelList.add(new AdjustModel(3, this.context.getString(R.string.sharpen), "sharpen", this.context.getDrawable(R.drawable.ic_sharpen), -1.0f, 0.0f, 10.0f));
        this.adjustModelList.add(new AdjustModel(4, this.context.getString(R.string.exposure), "exposure", this.context.getDrawable(R.drawable.ic_exposure), -2.0f, 0.0f, 2.0f));
    }
}
