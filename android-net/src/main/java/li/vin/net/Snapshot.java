package li.vin.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;

@AutoParcel
public class Snapshot implements VinliItem {
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Snapshot>>() {
  }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Snapshot>>() {
  }.getType();

  /*package*/
  static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(TIME_SERIES_TYPE,
        TimeSeries.Adapter.create(TIME_SERIES_TYPE, Snapshot.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Snapshot.class));
  }

  public static Observable<TimeSeries<Snapshot>> snapshotsWithDeviceId(@NonNull String deviceId,
      @NonNull String fields) {
    return snapshotsWithDeviceId(deviceId, fields, (Long) null, null, null, null);
  }

  public static Observable<TimeSeries<Snapshot>> snapshotsWithDeviceId(@NonNull String deviceId,
      @NonNull String fields, @Nullable Long sinceMs, @Nullable Long untilMs,
      @Nullable Integer limit, @Nullable String sortDir) {
    return Vinli.curApp().snapshots().snapshots(deviceId, fields, sinceMs, untilMs, limit, sortDir);
  }

  @Deprecated
  public static Observable<TimeSeries<Snapshot>> snapshotsWithDeviceId(@NonNull String deviceId,
      @NonNull String fields, @Nullable Date until, @Nullable Date since, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().snapshots().snapshots(deviceId, fields, untilMs, sinceMs, limit, sortDir);
  }

  public static Observable<TimeSeries<Snapshot>> snapshotsWithVehicleId(@NonNull String vehicleId,
      @NonNull String fields) {
    return snapshotsWithVehicleId(vehicleId, fields, (Long) null, null, null, null);
  }

  public static Observable<TimeSeries<Snapshot>> snapshotsWithVehicleId(@NonNull String vehicleId,
      @NonNull String fields, @Nullable Long sinceMs, @Nullable Long untilMs,
      @Nullable Integer limit, @Nullable String sortDir) {
    return Vinli.curApp().snapshots().vehicleSnapshots(vehicleId, fields, sinceMs, untilMs, limit, sortDir);
  }

  private String id;
  private String timestamp;
  private LinkedTreeMap<String, Object> data;
  /*package*/ Links links;

  /*package*/ Snapshot() {

  }

  /*package*/ Snapshot(Parcel in) {
    Gson gson = new Gson();
    Snapshot snapshot = gson.fromJson(in.readString(), Snapshot.class);
    this.id = snapshot.id();
    this.timestamp = snapshot.timestamp;
    this.data = snapshot.data;
  }

  public String id() {
    return id;
  }

  public String timestamp(){
    return timestamp;
  }

  @Nullable
  public String rawVal(@NonNull String key) {
    try {
      if (data == null) return null;
      Object val = data.get(key);
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

  public long longVal(@NonNull String key, long def) {
    return Math.round(doubleVal(key, def));
  }

  public float floatVal(@NonNull String key, float def) {
    return Double.valueOf(doubleVal(key, def)).floatValue();
  }

  public int intVal(@NonNull String key, int def) {
    return Long.valueOf(longVal(key, def)).intValue();
  }

  @Nullable
  public Coordinate coord() {
    try {
      if (data == null) return null;
      Object locObj = data.get("location");
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
      return Coordinate.builder()
          .lat(((Number) coords[1]).floatValue())
          .lon(((Number) coords[0]).floatValue())
          .build();
    } catch (Exception ignored) {
    }
    return null;
  }

  @Nullable
  public AccelData accel() {
    try {
      return new Gson().fromJson(rawVal("accel"), AccelData.class);
    } catch (Exception ignored) {
    }
    return null;
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

    AccelData(double maxX, double maxY, double maxZ, double minX, double minY, double minZ) {
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
    }

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    Gson gson = new Gson();
    dest.writeString(gson.toJson(this, Snapshot.class));
  }

  private static final Parcelable.Creator<Snapshot> CREATOR = new Parcelable.Creator<Snapshot>() {

    @Override
    public Snapshot createFromParcel(Parcel in) {
      return new Snapshot(in);
    }

    @Override
    public Snapshot[] newArray(int size) {
      return new Snapshot[size];
    }
  };

  static class Links {
    public String self;
  }
}
