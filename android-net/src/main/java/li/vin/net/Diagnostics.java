package li.vin.net;

import android.support.annotation.NonNull;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/*package*/ interface Diagnostics {

  @GET("vehicles/{vehicleId}/codes")
  Observable<Page<Dtc>> codes(@NonNull @Path("vehicleId") String vehicleId);

  @GET("codes")
  Observable<Page<Dtc.Code>> diagnose(@NonNull @Query("number") String number);

}
