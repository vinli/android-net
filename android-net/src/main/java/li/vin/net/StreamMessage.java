package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import li.vin.net.Message.AccelData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import rx.Observable;
import rx.Subscriber;
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

  public static Func1<StreamMessage, Observable<Coordinate>> coordinate() {
    return new Func1<StreamMessage, Observable<Coordinate>>() {
      @Override
      public Observable<Coordinate> call(StreamMessage message) {
        Coordinate coordinate = message.coord();
        if (coordinate == null) {
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

  private static final float ACCEL_CONVERT = (9.807f / 16384f);

  /*package*/
  static void processRawLine(@Nullable String line, @NonNull Duktaper dt, //
      @NonNull Subscriber<? super StreamMessage> subscriber) {
    if (subscriber.isUnsubscribed()) return; // sanity
    // no empty lines, min valid line length is 2
    if (line == null || (line = line.trim()).length() < 2) return;

    String pfx;
    int colonIndex = line.indexOf(':');
    if (colonIndex != -1) {
      if (colonIndex == line.length() - 1) return; // can't end with colon
      pfx = line.substring(0, colonIndex);
      line = line.substring(colonIndex + 1);
    } else if (line.length() == 2) {
      pfx = line;
      line = null;
    } else if (line.startsWith("41")) {
      pfx = "41";
      line = line.substring(2);
    } else {
      return;
    }

    if (pfx.equals("41") && line != null && line.length() > 2) {
      processObd(line.substring(0, 2), line.substring(2), dt, subscriber);
    } else if (pfx.equals("A") && line != null && line.length() >= 14) {
      StreamMessage sm = emptyStreamMessage();
      boolean accelFound = false;
      try {
        sm.payload.data.put("udpCollision",
            Integer.valueOf(line.substring(line.length() - 2), 16) == 0
                ? Boolean.FALSE
                : Boolean.TRUE);
        accelFound = true;
      } catch (Exception ignored) {
      }
      try {
        float xAccel = Integer.valueOf(line.substring(0, 4), 16).shortValue() * ACCEL_CONVERT;
        float yAccel = Integer.valueOf(line.substring(4, 8), 16).shortValue() * ACCEL_CONVERT;
        float zAccel = Integer.valueOf(line.substring(8, 12), 16).shortValue() * ACCEL_CONVERT;
        sm.payload.data.put("accel", new AccelData(xAccel, yAccel, zAccel, xAccel, yAccel, zAccel));
        accelFound = true;
      } catch (Exception ignored) {
      }
      if (accelFound) subscriber.onNext(sm);
    } else if (pfx.equals("G") && line != null) {
      try {
        String[] split = line.split(",");
        if (split.length == 2) {
          double lat = Double.parseDouble(split[0]);
          double lon = Double.parseDouble(split[1]);
          StreamMessage sm = emptyStreamMessage();
          LinkedTreeMap<String, Object> loc = new LinkedTreeMap<>();
          sm.payload.data.put("location", loc);
          loc.put("type", "Point");
          loc.put("coordinates", new Object[] { lon, lat });
          subscriber.onNext(sm);
        }
      } catch (Exception ignored) {
      }
    } else if (pfx.equals("S") && line != null) {
      StreamMessage sm = emptyStreamMessage();
      String[] split = line.split(",");
      boolean sigFound = false;
      if (split.length > 0) {
        String sigType = split[0];
        if (sigType != null && !(sigType = sigType.trim()).isEmpty() && //
            !sigType.equalsIgnoreCase("null")) {
          sm.payload.data.put("udpSignalType", sigType);
          sigFound = true;
        }
      }
      if (split.length > 1) {
        try {
          sm.payload.data.put("udpSignalStrength", Integer.parseInt(split[1].trim()));
          sigFound = true;
        } catch (Exception ignored) {
        }
      }
      if (sigFound) subscriber.onNext(sm);
    } else if (pfx.equals("B") && line != null && line.length() == 4) {
      StreamMessage sm = emptyStreamMessage();
      boolean voltageFound = false;
      try {
        sm.payload.data.put("udpBatteryVoltage", Integer.valueOf(line, 16) * 0.006);
        voltageFound = true;
      } catch (Exception ignored) {
      }
      if (voltageFound) subscriber.onNext(sm);
    } else if (pfx.equals("SVER") && line != null) {
      StreamMessage sm = emptyStreamMessage();
      sm.payload.data.put("udpStmVersion", line);
      subscriber.onNext(sm);
    } else if (pfx.equals("HVER") && line != null) {
      StreamMessage sm = emptyStreamMessage();
      sm.payload.data.put("udpHeVersion", line);
      subscriber.onNext(sm);
    } else if (pfx.equals("BVER") && line != null) {
      StreamMessage sm = emptyStreamMessage();
      sm.payload.data.put("udpBleVersion", line);
      subscriber.onNext(sm);
    } else if (pfx.equals("K") && line != null) {
      StreamMessage sm = emptyStreamMessage();
      boolean supportedPidsFound = false;
      try {
        sm.payload.data.put("udpSupportedPids", new SupportedPids(line).getSupport());
        supportedPidsFound = true;
      } catch (Exception ignored) {
      }
      if (supportedPidsFound) subscriber.onNext(sm);
    } else if (pfx.equals("V") && line != null) {
      if (!line.startsWith("NULL") && line.matches("^[A-Z0-9]{17}$")) {
        StreamMessage sm = emptyStreamMessage();
        sm.payload.data.put("udpVin", line);
        subscriber.onNext(sm);
      }
    } else if (pfx.equals("D") && line != null) {
      StreamMessage sm = emptyStreamMessage();
      boolean foundDtcs = false;
      try {
        String[] split = line.split(",");
        Set<String> dtcs = new HashSet<>();
        for (String s : split) {
          if (s != null && TextUtils.getTrimmedLength(s) != 0) {
            dtcs.add(s);
          }
        }
        sm.payload.data.put("udpDtcs", dtcs.toArray(new String[dtcs.size()]));
        foundDtcs = true;
      } catch (Exception ignored) {
      }
      if (foundDtcs) subscriber.onNext(sm);
    } else if (pfx.equals("P0")) {
      StreamMessage sm = emptyStreamMessage();
      sm.payload.data.put("udpPower", Boolean.FALSE);
      subscriber.onNext(sm);
    } else if (pfx.equals("P1")) {
      StreamMessage sm = emptyStreamMessage();
      sm.payload.data.put("udpPower", Boolean.TRUE);
      subscriber.onNext(sm);
    }
  }

  private static void processObd(String key, String val, Duktaper dt,
      Subscriber<? super StreamMessage> subscriber) {
    try {
      // normal obd data
      String eval = dt.evaluate( //
          String.format("JSON.stringify(mainlib.translate('01-%1$s', '%2$s'));", key, val));

      JSONArray arr;
      Object json = new JSONTokener(eval).nextValue();
      if (json instanceof JSONObject) {
        arr = new JSONArray();
        arr.put(json);
      } else if (json instanceof JSONArray) {
        arr = (JSONArray) json;
      } else {
        throw new RuntimeException("not json.");
      }

      for (int i = 0; i < arr.length(); i++) {

        JSONObject jobj = null;
        try {
          jobj = arr.getJSONObject(i);
        } catch (Exception ignored) {
        }
        if (jobj == null) continue;

        String k = null;
        try {
          k = jobj.getString("key");
        } catch (Exception ignored) {
        }
        if (k == null) continue;

        String type = null;
        try {
          type = jobj.getString("dataType");
        } catch (Exception ignored) {
        }
        if (type == null) continue;

        if (type.equalsIgnoreCase("decimal")) {
          try {
            double ret = jobj.getDouble("value");
            StreamMessage sm = emptyStreamMessage();
            sm.payload.data.put(k, ret);
            if (!subscriber.isUnsubscribed()) subscriber.onNext(sm);
          } catch (Exception ignored) {
          }
        } else /*if (type.equalsIgnoreCase("string"))*/ {
          try {
            String ret = jobj.getString("value");
            StreamMessage sm = emptyStreamMessage();
            sm.payload.data.put(k, ret);
            if (!subscriber.isUnsubscribed()) subscriber.onNext(sm);
          } catch (Exception ignored) {
          }
        }
      }
    } catch (Exception ignored) {
    }
  }

  private static final SimpleDateFormat VINLI_DATE_FMT;

  static {
    VINLI_DATE_FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    VINLI_DATE_FMT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @NonNull
  private static StreamMessage emptyStreamMessage() {
    StreamMessage sm = new StreamMessage();
    sm.payload = new StreamMessagePayload();
    sm.type = "pub";
    sm.payload.id = UUID.randomUUID().toString();
    sm.payload.timestamp = VINLI_DATE_FMT.format(new Date());
    sm.payload.data = new LinkedTreeMap<>();
    return sm;
  }

  private String type;
  private StreamMessageSubject subject;
  private StreamMessagePayload payload;

  StreamMessage() {
  }

  @Nullable
  public String getType() {
    return this.type;
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
  public static abstract class ParametricFilter {

    /*package*/ static final String TYPE = "parametric";

    public abstract String parameter();

    public abstract Float min();

    public abstract Float max();

    public abstract String deviceId();

    /*package*/ ParametricFilter() {
    }

    /*package*/
    static final void registerGson(GsonBuilder gb) {
      gb.registerTypeAdapter(AutoParcel_StreamMessage_ParametricFilter.Seed.class,
          new Seed.Adapter());
    }

    public static final Seed.Builder create() {
      return new AutoParcel_StreamMessage_ParametricFilter_Seed.Builder();
    }

    @AutoParcel
    public static abstract class Seed {
      private final String type = ParametricFilter.TYPE;

      @NonNull
      public abstract String parameter();

      @Nullable
      public abstract Float min();

      @Nullable
      public abstract Float max();

      @Nullable
      public abstract String deviceId();

      /*package*/ Seed() {
      }

      @AutoParcel.Builder
      public static abstract class Builder {
        public abstract Builder parameter(@NonNull String parameter);

        public abstract Builder min(@Nullable Float min);

        public abstract Builder max(@Nullable Float max);

        public abstract Builder deviceId(@Nullable String deviceId);

        public abstract Seed build();

        /*package*/ Builder() {
        }
      }

      /*package*/ static final class Adapter extends TypeAdapter<ParametricFilter.Seed> {

        private Gson gson;

        @Override
        public void write(com.google.gson.stream.JsonWriter out, Seed value) throws IOException {
          if (gson == null) {
            gson = Vinli.curApp().gson();
          }

          out.beginObject();
          out.name("type").value("filter");
          if (value.deviceId() != null) {
            out.name("id").value(value.deviceId());
          }
          out.name("filter").beginObject();
          out.name("type").value(value.type);
          out.name("parameter").value(value.parameter());
          if (value.min() != null) {
            out.name("min").value(value.min());
          }
          if (value.max() != null) {
            out.name("max").value(value.max());
          }
          out.endObject();
          out.endObject();
        }

        @Override
        public ParametricFilter.Seed read(JsonReader in) throws IOException {
          throw new UnsupportedOperationException(
              "reading a ParametricFilter.Seed is not supported");
        }
      }
    }
  }

  @AutoParcel
  public static abstract class GeometryFilter {

    public enum Direction {
      INSIDE("inside"),
      OUTSIDE("outside");

      private String str;

      private Direction(String str) {
        this.str = str;
      }

      /*package*/ String getDirectionAsString() {
        return this.str;
      }
    }

    /*package*/ static final String TYPE = "geometry";

    public abstract Direction direction();

    public abstract List<Coordinate.Seed> geometry();

    public abstract String deviceId();

    /*package*/ GeometryFilter() {
    }

    /*package*/
    static final void registerGson(GsonBuilder gb) {
      gb.registerTypeAdapter(AutoParcel_StreamMessage_GeometryFilter.Seed.class,
          new Seed.Adapter());
    }

    public static final Seed.Builder create() {
      return new AutoParcel_StreamMessage_GeometryFilter_Seed.Builder();
    }

    @AutoParcel
    public static abstract class Seed {
      private final String type = GeometryFilter.TYPE;

      @NonNull
      public abstract Direction direction();

      @NonNull
      public abstract List<Coordinate.Seed> geometry();

      @Nullable
      public abstract String deviceId();

      /*package*/ Seed() {
      }

      @AutoParcel.Builder
      public static abstract class Builder {
        public abstract Builder direction(@NonNull Direction direction);

        public abstract Builder geometry(@NonNull List<Coordinate.Seed> geometry);

        public abstract Builder deviceId(@Nullable String deviceId);

        public abstract Seed build();

        /*package*/ Builder() {
        }
      }

      /*package*/ static final class Adapter extends TypeAdapter<GeometryFilter.Seed> {

        private Gson gson;

        @Override
        public void write(com.google.gson.stream.JsonWriter out, Seed value) throws IOException {
          if (gson == null) {
            gson = Vinli.curApp().gson();
          }

          out.beginObject();
          out.name("type").value("filter");
          if (value.deviceId() != null) {
            out.name("id").value(value.deviceId());
          }
          out.name("filter").beginObject();
          out.name("type").value(value.type);
          out.name("direction").value(value.direction().getDirectionAsString());
          out.name("geometry").beginObject();
          out.name("type").value("Polygon");
          out.name("coordinates").beginArray().beginArray();
          for (Coordinate.Seed seed : value.geometry()) {
            gson.toJson(seed, Coordinate.Seed.class, out);
          }
          out.endArray().endArray();
          out.endObject();
          out.endObject();
          out.endObject();
        }

        @Override
        public GeometryFilter.Seed read(JsonReader in) throws IOException {
          throw new UnsupportedOperationException("reading a GeometryFilter.Seed is not supported");
        }
      }
    }
  }
}
