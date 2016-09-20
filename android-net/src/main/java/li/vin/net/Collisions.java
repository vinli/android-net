package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface Collisions {

  @GET("/collisions/{collisionId}")
  Observable<Wrapped<Collision>> collision(
      @NonNull @Path("collisionId") String collisionId);

  @GET("/vehicles/{vehicleId}/collisions")
  Observable<TimeSeries<Collision>> collisionsForVehicle(
      @NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("/devices/{deviceId}/collisions")
  Observable<TimeSeries<Collision>> collisionsForDevice(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

}
