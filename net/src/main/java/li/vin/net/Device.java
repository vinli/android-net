package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Device implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Device>>() { }.getType();
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Device>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Device.class, AutoParcelAdapter.create(AutoParcel_Device.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Device_Links.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Device.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Device.class));
  }

  /*package*/ abstract Links links();

  /*package*/ Device() { }

  public Observable<Page<Vehicle>> vehicles() {
    return Vinli.curApp().linkLoader().read(links().vehicles(), Vehicle.PAGE_TYPE);
  }

  public Observable<Vehicle> latestVehicle() {
    return Vinli.curApp().linkLoader()
        .<Wrapped<Vehicle>>read(links().latestVehicle(), Vehicle.WRAPPED_TYPE)
        .map(Wrapped.<Vehicle>pluckItem());
  }

  public Observable<Page<Rule>> rules() {
    return Vinli.curApp().rules().rules(id(), null, null);
  }

  public Observable<Rule> rule(@NonNull String ruleId) {
    return Vinli.curApp().rules().rule(id(), ruleId).map(Wrapped.<Rule>pluckItem());
  }

  public Observable<TimeSeries<Event>> events() {
    return events(null, null, null, null, null);
  }

  public Observable<TimeSeries<Event>> events(
      @Nullable String type,
      @Nullable String objectId,
      @Nullable Date since,
      @Nullable Date until,
      @Nullable Integer limit) {
    return Vinli.curApp().events().events(id(), type, objectId, since, until, limit);
  }

  public Observable<TimeSeries<Location>> locations() {
    return locations(null, null, null, null, null);
  }

  public Observable<TimeSeries<Location>> locations(
      @Nullable String fields,
      @Nullable Date until,
      @Nullable Date since,
      @Nullable Integer limit,
      @Nullable String sortDir) {
    return Vinli.curApp().locations().locations(id(), fields, until, since, limit, sortDir);
  }

  public Observable<Location> latestlocation() {
    return locations(null, null, null, 1, null)
        .flatMap(TimeSeries.<Location>extractItems())
        .firstOrDefault(null);
  }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String self();
    public abstract String rules();
    public abstract String vehicles();
    public abstract String latestVehicle();

    /*package*/ Links() { }
  }
}
