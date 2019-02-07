package ly.img.pesdk_instagram_ui.app.instagram_ui.panel;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import ly.img.android.pesdk.annotations.OnEvent;
import ly.img.android.pesdk.backend.brush.models.Brush;
import ly.img.android.pesdk.backend.brush.models.Painting;
import ly.img.android.pesdk.backend.model.state.BrushSettings;
import ly.img.android.pesdk.backend.model.state.EditorShowState;
import ly.img.android.pesdk.backend.model.state.HistoryState;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.backend.views.abstracts.ImgLyUIRelativeContainer;
import ly.img.android.pesdk.ui.model.state.UiConfigBrush;
import ly.img.android.pesdk.ui.panels.item.ColorItem;
import ly.img.pesdk_instagram_ui.app.R;
import ly.img.pesdk_instagram_ui.app.instagram_ui.InstagramColorAdapter;
import ly.img.pesdk_instagram_ui.app.instagram_ui.widget.BrushUndoButton;

import ly.img.android.PESDKEvents;

/**
 * Created by niklasbachmann on 06.12.17.
 */

public class BrushPanel extends ImgLyUIRelativeContainer implements InstagramColorAdapter.ListItemClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int DEFAULT_COLOR = 0xFFFFFFFF; // ARGB

    private static final int ANIMATION_DURATION = 100;

    private BrushSettings brushSettings;

    private EditorShowState editorState;
    private HistoryState historyState;

    private LinearLayout brushSizeSeekBarLayout;
    private SeekBar brushSizeSeekBar;
    private float brushSeekBarPositionX;

    private RecyclerView brushColorList;

    private float minImageRectSize;
    private float sliderProgress;

    private InstagramColorAdapter brushColorAdapter;

    private OnPanelCloseListener onPanelCloseListener = null;

    private BrushUndoButton brushUndoButton;

    private boolean hasInitSaveState = false;

    public BrushPanel(Context context) {
        this(context, null);
    }

    public BrushPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrushPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.instagram_ui_brush, this);

        init();
    }

    private void init() {
        setVisibility(GONE);

        brushUndoButton = (BrushUndoButton) findViewById(R.id.iui_brush_undo_button);

        brushSizeSeekBarLayout = (LinearLayout) findViewById(R.id.iui_seekbar_layout);
        brushSizeSeekBar = (SeekBar) findViewById(R.id.iui_brush_seekbar);
        brushColorList = (RecyclerView) findViewById(R.id.rv_brush_colors);

        brushSizeSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.iui_seekbar_bg_empty));
        brushSizeSeekBar.setThumb(getResources().getDrawable(R.drawable.iui_seekbar_thumb));
        brushSizeSeekBar.setPadding(64, 0, 64, 0);
        brushSeekBarPositionX = brushSizeSeekBarLayout.getX();

        brushColorAdapter = new InstagramColorAdapter(this);
        brushColorAdapter.setColorData(getStateHandler().getStateModel(UiConfigBrush.class).getBrushColorList());

        brushColorList.setLayoutManager(
          new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true)
        );
        brushColorList.setHasFixedSize(true);
        brushColorList.setAdapter(brushColorAdapter);

        brushSizeSeekBar.setOnSeekBarChangeListener(this);

        // Undo
        findViewById(R.id.iui_brush_undo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyState.getPosition(1) > 1) {
                    historyState.undo(1);
                }
                brushUndoButton.setUndoStep(historyState.getPosition(1) > 1);
            }
        });

        // Done
        findViewById(R.id.iui_brush_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPanelClose();
            }
        });
    }

    @Override
    protected void onAttachedToUI(StateHandler stateHandler) {
        super.onAttachedToUI(stateHandler);
        brushSettings = stateHandler.getStateModel(BrushSettings.class);
        editorState = stateHandler.getStateModel(EditorShowState.class);
        historyState = stateHandler.getStateModel(HistoryState.class);
        brushSettings.setBrushHardness(1f);
        brushUndoButton.setUndoStep(false);
    }

    public void switchVisibility(boolean isVisible) {
        if (isVisible) {
            setVisibility(VISIBLE);
            onPanelOpen();
        } else {
            setVisibility(GONE);
        }
    }

    private void onPanelOpen() {
        //editorState.setEditMode(EditMode.BRUSH);
        brushSettings.setInEditMode(true);

        brushSettings.setBrushColor(DEFAULT_COLOR);
        brushColorAdapter.setSelection(null);
        if (!hasInitSaveState) {
            Painting painting = brushSettings.getPainting();
            painting.startPaintChunk(new Brush(0.1, 1, 0));
            painting.finalizePaintChunk();
            historyState.save(1, BrushSettings.class);
            hasInitSaveState = true;
        }
    }

    @OnEvent(value = PESDKEvents.EditorShowState_IMAGE_RECT)
    void onImageRect() {
        Rect imageRect = getStateHandler().getStateModel(EditorShowState.class).getImageRect();
        minImageRectSize = Math.min(imageRect.width(), imageRect.height()) - 1;
        brushSizeSeekBar.setMax((int) minImageRectSize / 20);

        //SeekBar start value
        int startValue = 25;
        brushSizeSeekBar.setProgress(startValue);
        brushSettings.setBrushSize((startValue + 1) / minImageRectSize);
    }

    @Override
    public void onColorListItemClick(ColorItem clickedItem) {
        brushSettings.setBrushColor(clickedItem.getData().getColor());
        brushColorAdapter.setSelection(clickedItem);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        sliderProgress = (progress + 1) / minImageRectSize;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        // todo: multiplatform animation
        // Input animation
        /*AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
          ObjectAnimator.ofFloat(brushSizeSeekBarLayout, "translationX", brushSizeSeekBarLayout.getTranslationX(), -380)
        );
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.start();*/
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        brushSettings.setBrushSize(sliderProgress);

        // todo: multiplatform animation
        // Exit Animation
        /*AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
          ObjectAnimator.ofFloat(brushSizeSeekBarLayout, "translationX", brushSizeSeekBarLayout.getTranslationX(), brushSeekBarPositionX)
        );
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.start();*/
    }

    public void setOnPanelCloseListener(OnPanelCloseListener onPanelCloseListener) {
        this.onPanelCloseListener = onPanelCloseListener;
    }

    public void onPanelClose(){
        if (onPanelCloseListener != null) {
            onPanelCloseListener.onPanelClose(this);
        }
    }

    public void onBrushEnd() {
        brushUndoButton.setUndoStep(true);
    }
}
