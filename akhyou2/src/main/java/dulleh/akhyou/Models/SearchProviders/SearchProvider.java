package dulleh.akhyou.Models.SearchProviders;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Utils.CloudFlareInitializationException;
import rx.exceptions.OnErrorThrowable;

public interface SearchProvider {

    List<Anime> searchFor (String searchTerm) throws OnErrorThrowable, CloudFlareInitializationException;

    Element isolate (String document);

    boolean hasSearchResults (Element element) throws OnErrorThrowable;

}
