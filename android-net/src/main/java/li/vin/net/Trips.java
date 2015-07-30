package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Trips {

  @GET("/devices/{deviceId}/trips")
  Observable<Page<Trip>> trips(@NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("/vehicles/{vehicleId}/trips")
  Observable<Page<Trip>> vehicleTrips(@NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("/trips/{tripId}")
  Observable<Wrapped<Trip>> trip(@NonNull @Path("tripId") String tripId);

}
