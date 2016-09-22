package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/*package*/ interface Devices {

  @GET("devices")
  Observable<Page<Device>> devices(
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("devices/{deviceId}")
  Observable<Wrapped<Device>> device(
      @NonNull @Path("deviceId") String deviceId);

  @GET Observable<Page<Device>> devicesForUrl(@NonNull @Url String url);

}
