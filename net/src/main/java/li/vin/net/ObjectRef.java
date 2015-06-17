package li.vin.net;

import com.google.gson.GsonBuilder;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ObjectRef implements VinliItem {
  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(ObjectRef.class, AutoParcelAdapter.create(AutoParcel_ObjectRef.class));
  }

  public static final Builder builder() {
    return new AutoParcel_ObjectRef.Builder();
  }

  public abstract String type();

  /*package*/ ObjectRef() { }

  @AutoParcel.Builder
  /*package*/ public static abstract class Builder{
    public abstract Builder id(String s);
    public abstract Builder type(String s);

    public abstract ObjectRef build();
  /*package*/ Builder(){  }
  }
}
