package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.constants.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.StickerFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.sticker.SplashSticker;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;

import java.util.ArrayList;
import java.util.List;

public class SquareAdapter extends RecyclerView.Adapter<SquareAdapter.ViewHolder> {
    private final int borderWidth;
    private final Context context;
    private final SplashChangeListener splashChangeListener;
    private final List<SplashItem> splashList = new ArrayList<>();
    private int selectedSquareIndex = -1;

    // Memory optimization
    private final BitmapFactory.Options bitmapOptions;
    private final LruCache<String, Bitmap> bitmapCache;
    private final int maxCacheSize;

    public interface SplashChangeListener {
        void onSelected(SplashSticker splashSticker);
    }

    public SquareAdapter(Context context, SplashChangeListener splashChangeListener, boolean z) {
        this.context = context;
        this.splashChangeListener = splashChangeListener;
        this.borderWidth = SystemUtil.dpToPx(context, Constants.BORDER_WIDTH);

        // Calculate cache size (1/8th of available memory)
        this.maxCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
        this.bitmapCache = new LruCache<String, Bitmap>(maxCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        // Configure bitmap loading options
        this.bitmapOptions = new BitmapFactory.Options();
        this.bitmapOptions.inSampleSize = 2; // Downsample by 2x
        this.bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565; // Use less memory
        this.bitmapOptions.inJustDecodeBounds = false;
        this.bitmapOptions.inPurgeable = true;
        this.bitmapOptions.inInputShareable = true;

        // Load splash items with memory optimization
        loadSplashItems();
    }

    private void loadSplashItems() {
        try {
            // Load items in batches to avoid memory spikes
            loadItemBatch(1, 5);
            loadItemBatch(6, 10);
            loadItemBatch(11, 15);
        } catch (OutOfMemoryError e) {
            Log.e("SquareAdapter", "Memory error loading splash items", e);
            // Clear cache and try with smaller bitmaps
            bitmapCache.evictAll();
            loadMinimalSplashItems();
        }
    }

    private void loadItemBatch(int start, int end) {
        for (int i = start; i <= end; i++) {
            String maskPath = "blur/image_mask_" + i + ".webp";
            String framePath = "blur/image_frame_" + i + ".webp";
            int drawableId = context.getResources().getIdentifier(
                    "image_mask_" + i, "drawable", context.getPackageName());

            addSplashItem(maskPath, framePath, drawableId);
        }
    }

    private void loadMinimalSplashItems() {
        // Load only essential items with higher downsampling
        bitmapOptions.inSampleSize = 4;
        try {
            addSplashItem("blur/image_mask_1.webp", "blur/image_frame_1.webp", R.drawable.image_mask_1);
            addSplashItem("blur/image_mask_2.webp", "blur/image_frame_2.webp", R.drawable.image_mask_2);
            addSplashItem("blur/image_mask_3.webp", "blur/image_frame_3.webp", R.drawable.image_mask_3);
        } catch (OutOfMemoryError e) {
            Log.e("SquareAdapter", "Critical memory error", e);
        }
    }

    private void addSplashItem(String maskPath, String framePath, int drawableId) {
        try {
            Bitmap mask = getCachedBitmap(maskPath);
            Bitmap frame = getCachedBitmap(framePath);

            if (mask != null && frame != null) {
                splashList.add(new SplashItem(new SplashSticker(mask, frame), drawableId));
            }
        } catch (OutOfMemoryError e) {
            Log.e("SquareAdapter", "Memory error loading: " + maskPath, e);
            // Free up memory and retry with higher downsampling
            System.gc();
            bitmapOptions.inSampleSize *= 2;
            addSplashItem(maskPath, framePath, drawableId);
        }
    }

    private Bitmap getCachedBitmap(String assetPath) {
        // Check cache first
        Bitmap cached = bitmapCache.get(assetPath);
        if (cached != null && !cached.isRecycled()) {
            return cached;
        }

        // Load from assets with memory optimization
        Bitmap bitmap = StickerFile.loadBitmapFromAssets(context, assetPath);
        if (bitmap != null) {
            bitmapCache.put(assetPath, bitmap);
        }
        return bitmap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_splash, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SplashItem item = splashList.get(position);

        // Load drawable directly instead of bitmap to save memory
        holder.splash.setImageResource(item.drawableId);

        if (selectedSquareIndex == position) {
            holder.relativeLayoutImage.setBackgroundResource(R.drawable.background_item_selected);
            holder.splash.setColorFilter(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.relativeLayoutImage.setBackgroundResource(R.drawable.background_item);
            holder.splash.setColorFilter(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return splashList.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        // Clear image when view is recycled
        holder.splash.setImageDrawable(null);
    }

    public void clearCache() {
        bitmapCache.evictAll();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView splash;
        final RelativeLayout relativeLayoutImage;

        ViewHolder(View itemView) {
            super(itemView);
            splash = itemView.findViewById(R.id.round_image_view_splash_item);
            relativeLayoutImage = itemView.findViewById(R.id.relativeLayoutImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            selectedSquareIndex = position;
            notifyDataSetChanged();

            SplashItem item = splashList.get(position);
            splashChangeListener.onSelected(item.splashSticker);
        }
    }

    static class SplashItem {
        final SplashSticker splashSticker;
        final int drawableId;

        SplashItem(SplashSticker splashSticker, int drawableId) {
            this.splashSticker = splashSticker;
            this.drawableId = drawableId;
        }
    }
}