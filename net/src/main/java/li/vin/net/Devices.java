package li.vin.net;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Devices {

  @GET("/devices") Observable<Page<Device>> devices();

  @GET("/devices")
  Observable<Page<Device>> devices(@Query("limit") Integer limit, @Query("offset") Integer offset);

  @GET("/devices/{id}") Observable<Device> device(@Path("id") String deviceId);

  @POST("/devices") Observable<Device> registerDevice(@Body Device device);

}
