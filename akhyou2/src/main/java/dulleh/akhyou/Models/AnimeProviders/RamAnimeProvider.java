package dulleh.akhyou.Models.AnimeProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Models.SourceProviders.SourceProvider;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class RamAnimeProvider implements AnimeProvider {
    private static final String BASE_URL = "http://www.animeram.co";

    @Override
    public Anime fetchAnime(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);

        if (!hasAnime(body)) {
            throw OnErrorThrowable.from(new Throwable("Failed to retrieve anime."));
        }

        Element animeBox = isolate(body);

        Element info = animeBox.select("div.fattynavinside > div.container > div.media").first();

        Elements episodes = animeBox.select("div.container > div > div > div.col-md-10 > div.cblock > ul").first().children();

        Anime anime = new Anime()
                .setProviderType(Anime.ANIME_RAM)
                .setUrl(url);

        anime = parseForInfo(info, anime);

        anime.setEpisodes(parseForEpisodes(episodes));

        return anime;
    }

    @Override
    public Anime updateCachedAnime(Anime cachedAnime) throws OnErrorThrowable {
        Anime updatedAnime = fetchAnime(cachedAnime.getUrl());

        updatedAnime.inheritWatchedFrom(cachedAnime.getEpisodes());

        updatedAnime.setMajorColour(cachedAnime.getMajorColour());

        return updatedAnime;
    }

    @Override
    public List<Source> fetchSources(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);

        return parseForSources(isolateForSources(body));
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(source.getPageUrl());

        source.setEmbedUrl(parseForEmbedUrl(body));

        source.setVideos(source.getSourceProvider().fetchSource(source.getEmbedUrl()));

        return source;
    }

    private Element isolate (String body) {
        return Jsoup.parse(body).select("body > div.fattynav").first();
    }

    private Elements isolateForSources (String body) {
        return Jsoup.parse(body).select("body > div.darkness > div > div > div.col-md-10 > div:nth-child(1) > ul.nav.nav-tabs").first().children();
    }

    private boolean hasAnime (String body) {
        return !body.toLowerCase().contains("show not found");
    }

    private Anime parseForInfo (Element info, Anime anime) {
        anime.setImageUrl("http:" + info.select("img").attr("src"));

        info = info.select("div.media-body").first();

        return anime
                .setTitle(info.select("h1").text())
                .setAlternateTitle(info.child(0).child(2).text().substring(18))
                .setGenresString(info.child(1).child(0).text())
                .setStatus(info.child(0).child(1).child(1).text().substring(8))
                .setDesc(info.select("p.ptext").text());

    }

    private List<Episode> parseForEpisodes (Elements episodesElement) {
        List<Episode> episodes = new ArrayList<>(episodesElement.size());

        for (Element episodeElement : episodesElement) {
            Elements info = episodeElement.child(0).children();

            String title = info.first().text() + " " + info.get(1).text();

            episodes.add(new Episode()
                    .setTitle(title.trim())
                    .setUrl(BASE_URL + info.first().attr("href")));
        }

        return episodes;
    }

    private List<Source> parseForSources (Elements sourcesElements) throws OnErrorThrowable{

        List<Source> sources = new ArrayList<>(sourcesElements.size());

        for (Element sourceElement : sourcesElements) {
            sourceElement = sourceElement.child(0);

            StringBuilder titleBuilder = new StringBuilder();
            for (Element child : sourceElement.children()) {
                titleBuilder.append(child.text());
                titleBuilder.append(" ");
            }
            String title = titleBuilder.toString();

            SourceProvider sourceProvider = GeneralUtils.determineSourceProvider(title.toLowerCase());
            if (sourceProvider != null) {
                sources.add(new Source()
                                .setPageUrl(BASE_URL + sourceElement.attr("href"))
                                .setTitle(title.trim())
                                .setSourceProvider(sourceProvider)
                );
            }
        }

        return sources;
    }

    private String parseForEmbedUrl (String body) {
        return Jsoup.parse(body)
                .select("body > div.darkness > div > div > div.col-md-10 > div:nth-child(1) > div.tab-content.embed-responsive.embed-responsive-16by9 > div > iframe")
                .attr("src");
    }

}
