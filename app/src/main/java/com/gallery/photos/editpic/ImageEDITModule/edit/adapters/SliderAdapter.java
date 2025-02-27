package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.gallery.photos.editpic.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

/* loaded from: classes.dex */
public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    int[] images;

    public SliderAdapter(int[] iArr) {
        this.images = iArr;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.smarteist.autoimageslider.SliderViewAdapter
    public Holder onCreateViewHolder(ViewGroup viewGroup) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_slider, viewGroup, false));
    }

    @Override // com.smarteist.autoimageslider.SliderViewAdapter
    public void onBindViewHolder(Holder holder, int i) {
        holder.imageView.setImageResource(this.images[i]);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.images.length;
    }

    public class Holder extends ViewHolder {
        ImageView imageView;

        public Holder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.image_view);
        }
    }
}
