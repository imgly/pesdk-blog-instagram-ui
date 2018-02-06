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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ly.img.android.PESDK;
import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.EditorLoadSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.sdk.utils.PrefManger;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.pesdk_instagram_ui.app.instagram_ui.InstagramUIBuilder;
import ly.img.android.ui.utilities.PermissionRequest;

public class MainActivity extends Activity implements PermissionRequest.Response {

    private static final String FOLDER = "ImgLy";
    private static int EDITOR_PREVIEW_RESULT = 1;
    private static int GALLERY_IMAGE_RESULT = 2;

    private static final EditorSaveSettings.SavePolicy SAVE_POLICY = EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT;

    private final PrefManger.Config.StringPref latestSourceImage = new PrefManger.Config.StringPref(new PrefManger.PropertyConfig("LATEST_SOURCE_IMAGE", ""));
    private final PrefManger.Config.StringPref latestResultImage = new PrefManger.Config.StringPref(new PrefManger.PropertyConfig("LATEST_RESULT_IMAGE", ""));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open 'Instagram UI' with gallery picture
        findViewById(R.id.openGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, GALLERY_IMAGE_RESULT);
                } else {
                    Toast.makeText(PESDK.getAppContext(), ly.img.android.R.string.imgly_issue_gallery_not_found, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Open 'Instagram UI' with test picture
        findViewById(R.id.openInstagramUI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SettingsList settingsList = createInitialSettingsList();

                settingsList
                  .getSettingsModel(EditorLoadSettings.class)
                  .setImageSourcePath(getResourceImage(R.drawable.iui_bg));

                new InstagramUIBuilder(MainActivity.this)
                  .setSettingsList(settingsList)
                  .startActivityForResult(MainActivity.this, EDITOR_PREVIEW_RESULT);

            }
        });

    }

    private SettingsList createInitialSettingsList(){
        SettingsList settingsList = new SettingsList();
        settingsList.setEventProcessor(PESDKEvents.class);

        return settingsList;
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

            SettingsList settingsList = createInitialSettingsList();

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
              filePathColumn, null, null, null);

            if (cursor == null || cursor.getCount() < 1) {
                return; // TODO: ERROR HANDLING
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            if (columnIndex < 0){
                return; // TODO: ERROR HANDLING
            }

            String picturePath = cursor.getString(columnIndex);

            cursor.close();

            if (picturePath != null) {

                settingsList
                  .getSettingsModel(EditorLoadSettings.class)
                  .setImageSourcePath(picturePath, true)

                  .getSettingsModel(EditorSaveSettings.class)
                  .setExportDir(Directory.DCIM, FOLDER)
                  .setExportPrefix("result_")
                  .setJpegQuality(80, true)
                  .setExportFormat(EditorSaveSettings.FORMAT.AUTO)
                  .setSavePolicy(SAVE_POLICY);


                new InstagramUIBuilder(MainActivity.this)
                  .setSettingsList(settingsList)
                  .startActivityForResult(MainActivity.this, EDITOR_PREVIEW_RESULT);

            } else {
                Toast.makeText(MainActivity.this, "No picture found, please take a snapshot", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == EDITOR_PREVIEW_RESULT) {

            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);

            if (resultPath != null) {
                // Add result file to Gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(resultPath))));
                latestResultImage.set(resultPath);
            }

            if (sourcePath != null) {
                // Add sourceType file to Gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sourcePath))));
                latestSourceImage.set(sourcePath);
            }

            Toast.makeText(PESDK.getAppContext(), "Image saved on: " + resultPath, Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED && requestCode == EDITOR_PREVIEW_RESULT && data != null) {
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);
            Toast.makeText(PESDK.getAppContext(), "Editor canceled, sourceType image is:\n" + sourcePath, Toast.LENGTH_LONG).show();
        } else {
            //finish();
        }
    }

    public String getResourceImage(@DrawableRes int resourceId) {

        Resources res = getResources();
        //noinspection ResourceType
        InputStream imageInput = res.openRawResource(resourceId);
        try {
            File file = File.createTempFile("test", "image");
            file.deleteOnExit();
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = imageInput.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }

            outputStream.flush();
            outputStream.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
