package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;

@AutoParcel
public abstract class Location implements VinliItem {
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Location>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Location.class, new LocationAdapter());
    gb.registerTypeAdapter(TIME_SERIES_TYPE, new LocationTimeSeriesAdapter());
  }

  public static Observable<TimeSeries<Location>> locationsWithDeviceId(@NonNull String deviceId) {
    return locationsWithDeviceId(deviceId, null, null, null, null);
  }

  public static Observable<TimeSeries<Location>> locationsWithDeviceId(@NonNull String deviceId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp().locations().locations(deviceId, sinceMs, untilMs, limit, sortDir);
  }

  public abstract Coordinate coordinate();
  public abstract String timestamp();

  /*package*/ Location() { }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder coordinate(Coordinate c);
    Builder timestamp(String s);

    Location build();
  }

  private static final class LocationAdapter extends TypeAdapter<Location> {
    private Gson gson;

    @Override public void write(JsonWriter out, Location value) throws IOException {
      throw new UnsupportedOperationException("writing a location is not supported");
    }

    @Override public Location read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      if(in.peek() == JsonToken.NULL){
        in.nextNull();
        return null;
      }

      final Location.Builder b = new AutoParcel_Location.Builder();

      in.beginObject();
      while (in.hasNext()) {
        final String locationName = in.nextName();

        switch (locationName) {
          case "type": in.skipValue(); break;
          case "geometry":
            in.beginObject();
            while (in.hasNext()) {
              final String geoName = in.nextName();

              switch (geoName) {
                case "type": in.skipValue(); break;
                case "coordinates": b.coordinate(gson.<Coordinate>fromJson(in, Coordinate.class)); break;
                default: throw new JsonParseException("unknown location geometry key " + geoName);
              }
            }
            in.endObject();
            break;
          case "properties":
            in.beginObject();
            while (in.hasNext()) {
              final String propName = in.nextName();

              switch (propName) {
                case "id": b.id(in.nextString()); break;
                case "timestamp": b.timestamp(in.nextString()); break;
                case "links": in.skipValue(); break;
                case "data":
                  in.beginObject();
                  in.endObject();
                  break;
                default: throw new JsonParseException("unknown location geometry key " + propName);
              }
            }
            in.endObject();
            break;
          default: throw new JsonParseException("unknown location key " + locationName);
        }
      }
      in.endObject();

      return b.build();
    }
  }

  private static final class LocationTimeSeriesAdapter extends TypeAdapter<TimeSeries<Location>> {

    private Gson gson;

    @Override public void write(JsonWriter out, TimeSeries<Location> value) throws IOException {
      throw new UnsupportedOperationException("writing a location time series is not supported");
    }

    @Override public TimeSeries<Location> read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      final TimeSeries.Builder<Location> b = new AutoParcel_TimeSeries.Builder<Location>()
          .type(TIME_SERIES_TYPE);

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        switch (name) {
          case "meta": b.meta(gson.<TimeSeries.Meta>fromJson(in, TimeSeries.Meta.class)); break;
          case "locations":
            in.beginObject();
            while (in.hasNext()) {
              final String locName = in.nextName();

              switch (locName) {
                case "type": in.skipValue(); break;
                case "features":
                  final List<Location> locations = new ArrayList<>();

                  in.beginArray();
                  while (in.hasNext()) {
                    locations.add(gson.<Location>fromJson(in, Location.class));
                  }
                  in.endArray();

                  b.items(locations);
                  break;
                default: throw new JsonParseException("unrecognized key '" + locName + "' while parsing locations");
              }
            }
            in.endObject();
            break;
          default: throw new JsonParseException("unrecognized key '" + name + "' while parsing locations");
        }
      }
      in.endObject();

      return b.build();
    }
  }
}
