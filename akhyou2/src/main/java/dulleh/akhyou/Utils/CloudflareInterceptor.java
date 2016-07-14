package dulleh.akhyou.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CloudflareInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() == 503 && response.header("Server").equals("cloudflare-nginx")) {
            try {
                Response finalResponse = chain.proceed(CloudflareSolver.solveCloudflare(response));
                System.out.println("finalResponse headers: " + response.headers() + "    code: " + response.code() + "   is redirect: " + response.isRedirect());
                return finalResponse;
            } catch (InterruptedException e) { //TODO: error handling
                e.printStackTrace();
            } catch (CloudflareException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

}
