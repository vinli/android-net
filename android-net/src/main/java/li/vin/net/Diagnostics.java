package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/*package*/ interface Diagnostics {

  @GET("vehicles/{vehicleId}/codes")
  Observable<TimeSeries<Dtc>> codes(@NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("codes")
  Observable<Page<Dtc.Code>> diagnose(@NonNull @Query("number") String number);

  @GET("vehicles/{vehicleId}/battery_statuses/_current")
  Observable<Wrapped<BatteryStatus>> currentBatteryStatus(@NonNull @Path("vehicleId") String vehicleId);

  @GET Observable<TimeSeries<Dtc>> codesForUrl(@NonNull @Url String url);

}
