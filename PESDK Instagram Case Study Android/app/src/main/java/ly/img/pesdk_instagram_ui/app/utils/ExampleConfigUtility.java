package ly.img.pesdk_instagram_ui.app.utils;

import java.util.HashSet;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.backend.exif.Exify;
import ly.img.android.pesdk.backend.exif.modes.ExifModeBlackListCopy;
import ly.img.android.pesdk.backend.model.constant.Directory;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.SaveSettings;

public class ExampleConfigUtility {

    public static final String FOLDER = "ImgLy";

    public static PhotoEditorSettingsList createInitialPesdkSettingsList() {

        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList();

        HashSet<Exify.TAG> exifBlackList = new HashSet<>();
        exifBlackList.add(Exify.TAG.DATE_TIME);
        exifBlackList.add(Exify.TAG.DATE_TIME_DIGITIZED);
        exifBlackList.add(Exify.TAG.DATE_TIME_ORIGINAL);
        exifBlackList.add(Exify.TAG.GPS_TIME_STAMP);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("result_")
                .setJpegQuality(80, true)
                .setExportFormat(SaveSettings.FORMAT.JPEG)
                .setExifMode(new ExifModeBlackListCopy(exifBlackList))
                .setSavePolicy(SaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT);

        return settingsList;
    }
}
