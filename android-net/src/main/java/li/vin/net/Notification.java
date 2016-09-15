package li.vin.net;

import android.support.annotation.NonNull;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel public abstract class Notification implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Notification>>() {
  }.getType();
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Notification>>() {
  }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Notification>>() {
  }.getType();

  /*package*/
  static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Notification.class,
        AutoParcelAdapter.create(AutoParcel_Notification.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Notification.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE,
        TimeSeries.Adapter.create(TIME_SERIES_TYPE, Notification.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Notification.class));

    gb.registerTypeAdapter(Links.class,
        AutoParcelAdapter.create(AutoParcel_Notification_Links.class));
  }

  public static Observable<Notification> notificationWithId(@NonNull String notificationId) {
    return Vinli.curApp().notification(notificationId);
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

  /*package*/
  abstract Links links();

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();

    public abstract String subscription();

    public abstract String event();

    /*package*/ Links() {
    }
  }
}
