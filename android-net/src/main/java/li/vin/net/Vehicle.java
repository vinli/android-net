package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

  public static Observable<Vehicle> vehicleWithId(@NonNull String vehicleId) {
    return Vinli.curApp().vehicle(vehicleId);
  }

  public static Observable<Page<Vehicle>> vehiclesWithDeviceId(@NonNull String deviceId) {
    return Vehicle.vehiclesWithDeviceId(deviceId, null, null);
  }

  public static Observable<Page<Vehicle>> vehiclesWithDeviceId(@NonNull String deviceId,
      @Nullable Integer limit, @Nullable Integer offset) {
    return Vinli.curApp().vehicles().vehicles(deviceId, limit, offset);
  }

  public static Observable<Vehicle> latestVehicleWithDeviceId(@NonNull String deviceId) {
    return Vinli.curApp().vehicles().latestVehicle(deviceId).map(Wrapped.<Vehicle>pluckItem());
  }

  public Observable<TimeSeries<Trip>> trips() {
    return Vinli.curApp().trips().vehicleTrips(id(), null, null, null, null);
  }

  public Observable<TimeSeries<Trip>> trips(
      @Nullable Date since,
      @Nullable Date until,
      @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().trips().vehicleTrips(id(), sinceMs, untilMs, limit, sortDir);
  }

  public Observable<DistanceList> distances() {
    return Vinli.curApp().distances().distances(this.id(), null, null, null);
  }

  public Observable<DistanceList> distances(@Nullable String since, @Nullable String until,
      @Nullable DistanceUnit unit) {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.getDefault());
    Date sinceDate = null;
    Date untilDate = null;

    try {
      sinceDate = (since != null) ? format.parse(since) : null;
      untilDate = (until != null) ? format.parse(until) : null;
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return distances(sinceDate, untilDate, unit);
  }

  public Observable<DistanceList> distances(@Nullable Date since, @Nullable Date until,
      @Nullable DistanceUnit unit) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp()
        .distances()
        .distances(this.id(), sinceMs, untilMs, (unit == null) ? null : unit.getDistanceUnitStr());
  }

  public Observable<DistanceList.Distance> bestDistance(){
    return bestDistance(null);
  }

  public Observable<DistanceList.Distance> bestDistance(@Nullable DistanceUnit unit){
    return Vinli.curApp().distances().bestDistance(id(), (unit == null) ? null : unit.getDistanceUnitStr()).map(
        Wrapped.<DistanceList.Distance>pluckItem());
  }

  public Observable<TimeSeries<Odometer>> odometerReports(){
    return odometerReports(null, null, null, null);
  }

  public Observable<TimeSeries<Odometer>> odometerReports(@Nullable String since, @Nullable String until){
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.getDefault());
    Date sinceDate = null;
    Date untilDate = null;

    try {
      sinceDate = (since != null) ? format.parse(since) : null;
      untilDate = (until != null) ? format.parse(until) : null;
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return odometerReports(sinceDate, untilDate, null, null);
  }

  public Observable<TimeSeries<Odometer>> odometerReports(@Nullable Date since, @Nullable Date until, @Nullable Integer limit, @Nullable String sortDir){
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().distances().odometerReports(this.id(), sinceMs, untilMs, limit, sortDir);
  }

  public Observable<TimeSeries<OdometerTrigger>> odomterTriggers(){
    return odometerTriggers(null, null, null, null);
  }

  public Observable<TimeSeries<OdometerTrigger>> odometerTriggers(@Nullable String since, @Nullable String until){
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.getDefault());
    Date sinceDate = null;
    Date untilDate = null;

    try {
      sinceDate = (since != null) ? format.parse(since) : null;
      untilDate = (until != null) ? format.parse(until) : null;
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return odometerTriggers(sinceDate, untilDate, null, null);
  }

  public Observable<TimeSeries<OdometerTrigger>> odometerTriggers(@Nullable Date since, @Nullable Date until, @Nullable Integer limit, @Nullable String sortDir){
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().distances().odometerTriggers(this.id(), sinceMs, untilMs, limit, sortDir);
  }

  public Observable<TimeSeries<Collision>> collisions() {
    return collisions(null, null, null, null);
  }

  public Observable<TimeSeries<Collision>> collisions(@Nullable Date since, @Nullable Date until,
      @Nullable Integer limit, @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp()
        .collisions()
        .collisionsForVehicle(this.id(), sinceMs, untilMs, limit, sortDir);
  }

  public Observable<TimeSeries<ReportCard>> reportCards(@Nullable Date since, @Nullable Date until, @Nullable Integer limit, @Nullable String sortDir){
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().reportCards().reportCardsForVehicle(this.id(), sinceMs, untilMs, limit,
        sortDir);
  }

  public Observable<BatteryStatus> currentBatteryStatus(){
    return BatteryStatus.currentBatteryStatusForVehicle(this.id());
  }

  /*package*/ Vehicle() { }
}
