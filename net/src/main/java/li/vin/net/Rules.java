package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Rules {

  @GET("/devices/{deviceId}/rules")
  Observable<Page<Rule>> rules(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("/devices/{deviceId}/rules/{ruleId}")
  Observable<Wrapped<Rule>> rule(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Path("ruleId") String ruleId);


  @POST("/devices/{deviceId}/rules")
  Observable<Wrapped<Rule>> create(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Body Rule.RuleSeed ruleSeed);
}
