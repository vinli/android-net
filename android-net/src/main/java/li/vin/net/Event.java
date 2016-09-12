package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Event implements VinliItem {
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Event>>() { }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Event>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Event.class, AutoParcelAdapter.create(AutoParcel_Event.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Event_Links.class));
    gb.registerTypeAdapter(Meta.class, AutoParcelAdapter.create(AutoParcel_Event_Meta.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Event.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Event.class));
  }

  public abstract String eventType();
  public abstract String timestamp();
  public abstract String deviceId();
  public abstract Meta meta();
  @Nullable public abstract ObjectRef object();

  public Observable<Page<Notification>> notifications() {
    return Vinli.curApp().linkLoader().read(links().notifications(), Notification.PAGE_TYPE);
  }

  /*package*/ abstract Links links();

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String self();
//    public abstract String rules();
//    public abstract String vehicles();
//    public abstract String latestVehicle();
    public abstract String notifications();

    /*package*/ Links() { }
  }

  @AutoParcel
  public static abstract class Meta implements Parcelable {
    public abstract String direction();
    public abstract boolean firstEval();
    public abstract Rule rule();
    public abstract Message message();
  }
}
