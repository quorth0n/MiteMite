package dulleh.akhyou.Models.SearchProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.AnimeProviders.KissAnimeProvider;
import dulleh.akhyou.Models.Providers;
import dulleh.akhyou.Utils.CloudFlareInitializationException;
import dulleh.akhyou.Utils.GeneralUtils;
import okhttp3.Request;
import okhttp3.Response;
import rx.exceptions.OnErrorThrowable;

public class KissSearchProvider implements SearchProvider {
    private static final Pattern PARSER = Pattern.compile(".*src=\"(.*?)\".*href=\"(.*)\">(.*?)</a>.*<p>\\s*(.*?)\\s*</p>", Pattern.DOTALL);

    private int retries = 0;

    @Override
    public List<Anime> searchFor(String searchTerm) throws OnErrorThrowable, CloudFlareInitializationException {

        //if (!CloudflareHttpClient.INSTANCE.isInitialized()) {
        //    throw new CloudFlareInitializationException();
        //}

/*
This was meant for /AdvanceSearch but no longer works after an update to the site. Not sure why.
        RequestBody query = searchTemplate()
                .add("animeName", searchTerm)
                .build();

        Request search = new Request.Builder()
                .url(Providers.KISS_SEARCH_URL)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0")
                .addHeader("Referer", Providers.KISS_SEARCH_URL)
                .post(query)
                .build();
*/
        Request search = new Request.Builder()
                .url(Providers.KISS_SEARCH_URL + GeneralUtils.encodeForUtf8(searchTerm))
                .addHeader("Referer", Providers.KISS_BASE_URL)
                .build();

        Response response = GeneralUtils.makeRequest(search);

        String responseBody;

        try {
            responseBody = response.body().string();
        } catch (IOException io) {
            throw OnErrorThrowable.from(new Throwable("Conection failed."));
        }

        if (responseBody.isEmpty()) {
            throw OnErrorThrowable.from(new Throwable("Connection failed."));
        }

        // handle redirects straight to anime page (for searches with only one result)
        if (!response.request().url().pathSegments().contains("Search")) {
            List<Anime> animes = new ArrayList<>(1);
            animes.add(KissAnimeProvider.parse(Jsoup.parse(responseBody), response.request().url().toString()));
            return animes;
        }

        Element resultTable = isolate(responseBody);

        if (resultTable == null) {

            if (responseBody.contains("allow_5_secs")) {

                if (retries < 2) {
                    retries++;
                    GeneralUtils.getWebPage(Providers.KISS_BASE_URL);
                    searchFor(searchTerm);
                } else {
                    throw new CloudFlareInitializationException();
                }

            } else {
                throw OnErrorThrowable.from(new Throwable("Parsing failed."));
            }

        }

        return parseElements(resultTable.children().removeClass("head"));
    }

    @Override
    public Element isolate(String document) {
        return Jsoup.parse(document).select("#leftside > div > div.barContent.full > div.listing.full").first();
    }

    @Override
    public boolean hasSearchResults(Element element) throws OnErrorThrowable {
        return element.select("td[title]").size() > 0;
    }

    private List<Anime> parseElements(Elements rows) {
        List<Anime> results = new ArrayList<>(rows.size());
        for (Element row : rows) { //ignore headings in divs
            String popupHtml = row.children().attr("title");

            if (popupHtml != null && !popupHtml.isEmpty()) {
                Anime anime = new Anime().setProviderType(Providers.KISS);

                Document popup = Jsoup.parse(popupHtml);

                Element title = popup.select("a.bigChar").first();
                anime.setTitle(title.text());
                anime.setUrl("http:" + title.attr("href"));

                anime.setImageUrl("http:" + popup.select("img").first().attr("src"));
                anime.setDesc(popup.select("p").text());

                results.add(anime);
            }
        }
        return results;
    }
/*
This was meant for /AdvanceSearch but no longer works after an update to the site. Not sure why.

    private static final int NUM_GENRES = 47;

    private FormBody.Builder searchTemplate() {
        FormBody.Builder searchTemplate = new FormBody.Builder();
        for (int i = 0; i < NUM_GENRES; ++i) {
            searchTemplate.add("genres", "0");
        }
        searchTemplate.add("status", "");
        return searchTemplate;
    }

*/
}
