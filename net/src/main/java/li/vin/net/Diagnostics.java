package li.vin.net;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by kyle on 6/22/14.
 */
/*package*/ interface Diagnostics {
  // TODO: real path
  @GET("/dtc_codes/{dtcCode}")
  Observable<Dtc> diagnoseDtcCode(@Path("dtcCode") String dtcCode);

}
