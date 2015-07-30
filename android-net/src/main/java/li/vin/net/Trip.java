package li.vin.net;

import android.os.Parcelable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import rx.Observable;

@AutoParcel
public abstract class Trip implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Trip>>() { }.getType();
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Trip>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Trip.class, AutoParcelAdapter.create(AutoParcel_Trip.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Trip_Links.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Trip.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Trip.class));
  }
  public abstract String start();
  public abstract String stop();
  public abstract String status();
  public abstract String vehicleId();
  public abstract String deviceId();

  public Observable<Device> device() {
    return Vinli.curApp().device(deviceId());
  }

  public Observable<Vehicle> vehicle() {
    return Vinli.curApp().vehicle(vehicleId());
  }

  public Observable<TimeSeries<Location>> locations() {
    return Vinli.curApp().linkLoader().read(links().locations(), Location.TIME_SERIES_TYPE);
  }

  //public Observable<TimeSeries<Message>> messages() {
  //  return Vinli.curApp().linkLoader().read(links().messages(), Message.TIME_SERIES_TYPE);
  //}

  public Observable<TimeSeries<Event>> events() {
    return Vinli.curApp().linkLoader().read(links().events(), Event.TIME_SERIES_TYPE);
  }

  /*package*/ abstract Links links();

  /*package*/ Trip() { }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String self();
    public abstract String device();
    public abstract String vehicle();
    public abstract String locations();
    //public abstract String messages();
    public abstract String events();

    /*package*/ Links() { }
  }
}
