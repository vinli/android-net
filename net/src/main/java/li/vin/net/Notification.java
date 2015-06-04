package li.vin.net;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Notification implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Snapshot>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Notification.class, AutoParcelAdapter.create(Notification.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Notification.class));
  }

  public abstract String createdAt();
  public abstract String eventId();
  public abstract String eventTimestamp();
  public abstract String eventType();
  public abstract String notifiedAt();
  public abstract String payload();
  public abstract String respondedAt();
  public abstract String response();
  public abstract int responseCode();
  public abstract String state();
  public abstract String subscriptionId();
  public abstract String url();

  /*package*/ abstract Links links();

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();
    public abstract String subscription();
    public abstract String event();

    /*package*/ Links() { }
  }
}
