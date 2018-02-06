package ly.img.pesdk_instagram_ui.app.instagram_ui.panel;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import ly.img.pesdk_instagram_ui.app.instagram_ui.InstagramColorAdapter;

import ly.img.pesdk_instagram_ui.app.R;

import ly.img.android.PESDK;
import ly.img.android.sdk.models.chunk.RectRecycler;
import ly.img.android.sdk.models.config.TextStickerConfig;
import ly.img.android.sdk.models.config.interfaces.ColorConfigInterface;
import ly.img.android.sdk.models.config.interfaces.StickerConfigInterface;
import ly.img.android.sdk.models.constant.EditMode;
import ly.img.android.sdk.models.state.EditorShowState;
import ly.img.android.sdk.models.state.LayerListSettings;
import ly.img.android.sdk.models.state.PESDKConfig;
import ly.img.android.sdk.models.state.layer.StickerLayerSettings;
import ly.img.android.sdk.models.state.layer.TextLayerSettings;
import ly.img.android.sdk.models.state.manager.StateHandler;
import ly.img.android.sdk.views.abstracts.ImgLyUIRelativeContainer;
import ly.img.pesdk_instagram_ui.app.instagram_ui.widget.TextAlignButton;
import ly.img.pesdk_instagram_ui.app.instagram_ui.widget.TextFillButton;

/**
 * Created by niklasbachmann on 06.12.17.
 */

public class TextPanel extends ImgLyUIRelativeContainer implements InstagramColorAdapter.ListItemClickListener, ViewTreeObserver.OnGlobalLayoutListener  {

    private static final int X = 0;
    private static final int Y = 1;

    private static final int DEFAULT_COLOR    = 0xFFFFFFFF; // ARGB
    private static final int DEFAULT_BG_COLOR = 0x00FFFFFF; // ARGB
    private static final int BLACK_COLOR = 0xFF000000; // ARGB

    private static final boolean DEFAULT_TEXT_FILL_STATE = false;
    private static final Paint.Align DEFAULT_TEXT_ALIGN_STATE = Paint.Align.CENTER;

    private int textColor = DEFAULT_COLOR;
    private int textBgColor = DEFAULT_BG_COLOR;

    private TextLayerSettings currentTextStickerConfig;

    private boolean isTextEditing = false;

    private InstagramColorAdapter textColorAdapter;

    private ly.img.pesdk_instagram_ui.app.instagram_ui.widget.TextAlignButton textAlignmentButton;
    private ly.img.pesdk_instagram_ui.app.instagram_ui.widget.TextFillButton textFillButton;
    private EditText textInputField;

    private RecyclerView textColorList;

    private EditorShowState editorState;
    private LayerListSettings layerListSettings;

    InputMethodManager imm = (InputMethodManager) PESDK.getAppSystemService(Context.INPUT_METHOD_SERVICE);

    private OnPanelCloseListener onPanelCloseListener = null;

    public TextPanel(Context context) {
        this(context, null);
    }

