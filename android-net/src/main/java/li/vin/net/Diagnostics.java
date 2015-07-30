package li.vin.net;

import android.support.annotation.NonNull;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Diagnostics {

  @GET("/vehicles/{vehicleId}/codes")
  Observable<Page<Dtc>> codes(@NonNull @Path("vehicleId") String vehicleId);

  @GET("/codes")
  Observable<Wrapped<Dtc.Code>> diagnose(@NonNull @Query("number") String number);

}
