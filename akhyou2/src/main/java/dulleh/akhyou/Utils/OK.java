package dulleh.akhyou.Utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.OkHttpClient;

public enum OK {
    INSTANCE;

    public OkHttpClient Client;

    public OkHttpClient createClient (Context context) {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        Client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(new CloudflareInterceptor())
                .build();

        return Client;
    }

}
