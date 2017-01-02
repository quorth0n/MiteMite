package dulleh.akhyou.Models.AnimeProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.Models.Providers;
import dulleh.akhyou.Models.Source;
import dulleh.akhyou.Models.Video;
import dulleh.akhyou.Utils.CloudFlareInitializationException;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class KissAnimeProvider implements AnimeProvider {

    private static final byte[] DECODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".getBytes(Charset.forName("UTF-8"));
    private static int[] DECODE_LOOKUP = new int[129];

    static {
        for (int i = 0; i < DECODE_ALPHABET.length; i++) {
            DECODE_LOOKUP[DECODE_ALPHABET[i]] = i;
        }
    }

    @Override
    public Anime fetchAnime(String url) throws OnErrorThrowable, CloudFlareInitializationException {
       // if (!CloudflareHttpClient.INSTANCE.isInitialized()) {
        //    throw new CloudFlareInitializationException();
        //}
        String body = GeneralUtils.getWebPage(url);

        Document doc = Jsoup.parse(body);

        return parse(doc, url);
    }

    public static Anime parse (Document doc, String url) {
        Anime anime = new Anime()
                .setUrl(url)
                .setProviderType(Providers.KISS);
        anime = parseInfo(doc, anime);
        anime.setEpisodes(KissAnimeProvider.parseEpisodes(doc));

        return anime;
    }

    @Override
    public Anime updateCachedAnime(Anime cachedAnime) throws OnErrorThrowable, CloudFlareInitializationException {
        Anime updatedAnime = fetchAnime(cachedAnime.getUrl());
        updatedAnime.inheritWatchedFrom(cachedAnime.getEpisodes());
        updatedAnime.setMajorColour(cachedAnime.getMajorColour());
        return updatedAnime;
    }

    @Override
    public List<Source> fetchSources(String url) throws OnErrorThrowable, CloudFlareInitializationException {

        //if (!CloudflareHttpClient.INSTANCE.isInitialized()) {
        //    throw new CloudFlareInitializationException();
        //}

        String body = GeneralUtils.getWebPage(url);

        Elements downloads = Jsoup.parse(body).select("#selectQuality option");
        List<Source> sources = new ArrayList<>(downloads.size());

        for (Element source : downloads) {
            String value = source.attr("value");
            String decoded = decode(value);
            sources.add(new Source()
                .setTitle(source.text())
                .setPageUrl(decoded));
        }

        return sources;
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {
        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(source.getTitle(), source.getPageUrl()));
        return source.setVideos(videos);
    }

    private static Anime parseInfo(Document doc, Anime anime) {
        Element info = doc.select("#leftside > div:nth-child(1) > div.barContent > div:not(.arrow-general)").first();

        String title = info.select("a[href]").first().text();

        String imageUrl = doc.select("#rightside > div:nth-child(1) > div.barContent > div:nth-child(2) > img").first().attr("src");

        String alternateTitle = info.select("p").get(0).select("a").text();

        String genresString = info.select("p").get(1).select("a").text();

        String status = info.select("p").get(2).ownText().substring(1, 12).trim();

        String desc = info.select("p").get(4).text();

        anime.setTitle(title)
             .setAlternateTitle(alternateTitle)
             //.setGenres(genres.toArray(new String[genres.size()]))
             .setGenresString(genresString)
             .setDesc(desc)
             .setStatus(status)
             .setImageUrl(imageUrl)
             .setDate("-");

        return anime;
    }

    private static List<Episode> parseEpisodes(Document doc) {
        Elements episodeElements = doc.select("#leftside > div:nth-child(4) > div.barContent.episodeList > div:nth-child(2) > table > tbody > tr > td > a");

        List<Episode> episodes = new ArrayList<>(episodeElements.size() / 2);

        for (Element episode : episodeElements) {
            String episodeTitle = episode.text();
            String episodeLink = episode.select("a[href]").attr("href");

            if (!episodeLink.isEmpty()) {
                episodes.add(new Episode()
                        .setTitle(episodeTitle)
                        .setUrl(Providers.KISS_BASE_URL + episodeLink));
            }
        }

        return episodes;
    }

    private List<Integer> decoderFromUtf8(byte[] s) {
        List<Integer> result = new ArrayList<>();
        int[] enc = {-1, -1, -1, -1};

        int position = 0;
        while (position < s.length) {
            enc[0] = DECODE_LOOKUP[s[position++]];
            enc[1] = DECODE_LOOKUP[s[position++]];
            result.add(enc[0] << 2 | enc[1] >> 4);

            enc[2] = DECODE_LOOKUP[s[position++]];
            if (enc[2] == 64)
                break;
            result.add(((enc[1] & 15) << 4) | (enc[2] >> 2));

            enc[3] = DECODE_LOOKUP[s[position++]];
            if (enc[3] == 64)
                break;
            result.add(((enc[2] & 3) << 6) | enc[3]);
        }

        return result;
    }

    private String decode(String s) {
         List<Integer> buffer = decoderFromUtf8(s.getBytes(Charset.forName("UTF-8")));
         StringBuilder result = new StringBuilder();

         int position = 0;
         while (position < buffer.size()) {
             if (buffer.get(position) < 128) {
                result.append(Character.toChars(buffer.get(position++)));
             } else if (buffer.get(position) > 191 && buffer.get(position) < 224) {
                int a = (buffer.get(position++) & 31) << 6;
                int b = (buffer.get(position++) & 63);
                result.append(Character.toChars(a | b));
             } else {
                int a = (buffer.get(position++) & 15) << 12;
                int b = (buffer.get(position++) & 63) << 6;
                int c = (buffer.get(position++) & 63);
                result.append(Character.toChars(a | b | c));
             }
         }
         return result.toString();
    }
}
