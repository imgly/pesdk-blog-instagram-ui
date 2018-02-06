/*
 * This file is part of the PhotoEditor Software Development Kit.
 *
 * Copyright (C) 2016 9elements GmbH <contact@9elements.com>
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

package ly.img.pesdk_instagram_ui.app;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ly.img.android.PESDK;
import ly.img.android.sdk.models.state.manager.SettingsList;


public class Application extends android.app.Application {

    private static Properties properties;
    private static final String PROPERTIES_FILE = "nongit.properties";

    public static final String HOCKEYAPP_API_KEY_PROPERTY = "HOCKEYAPP_API_KEY";
    public static final String ANALYTICS_TRACK_ID_PROPERTY = "ANALYTICS_TRACK_ID";

    public static String ANALYTICS_TRACK_ID = "";

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            /*CrashManager.register(this, readPropertyValue(HOCKEYAPP_API_KEY_PROPERTY), new CrashManagerListener() {

                @Override
                public boolean shouldAutoUploadCrashes() {
                    return true;
                }
            });*/
        }

        PESDK.init(this, "LICENSE.dms");

        ANALYTICS_TRACK_ID = readPropertyValue(ANALYTICS_TRACK_ID_PROPERTY);
    }

    protected String readPropertyValue(String name) {
        if (properties == null) {
            properties = new Properties();
            try {
                new Properties();
                AssetManager assetManager = getAssets();

                InputStream in = assetManager.open(PROPERTIES_FILE);
                properties.load(in);
                in.close();

            } catch (IOException e) {
                throw new RuntimeException("Error while loading the nongit.properties file see /assets/example.properties", e);
            }
        }
        return properties.getProperty(name);
    }
}
