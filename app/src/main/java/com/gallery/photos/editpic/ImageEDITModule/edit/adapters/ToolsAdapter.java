package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photos.editpic.ImageEDITModule.edit.tools.ToolEditor;
import com.gallery.photos.editpic.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.ViewHolder> {
    public OnItemSelected onItemSelected;
    public int selectedSquareIndex;
    public List<ToolModel> toolLists;

    public interface OnItemSelected {
        void onToolSelected(ToolEditor toolEditor);
    }

    public ToolsAdapter(OnItemSelected onItemSelected) {
        ArrayList arrayList = new ArrayList();
        this.toolLists = arrayList;
        this.selectedSquareIndex = 0;
        this.onItemSelected = onItemSelected;
//        arrayList.add(new ToolModel(R.string.filter, R.drawable.ic_filter, ToolEditor.FILTER));
        this.toolLists.add(new ToolModel(R.string.Effect, R.drawable.ic_effect, ToolEditor.E_TOOLS));
        this.toolLists.add(new ToolModel(R.string.crop, R.drawable.ic_crop, ToolEditor.CROP));
//        this.toolLists.add(new ToolModel(R.string.hsl, R.drawable.ic_hsl, ToolEditor.HSL));
        this.toolLists.add(new ToolModel(R.string.sticker, R.drawable.ic_sticker, ToolEditor.STICKER));
        this.toolLists.add(new ToolModel(R.string.text, R.drawable.ic_text, ToolEditor.TEXT));
        this.toolLists.add(new ToolModel(R.string.adjust, R.drawable.ic_set, ToolEditor.ADJUST));
        this.toolLists.add(new ToolModel(R.string.mirror, R.drawable.ic_mirror, ToolEditor.MIRROR));
        this.toolLists.add(new ToolModel(R.string.paint, R.drawable.ic_paint, ToolEditor.PAINT));
        this.toolLists.add(new ToolModel(R.string.ratio, R.drawable.ic_ratio, ToolEditor.RATIO));
        this.toolLists.add(new ToolModel(R.string.square, R.drawable.ic_blur_bg, ToolEditor.SQUARE));
//        this.toolLists.add(new ToolModel(R.string.splas, R.drawable.ic_splash_bg, ToolEditor.SPLASHING));
    }

    class ToolModel {
        public int toolIcon;
        public int toolName;
        public ToolEditor toolType;

        ToolModel(int i, int i2, ToolEditor toolEditor) {
            this.toolName = i;
            this.toolIcon = i2;
            this.toolType = toolEditor;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_editing, viewGroup, false));
    }

    public void reset() {
        this.selectedSquareIndex = 0;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ToolModel toolModel = this.toolLists.get(i);
        viewHolder.text_view_tool_name.setText(toolModel.toolName);
        viewHolder.image_view_tool_icon.setImageResource(toolModel.toolIcon);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.toolLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view_tool_icon;
        RelativeLayout relative_layout_wrapper_tool;
        TextView text_view_tool_name;

        ViewHolder(View view) {
            super(view);
            this.image_view_tool_icon = (ImageView) view.findViewById(R.id.image_view_tool_icon);
            this.text_view_tool_name = (TextView) view.findViewById(R.id.text_view_tool_name);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_wrapper_tool);
            this.relative_layout_wrapper_tool = relativeLayout;
            relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.adapters.ToolsAdapter.ViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    ToolsAdapter.this.selectedSquareIndex = ViewHolder.this.getLayoutPosition();
                    ToolsAdapter.this.onItemSelected.onToolSelected(ToolsAdapter.this.toolLists.get(ToolsAdapter.this.selectedSquareIndex).toolType);
                    ToolsAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
