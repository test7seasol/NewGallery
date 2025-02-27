package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.resource.BrushColor;
import com.gallery.photos.editpic.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TextColorAdapter extends RecyclerView.Adapter<TextColorAdapter.ViewHolder> {
    public ColorListener colorListener;
    private Context context;
    public int selectedSquareIndex;
    public List<SquareView> squareViewList = new ArrayList();

    public interface ColorListener {
        void onColorSelected(int i, SquareView squareView);
    }

    public TextColorAdapter(Context context, ColorListener colorListener) {
        this.context = context;
        this.colorListener = colorListener;
        List<String> listColorBrush = BrushColor.listColorBrush();
        for (int i = 0; i < listColorBrush.size() - 2; i++) {
            this.squareViewList.add(new SquareView(Color.parseColor(listColorBrush.get(i)), true));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color_radtio, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        SquareView squareView = this.squareViewList.get(i);
        if (squareView.isColor) {
            viewHolder.squareView.setBackgroundColor(squareView.drawableId);
        } else {
            viewHolder.squareView.setBackgroundColor(squareView.drawableId);
        }
        if (this.selectedSquareIndex == i) {
            viewHolder.viewSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.viewSelected.setVisibility(View.GONE);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.squareViewList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View squareView;
        ImageView viewSelected;
        public RelativeLayout wrapSquareView;

        public ViewHolder(View view) {
            super(view);
            this.squareView = view.findViewById(R.id.square_view);
            this.viewSelected = (ImageView) view.findViewById(R.id.imageSelection);
            this.wrapSquareView = (RelativeLayout) view.findViewById(R.id.filterRoot);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TextColorAdapter.this.selectedSquareIndex = getAdapterPosition();
            TextColorAdapter.this.colorListener.onColorSelected(TextColorAdapter.this.selectedSquareIndex, TextColorAdapter.this.squareViewList.get(TextColorAdapter.this.selectedSquareIndex));
            TextColorAdapter.this.notifyDataSetChanged();
        }
    }

    public void setSelectedColorIndex(int i) {
        this.selectedSquareIndex = i;
    }

    public class SquareView {
        public int drawableId;
        public boolean isColor;

        SquareView(int i) {
            this.drawableId = i;
        }

        SquareView(int i, boolean z) {
            this.drawableId = i;
            this.isColor = z;
        }
    }
}