    public TextPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.instagram_ui_text, this);

        init();
    }

    private void init () {
        setVisibility(GONE);

        textAlignmentButton = (TextAlignButton) findViewById(R.id.iui_text_align_button);
        textFillButton = (TextFillButton) findViewById(R.id.iui_text_fill_button);
        textInputField = (EditText) findViewById(R.id.iui_text_input);
        textColorList = (RecyclerView) findViewById(R.id.rv_text_colors);

        textColorAdapter = new InstagramColorAdapter(this);
        textColorAdapter.setColorData(getStateHandler().getStateModel(PESDKConfig.class).getTextColorConfig());

        textColorList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        textColorList.setHasFixedSize(true);
        textColorList.setAdapter(textColorAdapter);

        textFillButton.setOnTextFillStateChangeListener(new TextFillButton.OnTextFillStateChangeListener() {
            @Override
            public void onTextFillStateChange(boolean isTextFillClicked) {
            if (isTextFillClicked) {
                if (textColor == DEFAULT_COLOR) {
                    textBgColor = BLACK_COLOR;
                } else {
                    textBgColor = textColor;
                    textColor = DEFAULT_COLOR;
                }
            } else {
                textColor = textBgColor;
                textBgColor = DEFAULT_BG_COLOR;
            }
            textInputField.setBackgroundColor(textBgColor);
            textInputField.setTextColor(textColor);
            }
        });

        // Done
        findViewById(R.id.iui_text_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textInputField != null) {
                    String text = textInputField.getText().toString().trim();
                    if (text.length() > 0) {
                        onTextChanged(text, textAlignmentButton.getTextAlignState());
                    }
                }
                onPanelClose();
            }
        });

        // Keyboard action
        ((EditText) findViewById(R.id.iui_text_input)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (textInputField != null) {
                        String text = textInputField.getText().toString().trim();
                        if (text.length() > 0) {
                            onTextChanged(text, textAlignmentButton.getTextAlignState());
                        }
                    }
                    onPanelClose();
                }
                return false;
            }
        });

    }

    @Override
    protected void onAttachedToUI(StateHandler stateHandler) {
        super.onAttachedToUI(stateHandler);
        editorState = stateHandler.getStateModel(EditorShowState.class);
        registerLayoutHeightListener();
    }

    @Override
    protected void onDetachedFromUI(StateHandler stateHandler) {
        super.onDetachedFromUI(stateHandler);
        unregisterLayoutHeightListener();
    }

    public void switchVisibility(boolean isVisible) {
        if (isVisible) {
            setVisibility(VISIBLE);
            onPanelOpen();
        } else {
            imm.hideSoftInputFromWindow(textInputField.getWindowToken(), 0);
            setVisibility(GONE);
        }
    }

    private void onPanelOpen() {
        editorState.setEditMode(EditMode.NORMAL);

        LayerListSettings.LayerSettings currentSelection;

        if (layerListSettings != null) {
            currentSelection = layerListSettings.getSelected();
        } else {
            currentSelection = null;
        }

        if (currentSelection instanceof TextLayerSettings) {

            isTextEditing = true;

            currentTextStickerConfig = (TextLayerSettings) currentSelection;

            if (textInputField != null) {
                textInputField.requestFocusFromTouch();
                imm.showSoftInput(textInputField, InputMethodManager.SHOW_IMPLICIT);

                TextStickerConfig config = (TextStickerConfig) currentTextStickerConfig.getStickerConfig();

                textColor = config.getColor();
                textBgColor = config.getBackgroundColor();

                textInputField.setText(config.getText());
            }
        } else {
            textFillButton.setTextFillState(DEFAULT_TEXT_FILL_STATE);
            textAlignmentButton.setTextAlignState(DEFAULT_TEXT_ALIGN_STATE);
            if (textInputField != null) {
                textInputField.requestFocusFromTouch();
                imm.showSoftInput(textInputField, InputMethodManager.SHOW_IMPLICIT);

                textColor = DEFAULT_COLOR;
                textBgColor = DEFAULT_BG_COLOR;

                textInputField.setText("");
            }
        }
        textInputField.setBackgroundColor(textBgColor);
        textInputField.setTextColor(textColor);
        textInputField.setSelection(textInputField.getText().length());
    }

    public void onTextChanged(@NonNull String text, Paint.Align align) {
        TextStickerConfig newConfig = new TextStickerConfig(text, align, getStateHandler().getStateModel(PESDKConfig.class).getFontConfig().get(0), textColor, textBgColor);
        if (isTextEditing) {
            setTextSticker(newConfig);
        } else {
            addTextSticker(newConfig);
        }
        isTextEditing = false;
    }

    private void setTextSticker(StickerConfigInterface textSticker) {
        currentTextStickerConfig.setStickerConfig(textSticker);
    }

    private void addTextSticker(StickerConfigInterface textSticker) {
        StickerLayerSettings stickerLayerSettings = new TextLayerSettings(textSticker);
        getLayerListSettings().addLayer(stickerLayerSettings);
        getLayerListSettings().setSelected(stickerLayerSettings);
    }

    @Override
    public void onColorListItemClick(ColorConfigInterface clickedItem) {

        if (textFillButton.getTextFillState()) {
            textBgColor = clickedItem.getColor();
            textInputField.setBackgroundColor(textBgColor);
        } else {
            textColor = clickedItem.getColor();
            textInputField.setTextColor(textColor);
        }


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

    public void registerLayoutHeightListener() {
        unregisterLayoutHeightListener();
        if (this.getRootView() != null) {
            this.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    public void unregisterLayoutHeightListener() {
        if (this.getRootView() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                this.getRootView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        if (this.getRootView() != null) {
            Rect visibleDisplayFrame = RectRecycler.obtain();
            this.getRootView().getWindowVisibleDisplayFrame(visibleDisplayFrame);

            int[] colorListWindowPos = new int[2];
            this.textColorList.getLocationInWindow(colorListWindowPos);
            colorListWindowPos[Y] -= textColorList.getTranslationY();
            int newColorListWindowY = (visibleDisplayFrame.bottom - textColorList.getMeasuredHeight());
            int textColorListTransY = newColorListWindowY - colorListWindowPos[Y];

            int[] textInputWindowPos = new int[2];
            this.textInputField.getLocationInWindow(textInputWindowPos);
            textInputWindowPos[Y] -= textInputField.getTranslationY();
            int newTextInputWindowY = (
              ((visibleDisplayFrame.bottom - visibleDisplayFrame.top) / 2)
              - (textInputField.getMeasuredHeight() / 2)
            );
            int textInputFieldTransY = newTextInputWindowY - textInputWindowPos[Y];

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
              ObjectAnimator.ofFloat(textInputField, "translationY", textInputField.getTranslationY(), textInputFieldTransY),
              ObjectAnimator.ofFloat(textColorList, "translationY", textColorList.getTranslationY(), textColorListTransY)
            );

            RectRecycler.recycle(visibleDisplayFrame);

            animatorSet.start();
        }
    }

}
