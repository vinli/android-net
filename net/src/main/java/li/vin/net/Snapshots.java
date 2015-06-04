package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Snapshots {

  @GET("/devices/{deviceId}/snapshots")
  Observable<TimeSeries<Snapshot>> snapshots(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("fields") String fields,
      @Nullable @Query("since") Date since,
      @Nullable @Query("until") Date until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

}
