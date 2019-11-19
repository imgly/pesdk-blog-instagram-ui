/*
 * This file is part of the PhotoEditor Software Development Kit.
 *
 * Copyright (C) 2017 9elements GmbH <contact@9elements.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, without
 * modification, are permitted provided that the following license agreement
 * is approved and a legal/financial contract was signed by the user.
 *
 * The license agreement can be found under the following link:
 *
 * https://www.photoeditorsdk.com/LICENSE.txt
 */

package ly.img.pesdk_instagram_ui.app.instagram_ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import ly.img.android.IMGLYEvents;
import ly.img.android.pesdk.annotations.OnEvent;
import ly.img.android.pesdk.annotations.StateEvents;
import ly.img.android.pesdk.backend.model.chunk.SourceRequestAnswerI;
import ly.img.android.pesdk.backend.model.state.BrushSettings;
import ly.img.android.pesdk.backend.model.state.EditorLoadSettings;
import ly.img.android.pesdk.backend.model.state.EditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.EditorShowState;
import ly.img.android.pesdk.backend.model.state.HistoryState;
import ly.img.android.pesdk.backend.model.state.LayerListSettings;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.ProgressState;
import ly.img.android.pesdk.backend.model.state.SaveSettings;
import ly.img.android.pesdk.backend.model.state.layer.ImageStickerLayerSettings;
import ly.img.android.pesdk.backend.model.state.layer.TextLayerSettings;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.backend.views.EditorPreview;
import ly.img.android.pesdk.ui.activity.EditorActivity;
import ly.img.android.pesdk.ui.activity.ImgLyActivity;
import ly.img.android.pesdk.ui.activity.ImgLyIntent;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.pesdk.ui.widgets.ConfirmPopupView;
import ly.img.android.pesdk.utils.OrientationSensor;
import ly.img.android.pesdk.utils.ThreadUtils;
import ly.img.android.pesdk.utils.TimeOut;
import ly.img.pesdk_instagram_ui.app.R;
import ly.img.pesdk_instagram_ui.app.instagram_ui.panel.BrushPanel;
import ly.img.pesdk_instagram_ui.app.instagram_ui.panel.OnPanelCloseListener;
import ly.img.pesdk_instagram_ui.app.instagram_ui.panel.StickerPanel;
import ly.img.pesdk_instagram_ui.app.instagram_ui.panel.TextPanel;
import ly.img.pesdk_instagram_ui.app.instagram_ui.widget.ConfirmPopupWindow;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ly.img.android.acs.Camera;

@StateEvents
public class InstagramUIActivity extends ImgLyActivity implements OnPanelCloseListener, TimeOut.Callback {


    private static final int ANIMATION_DURATION = 180;

    private static final int EDITOR_MODE_MENU = 0;
    private static final int EDITOR_MODE_STICKER = 1;
    private static final int EDITOR_MODE_BRUSH = 2;
    private static final int EDITOR_MODE_TEXT = 3;
    private static final int EDITOR_MODE_EDIT_TEXT = 4;

    @IntDef({EDITOR_MODE_MENU, EDITOR_MODE_STICKER, EDITOR_MODE_BRUSH, EDITOR_MODE_TEXT, EDITOR_MODE_EDIT_TEXT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface EDITOR_MODE {}

    @EDITOR_MODE
    private int editorMode = 0;

    private HistoryState historyState;
    private EditorShowState editorState;
    private BrushSettings brushSettings;

    private LayerListSettings layerListSettings;

    private View rootView;
    public EditorPreview editorPreviewView;

    private RelativeLayout menuPanel;
    private TextPanel textPanel;
    private BrushPanel brushPanel;
    private StickerPanel stickerPanel;

    private TimeOut timeOut = new TimeOut(null);

    private boolean canvasTouched = false;

    AnimatorSet animatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instagram_ui_activity);
        getStateHandler().registerSettingsEventListener(this);

        editorState = getStateHandler().getStateModel(EditorShowState.class);
        historyState = getStateHandler().getStateModel(HistoryState.class);
        brushSettings = getStateHandler().getStateModel(BrushSettings.class);
        brushSettings.saveInitState();

        layerListSettings = getStateHandler().getStateModel(LayerListSettings.class);
        layerListSettings.setSelected(null);

        timeOut.addCallback(this);

        initViews();
    }

