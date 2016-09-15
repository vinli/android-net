package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface Notifications {

  @GET("/notifications/{notificationId}") Observable<Wrapped<Notification>> notification(
      @NonNull @Path("notificationId") String notificationId);

  @GET("/subscriptions/{subscriptionId}/notifications")
  Observable<TimeSeries<Notification>> notificationsForSubscription(
      @NonNull @Path("subscriptionId") String subscriptionId, @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until, @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("/events/{eventId}/notifications")
  Observable<TimeSeries<Notification>> notificationsForEvent(
      @NonNull @Path("eventId") String eventId, @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until, @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);
}
