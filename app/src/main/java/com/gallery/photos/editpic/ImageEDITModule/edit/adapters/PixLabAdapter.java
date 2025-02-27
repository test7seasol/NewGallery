package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.activities.BorderActivity;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.WingsItemListener;
import com.gallery.photos.editpic.R;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PixLabAdapter extends RecyclerView.Adapter<PixLabAdapter.ViewHolder> {
    public WingsItemListener clickListener;
    Context mContext;
    public int selectedPos = 0;
    private ArrayList<String> pixLabItemList = new ArrayList<>();

    public PixLabAdapter(Context context) {
        this.mContext = context;
    }

    public void addData(ArrayList<String> arrayList) {
        this.pixLabItemList.clear();
        this.pixLabItemList.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pixlab, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mSelectedBorder.setVisibility(i == this.selectedPos ? 0 : 8);
        Glide.with(this.mContext).load("file:///android_asset/pixlab/" + this.pixLabItemList.get(i) + ".webp").fitCenter().into(viewHolder.mIvImage);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.pixLabItemList.size();
    }

    public void setClickListener(BorderActivity borderActivity) {
        this.clickListener = borderActivity;
    }

    public ArrayList<String> getItemList() {
        return this.pixLabItemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mIvImage;
        View mSelectedBorder;

        ViewHolder(View view) {
            super(view);
            this.mIvImage = (ImageView) view.findViewById(R.id.imageViewItem);
            this.mSelectedBorder = view.findViewById(R.id.selectedBorder);
            view.setTag(view);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int i = PixLabAdapter.this.selectedPos;
            PixLabAdapter.this.selectedPos = getAdapterPosition();
            PixLabAdapter.this.notifyItemChanged(i);
            PixLabAdapter pixLabAdapter = PixLabAdapter.this;
            pixLabAdapter.notifyItemChanged(pixLabAdapter.selectedPos);
            PixLabAdapter.this.clickListener.onWingListClick(view, getAdapterPosition());
        }
    }
}
