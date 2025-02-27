package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.listener.LayoutItemListener;
import com.gallery.photos.editpic.R;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class BgAdapter extends RecyclerView.Adapter<BgAdapter.ViewHolder> {
    Context context;
    public LayoutItemListener menuItemClickLister;
    public int selectedItem = 0;
    private ArrayList<String> wingsIcons = new ArrayList<>();

    public BgAdapter(Context context) {
        this.context = context;
    }

    public void addData(ArrayList<String> arrayList) {
        this.wingsIcons.clear();
        this.wingsIcons.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mSelectedBorder.setVisibility(i == this.selectedItem ? 0 : 8);
        Glide.with(this.context).load("file:///android_asset/background/" + this.wingsIcons.get(i) + ".webp").fitCenter().into(viewHolder.imageViewItem1);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.wingsIcons.size();
    }

    public ArrayList<String> getItemList() {
        return this.wingsIcons;
    }

    public void setMenuItemClickLister(LayoutItemListener layoutItemListener) {
        this.menuItemClickLister = layoutItemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewItem1;
        View mSelectedBorder;

        ViewHolder(View view) {
            super(view);
            this.imageViewItem1 = (ImageView) view.findViewById(R.id.imageViewItem);
            this.mSelectedBorder = view.findViewById(R.id.selectedBorder);
            view.setTag(view);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int i = BgAdapter.this.selectedItem;
            BgAdapter.this.selectedItem = getAdapterPosition();
            BgAdapter.this.notifyItemChanged(i);
            BgAdapter bgAdapter = BgAdapter.this;
            bgAdapter.notifyItemChanged(bgAdapter.selectedItem);
            BgAdapter.this.menuItemClickLister.onLayoutListClick(view, getAdapterPosition());
        }
    }
}
