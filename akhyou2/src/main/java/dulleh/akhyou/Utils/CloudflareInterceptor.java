package dulleh.akhyou.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CloudflareInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response initialResponse = chain.proceed(chain.request());
        System.out.println(initialResponse.request().url() + " (init) cookies: " + OK.INSTANCE.Client.cookieJar().loadForRequest(initialResponse.request().url()));

        if (initialResponse.code() == 503 && initialResponse.header("Server").equals("cloudflare-nginx")) {
            try {
                Response finalResponse = chain.proceed(CloudflareSolver.solveCloudflare(initialResponse));
                //initialResponse..string() has to be here or causes IllegalStateException in solveCloudflare()
                //cannot call .string() on finalResponse here because used in GeneralUtils.getWebPage()
                //System.out.println("initialResponse body: " + initialResponse.body().string());
                System.out.println("finalResponse headers: " + finalResponse.headers() + "    code: " + finalResponse.code() + "   is redirect: " + finalResponse.isRedirect());
                System.out.println(finalResponse.request().url() + " (final) cookies: " + OK.INSTANCE.Client.cookieJar().loadForRequest(finalResponse.request().url()));
                return finalResponse;
            } catch (InterruptedException e) { //TODO: error handling
                e.printStackTrace();
            } catch (CloudflareException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return initialResponse;
    }

}
