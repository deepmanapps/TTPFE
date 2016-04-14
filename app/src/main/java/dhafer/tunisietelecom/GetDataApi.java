package dhafer.tunisietelecom;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Dhafer ZAHROUNI on 28/03/2016.
 */
public interface GetDataApi {
    @GET("/datareport.php")
    public void getreports(Callback<List<DataReportObject>> response);
}
