package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import rx.Observable;

@AutoParcel
public abstract class OdometerTrigger implements VinliItem{

  public enum TriggerType{
    SPECIFIC("specific"),
    FROM_NOW("from_now"),
    MILESTONE("milestone");

    private String typeStr;

    private TriggerType(String unit){
      this.typeStr = unit;
    }

    /*package*/ String getTriggerTypeStr(){
      return this.typeStr;
    }
  }

  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<OdometerTrigger>>() { }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<OdometerTrigger>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(OdometerTrigger.class, AutoParcelAdapter.create(AutoParcel_OdometerTrigger.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_OdometerTrigger_Links.class));
    gb.registerTypeAdapter(AutoParcel_OdometerTrigger_Seed.class, new Seed.Adapter());

    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(OdometerTrigger.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, OdometerTrigger.class));
  }

  public abstract String vehicleId();
  public abstract TriggerType type();
  public abstract Double threshold();
  public abstract Double events();

  /*package*/ abstract Links links();

  public static final Seed.Saver create() {
    return new AutoParcel_OdometerTrigger_Seed.Builder();
  }

  public Observable<Void> delete(){
    return Vinli.curApp().distances().deleteOdometerTrigger(id());
  }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String vehicle();

    /*package*/ Links() { }
  }

  @AutoParcel
  public static abstract class Seed{
    @NonNull public abstract String vehicleId();
    @NonNull public abstract TriggerType type();
    @NonNull public abstract Double threshold();
    @NonNull public abstract DistanceUnit unit();

    /*package*/ Seed() { }

    @AutoParcel.Builder
    public static abstract class Saver{
      public abstract Saver vehicleId(@NonNull String vehicleId);
      public abstract Saver type(@NonNull TriggerType type);
      public abstract Saver threshold(@NonNull Double threshold);
      public abstract Saver unit(@NonNull DistanceUnit unit);

      /*package*/ Saver() {}

      /*package*/ abstract Seed autoBuild();

      public Observable<OdometerTrigger> save() {
        final Seed s = autoBuild();

        return Vinli.curApp().distances().createOdometerTrigger(s.vehicleId(), s)
            .map(Wrapped.<OdometerTrigger>pluckItem());
      }
    }

    /*package*/ static final class Adapter extends TypeAdapter<Seed> {
      private Gson gson;

      @Override public void write(JsonWriter out, Seed value) throws IOException {
        if (gson == null) {
          gson = Vinli.curApp().gson();
        }

        out.beginObject();
          out.name("odometerTrigger").beginObject();
            out.name("type").value(value.type().getTriggerTypeStr());
            out.name("threshold").value(value.threshold());
            out.name("unit").value(value.unit().getDistanceUnitStr());
          out.endObject();
        out.endObject();
      }

      @Override public Seed read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("reading a OdometerTriggerSeed is not supported");
      }
    }
  }
}
