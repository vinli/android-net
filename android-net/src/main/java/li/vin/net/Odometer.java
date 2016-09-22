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
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import rx.Observable;

@AutoParcel
public abstract class Odometer implements VinliItem{
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Odometer>>() { }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Odometer>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Odometer.class, AutoParcelAdapter.create(AutoParcel_Odometer.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Odometer_Links.class));
    gb.registerTypeAdapter(AutoParcel_Odometer_Seed.class, new Seed.Adapter());

    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Odometer.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Odometer.class));
  }

  public static Observable<Odometer> odometerWithId(@NonNull String odometerId) {
    return Vinli.curApp().odometerReport(odometerId);
  }

  public static Observable<TimeSeries<Odometer>> odometersWithVehicleId(@NonNull String vehicleId) {
    return odometersWithVehicleId(vehicleId, (Long) null, null, null, null);
  }

  public static Observable<TimeSeries<Odometer>> odometersWithVehicleId(@NonNull String vehicleId,
      @Nullable Long sinceMs, @Nullable Long untilMs, @Nullable Integer limit,
      @Nullable String sortDir) {
    return Vinli.curApp().distances().odometerReports(vehicleId, sinceMs, untilMs, limit, sortDir);
  }

  @Deprecated
  public static Observable<TimeSeries<Odometer>> odometersWithVehicleId(@NonNull String vehicleId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().distances().odometerReports(vehicleId, sinceMs, untilMs, limit, sortDir);
  }

  public abstract String vehicleId();
  public abstract Double reading();
  public abstract String date();

  /*package*/ abstract Links links();

  public static final Seed.Saver create() {
    return new AutoParcel_Odometer_Seed.Builder();
  }

  public Observable<Void> delete(){
    return Vinli.curApp().distances().deleteOdometerReport(id());
  }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String vehicle();

    /*package*/ Links() { }
  }

  @AutoParcel
  public static abstract class Seed{
    @NonNull public abstract Double reading();
    @Nullable public abstract String date();
    @NonNull public abstract DistanceUnit unit();
    @NonNull public abstract String vehicleId();

    /*package*/ Seed() { }

    @AutoParcel.Builder
    public static abstract class Saver{
      public abstract Saver reading(@NonNull Double reading);
      public abstract Saver date(@Nullable String date);
      public abstract Saver unit(@NonNull DistanceUnit unit);
      public abstract Saver vehicleId(@NonNull String vehicleId);

      /*package*/ Saver() {}

      /*package*/ abstract Seed autoBuild();

      public Observable<Odometer> save() {
        final Seed s = autoBuild();

        return Vinli.curApp().distances().createOdometerReport(s.vehicleId(), s)
            .map(Wrapped.<Odometer>pluckItem());
      }
    }

    /*package*/ static final class Adapter extends TypeAdapter<Seed> {
      private Gson gson;

      @Override public void write(JsonWriter out, Seed value) throws IOException {
        if (gson == null) {
          gson = Vinli.curApp().gson();
        }

        out.beginObject();
          out.name("odometer").beginObject();
            out.name("reading").value(value.reading());

            final String date = value.date();
            if(date != null){
              out.name("date").value(value.date());
            }
            out.name("unit").value(value.unit().getDistanceUnitStr());
          out.endObject();
        out.endObject();
      }

      @Override public Seed read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("reading a OdometerSeed is not supported");
      }
    }
  }
}
