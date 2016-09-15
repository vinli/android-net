package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Vehicles {

  @GET("/devices/{deviceId}/vehicles")
  Observable<Page<Vehicle>> vehicles(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("/devices/{deviceId}/vehicles/_latest")
  Observable<Wrapped<Vehicle>> latestVehicle(
      @NonNull @Path("deviceId") String deviceId);

  @GET("/vehicles/{vehicleId}")
  Observable<Wrapped<Vehicle>> vehicle(
      @NonNull @Path("vehicleId") String vehicleId);

}
