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
public class ToolsEffectAdapter extends RecyclerView.Adapter<ToolsEffectAdapter.ViewHolder> {
    public OnItemEffectSelected onItemEffectSelected;
    public int selectedSquareIndex;
    public List<ToolModel> toolLists;

    public interface OnItemEffectSelected {
        void onToolEffectSelected(ToolEditor toolEditor);
    }

    public ToolsEffectAdapter(OnItemEffectSelected onItemEffectSelected) {
        ArrayList arrayList = new ArrayList();
        this.toolLists = arrayList;
        this.selectedSquareIndex = 0;
        this.onItemEffectSelected = onItemEffectSelected;
//        arrayList.add(new ToolModel(R.string.drip, R.drawable.ic_drip, ToolEditor.DRIP));
        this.toolLists.add(new ToolModel(R.string.overlay, R.drawable.ic_overlay, ToolEditor.EFFECT));
        this.toolLists.add(new ToolModel(R.string.neon, R.drawable.ic_neon, ToolEditor.NEON));
//        this.toolLists.add(new ToolModel(R.string.doube, R.drawable.ic_motion, ToolEditor.DOUBLE));
//        this.toolLists.add(new ToolModel(R.string.splash, R.drawable.ic_splash, ToolEditor.SPLASH));
//        this.toolLists.add(new ToolModel(R.string.bg_change, R.drawable.ic_bg, ToolEditor.BG_CHANGE));
//        this.toolLists.add(new ToolModel(R.string.body, R.drawable.ic_body, ToolEditor.BODY));
//        this.toolLists.add(new ToolModel(R.string.portrait, R.drawable.ic_art, ToolEditor.ART));
        this.toolLists.add(new ToolModel(R.string.blur, R.drawable.ic_blur, ToolEditor.BLUR));
        this.toolLists.add(new ToolModel(R.string.frame, R.drawable.ic_frame, ToolEditor.FRAME));
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
            relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.gallery.photos.editphotovideo.adapters.ToolsEffectAdapter.ViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    ToolsEffectAdapter.this.selectedSquareIndex = ViewHolder.this.getLayoutPosition();
                    ToolsEffectAdapter.this.onItemEffectSelected.onToolEffectSelected(ToolsEffectAdapter.this.toolLists.get(ToolsEffectAdapter.this.selectedSquareIndex).toolType);
                    ToolsEffectAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}
