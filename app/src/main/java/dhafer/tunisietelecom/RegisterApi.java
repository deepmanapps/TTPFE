package dhafer.tunisietelecom;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Dhafer ZAHROUNI on 22/03/2016.
 */
public interface RegisterApi {

    @FormUrlEncoded
    @POST("/insert.php")
    public void insertcalldata(
            @Field("number") String number,
            @Field("type") String type,
            @Field("duration") String duration,
            @Field("date") String date,
            @Field("result") String result,
            Callback<Response> callback);



}
