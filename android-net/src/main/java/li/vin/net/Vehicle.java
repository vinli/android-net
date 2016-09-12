package li.vin.net;

import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Date;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Vehicle implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Vehicle>>() { }.getType();
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Vehicle>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Vehicle.class, AutoParcelAdapter.create(AutoParcel_Vehicle.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Vehicle.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Vehicle.class));
  }

  @Nullable public abstract String make();
  @Nullable public abstract String model();
  @Nullable public abstract String year();
  @Nullable public abstract String trim();
  @Nullable public abstract String vin();

  public Observable<TimeSeries<Trip>> trips() {
    return Vinli.curApp().trips().vehicleTrips(id(), null, null, null, null);
  }

  public Observable<TimeSeries<Trip>> trips(
      @Nullable Date since,
      @Nullable Date until,
      @Nullable Integer limit,
      @Nullable String sortDir) {
    return Vinli.curApp().trips().vehicleTrips(id(), since, until, limit, sortDir);
  }

  public Observable<DistanceList> distances(){
    return distances(null, null, null);
  }

  public Observable<DistanceList> distances(@Nullable String since, @Nullable String until, @Nullable DistanceUnit unit){
    return Vinli.curApp().distances().distances(id(), since, until, (unit == null) ? null : unit.getDistanceUnitStr());
  }

  public Observable<DistanceList.Distance> bestDistance(){
    return bestDistance(null);
  }

  public Observable<DistanceList.Distance> bestDistance(@Nullable DistanceUnit unit){
    return Vinli.curApp().distances().bestDistance(id(), (unit == null) ? null : unit.getDistanceUnitStr()).map(Wrapped.<DistanceList.Distance>pluckItem());
  }

  public Observable<TimeSeries<Odometer>> odometerReports(){
    return odometerReports(null, null);
  }

  public Observable<TimeSeries<Odometer>> odometerReports(@Nullable String since, @Nullable String until){
    return Vinli.curApp().distances().odometerReports(id(), since, until);
  }

  public Observable<TimeSeries<OdometerTrigger>> odometerTriggers(){
    return odometerTriggers(null, null);
  }

  public Observable<TimeSeries<OdometerTrigger>> odometerTriggers(@Nullable String since, @Nullable String until){
    return Vinli.curApp().distances().odometerTriggers(id(), since, until);
  }

  /*package*/ Vehicle() { }
}
