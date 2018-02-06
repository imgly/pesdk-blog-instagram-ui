package ly.img.pesdk_instagram_ui.app.instagram_ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ly.img.pesdk_instagram_ui.app.R;

import java.util.List;

import ly.img.android.sdk.decoder.ImageSource;
import ly.img.android.sdk.models.config.interfaces.StickerConfigInterface;
import ly.img.android.sdk.models.config.interfaces.StickerListConfigInterface;
import ly.img.android.ui.widgets.ImageSourceView;

/**
 * Created by niklasbachmann on 28.11.17.
 */

public class InstagramStickerAdapter extends RecyclerView.Adapter<InstagramStickerAdapter.StickerViewHolder> {

    final private ListItemClickListener mOnClickListener;

    private List<StickerConfigInterface> stickerData;

    public void setStickerData(List<StickerConfigInterface> stickerData) {
        this.stickerData = stickerData;

    }

    public interface ListItemClickListener {
        void onStickerListItemClick(StickerConfigInterface clickedSticker);
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

        StickerConfigInterface lastBindedSticker;


        public StickerViewHolder (View itemView) {
            super(itemView);

            listItemStickerView = (ImageSourceView) itemView.findViewById(R.id.iui_sticker_item);

            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {

            lastBindedSticker = stickerData.get(listIndex);

            listItemStickerView.setImageSource(lastBindedSticker.getStickerSource());

        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onStickerListItemClick(lastBindedSticker);
        }
    }
}
