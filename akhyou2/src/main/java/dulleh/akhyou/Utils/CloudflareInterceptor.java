package dulleh.akhyou.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CloudflareInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response initialResponse = chain.proceed(chain.request());

        if (initialResponse.code() == 503 && initialResponse.header("Server").equals("cloudflare-nginx")) {
            try {
                return chain.proceed(CloudflareSolver.solveCloudflare(initialResponse));
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
