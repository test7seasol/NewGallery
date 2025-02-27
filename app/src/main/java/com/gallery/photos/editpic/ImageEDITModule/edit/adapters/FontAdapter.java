package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.resource.FontFile;
import com.gallery.photos.editpic.R;
import java.util.List;

/* loaded from: classes.dex */
public class FontAdapter extends RecyclerView.Adapter<FontAdapter.ViewHolder> {
    private Context context;
    private List<String> lstFonts;
    public ItemClickListener mClickListener;
    private LayoutInflater mInflater;
    public int selectedItem = 0;

    public interface ItemClickListener {
        void onItemClick(View view, int i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return i;
    }

    public FontAdapter(Context context, List<String> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.lstFonts = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(this.mInflater.inflate(R.layout.item_font, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        FontFile.setFontByName(this.context, viewHolder.font, this.lstFonts.get(i));
        viewHolder.wrapperFontItems.setBackground(ContextCompat.getDrawable(this.context, this.selectedItem != i ? R.drawable.border_view : R.drawable.border_black_view));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.lstFonts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView font;
        ConstraintLayout wrapperFontItems;

        ViewHolder(View view) {
            super(view);
            this.font = (TextView) view.findViewById(R.id.text_view_font_item);
            this.wrapperFontItems = (ConstraintLayout) view.findViewById(R.id.constraint_layout_wrapper_font_item);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            FontAdapter.this.selectedItem = getAdapterPosition();
            if (FontAdapter.this.mClickListener != null) {
                FontAdapter.this.mClickListener.onItemClick(view, FontAdapter.this.selectedItem);
            }
            FontAdapter.this.notifyDataSetChanged();
        }
    }

    public void setSelectedItem(int i) {
        this.selectedItem = i;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
