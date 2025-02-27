package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.resource.BrushColor;
import com.gallery.photos.editpic.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ColorRatioAdapter extends RecyclerView.Adapter<ColorRatioAdapter.ViewHolder> {
    public BackgroundColorListener backgroundInstaListener;
    private Context context;
    public int selectedSquareIndex;
    public List<SquareView> squareViews;

    public interface BackgroundColorListener {
        void onBackgroundColorSelected(int i, SquareView squareView);
    }

    public ColorRatioAdapter(Context context, BackgroundColorListener backgroundColorListener) {
        ArrayList arrayList = new ArrayList();
        this.squareViews = arrayList;
        this.context = context;
        this.backgroundInstaListener = backgroundColorListener;
        arrayList.add(new SquareView(R.drawable.none, "None"));
        List<String> listColorBrush = BrushColor.listColorBrush();
        for (int i = 0; i < listColorBrush.size() - 2; i++) {
            this.squareViews.add(new SquareView(Color.parseColor(listColorBrush.get(i)), "", true));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color_radtio, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        SquareView squareView = this.squareViews.get(i);
        if (squareView.isColor) {
            viewHolder.squareView.setBackgroundColor(squareView.drawableId);
            viewHolder.imageViewSelected.setBackgroundColor(squareView.drawableId);
        } else {
            viewHolder.squareView.setBackgroundResource(squareView.drawableId);
            viewHolder.imageViewSelected.setBackgroundResource(squareView.drawableId);
        }
        if (this.selectedSquareIndex == i) {
            viewHolder.imageViewSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewSelected.setVisibility(View.GONE);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.squareViews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View imageViewSelected;
        public View squareView;
        public RelativeLayout wrapSquareView;

        public ViewHolder(View view) {
            super(view);
            this.squareView = view.findViewById(R.id.square_view);
            this.imageViewSelected = view.findViewById(R.id.imageSelection);
            this.wrapSquareView = (RelativeLayout) view.findViewById(R.id.filterRoot);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ColorRatioAdapter.this.selectedSquareIndex = getAdapterPosition();
            ColorRatioAdapter.this.backgroundInstaListener.onBackgroundColorSelected(ColorRatioAdapter.this.selectedSquareIndex, ColorRatioAdapter.this.squareViews.get(ColorRatioAdapter.this.selectedSquareIndex));
            ColorRatioAdapter.this.notifyDataSetChanged();
        }
    }

    public class SquareView {
        public int drawableId;
        public boolean isColor;
        public String text;

        SquareView(int i, String str) {
            this.drawableId = i;
            this.text = str;
        }

        SquareView(int i, String str, boolean z) {
            this.drawableId = i;
            this.text = str;
            this.isColor = z;
        }
    }
}
