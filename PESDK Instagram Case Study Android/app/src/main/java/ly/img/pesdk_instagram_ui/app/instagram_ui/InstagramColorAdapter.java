package ly.img.pesdk_instagram_ui.app.instagram_ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ly.img.android.pesdk.ui.panels.item.ColorItem;
import ly.img.pesdk_instagram_ui.app.R;

import java.util.List;


/**
 * Created by niklasbachmann on 27.11.17.
 */

public class InstagramColorAdapter extends RecyclerView.Adapter<InstagramColorAdapter.ColorViewHolder> {

    final private ListItemClickListener onClickListener;

    private List<ColorItem> colorData;

    private int selectedPosition = -1;

    public void setColorData(List<ColorItem> colorData) {
        this.colorData = colorData;
    }

    public interface ListItemClickListener {
        void onColorListItemClick(ColorItem clickedItem);
    }


    public InstagramColorAdapter(ListItemClickListener listener) {
        onClickListener = listener;
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.iui_color_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmidiately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmidiately);
        ColorViewHolder viewHolder = new ColorViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position, @Nullable List<Object> payloads) {
        if (payloads != null && payloads.size() > 0) {
            holder.setSelectionState(selectedPosition == position);
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return colorData.size();
    }

    protected class ColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ColorItem lastBoundData;
        private View listItemColorView;

        public ColorViewHolder (View itemView) {
            super(itemView);
            listItemColorView = itemView.findViewById(R.id.iui_color_item);
            itemView.setOnClickListener(this);
        }

        protected void bind(int listIndex) {
            lastBoundData = colorData.get(listIndex);
            GradientDrawable gradientDrawable = (GradientDrawable) itemView.getResources().getDrawable(R.drawable.iui_rounded_color_list_item_background);
            gradientDrawable.setColor(lastBoundData.getData().getColor());
            listItemColorView.setBackgroundDrawable(gradientDrawable);
            setSelectionState(selectedPosition == listIndex);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onColorListItemClick(lastBoundData);
        }

        public void setSelectionState(boolean isSelected) {
            itemView.setSelected(isSelected);
        }
    }

    public void setSelection(ColorItem item) {
        if (colorData != null) {
            notifyItemChanged(selectedPosition, new Object()); // Old Deselect
            selectedPosition = colorData.indexOf(item);
            notifyItemChanged(selectedPosition, new Object()); // New Select
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
