package li.vin.net;

import android.support.annotation.NonNull;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by JoshBeridon on 11/18/16.
 */

/*package*/ interface Dummies {

  @GET("dummies")
  Observable<Page<Dummy>> dummies();

  @GET("dummies/{dummyId}")
  Observable<Wrapped<Trip>> trip(@NonNull @Path("dummyId") String dummyId);

  @GET Observable<TimeSeries<Dummy>> dummiesForUrl(@NonNull @Url String url);
}
