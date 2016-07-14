package dulleh.akhyou;

import android.app.Application;
import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import dulleh.akhyou.Utils.OK;

@ReportsCrashes(
        formUri = "https://collector.tracepot.com/02e21e70"
)
public class MainApplication extends Application{
    public static int RED_ACCENT_RGB = 16777215;

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher (Context context) {
        return ((MainApplication) context.getApplicationContext()).refWatcher;
    }

    /**
     * We use a persistent Cookie storage to minimize the need of doing the high-latency connections
     * to Cloudflare protected servers.
     * The Android application's context is used to get a instance of SharedPreferences to load cookies from storage.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        refWatcher = LeakCanary.install(this);

        OK.INSTANCE.createClient(getApplicationContext());

        Picasso.setSingletonInstance(new Picasso.Builder(getApplicationContext())
                .downloader(new OkHttp3Downloader(OK.INSTANCE.Client))
                .build());
    }

}
