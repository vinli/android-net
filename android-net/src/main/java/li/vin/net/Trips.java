package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/*package*/ interface Trips {

  @GET("devices/{deviceId}/trips")
  Observable<TimeSeries<Trip>> trips(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("vehicles/{vehicleId}/trips")
  Observable<TimeSeries<Trip>> vehicleTrips(
      @NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("trips/{tripId}")
  Observable<Wrapped<Trip>> trip(@NonNull @Path("tripId") String tripId);

  @GET Observable<TimeSeries<Trip>> tripsForUrl(@NonNull @Url String url);

}
