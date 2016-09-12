package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
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

    /*package*/ static TriggerType getEnumFromString(String str){
      switch(str){
        case "specific":
          return SPECIFIC;
        case "from_now":
          return FROM_NOW;
        case "milestone":
          return MILESTONE;
        default:
          throw new IllegalArgumentException("str is not a valid string to be used for TriggerType");
      }
    }
  }

  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<OdometerTrigger>>() { }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<OdometerTrigger>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(OdometerTrigger.class, new OdometerTriggerAdapter());
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_OdometerTrigger_Links.class));
    gb.registerTypeAdapter(AutoParcel_OdometerTrigger_Seed.class, new Seed.Adapter());

    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(OdometerTrigger.class, "odometerTrigger"));
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, OdometerTrigger.class, "odometerTriggers"));
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

  @AutoParcel.Builder
  /*package*/ static abstract class Builder{
    public abstract Builder id(String id);
    public abstract Builder vehicleId(String vehicleId);
    public abstract Builder type(TriggerType type);
    public abstract Builder threshold(Double threshold);
    public abstract Builder events(Double events);
    public abstract Builder links(Links links);

    public abstract OdometerTrigger build();
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

  private static final class OdometerTriggerAdapter extends TypeAdapter<OdometerTrigger> {
    private Gson gson;

    @Override public void write(JsonWriter out, OdometerTrigger value) throws IOException {
      throw new UnsupportedOperationException("writing an OdometerTrigger is not supported");
    }

    @Override public OdometerTrigger read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      final OdometerTrigger.Builder b = new AutoParcel_OdometerTrigger.Builder();

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        switch (name) {
          case "id": b.id(in.nextString()); break;
          case "vehicleId": b.vehicleId(in.nextString()); break;
          case "type": b.type(TriggerType.getEnumFromString(in.nextString())); break;
          case "threshold": b.threshold(in.nextDouble()); break;
          case "events": b.events(in.nextDouble()); break;
          case "links": b.links(gson.<OdometerTrigger.Links>fromJson(in, Links.class)); break;
          default: throw new JsonParseException("unknown rule key " + name);
        }
      }
      in.endObject();

      return b.build();
    }
  }
}
