package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import java.util.Date;
import rx.Observable;

@AutoParcel
public abstract class Collision implements VinliItem{
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Collision>>() { }.getType();
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Collision>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Collision.class, AutoParcelAdapter.create(AutoParcel_Collision.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Collision.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Collision.class));
  }

  @NonNull public abstract String deviceId();
  @NonNull public abstract String vehicleId();
  @NonNull public abstract String timestamp();
  @Nullable public abstract Location location();

  public static Observable<Collision> collisionWithId(@NonNull String collisionId) {
    return Vinli.curApp().collision(collisionId);
  }

  public static Observable<TimeSeries<Collision>> collisionsWithDeviceId(@NonNull String deviceId) {
    return collisionsWithDeviceId(deviceId, (Long) null, null, null, null);
  }

  @Deprecated
  public static Observable<TimeSeries<Collision>> collisionsWithDeviceId(@NonNull String deviceId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp()
        .collisions()
        .collisionsForDevice(deviceId, sinceMs, untilMs, limit, sortDir);
  }

  public static Observable<TimeSeries<Collision>> collisionsWithDeviceId(@NonNull String deviceId,
      @Nullable Long sinceMs, @Nullable Long untilMs, @Nullable Integer limit,
      @Nullable String sortDir) {
    return Vinli.curApp().collisions().collisionsForDevice(deviceId, sinceMs, untilMs, limit, sortDir);
  }

  public static Observable<TimeSeries<Collision>> collisionsWithVehicleId(
      @NonNull String vehicleId) {
    return collisionsWithVehicleId(vehicleId, (Long) null, null, null, null);
  }

  @Deprecated
  public static Observable<TimeSeries<Collision>> collisionsWithVehicleId(@NonNull String vehicleId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return collisionsWithVehicleId(vehicleId, sinceMs, untilMs, limit, sortDir);
  }

  public static Observable<TimeSeries<Collision>> collisionsWithVehicleId(@NonNull String vehicleId,
      @Nullable Long sinceMs, @Nullable Long untilMs, @Nullable Integer limit,
      @Nullable String sortDir) {
    return Vinli.curApp()
        .collisions()
        .collisionsForVehicle(vehicleId, sinceMs, untilMs, limit, sortDir);
  }
}
