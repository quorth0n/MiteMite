package dulleh.akhyou.Models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dulleh.akhyou.Models.SourceProviders.AnimeBamSourceProvider;
import dulleh.akhyou.Models.SourceProviders.DailyMotionSourceProvider;
import dulleh.akhyou.Models.SourceProviders.EngineSourceProvider;
import dulleh.akhyou.Models.SourceProviders.GoSourceProvider;
import dulleh.akhyou.Models.SourceProviders.Mp4UploadSourceProvider;
import dulleh.akhyou.Models.SourceProviders.SourceProvider;
import dulleh.akhyou.Models.SourceProviders.YourUploadSourceProvider;

public class Source implements Serializable{

    private String title;
    private String pageUrl;
    private String embedUrl;
    private List<Video> videos;
    private SourceProvider sourceProvider;

    public String getTitle() {
        return title;
    }

    public Source setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public Source setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public Source setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
        return this;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public Source setVideos(List<Video> videos) {
        this.videos = videos;
        return this;
    }

    public SourceProvider getSourceProvider() {
        return sourceProvider;
    }

    public Source setSourceProvider(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
        return this;
    }

}
