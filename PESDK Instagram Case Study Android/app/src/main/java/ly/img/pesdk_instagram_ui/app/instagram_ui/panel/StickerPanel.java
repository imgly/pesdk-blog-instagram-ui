package ly.img.pesdk_instagram_ui.app.instagram_ui.panel;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import ly.img.android.pesdk.backend.model.config.ImageStickerAsset;
import ly.img.android.pesdk.backend.model.state.EditorShowState;
import ly.img.android.pesdk.backend.model.state.LayerListSettings;
import ly.img.android.pesdk.backend.model.state.layer.ImageStickerLayerSettings;
import ly.img.android.pesdk.backend.model.state.layer.SpriteLayerSettings;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.backend.views.abstracts.ImgLyUIRelativeContainer;
import ly.img.pesdk_instagram_ui.app.R;
import ly.img.pesdk_instagram_ui.app.instagram_ui.InstagramStickerAdapter;
import ly.img.pesdk_instagram_ui.app.utils.AssetsUtils;

/**
 * Created by niklasbachmann on 06.12.17.
 */

public class StickerPanel extends ImgLyUIRelativeContainer implements InstagramStickerAdapter.ListItemClickListener {

    private View header;
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

    private void init() {
        setVisibility(GONE);

        stickerList = (RecyclerView) findViewById(R.id.rv_stickers);
        header = findViewById(R.id.header);

        InstagramStickerAdapter stickerAdapter = new InstagramStickerAdapter(this);
        stickerAdapter.setStickerData(AssetsUtils.getStickers());

        stickerList.setLayoutManager(new GridLayoutManager(getContext(), 5));
        stickerList.setHasFixedSize(true);
        stickerList.setAdapter(stickerAdapter);

        header.setOnClickListener(v -> onPanelClose());
    }

    @Override
    protected void onAttachedToUI(StateHandler stateHandler) {
        super.onAttachedToUI(stateHandler);
        editorState = stateHandler.getStateModel(EditorShowState.class);
    }

    public void switchVisibility(boolean isVisible) {
        if (isVisible) {
            setVisibility(VISIBLE);
            getLayerListSettings().setSelected(null);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onStickerListItemClick(ImageStickerAsset clickedSticker) {
        addImageSticker(clickedSticker);
        onPanelClose();
    }

    private void addImageSticker(ImageStickerAsset config) {
        SpriteLayerSettings spriteLayerSettings = new ImageStickerLayerSettings(config);
        getLayerListSettings().addLayer(spriteLayerSettings);
        getLayerListSettings().setSelected(spriteLayerSettings);
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

    public void onPanelClose() {
        if (onPanelCloseListener != null) {
            onPanelCloseListener.onPanelClose(this);
        }
    }
}
