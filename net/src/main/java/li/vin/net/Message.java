package li.vin.net;

import com.google.gson.GsonBuilder;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Message implements VinliItem {
  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Message.class, AutoParcelAdapter.create(AutoParcel_Message.class));
  }

  public abstract String timestamp();

  /*package*/ Message() { }
}
