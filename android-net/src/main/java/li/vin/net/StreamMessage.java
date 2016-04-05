package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.JsonWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import auto.parcel.AutoParcel;
import rx.Observable;
import rx.functions.Func1;

public final class StreamMessage {

  public enum DataType {

    RPM("rpm"), //
    VEHICLE_SPEED("vehicleSpeed"), //
    MASS_AIRFLOW("massAirFlow"), //
    CALCULATED_ENGINE_LOAD("calculatedLoadValue"),
    ENGINE_COOLANT_TEMP("coolantTemp"),
    THROTTLE_POSITION("absoluteThrottleSensorPosition"),
    TIME_SINCE_ENGINE_START("runTimeSinceEngineStart"),
    FUEL_PRESSURE("fuelPressure"),
    INTAKE_AIR_TEMP("intakeAirTemperature"),
    INTAKE_MANIFOLD_PRESSURE("intakeManifoldPressure"),
    TIMING_ADVANCE("timingAdvance"),
    FUEL_RAIL_PRESSURE("fuelRailPressure");

    @NonNull private final String name;

    DataType(@NonNull String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public static Func1<StreamMessage, Observable<String>> onlyWithRawVal(
      @NonNull final DataType dataType) {
    return new Func1<StreamMessage, Observable<String>>() {
      @Override
      public Observable<String> call(StreamMessage message) {
        String val = message.rawVal(dataType);
        if (val == null) return Observable.empty();
        return Observable.just(val);
      }
    };
  }

  public static Func1<StreamMessage, Observable<Double>> onlyWithDoubleVal(
      @NonNull final DataType dataType) {
    return new Func1<StreamMessage, Observable<Double>>() {
      @Override
      public Observable<Double> call(StreamMessage message) {
        double val = message.doubleVal(dataType, Integer.MIN_VALUE / 2);
        if (Double.compare(val, Integer.MIN_VALUE / 2) == 0) return Observable.empty();
        return Observable.just(val);
      }
    };
  }

  public static Func1<StreamMessage, Observable<Long>> onlyWithLongVal(
      @NonNull final DataType dataType) {
    return new Func1<StreamMessage, Observable<Long>>() {
      @Override
      public Observable<Long> call(StreamMessage message) {
        long val = message.longVal(dataType, Integer.MIN_VALUE / 2);
        if (val == Integer.MIN_VALUE / 2) return Observable.empty();
        return Observable.just(val);
      }
    };
  }

  public static Func1<StreamMessage, Observable<Float>> onlyWithFloatVal(
      @NonNull final DataType dataType) {
    return new Func1<StreamMessage, Observable<Float>>() {
      @Override
      public Observable<Float> call(StreamMessage message) {
        float val = message.floatVal(dataType, Integer.MIN_VALUE / 2);
        if (Float.compare(val, Integer.MIN_VALUE / 2) == 0) return Observable.empty();
        return Observable.just(val);
      }
    };
  }

  public static Func1<StreamMessage, Observable<Integer>> onlyWithIntVal(
      @NonNull final DataType dataType) {
    return new Func1<StreamMessage, Observable<Integer>>() {
      @Override
      public Observable<Integer> call(StreamMessage message) {
        int val = message.intVal(dataType, Integer.MIN_VALUE / 2);
        if (val == Integer.MIN_VALUE / 2) return Observable.empty();
        return Observable.just(val);
      }
    };
  }

  public static Func1<StreamMessage, Observable<Coordinate>> coordinate(){
    return new Func1<StreamMessage, Observable<Coordinate>>() {
      @Override
      public Observable<Coordinate> call(StreamMessage message) {
        Coordinate coordinate = message.coord();
        if(coordinate == null){
          return Observable.empty();
        }
        return Observable.just(coordinate);
      }
    };
  }

  /*
  {
    "type": "pub",
    "subject": {
      "type": "device",
      "id": "device-uuid-goes-here"
    },
    "payload": {
      "id": "payload-uuid-goes-here",
      "timestamp": "2016-03-02T05:09:17.145Z",
      "data": {
        "location": {
          "type": "Point",
          "coordinates": [-96.728254, 32.81202]
        },
        "accel": {
          "maxZ": 6.282417,
          "maxX": -2.911364,
          "maxY": -2.489982,
          "minX": -7.853021,
          "minY": -10.649463,
          "minZ": -0.804456
        },
        "vehicleSpeed": 61,
        "rpm": 1376,
        "calculatedLoadValue": 42.745098039215684,
        "massAirFlow": 7.24,
        "intakeManifoldPressure": 58,
        "coolantTemp": 74,
        "shortTermFuelTrimBank1": -0.78125,
        "longTermFuelTrimBank1": -8.59375,
        "timingAdvance": 31,
        "intakeAirTemperature": 26,
        "absoluteThrottleSensorPosition": 18.03921568627451
      }
    }
  }
   */

  private String type;
  private StreamMessageSubject subject;
  private StreamMessagePayload payload;

  StreamMessage() {
  }

  @Nullable
  public AccelData accel() {
    try {
      return new Gson().fromJson(rawVal("accel"), AccelData.class);
    } catch (Exception ignored) {
    }
    return null;
  }

  @Nullable
  public Coordinate coord() {
    try {
      if (payload == null || payload.data == null) return null;
      Object locObj = payload.data.get("location");
      if (!(locObj instanceof LinkedTreeMap)) return null;
      //noinspection unchecked
      Object coordObj = ((LinkedTreeMap<String, Object>) locObj).get("coordinates");
      Object[] coords;
      if (coordObj instanceof Collection) {
        coords = ((Collection) coordObj).toArray();
      } else if (coordObj instanceof Object[]) {
        coords = (Object[]) coordObj;
      } else {
        return null;
      }
      return Coordinate.builder() //
          .lat(((Number) coords[1]).floatValue()) //
          .lon(((Number) coords[0]).floatValue()) //
          .build();
    } catch (Exception ignored) {
    }
    return null;
  }

  @Nullable
  public String rawVal(@NonNull String key) {
    try {
      if (payload == null || payload.data == null) return null;
      Object val = payload.data.get(key);
      if (val == null) return null;
      if (val instanceof String) return (String) val;
      if (val instanceof Number) return val.toString();
      if (val instanceof LinkedTreeMap) {
        //noinspection unchecked
        return convertLinkedTreeMapToJSON((LinkedTreeMap<String, Object>) val).toString();
      }
      if (val instanceof Collection) {
        //noinspection unchecked
        return convertCollectionToJSON((Collection<Object>) val).toString();
      }
      if (val instanceof Object[]) {
        //noinspection unchecked
        return convertArrayToJSON((Object[]) val).toString();
      }
      if (val instanceof JSONObject || val instanceof JSONArray) {
        return val.toString();
      }
    } catch (Exception ignored) {
    }
    return null;
  }

  @Nullable
  public String rawVal(@NonNull DataType dataType) {
    return rawVal(dataType.toString());
  }

  public double doubleVal(@NonNull String key, double def) {
    String raw = rawVal(key);
    if (raw != null) {
      try {
        return Double.parseDouble(raw);
      } catch (NumberFormatException ignored) {
      }
    }
    return def;
  }

  public double doubleVal(@NonNull DataType dataType, double def) {
    return doubleVal(dataType.toString(), def);
  }

  public long longVal(@NonNull String key, long def) {
    return Math.round(doubleVal(key, def));
  }

  public long longVal(@NonNull DataType dataType, long def) {
    return longVal(dataType.toString(), def);
  }

  public float floatVal(@NonNull String key, float def) {
    return Double.valueOf(doubleVal(key, def)).floatValue();
  }

  public float floatVal(@NonNull DataType dataType, float def) {
    return floatVal(dataType.toString(), def);
  }

  public int intVal(@NonNull String key, int def) {
    return Long.valueOf(longVal(key, def)).intValue();
  }

  public int intVal(@NonNull DataType dataType, int def) {
    return intVal(dataType.toString(), def);
  }

  static class StreamMessageSubject {

    private String id;
    private String type;

    StreamMessageSubject() {
    }
  }

  static class StreamMessagePayload {

    private String id;
    private String timestamp;
    private LinkedTreeMap<String, Object> data;

    StreamMessagePayload() {
    }
  }

  public static final class AccelData {

    /*
    "accel": {
        "maxZ": 6.282417,
        "maxX": -2.911364,
        "maxY": -2.489982,
        "minX": -7.853021,
        "minY": -10.649463,
        "minZ": -0.804456
      }
     */

    private Double maxX;
    private Double maxY;
    private Double maxZ;
    private Double minX;
    private Double minY;
    private Double minZ;

    AccelData() {
    }

    public double maxX() {
      if (maxX == null) return 0d;
      return maxX;
    }

    public double maxY() {
      if (maxY == null) return 0d;
      return maxY;
    }

    public double maxZ() {
      if (maxZ == null) return 0d;
      return maxZ;
    }

    public double minX() {
      if (minX == null) return 0d;
      return minX;
    }

    public double minY() {
      if (minY == null) return 0d;
      return minY;
    }

    public double minZ() {
      if (minZ == null) return 0d;
      return minZ;
    }

    @Override
    public String toString() {
      return "AccelData{" +
          "maxX=" + maxX +
          ", maxY=" + maxY +
          ", maxZ=" + maxZ +
          ", minX=" + minX +
          ", minY=" + minY +
          ", minZ=" + minZ +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AccelData accelData = (AccelData) o;
      return !(maxX != null
          ? !maxX.equals(accelData.maxX)
          : accelData.maxX != null) && !(maxY != null
          ? !maxY.equals(accelData.maxY)
          : accelData.maxY != null) && !(maxZ != null
          ? !maxZ.equals(accelData.maxZ)
          : accelData.maxZ != null) && !(minX != null
          ? !minX.equals(accelData.minX)
          : accelData.minX != null) && !(minY != null
          ? !minY.equals(accelData.minY)
          : accelData.minY != null) && !(minZ != null
          ? !minZ.equals(accelData.minZ)
          : accelData.minZ != null);
    }

    @Override
    public int hashCode() {
      int result = maxX != null
          ? maxX.hashCode()
          : 0;
      result = 31 * result + (maxY != null
          ? maxY.hashCode()
          : 0);
      result = 31 * result + (maxZ != null
          ? maxZ.hashCode()
          : 0);
      result = 31 * result + (minX != null
          ? minX.hashCode()
          : 0);
      result = 31 * result + (minY != null
          ? minY.hashCode()
          : 0);
      result = 31 * result + (minZ != null
          ? minZ.hashCode()
          : 0);
      return result;
    }
  }

  @NonNull
  private static JSONObject convertLinkedTreeMapToJSON(LinkedTreeMap<String, Object> ltm) {
    JSONObject jo = new JSONObject();
    Object[] objs = ltm.entrySet().toArray();
    for (Object obj : objs) {
      Map.Entry o = (Map.Entry) obj;
      try {
        if (o.getValue() instanceof LinkedTreeMap) {
          //noinspection unchecked
          jo.put(o.getKey().toString(),
              convertLinkedTreeMapToJSON((LinkedTreeMap<String, Object>) o.getValue()));
        } else {
          jo.put(o.getKey().toString(), o.getValue());
        }
      } catch (JSONException ignored) {
      }
    }
    return jo;
  }

  @NonNull
  private static JSONArray convertCollectionToJSON(@NonNull Collection<Object> col) {
    JSONArray ja = new JSONArray();
    for (Object o : col) {
      if (o instanceof LinkedTreeMap) {
        //noinspection unchecked
        ja.put(convertLinkedTreeMapToJSON((LinkedTreeMap<String, Object>) o));
      } else {
        ja.put(o);
      }
    }
    return ja;
  }

  @NonNull
  private static JSONArray convertArrayToJSON(@NonNull Object[] arr) {
    JSONArray ja = new JSONArray();
    for (Object o : arr) {
      if (o instanceof LinkedTreeMap) {
        //noinspection unchecked
        ja.put(convertLinkedTreeMapToJSON((LinkedTreeMap<String, Object>) o));
      } else {
        ja.put(o);
      }
    }
    return ja;
  }

  @AutoParcel
  public static abstract class ParametricFilter{

    /*package*/ static final String TYPE = "parametric";
    public abstract String parameter();
    public abstract Float min();
    public abstract Float max();
    public abstract String deviceId();

    /*package*/ ParametricFilter(){}

    /*package*/ static final void registerGson(GsonBuilder gb) {
      gb.registerTypeAdapter(AutoParcel_StreamMessage_ParametricFilter.Seed.class, new Seed.Adapter());
    }

    public static final Seed.Builder create(){
      return new AutoParcel_StreamMessage_ParametricFilter_Seed.Builder();
    }

    @AutoParcel
    public static abstract class Seed{
      private final String type = ParametricFilter.TYPE;
      @NonNull public abstract String parameter();
      @Nullable public abstract Float min();
      @Nullable public abstract Float max();
      @Nullable public abstract String deviceId();

      /*package*/ Seed(){}

      @AutoParcel.Builder
      public static abstract class Builder{
        public abstract Builder parameter(@NonNull String parameter);
        public abstract Builder min(@Nullable Float min);
        public abstract Builder max(@Nullable Float max);
        public abstract Builder deviceId(@Nullable String deviceId);

        public abstract Seed build();

        /*package*/ Builder(){}
      }

      /*package*/ static final class Adapter extends TypeAdapter<ParametricFilter.Seed>{

        private Gson gson;

        @Override
        public void write(com.google.gson.stream.JsonWriter out, Seed value) throws IOException {
          if (gson == null) {
            gson = Vinli.curApp().gson();
          }

          out.beginObject();
            out.name("type").value("filter");
            if(value.deviceId() != null){
              out.name("id").value(value.deviceId());
            }
            out.name("filter").beginObject();
              out.name("type").value(value.type);
              out.name("parameter").value(value.parameter());
              if(value.min() != null){
                out.name("min").value(value.min());
              }
              if(value.max() != null){
                out.name("max").value(value.max());
              }
            out.endObject();
          out.endObject();
        }

        @Override public ParametricFilter.Seed read(JsonReader in) throws IOException {
          throw new UnsupportedOperationException("reading a ParametricFilter.Seed is not supported");
        }
      }
    }
  }

  @AutoParcel
  public static abstract class GeometricFilter{

    public enum Direction{
      INSIDE("inside"),
      OUTSIDE("outside");

      private String str;

      private Direction(String str){
        this.str = str;
      }

      /*package*/ String getDirectionAsString(){
        return this.str;
      }
    }

    /*package*/ static final String TYPE = "geometric";
    public abstract Direction direction();
    public abstract List<Coordinate> geometry();

    /*package*/ GeometricFilter(){}

    /*package*/ static final void registerGson(GsonBuilder gb) {
      gb.registerTypeAdapter(AutoParcel_StreamMessage_GeometricFilter.Seed.class, new Seed.Adapter());
    }

    public static final Seed.Builder create(){
      return new AutoParcel_StreamMessage_GeometricFilter_Seed.Builder();
    }

    @AutoParcel
    public static abstract class Seed{
      private final String type = GeometricFilter.TYPE;
      @NonNull public abstract Direction direction();
      @NonNull public abstract List<Coordinate> geometry();

      /*package*/ Seed(){}

      @AutoParcel.Builder
      public static abstract class Builder{
        public abstract Builder direction(@NonNull Direction direction);
        public abstract Builder geometry(@NonNull List<Coordinate> geometry);

        public abstract Seed build();

        /*package*/ Builder(){}
      }

      /*package*/ static final class Adapter extends TypeAdapter<GeometricFilter.Seed>{

        private Gson gson;

        @Override
        public void write(com.google.gson.stream.JsonWriter out, Seed value) throws IOException {
          if (gson == null) {
            gson = Vinli.curApp().gson();
          }

          out.beginObject();
            out.name("type").value("filter");
            out.name("filter").beginObject();
              out.name("type").value(value.type);
              out.name("direction").value(value.direction().getDirectionAsString());
              out.name("geometry").beginObject();
                out.name("type").value("FeatureCollection");
                out.name("features").beginArray();
                  for(Coordinate coord : value.geometry()){
                    out.beginObject();
                      out.name("type").name("Feature");
                      out.name("properties").beginObject().endObject();
                      out.name("geometry").beginObject();
                        out.name("type").name("Point");
                        out.name("coordinates").value(gson.toJson(coord));
                      out.endObject();
                    out.endObject();
                  }
                out.endArray();
              out.endObject();
            out.endObject();
          out.endObject();
        }

        @Override public GeometricFilter.Seed read(JsonReader in) throws IOException {
          throw new UnsupportedOperationException("reading a GeometricFilter.Seed is not supported");
        }
      }
    }
  }
}
