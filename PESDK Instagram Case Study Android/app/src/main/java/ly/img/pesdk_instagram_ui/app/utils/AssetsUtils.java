package ly.img.pesdk_instagram_ui.app.utils;

import java.util.ArrayList;
import java.util.List;

import ly.img.android.pesdk.backend.model.config.ColorAsset;
import ly.img.android.pesdk.backend.model.config.ImageStickerAsset;
import ly.img.android.pesdk.ui.panels.item.ColorItem;
import ly.img.android.pesdk.utils.DataSourceArrayList;
import ly.img.pesdk_instagram_ui.app.R;

/**
 * Created by Hasan Mhd Amin on 2019-11-19
 */
public class AssetsUtils {

    public static DataSourceArrayList<ColorItem> getColorList(){

        DataSourceArrayList<ColorItem> colorList = new DataSourceArrayList<>();
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_whiteColor, new ColorAsset(0xFFFFFFFF)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_grayColor, new ColorAsset(0xFF7D7D7D)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_blackColor, new ColorAsset(0xFF000000)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_lightBlueColor, new ColorAsset(0xFF66CCFF)));

        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_blueColor, new ColorAsset(0xFF6686FF)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_purpleColor, new ColorAsset(0xFF8666FF)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_orchidColor, new ColorAsset(0xFFBD67E3)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_pinkColor, new ColorAsset(0xFFFF65CB)));

        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_redColor, new ColorAsset(0xFFE75050)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_orangeColor, new ColorAsset(0xFFF28855)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_goldColor, new ColorAsset(0xFFFECC66)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_yellowColor, new ColorAsset(0xFFFFF763)));

        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_oliveColor, new ColorAsset(0xFFCBFF65)));
        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_greenColor, new ColorAsset(0xFF9CEF96)));

        colorList.add(new ColorItem(ly.img.android.pesdk.ui.text.R.string.pesdk_common_title_aquamarinColor, new ColorAsset(0xFF54FFEA)));
        return colorList;
    }



    public static List<ImageStickerAsset> getStickers(){
        List<ImageStickerAsset> stickerMap = new ArrayList<>();

        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_grin", R.drawable.imgly_sticker_emoticons_grin, ImageStickerAsset.OPTION_MODE.INK_STICKER));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_laugh", R.drawable.imgly_sticker_emoticons_laugh, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_smile", R.drawable.imgly_sticker_emoticons_smile, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_wink", R.drawable.imgly_sticker_emoticons_wink, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_tongue_out_wink", R.drawable.imgly_sticker_emoticons_tongue_out_wink, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_angel", R.drawable.imgly_sticker_emoticons_angel, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_kisses", R.drawable.imgly_sticker_emoticons_kisses, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_loving", R.drawable.imgly_sticker_emoticons_loving, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_kiss", R.drawable.imgly_sticker_emoticons_kiss, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_wave", R.drawable.imgly_sticker_emoticons_wave, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_nerd", R.drawable.imgly_sticker_emoticons_nerd, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_cool", R.drawable.imgly_sticker_emoticons_cool, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_blush", R.drawable.imgly_sticker_emoticons_blush, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_duckface", R.drawable.imgly_sticker_emoticons_duckface, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_furious", R.drawable.imgly_sticker_emoticons_furious, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_angry", R.drawable.imgly_sticker_emoticons_angry, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_steaming_furious", R.drawable.imgly_sticker_emoticons_steaming_furious, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_sad", R.drawable.imgly_sticker_emoticons_sad, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_anxious", R.drawable.imgly_sticker_emoticons_anxious, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_cry", R.drawable.imgly_sticker_emoticons_cry, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_sobbing", R.drawable.imgly_sticker_emoticons_sobbing, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_loud_cry", R.drawable.imgly_sticker_emoticons_loud_cry, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_wide_grin", R.drawable.imgly_sticker_emoticons_wide_grin, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_impatient", R.drawable.imgly_sticker_emoticons_impatient, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_tired", R.drawable.imgly_sticker_emoticons_tired, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_asleep", R.drawable.imgly_sticker_emoticons_asleep, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_sleepy", R.drawable.imgly_sticker_emoticons_sleepy, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_deceased", R.drawable.imgly_sticker_emoticons_deceased, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_attention", R.drawable.imgly_sticker_emoticons_attention, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_question", R.drawable.imgly_sticker_emoticons_question, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_not_speaking_to_you", R.drawable.imgly_sticker_emoticons_not_speaking_to_you, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_sick", R.drawable.imgly_sticker_emoticons_sick, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_pumpkin", R.drawable.imgly_sticker_emoticons_pumpkin, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_boxer", R.drawable.imgly_sticker_emoticons_boxer, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_idea", R.drawable.imgly_sticker_emoticons_idea, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_smoking", R.drawable.imgly_sticker_emoticons_smoking, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_beer", R.drawable.imgly_sticker_emoticons_beer, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_skateboard", R.drawable.imgly_sticker_emoticons_skateboard, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_guitar", R.drawable.imgly_sticker_emoticons_guitar, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_music", R.drawable.imgly_sticker_emoticons_music, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_sunbathing", R.drawable.imgly_sticker_emoticons_sunbathing, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_hippie", R.drawable.imgly_sticker_emoticons_hippie, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_humourous", R.drawable.imgly_sticker_emoticons_humourous, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_hitman", R.drawable.imgly_sticker_emoticons_hitman, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_harry_potter", R.drawable.imgly_sticker_emoticons_harry_potter, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_business", R.drawable.imgly_sticker_emoticons_business, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_batman", R.drawable.imgly_sticker_emoticons_batman, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_skull", R.drawable.imgly_sticker_emoticons_skull, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_ninja", R.drawable.imgly_sticker_emoticons_ninja, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_masked", R.drawable.imgly_sticker_emoticons_masked, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_alien", R.drawable.imgly_sticker_emoticons_alien, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_wrestler", R.drawable.imgly_sticker_emoticons_wrestler, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_devil", R.drawable.imgly_sticker_emoticons_devil, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_star", R.drawable.imgly_sticker_emoticons_star, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_baby_chicken", R.drawable.imgly_sticker_emoticons_baby_chicken, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_rabbit", R.drawable.imgly_sticker_emoticons_rabbit, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_pig", R.drawable.imgly_sticker_emoticons_pig, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));
        stickerMap.add(new ImageStickerAsset("imgly_sticker_emoticons_chicken", R.drawable.imgly_sticker_emoticons_chicken, ImageStickerAsset.OPTION_MODE.NO_OPTIONS));

        return stickerMap;
    }

}
