package li.vin.net;

import android.support.annotation.Nullable;

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
public abstract class Message extends DataItem {
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Message>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Message.class, new Message.Adapter());
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Message.class));
  }

  public abstract String timestamp();

  /*package*/ Message() { }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder timestamp(String s);
    Builder data(@Nullable Map<String, String> m);

    Message build();
  }

  /*package*/ static final class Adapter extends TypeAdapter<Message> {

    @Override public void write(JsonWriter out, Message value) throws IOException {
      throw new UnsupportedOperationException("writing a Message is not supported");
    }

    @Override public Message read(JsonReader in) throws IOException {
      final Message.Builder b = new AutoParcel_Message.Builder();
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
