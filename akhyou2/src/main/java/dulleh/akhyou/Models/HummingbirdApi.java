package dulleh.akhyou.Models;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.util.HashMap;
import java.util.Map;

import dulleh.akhyou.Utils.GeneralUtils;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import rx.exceptions.OnErrorThrowable;

public class HummingbirdApi {
    public static final String BASE_URL = "https://hummingbird.me";
    public static final String BASE_URL_V1 = "http://hummingbird.me/api/v1";

    public static final String STATUS_CURRENTLY_WATCHING = "currently-watching";
    public static final String STATUS_COMPLETED = "completed";
    public static final String PRIVACY_PUBLIC = "public";
    public static final String PRIVACY_PRIVATE = "private";

    private final HummingbirdService hummingbirdService;

    public HummingbirdApi () {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_V1) //uses api-v1 by default
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        hummingbirdService = retrofit.create(HummingbirdService.class);
    }

    public String getAuthToken (String usernameOrEmail, String password) {
        Map<String, String> userLoginData = new HashMap<>(2);

        if (usernameOrEmail.contains("@")) {
            userLoginData.put("email", usernameOrEmail);
        } else {
            userLoginData.put("username", usernameOrEmail);
        }

        userLoginData.put("password", password);

        return hummingbirdService.getAuthToken(userLoginData);
    }

    public static String getTitleFromRegularPage(String url) {
        String body = GeneralUtils.getWebPage(url.replace("anime/", "api/v1/anime/"));

        String title = null;

        try {

            JsonParser jsonParser = new JsonFactory().createParser(body);

            while (!jsonParser.isClosed()) {
                jsonParser.nextToken();

                if (jsonParser.getCurrentName() != null && jsonParser.getCurrentName().equals("title")) {
                    title = jsonParser.nextTextValue();
                    jsonParser.close();
                }

            }

        } catch (Exception e) {
            throw OnErrorThrowable.from(new Throwable("Hummingbird retrieval failed.", e));
        }

        return title;
        }

    public static void updateAnime (String url) {

    }

}
