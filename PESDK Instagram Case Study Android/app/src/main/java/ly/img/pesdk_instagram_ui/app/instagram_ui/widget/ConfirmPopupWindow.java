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

package ly.img.pesdk_instagram_ui.app.instagram_ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ly.img.pesdk_instagram_ui.app.R;


/**
 * Vertical scrollable RecyclerView
 */
public class ConfirmPopupWindow extends RelativeLayout {

    private Listener listener;
    private final OnClickListener cancelClick =  new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onConfirmPopupResult(false);
            }
            dismiss();
        }
    };

    private final OnClickListener agreeClick =  new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onConfirmPopupResult(true);
            }
            dismiss();
        }
    };

    public static boolean onBackPressed(@NonNull View rootView) {
        ViewGroup viewGroup = (ViewGroup) rootView;
        ConfirmPopupWindow confirmPopupView = (ConfirmPopupWindow) viewGroup.findViewById(R.id.confirmCancelDialogId);
        if (confirmPopupView != null) {
            confirmPopupView.cancelClick.onClick(confirmPopupView);
            return true;
        } else return false;
    }

    public ConfirmPopupWindow(Context context) {
        super(context);

        View v = inflate(context, R.layout.iui_popup_confirm_dialog, this);
        v.findViewById(R.id.agreeButton).setOnClickListener(agreeClick);
        v.findViewById(R.id.disagreeButton).setOnClickListener(cancelClick);
        v.findViewById(R.id.notificationBackground).setOnClickListener(cancelClick);
    }

    @NonNull
    public ConfirmPopupWindow setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    private ViewGroup viewGroup;
    public void show(@NonNull View rootView) {
        if (this.viewGroup == null) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            this.setId(R.id.confirmCancelDialogId);
            viewGroup.addView(this);
            this.viewGroup = viewGroup;

            setAlpha(0f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 1f)
            );
            animatorSet.setDuration(300);
            animatorSet.start();
        }
    }

    public void dismiss() {
        if (viewGroup != null) {
            viewGroup.removeView(this);
            viewGroup = null;
        }
    }


    public interface Listener {
        void onConfirmPopupResult(boolean accepted);
    }
}
