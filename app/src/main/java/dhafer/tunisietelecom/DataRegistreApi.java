package dhafer.tunisietelecom;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
/**
 * Created by Dhafer ZAHROUNI on 28/03/2016.
 */
public interface DataRegistreApi {
    @FormUrlEncoded
    @POST("/insertdata.php")
    public void insertdatatest(
            @Field("date") String date,
            @Field("ping") CharSequence ping,
            @Field("downlink") CharSequence downlink,
            @Field("uplink") CharSequence uplink,
            Callback<Response> callback);
}
