package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import java.util.Date;
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

  public static Observable<Event> eventWithId(@NonNull String eventId){
    return Vinli.curApp().event(eventId);
  }

  public static Observable<TimeSeries<Event>> eventsWithDeviceId(@NonNull String deviceId){
    return eventsWithDeviceId(deviceId, null, null, null, null, null, null);
  }

  public static Observable<TimeSeries<Event>> eventsWithDeviceId(@NonNull String deviceId,
      @Nullable String type, @Nullable String objectId, @Nullable Date since, @Nullable Date until,
      @Nullable Integer limit, @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp()
        .events()
        .events(deviceId, type, objectId, sinceMs, untilMs, limit, sortDir);
  }

  public abstract String eventType();
  public abstract String timestamp();
  public abstract String deviceId();
  public abstract Meta meta();
  @Nullable public abstract ObjectRef object();

  public Observable<TimeSeries<Notification>> notifications() {
    return Vinli.curApp().notifications().notificationsForEvent(this.id(), null, null, null, null);
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
