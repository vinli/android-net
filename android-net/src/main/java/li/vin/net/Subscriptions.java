package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/*package*/ interface Subscriptions {

  @GET("devices/{deviceId}/subscriptions")
  Observable<Page<Subscription>> subscriptions(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset,
      @Nullable @Query("objectId") String objectId,
      @Nullable @Query("objectType") String objectType);

  @GET("subscriptions/{subscriptionId}")
  Observable<Wrapped<Subscription>> subscription(
      @NonNull @Path("subscriptionId") String subscriptionId);

  @POST("devices/{deviceId}/subscriptions")
  Observable<Wrapped<Subscription>> create(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Body Subscription.SeedCreate seedCreate);

  @PUT("devices/{deviceId}/subscriptions/{subscriptionId}")
  Observable<Wrapped<Subscription>> edit(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Path("subscriptionId") String subscriptionId,
      @NonNull @Body Subscription.SeedEdit seedEdit);

  @DELETE("subscriptions/{subscriptionId}")
  Observable<Void> delete(@NonNull @Path("subscriptionId") String subscriptionId);

  @GET Observable<Page<Subscription>> subscriptionsForUrl(@NonNull @Url String url);

}
