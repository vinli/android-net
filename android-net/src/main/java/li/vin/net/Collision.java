package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
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

  public static Observable<Collision> collisionWithId(@NonNull String collisionId){
    return Vinli.curApp().collision(collisionId);
  }
}
