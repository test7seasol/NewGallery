package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.constants.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.OverlayListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.OverlayFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.R;
import java.util.List;

/* loaded from: classes.dex */
public class OverlayAdapter extends RecyclerView.Adapter<OverlayAdapter.ViewHolder> {
    private List<Bitmap> bitmaps;
    private int borderWidth;
    private Context context;
    public List<OverlayFile.OverlayCode> filterBeanList;
    public OverlayListener overlayListener;
    public int selectedFilterIndex = 0;

    public OverlayAdapter(List<Bitmap> list, OverlayListener overlayListener, Context context, List<OverlayFile.OverlayCode> list2) {
        this.overlayListener = overlayListener;
        this.bitmaps = list;
        this.context = context;
        this.filterBeanList = list2;
        this.borderWidth = SystemUtil.dpToPx(context, Constants.BORDER_WIDTH);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_overlay, viewGroup, false));
    }

    public void reset() {
        this.selectedFilterIndex = 0;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.round_image_view_filter_item.setImageBitmap(this.bitmaps.get(i));
        if (this.selectedFilterIndex == i) {
            viewHolder.viewSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.viewSelected.setVisibility(View.GONE);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.bitmaps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relative_layout_wrapper_filter_item;
        ImageView round_image_view_filter_item;
        View viewSelected;

        ViewHolder(View view) {
            super(view);
            this.round_image_view_filter_item = (ImageView) view.findViewById(R.id.round_image_view_filter_item);
            this.relative_layout_wrapper_filter_item = (RelativeLayout) view.findViewById(R.id.relative_layout_wrapper_filter_item);
            this.viewSelected = view.findViewById(R.id.viewSelected);
            view.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.adapters.OverlayAdapter.ViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    OverlayAdapter.this.selectedFilterIndex = ViewHolder.this.getLayoutPosition();
                    OverlayAdapter.this.overlayListener.onOverlaySelected(OverlayAdapter.this.selectedFilterIndex, OverlayAdapter.this.filterBeanList.get(OverlayAdapter.this.selectedFilterIndex).getImage());
                    OverlayAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
