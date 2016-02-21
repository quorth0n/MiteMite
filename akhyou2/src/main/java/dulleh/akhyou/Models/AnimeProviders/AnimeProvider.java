package dulleh.akhyou.Models.AnimeProviders;

import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Utils.CloudFlareInitializationException;
import rx.exceptions.OnErrorThrowable;

public interface AnimeProvider {

    Anime fetchAnime(String url) throws OnErrorThrowable, CloudFlareInitializationException;

    Anime updateCachedAnime (Anime cachedAnime) throws OnErrorThrowable, CloudFlareInitializationException;

    List<Source> fetchSources (String url) throws OnErrorThrowable, CloudFlareInitializationException;

    Source fetchVideo (Source source) throws OnErrorThrowable, CloudFlareInitializationException;

}
