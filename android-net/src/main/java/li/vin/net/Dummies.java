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

/**
 * Created by JoshBeridon on 11/18/16.
 */

/*package*/ interface Dummies {

  @GET("dummies")
  Observable<Page<Dummy>> dummies(
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @POST("dummies/{dummyId}/runs")
  Observable<Wrapped<Dummy.Run>> create(
      @NonNull @Path("dummyId") String dummyId,
      @NonNull @Body Dummy.Run.Seed runSeed);

  @GET("dummies/{dummyId}/runs/_current")
  Observable<Wrapped<Dummy.Run>> currentRun(
      @NonNull @Path("dummyId") String dummyId);

  @GET("dummies/{dummyId}")
  Observable<Wrapped<Dummy>> trip(@NonNull @Path("dummyId") String dummyId);

  @DELETE("dummies/{dummyId}/runs/_current")
  Observable<Void> deleteRun(@NonNull @Path("dummyId") String dummyId);

  @GET Observable<TimeSeries<Dummy>> dummiesForUrl(@NonNull @Url String url);
}
