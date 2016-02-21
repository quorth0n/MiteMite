package dulleh.akhyou.Utils;

import android.content.Context;

import dulleh.akhyou.Lib.PersistentCookieJar.ClearableCookieJar;
import dulleh.akhyou.Lib.PersistentCookieJar.PersistentCookieJar;
import dulleh.akhyou.Lib.PersistentCookieJar.cache.SetCookieCache;
import dulleh.akhyou.Lib.PersistentCookieJar.persistence.SharedPrefsCookiePersistor;
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
