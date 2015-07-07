package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Devices {

  @GET("/devices")
  Observable<Page<Device>> devices(
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("/devices/{deviceId}")
  Observable<Wrapped<Device>> device(
      @NonNull @Path("deviceId") String deviceId);

}
