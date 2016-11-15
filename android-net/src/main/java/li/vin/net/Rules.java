package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/*package*/ interface Rules {

  @GET("devices/{deviceId}/rules")
  Observable<Page<Rule>> rules(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("vehicles/{vehicleId}/rules")
  Observable<Page<Rule>> vehicleRules(
      @NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("rules/{ruleId}")
  Observable<Wrapped<Rule>> rule(
      @NonNull @Path("ruleId") String ruleId);


  @POST("devices/{deviceId}/rules")
  Observable<Wrapped<Rule>> create(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Body Rule.Seed ruleSeed);

  @DELETE("rules/{ruleId}")
  Observable<Void> delete(@NonNull @Path("ruleId") String ruleId);

  @GET Observable<Page<Rule>> rulesForUrl(@NonNull @Url String url);
}
