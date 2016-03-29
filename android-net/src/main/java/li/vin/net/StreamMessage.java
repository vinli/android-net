package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Collection;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.functions.Func1;

public final class StreamMessage {

  public enum DataType {

    RPM("rpm"), //
    VEHICLE_SPEED("vehicleSpeed"), //
    MASS_AIRFLOW("massAirFlow"), //
    CALCULATED_ENGINE_LOAD("calculatedLoadValue");

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
}
