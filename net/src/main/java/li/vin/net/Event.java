package li.vin.net;

import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Event implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Event>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb, VinliApp app) {
    final EventAdapter adapter = EventAdapter.create(app);

    gb.registerTypeAdapter(Event.class, WrappedJsonAdapter.create(Event.class, adapter));

    gb.registerTypeAdapter(PAGE_TYPE, PageAdapter.create(adapter, PAGE_TYPE, app, Event.class));
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Event.Builder();
  }

  public abstract String eventType();
  public abstract String timestamp();

  /*package*/ abstract VinliApp app();
  /*package*/ abstract Links links();

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();
    public abstract String rules();
    public abstract String vehicles();
    public abstract String latestVehicle();

    /*package*/ Links() { }

    @AutoParcel.Builder
    interface Builder {
      Builder self(String s);
      Builder rules(String s);
      Builder vehicles(String s);
      Builder latestVehicle(String s);

      Links build();
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder eventType(String s);
    Builder timestamp(String s);

    Builder app(VinliApp app);
    Builder links(Links l);

    Event build();
  }

  private static final class EventAdapter extends TypeAdapter<Event> {

    public static final EventAdapter create(VinliApp app) {
      return new EventAdapter(app);
    }

    private final VinliApp mApp;

    private EventAdapter(VinliApp app) {
      mApp = app;
    }

    @Override public void write(JsonWriter out, Event value) throws IOException {
      out.beginObject();
        out.name("id").value(value.id());
      out.endObject();
    }

    @Override public Event read(JsonReader in) throws IOException {
      final Event.Builder b = Event.builder()
          .app(mApp);

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        switch (name) {
          case "id": b.id(in.nextString()); break;
          case "eventType": b.eventType(in.nextString()); break;
          case "timestamp": b.timestamp(in.nextString()); break;
          case "links": b.links(mApp.gson().<Event.Links>fromJson(in, Event.Links.class)); break;
          default: throw new IOException("unknown event key " + name);
        }
      }
      in.endObject();

      return b.build();
    }

  }
}
