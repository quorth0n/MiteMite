package dulleh.akhyou.Models.SourceProviders;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.GeneralUtils;

public class Mp4UploadSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) {
        String body = GeneralUtils.getWebPage(embedPageUrl);

        String videoURL = GeneralUtils.jwPlayerIsolate(body);

        System.out.println(videoURL);

        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(null, videoURL));

        return videos;
    }

}
