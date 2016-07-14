package dulleh.akhyou.Models;

import java.util.HashMap;
import java.util.Map;

import dulleh.akhyou.Models.SourceProviders.AnimeBamSourceProvider;
import dulleh.akhyou.Models.SourceProviders.DailyMotionSourceProvider;
import dulleh.akhyou.Models.SourceProviders.EngineSourceProvider;
import dulleh.akhyou.Models.SourceProviders.GoSourceProvider;
import dulleh.akhyou.Models.SourceProviders.Mp4UploadSourceProvider;
import dulleh.akhyou.Models.SourceProviders.SourceProvider;
import dulleh.akhyou.Models.SourceProviders.YourUploadSourceProvider;

public class Providers {

    // Codes
    public static final int RUSH = 0;
    public static final int RAM = 1;
    public static final int BAM = 2;
    public static final int KISS = 3;

    // Titles
    public static final CharSequence RUSH_TITLE = "ANIMERUSH";
    public static final CharSequence RAM_TITLE = "ANIMERAM";
    public static final CharSequence BAM_TITLE = "ANIMEBAM";
    public static final CharSequence KISS_TITLE = "KISSANIME";
    public static final String MP4UPLOAD_TITLE = "mp4upload";
    public static final String DAILYMOTION_TITLE = "dailymotion";
    public static final String ENGINE_TITLE = "engine";
    public static final String YOURUPLOAD_TITLE = "yourupload";
    public static final String GO_TITLE = "go";
    public static final String ABVIDEO_TITLE = "abvideo";

    // Base URLs
    public static final String RUSH_BASE_URL = "http://www.animerush.tv";
    public static final String RAM_BASE_URL = "http://www.animeram.io";
    public static final String BAM_BASE_URL = "http://www.animebam.net";
    public static final String KISS_BASE_URL = "http://kissanime.to";

    // Base search paths (must start with / )
    public static final String RUSH_SEARCH_EXT = "/search.php?searchquery=";
    public static final String RAM_SEARCH_EXT = "/search?search=";
    public static final String BAM_SEARCH_EXT = "/search?search=";
    public static final String KISS_SEARCH_EXT = "/AdvanceSearch";

    // Base search URLs
    public static final String RUSH_SEARCH_URL = RUSH_BASE_URL + RUSH_SEARCH_EXT;
    public static final String RAM_SEARCH_URL = RAM_BASE_URL + RAM_SEARCH_EXT;
    public static final String BAM_SEARCH_URL = BAM_BASE_URL + BAM_SEARCH_EXT;
    public static final String KISS_SEARCH_URL = KISS_BASE_URL + KISS_SEARCH_EXT;

    public static final Map<String, SourceProvider> sourceMap = getSourceList();

    private static Map<String, SourceProvider> getSourceList () {
        Map<String, SourceProvider> sourceMap = new HashMap<>();

        sourceMap.put(MP4UPLOAD_TITLE, new Mp4UploadSourceProvider());
        sourceMap.put(DAILYMOTION_TITLE, new DailyMotionSourceProvider());
        sourceMap.put(ENGINE_TITLE, new EngineSourceProvider());
        sourceMap.put(YOURUPLOAD_TITLE, new YourUploadSourceProvider());
        sourceMap.put(GO_TITLE, new GoSourceProvider());
        sourceMap.put(ABVIDEO_TITLE, new AnimeBamSourceProvider());

        return sourceMap;
    }


}
