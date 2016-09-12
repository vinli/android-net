package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Trips {

  @GET("/devices/{deviceId}/trips")
  Observable<TimeSeries<Trip>> trips(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("since") Date since,
      @Nullable @Query("until") Date until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("/vehicles/{vehicleId}/trips")
  Observable<TimeSeries<Trip>> vehicleTrips(
      @NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("since") Date since,
      @Nullable @Query("until") Date until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("/trips/{tripId}")
  Observable<Wrapped<Trip>> trip(@NonNull @Path("tripId") String tripId);

}
