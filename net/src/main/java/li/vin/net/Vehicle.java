package li.vin.net;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Vehicle implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Vehicle>>() { }.getType();
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Vehicle>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Vehicle.class, AutoParcelAdapter.create(AutoParcel_Vehicle.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Vehicle.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Vehicle.class));
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Vehicle.Builder();
  }

  public abstract String make();
  public abstract String model();
  public abstract String year();
  public abstract String trim();
  public abstract String vin();

  /*package*/ Vehicle() { }

  @AutoParcel.Builder
  interface Builder {
    Builder id(String s);
    Builder make(String s);
    Builder model(String s);
    Builder year(String s);
    Builder trim(String s);
    Builder vin(String s);
    Vehicle build();
  }
}
