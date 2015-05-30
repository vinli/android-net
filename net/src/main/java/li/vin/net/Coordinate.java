package li.vin.net;

import android.os.Parcelable;

import com.google.gson.GsonBuilder;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Coordinate implements Parcelable {
  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(
        Coordinate.class,
        AutoParcelAdapter.create(AutoParcel_Coordinate.class));
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Coordinate.Builder();
  }

  public abstract float lon();
  public abstract float lat();

  @AutoParcel.Builder
    /*package*/ interface Builder {
    Builder lon(float f);
    Builder lat(float f);

    Coordinate build();
  }

  /*package*/ Coordinate() { }
}
