package li.vin.net;

import android.support.annotation.Nullable;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Vehicle implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Vehicle>>() { }.getType();
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Vehicle>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Vehicle.class, AutoParcelAdapter.create(AutoParcel_Vehicle.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Vehicle.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Vehicle.class));
  }

  public abstract String make();
  public abstract String model();
  public abstract String year();
  public abstract String trim();
  public abstract String vin();

  public Observable<Page<Trip>> trips() {
    return Vinli.curApp().trips().vehicleTrips(id(), null, null);
  }

  public Observable<Page<Trip>> trips(
      @Nullable Integer limit,
      @Nullable Integer offset) {
    return Vinli.curApp().trips().vehicleTrips(id(), limit, offset);
  }

  /*package*/ Vehicle() { }
}
