package li.vin.net;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Subscription implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Subscription>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Subscription.class, AutoParcelAdapter.create(AutoParcel_Subscription.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Subscription_Links.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Subscription.class));

    gb.registerTypeAdapter(AutoParcel_Subscription_SeedCreate.class, new SeedCreate.Adapter());
    gb.registerTypeAdapter(AutoParcel_Subscription_SeedEdit.class, new SeedEdit.Adapter());
  }

  public static final SeedCreate.Saver create() {
    return new AutoParcel_Subscription_SeedCreate.Builder();
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

  public Observable<Page<Notification>> notifications() {
    return Vinli.curApp().linkLoader().read(links().notifications(), Notification.PAGE_TYPE);
  }

  public SeedEdit.Saver edit() {
    return new AutoParcel_Subscription_SeedEdit.Builder()
        .appData(appData())
        .url(url())
        .deviceId(deviceId())
        .subscriptionId(id());
  }

  public Observable<Void> delete() {
    return Vinli.curApp().linkLoader().delete(links().self());
  }

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

  @AutoParcel
  public static abstract class SeedCreate {
    public abstract String eventType();
    @Nullable public abstract ObjectRef object();
    public abstract String url();
    @Nullable public abstract String appData();
    public abstract String deviceId();

    /*package*/ SeedCreate() { }

    @AutoParcel.Builder
    public static abstract class Saver {
      public abstract Saver eventType(String s);
      public abstract Saver object(@Nullable ObjectRef o);
      public abstract Saver url(String s);
      public abstract Saver appData(@Nullable String s);
      public abstract Saver deviceId(String deviceId);

      /*package*/ Saver() { }

      /*package*/ abstract SeedCreate autoBuild();

      public Observable<Subscription> save() {
        final SeedCreate sc = autoBuild();

        return Vinli.curApp().subscriptions().create(sc.deviceId(), sc)
            .map(Wrapped.<Subscription>pluckItem());
      }
    }

    /*package*/ static final class Adapter extends TypeAdapter<SeedCreate> {
      private Gson gson;

      @Override public void write(JsonWriter out, SeedCreate value) throws IOException {
        if (gson == null) {
          gson = Vinli.curApp().gson();
        }

        out.beginObject();
          out.name("subscription").beginObject();
            out.name("eventType").value(value.eventType());
            out.name("object"); gson.toJson(value.object(), ObjectRef.class, out);
            out.name("url").value(value.url());
            out.name("appData").value(value.appData());
          out.endObject();
        out.endObject();
      }

      @Override public SeedCreate read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("reading a Subscription.SeedCreate is not supported");
      }
    }
  }

  @AutoParcel
  public static abstract class SeedEdit {
    public abstract String url();
    @Nullable public abstract String appData();
    /*package*/ abstract String deviceId();
    /*package*/ abstract String subscriptionId();

    /*package*/ SeedEdit() { }

    @AutoParcel.Builder
    public static abstract class Saver {
      public abstract Saver url(String s);
      public abstract Saver appData(@Nullable String s);
      /*package*/ abstract Saver deviceId(String deviceId);
      /*package*/ abstract Saver subscriptionId(String deviceId);

      /*package*/ Saver() { }

      /*package*/ abstract SeedEdit autoBuild();

      public Observable<Subscription> save() {
        final SeedEdit se = autoBuild();

        return Vinli.curApp().subscriptions().edit(se.deviceId(), se.subscriptionId(), se)
            .map(Wrapped.<Subscription>pluckItem());
      }
    }

    /*package*/ static final class Adapter extends TypeAdapter<SeedEdit> {
      @Override public void write(JsonWriter out, SeedEdit value) throws IOException {
        out.beginObject();
          out.name("subscription").beginObject();
            out.name("url").value(value.url());
            out.name("appData").value(value.appData());
          out.endObject();
        out.endObject();
      }

      @Override public SeedEdit read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("reading a Subscription.SeedEdit is not supported");
      }
    }
  }
}
