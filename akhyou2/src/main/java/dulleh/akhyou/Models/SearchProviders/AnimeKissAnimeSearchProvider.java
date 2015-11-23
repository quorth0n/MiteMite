package dulleh.akhyou.Models.SearchProviders;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class AnimeKissAnimeSearchProvider implements SearchProvider {
    private static final String BASE_URL = "https://kissanime.to";
    private static final String SEARCH_URL = "https://kissanime.to/AdvanceSearch";
    private static final Pattern PARSER = Pattern.compile(".*src=\"(.*?)\".*href=\"(.*)\">(.*?)</a>.*<p>\\s*(.*?)\\s*</p>", Pattern.DOTALL);
    private static final int NUM_GENRES = 47;

    @Override
    public List<Anime> searchFor(String searchTerm) throws OnErrorThrowable {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw OnErrorThrowable.from(new Throwable("Please enter a search term."));
        }

        RequestBody query = searchTemplate()
                .add("animeName", searchTerm)
                .build();

        Request search = new Request.Builder()
                .url(SEARCH_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(query)
                .build();

        String responseBody = GeneralUtils.getWebPage(search);
        Element resultTable = isolate(responseBody);

        return parseElements(resultTable.select("td[title]"));
    }

    @Override
    public Element isolate(String document) {
        return Jsoup.parse(document).select(".listing").first();
    }

    @Override
    public boolean hasSearchResults(Element element) throws OnErrorThrowable {
        return element.select("td[title]").size() > 0;
    }

    private List<Anime> parseElements(Elements rows) {
        List<Anime> results = new ArrayList<>(rows.size());
        for (Element row : rows) {
            Anime anime = new Anime().setProviderType(Anime.ANIME_KISS);
            String titleTag = row.attr("title");
            Matcher matcher = PARSER.matcher(titleTag);
            if (matcher.find()) {
                anime.setImageUrl(matcher.group(1))
                     .setUrl(BASE_URL + matcher.group(2))
                     .setTitle(matcher.group(3))
                     .setDesc(matcher.group(4));
            }
            results.add(anime);
        }
        return results;
    }

    private FormEncodingBuilder searchTemplate() {
        FormEncodingBuilder searchTemplate = new FormEncodingBuilder();
        for (int i = 0; i < NUM_GENRES; ++i) {
            searchTemplate.add("genres", "0");
        }
        searchTemplate.add("status", "");
        return searchTemplate;
    }
}
