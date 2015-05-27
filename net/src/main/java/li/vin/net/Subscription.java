package li.vin.net;

import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Subscription implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Subscription>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb, VinliApp app) {
    final SubscriptionAdapter adapter = SubscriptionAdapter.create(app);

    gb.registerTypeAdapter(Subscription.class, WrappedJsonAdapter.create(Subscription.class, adapter));

    gb.registerTypeAdapter(PAGE_TYPE, PageAdapter.create(adapter, PAGE_TYPE, app, Subscription.class));
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Subscription.Builder();
  }

  public abstract String deviceId();
  public abstract String eventType();
  public abstract String url();
  @Nullable public abstract ObjectRef object();
  public abstract String appData();
  public abstract String createdAt();
  public abstract String updatedAt();

  /*package*/ abstract VinliApp app();
  /*package*/ abstract Links links();

  /*package*/ Subscription() { }

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();
    public abstract String notifications();

    /*package*/ Links() { }

    @AutoParcel.Builder
    interface Builder {
      Builder self(String s);
      Builder notifications(String s);

      Links build();
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder deviceId(String s);
    Builder eventType(String s);
    Builder url(String s);
    Builder object(ObjectRef o);
    Builder appData(String s);
    Builder createdAt(String s);
    Builder updatedAt(String s);

    Builder app(VinliApp app);
    Builder links(Links l);

    Subscription build();
  }

  private static final class SubscriptionAdapter extends TypeAdapter<Subscription> {

    public static final SubscriptionAdapter create(VinliApp app) {
      return new SubscriptionAdapter(app);
    }

    private final VinliApp mApp;

    private SubscriptionAdapter(VinliApp app) {
      mApp = app;
    }

    @Override public void write(JsonWriter out, Subscription value) throws IOException {
      out.beginObject();
      out.name("id").value(value.id());
      out.endObject();
    }

    @Override public Subscription read(JsonReader in) throws IOException {
      final Subscription.Builder b = Subscription.builder();
      b.app(mApp);

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        switch (name) {
          case "id": b.id(in.nextString()); break;
          case "deviceId": b.deviceId(in.nextString()); break;
          case "url": b.url(in.nextString()); break;
          case "object": b.object(mApp.gson().<ObjectRef>fromJson(in, ObjectRef.class)); break;
          case "appData": b.url(in.nextString()); break;
          case "createdAt": b.url(in.nextString()); break;
          case "updatedAt": b.url(in.nextString()); break;
          case "links": b.links(mApp.gson().<Subscription.Links>fromJson(in, Subscription.Links.class)); break;
          default: throw new IOException("unknown subscription key " + name);
        }
      }
      in.endObject();

      return b.build();
    }
  }
}
