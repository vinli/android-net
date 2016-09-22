package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/*package*/ interface Locations {

  @GET("devices/{deviceId}/locations")
  Observable<TimeSeries<Location>> locations(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET Observable<TimeSeries<Location>> locationsForUrl(@NonNull @Url String url);

}
