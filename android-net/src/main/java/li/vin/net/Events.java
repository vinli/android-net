package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Events {

  @GET("/devices/{deviceId}/events")
  Observable<TimeSeries<Event>> events(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("type") String type,
      @Nullable @Query("objectId") String objectId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("/events/{eventId}")
  Observable<Wrapped<Event>> event(@NonNull @Path("eventId") String eventId);

}
