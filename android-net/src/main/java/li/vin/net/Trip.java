package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import auto.parcel.AutoParcel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import rx.Observable;

@AutoParcel
public abstract class Trip implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Trip>>() { }.getType();
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Trip>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Trip.class, AutoParcelAdapter.create(AutoParcel_Trip.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Trip_Links.class));
    gb.registerTypeAdapter(Stats.class, AutoParcelAdapter.create(AutoParcel_Trip_Stats.class));
//    gb.registerTypeAdapter(Point.class, AutoParcelAdapter.create(AutoParcel_Trip_Point.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Trip.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Trip.class));
    Point.registerGson(gb);
  }
  public abstract String start();
  public abstract String stop();
  public abstract String status();
  public abstract String vehicleId();
  public abstract String deviceId();
  @Nullable public abstract Point startPoint();
  @Nullable public abstract Point stopPoint();
  @Nullable public abstract String preview();
  public abstract Stats stats();

  public static Observable<Trip> tripWithId(@NonNull String tripId) {
    return Vinli.curApp().trip(tripId);
  }

  public static Observable<TimeSeries<Trip>> tripsWithDeviceId(@NonNull String deviceId) {
    return tripsWithDeviceId(deviceId, null, null, null, null);
  }

  public static Observable<TimeSeries<Trip>> tripsWithDeviceId(@NonNull String deviceId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().trips().trips(deviceId, sinceMs, untilMs, limit, sortDir);
  }

  public static Observable<TimeSeries<Trip>> tripsWithVehicleId(@NonNull String vehicleId) {
    return tripsWithVehicleId(vehicleId, null, null, null, null);
  }

  public static Observable<TimeSeries<Trip>> tripsWithVehicleId(@NonNull String vehicleId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().trips().vehicleTrips(vehicleId, sinceMs, untilMs, limit, sortDir);
  }

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

  public Observable<ReportCard> reportCard(){
    return Vinli.curApp().reportCards().reportCardForTrip(this.id()).map(Wrapped.<ReportCard>pluckItem());
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

  @AutoParcel
  public static abstract class Stats implements Parcelable {
    public abstract float averageLoad();
    public abstract float averageMovingSpeed();
    public abstract float averageSpeed();
    public abstract float distance();
    public abstract float distanceByGPS();
    public abstract float distanceByVSS();
    public abstract long duration();
    public abstract float fuelConsumed();
    public abstract float fuelEconomy();
    public abstract int hardAccelCount();
    public abstract int hardBrakeCount();
    public abstract int locationCount();
    public abstract float maxSpeed();
    public abstract int messageCount();
    public abstract float stdDevMovingSpeed();
    public abstract int stopCount();
  }

  @AutoParcel
  public static abstract class Point implements Parcelable{
    @Nullable public abstract Coordinate coordinates();

    /*package*/ Point() { }

    /*package*/ static final void registerGson(GsonBuilder gb) {
      gb.registerTypeAdapter(Point.class, new PointAdapter());
    }

    /*package*/ static final Builder builder() {
      return new AutoParcel_Trip_Point.Builder();
    }

    @AutoParcel.Builder
    /*package*/ interface Builder{
      Builder coordinates(@Nullable Coordinate coordinate);

      Point build();
    }


    private static final class PointAdapter extends TypeAdapter<Point>{
      @Override
      public void write(JsonWriter out, Point value) throws IOException {
        throw new UnsupportedOperationException("Writing a Point is not supported");
      }

      @Override
      public Point read(JsonReader in) throws IOException {
        Point.Builder b = Point.builder();
        Gson gson = Vinli.curApp().gson();
        boolean hasValidCoordinates = false;

        if(in.peek() == JsonToken.NULL){
          in.nextNull();
          return null;
        }

        in.beginObject();

        while(in.hasNext()) {
          String name = in.nextName();

          switch(name){
            case "coordinates":
              if(in.peek() == JsonToken.NULL){
                in.nextNull();
                b.coordinates(null);
                hasValidCoordinates = false;
              }else{
                b.coordinates(gson.<Coordinate>fromJson(in, Coordinate.class));
                hasValidCoordinates = true;
              }
              break;
            case "type":
              if(in.peek() == JsonToken.NULL){
                in.nextNull();
              }else{
                in.nextString();
              }
              break;
          }
        }

        in.endObject();

        if(!hasValidCoordinates){
          return null;
        }

        return b.build();
      }
    }

  }
}
