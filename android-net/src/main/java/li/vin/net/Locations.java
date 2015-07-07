package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Locations {

  @GET("/devices/{deviceId}/locations")
  Observable<TimeSeries<Location>> locations(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("fields") String fields,
      @Nullable @Query("until") Date until,
      @Nullable @Query("since") Date since,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

}
