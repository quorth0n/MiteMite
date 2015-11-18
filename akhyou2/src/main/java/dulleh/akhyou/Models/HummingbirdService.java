package dulleh.akhyou.Models;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface HummingbirdService {

    @POST("/users/authenticate")
    String getAuthToken (@QueryMap Map<String, String> usernameAndPassword);

    @POST("/libraries/{id}")
    String updateLibraryEntry (@Path("id") String id,
                               @Header("auth_token") String authToken,
                               @Header("status") String status,
                               @Header("privacy") String privacy,
                               @Header("episodes_watched") int episodesWatched);

}
