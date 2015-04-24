package li.vin.net;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by kyle on 6/21/14.
 */
/*package*/ interface Devices {

  @GET("/devices") Observable<Page<Device>> getDevices();

  @GET("/devices")
  Observable<Page<Device>> getDevices(@Query("limit") Integer limit, @Query("offset") Integer offset);

  @GET("/devices/{id}") Observable<Device> getDevice(@Path("id") String deviceId);

  @POST("/devices") Observable<Device> registerDevice(@Body Device device);

}
