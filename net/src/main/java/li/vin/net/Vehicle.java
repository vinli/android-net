package li.vin.net;

import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Vehicle implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<AutoParcel_Vehicle>>() { }.getType();

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
