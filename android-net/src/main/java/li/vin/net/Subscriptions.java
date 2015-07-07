package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Subscriptions {

  @GET("/devices/{deviceId}/subscriptions")
  Observable<Page<Subscription>> subscriptions(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset,
      @Nullable @Query("objectId") String objectId,
      @Nullable @Query("objectType") String objectType);

  @GET("/devices/{deviceId}/subscriptions/{subscriptionId}")
  Observable<Wrapped<Subscription>> subscription(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Path("subscriptionId") String subscriptionId);

  @POST("/devices/{deviceId}/subscriptions")
  Observable<Wrapped<Subscription>> create(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Body Subscription.SeedCreate seedCreate);

  @PUT("/devices/{deviceId}/subscriptions/{subscriptionId}")
  Observable<Wrapped<Subscription>> edit(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Path("subscriptionId") String subscriptionId,
      @NonNull @Body Subscription.SeedEdit seedEdit);

}
