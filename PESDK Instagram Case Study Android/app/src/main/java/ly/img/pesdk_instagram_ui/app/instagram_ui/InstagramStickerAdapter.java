package ly.img.pesdk_instagram_ui.app.instagram_ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ly.img.android.pesdk.ui.panels.item.ImageStickerItem;
import ly.img.android.pesdk.ui.widgets.ImageSourceView;
import ly.img.pesdk_instagram_ui.app.R;



/**
 * Created by niklasbachmann on 28.11.17.
 */

public class InstagramStickerAdapter extends RecyclerView.Adapter<InstagramStickerAdapter.StickerViewHolder> {

    final private ListItemClickListener mOnClickListener;

    private List<ImageStickerItem> stickerData;

    public void setStickerData(List<ImageStickerItem> stickerData) {
        this.stickerData = stickerData;

    }

    public interface ListItemClickListener {
        void onStickerListItemClick(ImageStickerItem clickedSticker);
    }


    public InstagramStickerAdapter(InstagramStickerAdapter.ListItemClickListener listener) {
        mOnClickListener = listener;

    }

    @Override
    public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.iui_sticker_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmidiately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmidiately);
        StickerViewHolder viewHolder = new StickerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InstagramStickerAdapter.StickerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return stickerData.size();
    }



    class StickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageSourceView listItemStickerView;

        ImageStickerItem lastBindedSticker;


        public StickerViewHolder (View itemView) {
            super(itemView);

            listItemStickerView = (ImageSourceView) itemView.findViewById(R.id.iui_sticker_item);

            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {

            lastBindedSticker = stickerData.get(listIndex);

            listItemStickerView.setImageSource(lastBindedSticker.getData().getDgetStickerSource());

        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onStickerListItemClick(lastBindedSticker);
        }
    }
}
