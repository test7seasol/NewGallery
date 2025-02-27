package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.resource.StickerFile;
import com.gallery.photos.editpic.R;
import com.bumptech.glide.Glide;
import java.util.List;

/* loaded from: classes.dex */
public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {
    public Context context;
    public int screenWidth;
    public OnClickStickerListener stickerListener;
    public List<String> stickers;

    public interface OnClickStickerListener {
        void addSticker(int i, Bitmap bitmap);
    }

    public StickerAdapter(Context context, List<String> list, int i, OnClickStickerListener onClickStickerListener) {
        this.context = context;
        this.stickers = list;
        this.screenWidth = i;
        this.stickerListener = onClickStickerListener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_sticker, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Glide.with(this.context).load(StickerFile.loadBitmapFromAssets(this.context, this.stickers.get(i))).into(viewHolder.sticker);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.stickers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView sticker;

        public ViewHolder(View view) {
            super(view);
            this.sticker = (ImageView) view.findViewById(R.id.image_view_item_sticker);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            StickerAdapter.this.stickerListener.addSticker(getAdapterPosition(), StickerFile.loadBitmapFromAssets(StickerAdapter.this.context, StickerAdapter.this.stickers.get(getAdapterPosition())));
        }
    }
}
