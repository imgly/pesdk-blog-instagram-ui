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

package ly.img.pesdk_instagram_ui.app.instagram_ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


import ly.img.pesdk_instagram_ui.app.R;

import ly.img.android.PESDKEvents;
import ly.img.android.sdk.models.state.ProgressState;
import ly.img.android.sdk.models.state.manager.StateHandler;
import ly.img.android.sdk.views.abstracts.ImgLyUIRelativeContainer;
import ly.img.sdk.android.annotations.OnEvent;

// TODO: Make sticker layer drawing on a single caching layer and handle redrawing on reordering.
public class ProgressWindow extends ImgLyUIRelativeContainer {

    private TextView textView;
    private Resources resources;

    public ProgressWindow(Context context) {
        this(context, null, 0);
    }

    public ProgressWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resources = getResources();
        View view = inflate(getContext(), R.layout.iui_popup_activity_spinner, this);
        textView = (TextView) view.findViewById(R.id.progress);
        initSateHandler();
    }

    protected void initSateHandler() {
        try {
            if (!isInEditMode()) {
                StateHandler.findInViewContext(getContext()).registerSettingsEventListener(this);
            }
        } catch (StateHandler.StateHandlerNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @MainThread
    @OnEvent({PESDKEvents.ProgressState_EXPORT_START, PESDKEvents.ProgressState_EXPORT_FINISH})
    protected void onExportStateChanged(ProgressState state) {
        final boolean isExportRunning = state.isExportRunning();
        if (isExportRunning) {
            textView.setText(R.string.imgly_photo_editor_export_progress_unknown);
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    @MainThread
    @OnEvent(PESDKEvents.ProgressState_EXPORT_PROGRESS)
    protected void onExportProgressChanged(ProgressState state) {
        if (state.isExportRunning()) {
            String progress = (((int) (state.getExportProgress() * 1000)) / 10f) + "%";
            final String text = resources.getString(R.string.imgly_photo_editor_export_progress, progress);
            textView.setText(text);
        }
    }
}
