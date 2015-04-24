package li.vin.net;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ottowagner on 6/27/14.
 */
public interface Vehicles {

  @GET("/vehicles") Observable<Page<Vehicle>> getVehicles();

  @GET("/vehicles")
  Observable<Page<Vehicle>> getVehicles(@Query("limit") Integer limit, @Query("offset") Integer offset);
  @GET("/devices/{id}/vehicles/_latest") Observable<Vehicle> getLatestDeviceVehicle(@Path("id") String deviceId);
  @GET("/vehicles/{id}") Observable<Vehicle> getVehicle(@Path("id") String id);

}
