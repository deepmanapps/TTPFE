package dhafer.tunisietelecom;


import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;


/**
 * Created by Dhafer ZAHROUNI on 23/03/2016.
 */
public interface GetVoiceCallApi {
    @GET("/callreport.php")
    public void getreports(Callback<List<CallReport>> response);

}
