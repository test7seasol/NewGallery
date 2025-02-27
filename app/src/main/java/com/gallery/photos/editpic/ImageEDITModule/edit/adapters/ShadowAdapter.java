package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.gallery.photos.editpic.ImageEDITModule.edit.Editor.Text;
import com.gallery.photos.editpic.R;
import java.util.List;

/* loaded from: classes.dex */
public class ShadowAdapter extends RecyclerView.Adapter<ShadowAdapter.ViewHolder> {
    private Context context;
    private List<Text.TextShadow> lstTextShadows;
    public ShadowItemClickListener mClickListener;
    private LayoutInflater mInflater;
    public int selectedItem = 0;

    public interface ShadowItemClickListener {
        void onShadowItemClick(View view, int i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return i;
    }

    public ShadowAdapter(Context context, List<Text.TextShadow> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.lstTextShadows = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(this.mInflater.inflate(R.layout.item_font, viewGroup, false));
    }

  /*  @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.textShadow.setShadowLayer(r0.getRadius(), r0.getDx(), r0.getDy(), this.lstTextShadows.get(i).getColorShadow());
        viewHolder.wrapperFontItems.setBackground(ContextCompat.getDrawable(this.context, this.selectedItem != i ? R.drawable.border_view : R.drawable.border_black_view));
    }
*/
    public void onBindViewHolder(ViewHolder var1, int var2) {
        Text.TextShadow var3 = (Text.TextShadow)this.lstTextShadows.get(var2);
        var1.textShadow.setShadowLayer((float)var3.getRadius(), (float)var3.getDx(), (float)var3.getDy(), var3.getColorShadow());
        ConstraintLayout var4 = var1.wrapperFontItems;
        if (this.selectedItem != var2) {
            var2 = R.drawable.border_view;
        } else {
            var2 = R.drawable.border_black_view;
        }

        var4.setBackground(ContextCompat.getDrawable(this.context, var2));
    }


    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.lstTextShadows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textShadow;
        ConstraintLayout wrapperFontItems;

        ViewHolder(View view) {
            super(view);
            this.textShadow = (TextView) view.findViewById(R.id.text_view_font_item);
            this.wrapperFontItems = (ConstraintLayout) view.findViewById(R.id.constraint_layout_wrapper_font_item);
            view.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (ShadowAdapter.this.mClickListener != null) {
                ShadowAdapter.this.mClickListener.onShadowItemClick(view, getAdapterPosition());
            }
            ShadowAdapter.this.selectedItem = getAdapterPosition();
            ShadowAdapter.this.notifyDataSetChanged();
        }
    }

    public void setSelectedItem(int i) {
        this.selectedItem = i;
    }

    public void setClickListener(ShadowItemClickListener shadowItemClickListener) {
        this.mClickListener = shadowItemClickListener;
    }
}
