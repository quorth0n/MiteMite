package dulleh.akhyou.Utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public enum OK {
    INSTANCE;

    public OkHttpClient Client;

    public OkHttpClient createClient (Context context) {
        ClearableCookieJar cookieJar;
        try {
            cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        } catch (NullPointerException e) {
            e.printStackTrace();
            // TODO: fix the bug in SharedPrefsCookiePersistor that causes the need for this (null pointers on app update from dev -> release)
            context.getSharedPreferences("CookiePersistence", Context.MODE_PRIVATE).edit().clear().apply();
            cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        }

        Client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(new CloudflareInterceptor())
                //.connectTimeout(6, TimeUnit.SECONDS)
                .build();

        return Client;
    }
}
