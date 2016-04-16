package dulleh.akhyou.Utils;

import android.content.Context;

import dulleh.akhyou.LazyLibs.PersistentCookieJar.ClearableCookieJar;
import dulleh.akhyou.LazyLibs.PersistentCookieJar.PersistentCookieJar;
import dulleh.akhyou.LazyLibs.PersistentCookieJar.cache.SetCookieCache;
import dulleh.akhyou.LazyLibs.PersistentCookieJar.persistence.SharedPrefsCookiePersistor;
import okhttp3.OkHttpClient;

public enum OK {
    INSTANCE;

    public OkHttpClient Client;

    public OkHttpClient createClient (Context context) {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        Client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        return Client;
    }

}
