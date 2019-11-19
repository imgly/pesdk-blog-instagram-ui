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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import ly.img.android.PESDK;
import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.backend.decoder.Decoder;
import ly.img.android.pesdk.backend.model.constant.Directory;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.SaveSettings;
import ly.img.android.pesdk.ui.activity.ImgLyIntent;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.pesdk.utils.PrefManger;
import ly.img.pesdk_instagram_ui.app.utils.ExampleConfigUtility;
import ly.img.pesdk_instagram_ui.app.instagram_ui.InstagramUIBuilder;

public class MainActivity extends Activity implements PermissionRequest.Response {

    private static final String FOLDER = "ImgLy";
    private static int EDITOR_PREVIEW_RESULT = 1;
    private static int GALLERY_IMAGE_RESULT = 2;

    private static final SaveSettings.SavePolicy SAVE_POLICY = SaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT;


    private final PrefManger.Config.StringPref latestSourceImage = new PrefManger.Config.StringPref(new PrefManger.PropertyConfig("LATEST_SOURCE_IMAGE", ""));
    private final PrefManger.Config.StringPref latestResultImage = new PrefManger.Config.StringPref(new PrefManger.PropertyConfig("LATEST_RESULT_IMAGE", ""));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open 'Instagram UI' with gallery picture
        findViewById(R.id.openGallery).setOnClickListener(v -> {
            openSystemGallery();
        });

        // Open 'Instagram UI' with test picture
        findViewById(R.id.openInstagramUI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhotoEditorSettingsList settingsList = ExampleConfigUtility.createInitialPesdkSettingsList();

                settingsList
                        .getSettingsModel(LoadSettings.class)
                        .setSource(getResourceImage(R.drawable.iui_bg));

                new InstagramUIBuilder(MainActivity.this)
                  .setSettingsList(settingsList)
                  .startActivityForResult(MainActivity.this, EDITOR_PREVIEW_RESULT);

            }
        });

    }

    private void openSystemGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GALLERY_IMAGE_RESULT);
        } else {
            Toast.makeText(PESDK.getAppContext(), R.string.pesdk_issue_gallery_not_found, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY_IMAGE_RESULT) {

            Uri selectedImage = data.getData();

            if (selectedImage != null) {
                openPESDKEditor(selectedImage);
            } else {
                Toast.makeText(MainActivity.this, "No picture found, please take a snapshot", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == EDITOR_PREVIEW_RESULT) {

            Uri resultPath = data.getParcelableExtra(ImgLyIntent.RESULT_IMAGE_URI);
            Uri sourcePath = data.getParcelableExtra(ImgLyIntent.SOURCE_IMAGE_URI);

            if (resultPath != null) {
                // Add result file to Gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, resultPath));
                latestResultImage.set(resultPath.toString());
            }

            if (sourcePath != null) {
                // Add sourceType file to Gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, sourcePath));
                latestSourceImage.set(sourcePath.toString());
            }

            Toast.makeText(PESDK.getAppContext(), "Image saved on: " + resultPath, Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED && requestCode == EDITOR_PREVIEW_RESULT && data != null) {
            Uri sourcePath = data.getParcelableExtra(ImgLyIntent.SOURCE_IMAGE_URI);
            Toast.makeText(PESDK.getAppContext(), "Editor canceled, sourceType image is:\n" + sourcePath, Toast.LENGTH_LONG).show();
        } else {
            //finish();
        }
    }

    private void openPESDKEditor(Uri selectedImage) {
        PhotoEditorSettingsList settingsList = ExampleConfigUtility.createInitialPesdkSettingsList();
        settingsList.getSettingsModel(LoadSettings.class)
                .setSource(selectedImage, true)
                .getSettingsModel(SaveSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("result_")
                .setJpegQuality(80, true)
                .setExportFormat(SaveSettings.FORMAT.AUTO)
                .setSavePolicy(SAVE_POLICY);

        new InstagramUIBuilder(MainActivity.this)
                .setSettingsList(settingsList)
                .startActivityForResult(MainActivity.this, EDITOR_PREVIEW_RESULT);

    }


    public Uri getResourceImage(@AnyRes int resourceId) {
        return Decoder.resourceToUri(getResources(), resourceId);
    }



    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {
        finish();
        System.exit(0);
    }

}