    private void initViews() {
        rootView = findViewById(R.id.rootView);
        editorPreviewView = findView(R.id.editorImageView);

        menuPanel = findViewById(R.id.iui_main_ui);
        stickerPanel = findViewById(R.id.iui_sticker_ui);
        brushPanel = findViewById(R.id.iui_brush_ui);
        textPanel = findViewById(R.id.iui_text_ui);

        stickerPanel.setOnPanelCloseListener(this);
        textPanel.setOnPanelCloseListener(this);
        brushPanel.setOnPanelCloseListener(this);

        // Close
        findViewById(R.id.iui_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseClicked();
            }
        });
        // Export
        findViewById(R.id.iui_next_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });
        // Sticker Panel
        findViewById(R.id.iui_sticker_tool_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchEditorMode(EDITOR_MODE_STICKER);
            }
        });
        // Brush Panel
        findViewById(R.id.iui_brush_tool_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchEditorMode(EDITOR_MODE_BRUSH);
            }
        });
        // Text Panel
        findViewById(R.id.iui_text_tool_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchEditorMode(EDITOR_MODE_TEXT);
            }
        });
    }

    private void switchEditorMode (@EDITOR_MODE int editorMode) {
        this.editorMode = editorMode;

        switch (editorMode) {
            case EDITOR_MODE_MENU:
                getLayerListSettings().setSelected(null);
                break;
            case EDITOR_MODE_STICKER:
            case EDITOR_MODE_BRUSH:
            case EDITOR_MODE_TEXT:
                deselectSticker();
                break;
            case EDITOR_MODE_EDIT_TEXT:
                // do not deselect sticker
                break;
        }

        stickerPanel.switchVisibility(editorMode == EDITOR_MODE_STICKER);
        brushPanel.switchVisibility(editorMode == EDITOR_MODE_BRUSH);
        textPanel.switchVisibility(editorMode == EDITOR_MODE_TEXT || editorMode == EDITOR_MODE_EDIT_TEXT);
        menuPanel.setVisibility(editorMode == EDITOR_MODE_MENU ? View.VISIBLE : View.GONE);

    }

    @OnEvent(IMGLYEvents.LayerListSettings_SELECTED_LAYER)
    protected void onStickerSelect(LayerListSettings settings) {
        LayerListSettings.LayerSettings selected = settings.getSelected();
        if (selected != null) {
            settings.bringLayerToFront(selected);
        }
    }

    @Override
    public void onPanelClose(View closedPanel) {
        switchEditorMode(EDITOR_MODE_MENU);
    }

    private void closeEditor() {
        final Intent result = new Intent();
        result.putExtra(ImgLyIntent.SETTINGS_LIST, getStateHandler().createSettingsListDump());
        Uri input = getStateHandler().getSettingsModel(EditorLoadSettings.class).getImageSource();
        if (input != null && input.getScheme() != null) {
            result.putExtra(ImgLyIntent.SOURCE_IMAGE_PATH, isFileSchema(input) ? input.getPath() : input.toString());
        }
        result.putExtra(ImgLyIntent.SOURCE_IMAGE_URI, getStateHandler().getSettingsModel(EditorLoadSettings.class).getImageSource());
        setResult(RESULT_CANCELED, result);
        finish();
    }

    @MainThread
    protected void onCloseClicked() {
        if (!getStateHandler().getSettingsModel(EditorLoadSettings.class).isDeleteProtectedSource()
          || getStateHandler().hasChanges()
          ) {
            new ConfirmPopupWindow(this).setListener(new ConfirmPopupWindow.Listener() {
                @Override
                public void onConfirmPopupResult(boolean accepted) {
                    if (accepted) {
                        closeEditor();
                    }
                }
            }).show(rootView);
        } else {
            closeEditor();
        }
    }

    @MainThread
    protected void onSaveClicked() {
        StateHandler stateHandler = getStateHandler();
        stateHandler.getStateModel(ProgressState.class).notifyExportStart();

        LoadSettings loadSettings = stateHandler.getStateModel(LoadSettings.class);
        SaveSettings saveSettings = stateHandler.getStateModel(SaveSettings.class);


        if (saveSettings.isExportNecessary()) {
            saveSettings.saveImage((stateHandler1, inputPath, outputPath) -> {
                SaveSettings saveSettings1 = stateHandler1.getStateModel(SaveSettings.class);
                onImageReady(inputPath, outputPath, saveSettings1.getSavePolicy());
            });
        } else {
            Uri inputPath = loadSettings.getSource();
            onImageReady(inputPath, inputPath, saveSettings.getSavePolicy());
        }

    }

    @Override
    public void onBackPressed() {
        if (!ConfirmPopupView.onBackPressed(rootView)) {
            onCloseClicked();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        OrientationSensor.getInstance().stop();
    }

    public void onImageReady(final Uri input, final Uri output, final SaveSettings.SavePolicy savePolicy) {
        ThreadUtils.getWorker().addTask(new ThreadUtils.SequencedThreadRunnable("OnResultSaving") {
            @Override
            public void run() {
                final Intent result = new Intent();
                result.putExtra(ImgLyIntent.SETTINGS_LIST, getStateHandler().createSettingsListDump());

                switch (savePolicy) {
                    case KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT:
                    case KEEP_SOURCE_AND_CREATE_OUTPUT_IF_NECESSARY:
                        result.putExtra(ImgLyIntent.SOURCE_IMAGE_PATH, isFileSchema(input) ? input.getPath() : input.toString());
                        result.putExtra(ImgLyIntent.SOURCE_IMAGE_URI, input);
                        result.putExtra(ImgLyIntent.RESULT_IMAGE_PATH, isFileSchema(output) ? output.getPath() : output.toString());
                        result.putExtra(ImgLyIntent.RESULT_IMAGE_URI, output);

                        break;
                    case RETURN_ALWAYS_ONLY_OUTPUT:
                    case RETURN_SOURCE_OR_CREATE_OUTPUT_IF_NECESSARY:
                        //result.putExtra(CameraPreviewActivity.SOURCE_IMAGE_PATH, (String) null);
                        result.putExtra(ImgLyIntent.RESULT_IMAGE_PATH, isFileSchema(output) ? output.getPath() : output.toString());
                        result.putExtra(ImgLyIntent.RESULT_IMAGE_URI, output);
                        if (input != null) {
                            File file = new File(input.getPath());
                            if (file.exists()) {
                                //noinspection ResultOfMethodCallIgnored
                                file.delete();


                            }
                        }

                    case RENDER_NOTHING_RETURN_SOURCE_AND_SETTINGS_LIST:
                        if (input != null) {
                            result.putExtra(ImgLyIntent.SOURCE_IMAGE_PATH, isFileSchema(input) ? input.getPath() : input.toString());
                            result.putExtra(ImgLyIntent.SOURCE_IMAGE_URI, input);
                        }

                        break;
                    default:
                        throw new RuntimeException("Unsupported save policy");
                }

                runOnUi(new ThreadUtils.MainThreadRunnable() {
                    @Override
                    public void run() {
                        getStateHandler().getStateModel(ProgressState.class).notifyExportFinish();
                        setResult(RESULT_OK, result);
                        finish();
                    }
                });
            }
        });
    }

    private boolean isFileSchema(Uri uri) {
        return "file".equals(uri.getScheme());
    }

    protected LayerListSettings getLayerListSettings() {
        if (this.layerListSettings == null) {
            StateHandler stateHandler = getStateHandler();
            if (stateHandler == null) return null;
            this.layerListSettings = stateHandler.getStateModel(LayerListSettings.class);
        }
        return this.layerListSettings;
    }

    private void deselectSticker() {
        getLayerListSettings().setSelected(null);
    }

    @MainThread
    @OnEvent(value = IMGLYEvents.EditorShowState_LAYER_TOUCH_END)
    void onLayerTouchEnd() {

        if (brushSettings.isInEditMode) {
            historyState.save(0, BrushSettings.class);
            brushPanel.onBrushEnd();
        }

        // todo: fix animation or delete
        canvasTouched = false;
        updateUiVisibilityStatus();
    }

    @OnEvent(value = ly.img.android.IMGLYEvents.EditorShowState_LAYER_DOUBLE_TAPPED, doInitCall = false)
    public void onStickerDoubleTapped(){
        if (layerListSettings != null && (layerListSettings.getSelected() instanceof TextLayerSettings)) {
            switchEditorMode(EDITOR_MODE_EDIT_TEXT);
        }
    }

    // todo: fix animation or delete
    @MainThread
    @OnEvent(value = ly.img.android.IMGLYEvents.EditorShowState_LAYER_TOUCH_START)
    void onLayerTouchStart() {
        if (!canvasTouched) {
            canvasTouched = true;
            updateUiVisibilityStatus();
        }
    }

    @Override
    public void onTimeOut(Enum identifier) {
        // Animation for UI elements
        if (editorMode == EDITOR_MODE_MENU && layerListSettings != null &&
          (layerListSettings.getSelected() instanceof TextLayerSettings || layerListSettings.getSelected() instanceof ImageStickerLayerSettings)) {
            if (canvasTouched) {

                if (animatorSet != null && animatorSet.isRunning()) {
                    animatorSet.cancel();
                }
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                  ObjectAnimator.ofFloat(menuPanel, "alpha", 1f, 0f)
                );
                animatorSet.setDuration(ANIMATION_DURATION);
                animatorSet.start();
            } else {
                float alpha = menuPanel.getAlpha();

                if (animatorSet != null && animatorSet.isRunning()){
                    animatorSet.cancel();
                }
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                  ObjectAnimator.ofFloat(menuPanel, "alpha", alpha, 1f)
                );
                animatorSet.setDuration(ANIMATION_DURATION / 3);
                animatorSet.start();
            }
        }
    }

    @MainThread
    private void updateUiVisibilityStatus () {
        timeOut.setTimeOut(100);
    }

}
