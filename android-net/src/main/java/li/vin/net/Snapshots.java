package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/*package*/ interface Snapshots {

  @GET("devices/{deviceId}/snapshots")
  Observable<TimeSeries<Snapshot>> snapshots(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Query("fields") String fields,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET Observable<TimeSeries<Snapshot>> snapshotsForUrl(@NonNull @Url String url);
}
