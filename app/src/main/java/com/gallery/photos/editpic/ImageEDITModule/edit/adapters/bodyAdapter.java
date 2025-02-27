package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.tools.bodyModel;
import com.gallery.photos.editpic.R;
import java.util.List;

/* loaded from: classes.dex */
public class bodyAdapter extends RecyclerView.Adapter<bodyAdapter.ViewHolder> {
    private Context context;
    private List<bodyModel> mInfoList;
    private OnItemClickListener onItemClickListener;
    private boolean selectedMode;
    private final int defaultTextColor = Color.parseColor("#FFFFFFFF");
    private int selected = 0;
    private final int selectedTextColor = Color.parseColor("#FFF82B34");

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public bodyAdapter(List<bodyModel> list, Context context) {
        this.mInfoList = list;
        this.context = context;
    }

    public void setSelectedMode(boolean z) {
        this.selectedMode = z;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView title;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.icon = (ImageView) view.findViewById(R.id.image_view_tool_icon);
            this.title = (TextView) view.findViewById(R.id.text_view_tool_name);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            bodyAdapter.this.selected = getAdapterPosition();
            if (bodyAdapter.this.selectedMode) {
                bodyAdapter.this.notifyDataSetChanged();
            }
            if (bodyAdapter.this.onItemClickListener != null) {
                bodyAdapter.this.onItemClickListener.onItemClick(bodyAdapter.this.selected);
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(View.inflate(this.context, R.layout.item_body, null));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        bodyModel bodymodel = this.mInfoList.get(i);
        viewHolder.icon.setImageResource(bodymodel.iconResource);
        viewHolder.title.setText(bodymodel.title);
        if (this.selectedMode) {
            viewHolder.icon.setSelected(i == this.selected);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mInfoList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
