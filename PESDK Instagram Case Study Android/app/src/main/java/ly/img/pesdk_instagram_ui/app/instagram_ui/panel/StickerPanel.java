package ly.img.pesdk_instagram_ui.app.instagram_ui.panel;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import ly.img.pesdk_instagram_ui.app.R;
import ly.img.pesdk_instagram_ui.app.instagram_ui.InstagramStickerAdapter;

import ly.img.android.sdk.models.config.interfaces.StickerConfigInterface;
import ly.img.android.sdk.models.constant.EditMode;
import ly.img.android.sdk.models.state.EditorShowState;
import ly.img.android.sdk.models.state.LayerListSettings;
import ly.img.android.sdk.models.state.PESDKConfig;
import ly.img.android.sdk.models.state.layer.ImageStickerLayerSettings;
import ly.img.android.sdk.models.state.layer.StickerLayerSettings;
import ly.img.android.sdk.models.state.manager.StateHandler;
import ly.img.android.sdk.utils.Trace;
import ly.img.android.sdk.views.abstracts.ImgLyUIRelativeContainer;

/**
 * Created by niklasbachmann on 06.12.17.
 */

public class StickerPanel extends ImgLyUIRelativeContainer implements InstagramStickerAdapter.ListItemClickListener {

    private RecyclerView stickerList;

    private EditorShowState editorState;

    private LayerListSettings layerListSettings;

    private OnPanelCloseListener onPanelCloseListener = null;


    public StickerPanel(Context context) {
        this(context, null);
    }

    public StickerPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.instagram_ui_sticker, this);

        init();
    }

    private void init () {
        setVisibility(GONE);

        stickerList = (RecyclerView) findViewById(R.id.rv_stickers);

        InstagramStickerAdapter stickerAdapter = new InstagramStickerAdapter(this);
        stickerAdapter.setStickerData(getStateHandler().getStateModel(PESDKConfig.class).getStickerConfig().get(0).getStickerList());

        stickerList.setLayoutManager(new GridLayoutManager(getContext(), 5));
        stickerList.setHasFixedSize(true);
        stickerList.setAdapter(stickerAdapter);
    }

    @Override
    protected void onAttachedToUI(StateHandler stateHandler) {
        super.onAttachedToUI(stateHandler);
        editorState = stateHandler.getStateModel(EditorShowState.class);
    }

    public void switchVisibility(boolean isVisible) {
        if (isVisible) {
            setVisibility(VISIBLE);
            editorState.setEditMode(EditMode.NORMAL);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onStickerListItemClick(StickerConfigInterface clickedSticker) {
        addImageSticker(clickedSticker);
        onPanelClose();
    }

    private void addImageSticker(StickerConfigInterface imageSticker) {
        StickerLayerSettings stickerLayerSettings = new ImageStickerLayerSettings(imageSticker);
        getLayerListSettings().addLayer(stickerLayerSettings);
        getLayerListSettings().setSelected(stickerLayerSettings);
    }

    protected LayerListSettings getLayerListSettings() {
        if (this.layerListSettings == null) {
            StateHandler stateHandler = getStateHandler();
            if (stateHandler == null) return null;
            this.layerListSettings = stateHandler.getStateModel(LayerListSettings.class);
        }
        return this.layerListSettings;
    }

    public void setOnPanelCloseListener(OnPanelCloseListener onPanelCloseListener) {
        this.onPanelCloseListener = onPanelCloseListener;
    }

    public void onPanelClose(){
        if (onPanelCloseListener != null) {
            onPanelCloseListener.onPanelClose(this);
        }
    }
}
