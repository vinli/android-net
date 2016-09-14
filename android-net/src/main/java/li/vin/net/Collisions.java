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
  Observable<Page<Collision>> collisionsByVehicle(
      @NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @GET("/devices/{deviceId}/collisions")
  Observable<Page<Collision>> collisionsByDevice(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

}
