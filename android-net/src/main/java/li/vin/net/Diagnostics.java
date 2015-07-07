package li.vin.net;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/*package*/ interface Diagnostics {
  // TODO: real path
  @GET("/dtc_codes/{dtcCode}")
  Observable<Dtc> diagnoseDtcCode(@Path("dtcCode") String dtcCode);

}
