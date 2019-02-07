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

import android.app.Activity;
import android.app.Fragment;

import ly.img.android.pesdk.ui.activity.ImgLyIntent;
import ly.img.android.pesdk.ui.utils.PermissionRequest;

@SuppressWarnings("unused, WeakerAccess")
public class InstagramUIBuilder extends ImgLyIntent {

    public static final Class activityClass = InstagramUIActivity.class;

    public InstagramUIBuilder(android.content.Intent intent) {
        super(intent, activityClass);
    }

    public InstagramUIBuilder(Activity activity) {
        super(activity, activityClass);
    }

    public void startActivityForResult(Activity activity, final int resultId) {
        startActivityForResult(new ResultDelegator(activity), resultId, PermissionRequest.NEEDED_EDITOR_PERMISSIONS);
    }

    public void startActivityForResult(Fragment fragment, final int resultId) {
        startActivityForResult(new ResultDelegator(fragment), resultId, PermissionRequest.NEEDED_EDITOR_PERMISSIONS);
    }

    public void startActivityForResult(android.support.v4.app.Fragment fragment, final int resultId) {
        startActivityForResult(new ResultDelegator(fragment), resultId, PermissionRequest.NEEDED_EDITOR_PERMISSIONS);
    }


}
