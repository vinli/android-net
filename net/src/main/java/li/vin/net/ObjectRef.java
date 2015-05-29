package li.vin.net;

import com.google.gson.GsonBuilder;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ObjectRef implements VinliItem {
  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(ObjectRef.class, AutoParcelAdapter.create(AutoParcel_ObjectRef.class));
  }

  public abstract String type();

  /*package*/ ObjectRef() { }
}
