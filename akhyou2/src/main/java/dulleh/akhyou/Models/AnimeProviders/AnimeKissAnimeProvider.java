package dulleh.akhyou.Models.AnimeProviders;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Utils.CloudflareHttpClient;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeKissAnimeProvider implements AnimeProvider {
    private static final String BASE_URL = "https://kissanime.to";
    private static final Pattern EXTRACT_STATUS = Pattern.compile("Status:\\s*(.*?)\\s{2,}Views");

    public AnimeKissAnimeProvider() {
        CloudflareHttpClient.INSTANCE.registerSite("https://kissanime.to");
    }

    @Override
    public Anime fetchAnime(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);
        Document doc = Jsoup.parse(body);
        Anime anime = new Anime()
                .setUrl(url)
                .setProviderType(Anime.ANIME_KISS);
        anime = parseInfo(doc, anime);
        anime.setEpisodes(parseEpisodes(doc));
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
        return new ArrayList<>();
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {
        return source;
    }

    private Anime parseInfo(Document doc, Anime anime) {
        Elements info = doc .select("#leftside > .bigBarContainer:first-of-type > .barContent > div > p:not(:empty)");

        String title = doc.select(".bigChar").text();

        String image = doc.select(".rightBox img").attr("src");

        String altNames = Stream.of(info.select("p:contains(Other name)").select("[title]"))
                .map(Element::text)
                .collect(Collectors.joining(", "));

        List<String> genres = Stream.of(info.select("p:contains(Genres)").select("a"))
                .map(g -> g.attr("href"))
                .map(g -> g.substring(g.lastIndexOf('/') + 1))
                .collect(Collectors.toList());
        String genreString = Stream.of(genres).collect(Collectors.joining(", "));


        Element date = info.select("p:contains(Date)").first();
        if (date != null) {
            String text = date.text();
            anime.setDate(text.substring(text.indexOf(':') + 2));
        } else {
            anime.setDate("-");
        }

        Element status = info.select("p:contains(Status)").first();
        if (status != null) {
            Matcher statusMatcher = EXTRACT_STATUS.matcher(status.text());
            if (statusMatcher.find()) {
                anime.setStatus(statusMatcher.group(1));
            } else {
                anime.setStatus("-");
            }
        } else {
            anime.setStatus("-");
        }

        anime.setTitle(title)
             .setAlternateTitle(altNames)
             .setGenres(genres.toArray(new String[genres.size()]))
             .setGenresString(genreString)
             .setDesc(info.last().text())
             .setImageUrl(image);

        return anime;
    }

    private List<Episode> parseEpisodes(Document doc) {
        Elements episodeElements = doc.select(".episodeList .listing").first().select("[title]");
        List<Episode> episodes = new ArrayList<>(episodeElements.size());

        for (Element episode : episodeElements) {
            episodes.add(new Episode()
                .setTitle(episode.text())
                .setUrl(BASE_URL + episode.attr("href")));
        }

        return episodes;
    }

}
