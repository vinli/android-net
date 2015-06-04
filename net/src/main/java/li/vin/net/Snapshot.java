package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Snapshot implements VinliItem {
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Snapshot>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Snapshot.class, new Adapter());
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Snapshot.class));
  }

  public abstract String timestamp();

  @Nullable /*package*/ abstract Map<String, String> data();

  public boolean hasValue(@NonNull String name) {
    final Map<String, String> data = data();
    if (data == null) {
      return false;
    }

    return data.containsKey(name);
  }

  @Nullable public String value(@NonNull String name) {
    final Map<String, String> data = data();
    if (data == null) {
      return null;
    }

    return data.get(name);
  }

  /*package*/ Snapshot() { }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder timestamp(String s);
    Builder data(@Nullable Map<String, String> m);

    Snapshot build();
  }

  private static final class Adapter extends TypeAdapter<Snapshot> {
    private Gson gson;

    @Override public void write(JsonWriter out, Snapshot value) throws IOException {
      throw new UnsupportedOperationException("writing a Snapshot is not supported");
    }

    @Override public Snapshot read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      final Snapshot.Builder b = new AutoParcel_Snapshot.Builder();
      Map<String, String> data = null;

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        switch (name) {
          case "id": b.id(in.nextString()); break;
          case "timestamp": b.timestamp(in.nextString()); break;
          case "links": in.skipValue(); break;
          default:
            if (data == null) {
              data = new HashMap<>();
              b.data(data);
            }
            data.put(name, in.nextString());
        }
      }
      in.endObject();

      return b.build();
    }
  }
}
