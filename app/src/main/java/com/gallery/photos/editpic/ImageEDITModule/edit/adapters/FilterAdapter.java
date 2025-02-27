package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.constants.Constants;
import com.gallery.photos.editpic.ImageEDITModule.edit.listener.FilterListener;
import com.gallery.photos.editpic.ImageEDITModule.edit.resource.FilterFile;
import com.gallery.photos.editpic.ImageEDITModule.edit.utils.SystemUtil;
import com.gallery.photos.editpic.ImageEDITModule.github.siyamed.shapeimageview.RoundedImageView;
import com.gallery.photos.editpic.R;

import java.util.List;

/* loaded from: classes.dex */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private List<Bitmap> bitmaps;
    private int borderWidth;
    private Context context;
    public List<FilterFile.FiltersCode> filterBeanList;
    public FilterListener filterListener;
    public int selectedFilterIndex = 0;

    public FilterAdapter(List<Bitmap> list, FilterListener filterListener, Context context, List<FilterFile.FiltersCode> list2) {
        this.filterListener = filterListener;
        this.bitmaps = list;
        this.context = context;
        this.filterBeanList = list2;
        this.borderWidth = SystemUtil.dpToPx(context, Constants.BORDER_WIDTH);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter, viewGroup, false));
    }

    public void reset() {
        this.selectedFilterIndex = 0;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.round_image_view_filter_item.setImageBitmap(this.bitmaps.get(i));
        viewHolder.textViewTitle.setText(this.filterBeanList.get(i).getName());
        viewHolder.textViewTitle.setBackgroundColor(this.filterBeanList.get(i).getColor());
        viewHolder.viewSelected.setBackgroundColor(this.filterBeanList.get(i).getColor());
        if (this.filterBeanList.get(i).isLastItem()) {
            viewHolder.viewSpace.setVisibility(View.VISIBLE);
        } else {
            viewHolder.viewSpace.setVisibility(View.GONE);
        }
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
        RoundedImageView round_image_view_filter_item;
        TextView textViewTitle;
        View viewSelected;
        View viewSpace;

        ViewHolder(View view) {
            super(view);
            this.round_image_view_filter_item = (RoundedImageView) view.findViewById(R.id.round_image_view_filter_item);
            this.relative_layout_wrapper_filter_item = (RelativeLayout) view.findViewById(R.id.relative_layout_wrapper_filter_item);
            this.viewSpace = view.findViewById(R.id.viewSpace);
            this.viewSelected = view.findViewById(R.id.viewSelected);
            this.textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            view.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.adapters.FilterAdapter.ViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    FilterAdapter.this.selectedFilterIndex = ViewHolder.this.getLayoutPosition();
                    FilterAdapter.this.filterListener.onFilterSelected(FilterAdapter.this.selectedFilterIndex, FilterAdapter.this.filterBeanList.get(FilterAdapter.this.selectedFilterIndex).getCode());
                    FilterAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
