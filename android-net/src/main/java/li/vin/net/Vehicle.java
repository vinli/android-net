package li.vin.net;

import android.support.annotation.Nullable;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

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

  @Nullable public abstract String make();
  @Nullable public abstract String model();
  @Nullable public abstract String year();
  @Nullable public abstract String trim();
  @Nullable public abstract String vin();

  public Observable<TimeSeries<Trip>> trips() {
    return Vinli.curApp().trips().vehicleTrips(id(), null, null, null, null);
  }

  public Observable<TimeSeries<Trip>> trips(
      @Nullable Date since,
      @Nullable Date until,
      @Nullable Integer limit,
      @Nullable String sortDir) {
    return Vinli.curApp().trips().vehicleTrips(id(), since, until, limit, sortDir);
  }

  /*package*/ Vehicle() { }
}
