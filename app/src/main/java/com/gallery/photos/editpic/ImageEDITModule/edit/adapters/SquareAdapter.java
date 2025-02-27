package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.constants.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.StickerFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.SplashSticker;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SquareAdapter extends RecyclerView.Adapter<SquareAdapter.ViewHolder> {
    private int borderWidth;
    private Context context;
    public int selectedSquareIndex;
    public SplashChangeListener splashChangeListener;
    public List<SplashItem> splashList = new ArrayList();

    public interface SplashChangeListener {
        void onSelected(SplashSticker splashSticker);
    }

    public SquareAdapter(Context context, SplashChangeListener splashChangeListener, boolean z) {
        this.context = context;
        this.splashChangeListener = splashChangeListener;
        this.borderWidth = SystemUtil.dpToPx(context, Constants.BORDER_WIDTH);
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_1.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_1.webp")), R.drawable.image_mask_1));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_2.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_2.webp")), R.drawable.image_mask_2));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_3.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_3.webp")), R.drawable.image_mask_3));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_4.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_4.webp")), R.drawable.image_mask_4));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_5.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_5.webp")), R.drawable.image_mask_5));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_6.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_6.webp")), R.drawable.image_mask_6));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_7.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_7.webp")), R.drawable.image_mask_7));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_8.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_8.webp")), R.drawable.image_mask_8));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_9.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_9.webp")), R.drawable.image_mask_9));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_10.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_10.webp")), R.drawable.image_mask_10));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_11.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_11.webp")), R.drawable.image_mask_11));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_12.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_12.webp")), R.drawable.image_mask_12));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_13.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_13.webp")), R.drawable.image_mask_13));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_14.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_14.webp")), R.drawable.image_mask_14));
        this.splashList.add(new SplashItem(new SplashSticker(StickerFile.loadBitmapFromAssets(context, "blur/image_mask_15.webp"), StickerFile.loadBitmapFromAssets(context, "blur/image_frame_15.webp")), R.drawable.image_mask_15));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_splash, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.splash.setImageResource(this.splashList.get(i).drawableId);
        if (this.selectedSquareIndex == i) {
            viewHolder.relativeLayoutImage.setBackgroundResource(R.drawable.background_item_selected);
            viewHolder.splash.setColorFilter(this.context.getResources().getColor(R.color.white));
        } else {
            viewHolder.relativeLayoutImage.setBackgroundResource(R.drawable.background_item);
            viewHolder.splash.setColorFilter(this.context.getResources().getColor(R.color.black));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.splashList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout relativeLayoutImage;
        public ImageView splash;

        public ViewHolder(View view) {
            super(view);
            this.splash = (ImageView) view.findViewById(R.id.round_image_view_splash_item);
            this.relativeLayoutImage = (RelativeLayout) view.findViewById(R.id.relativeLayoutImage);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
         /*   SquareAdapter.this.selectedSquareIndex = getAdapterPosition();
            if (SquareAdapter.this.selectedSquareIndex < 0) {
                SquareAdapter.this.selectedSquareIndex = 0;
            }
            if (SquareAdapter.this.selectedSquareIndex >= SquareAdapter.this.splashList.size()) {
                SquareAdapter.this.selectedSquareIndex = r3.splashList.size() - 1;
            }
            SquareAdapter.this.splashChangeListener.onSelected(SquareAdapter.this.splashList.get(SquareAdapter.this.selectedSquareIndex).splashSticker);
            SquareAdapter.this.notifyDataSetChanged();*/

            SquareAdapter.this.selectedSquareIndex = this.getAdapterPosition();
            if (SquareAdapter.this.selectedSquareIndex < 0) {
                SquareAdapter.this.selectedSquareIndex = 0;
            }

            if (SquareAdapter.this.selectedSquareIndex >= SquareAdapter.this.splashList.size()) {
                SquareAdapter var2 = SquareAdapter.this;
                var2.selectedSquareIndex = var2.splashList.size() - 1;
            }

            SquareAdapter.this.splashChangeListener.onSelected(((SplashItem)SquareAdapter.this.splashList.get(SquareAdapter.this.selectedSquareIndex)).splashSticker);
            SquareAdapter.this.notifyDataSetChanged();


        }
    }

    static class SplashItem {
        int drawableId;
        SplashSticker splashSticker;

        SplashItem(SplashSticker splashSticker, int i) {
            this.splashSticker = splashSticker;
            this.drawableId = i;
        }
    }
}
