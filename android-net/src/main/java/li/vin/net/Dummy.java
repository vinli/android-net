package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import rx.Observable;

/**
 * Created by JoshBeridon on 11/18/16.
 */
@AutoParcel public abstract class Dummy implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Dummy>>() {
  }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Dummy>>() {
  }.getType();
  /*package*/ static final Type WRAPPED_TYPE_RUN = new TypeToken<Wrapped<Run>>() {
  }.getType();

  static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Dummy.class, AutoParcelAdapter.create(AutoParcel_Dummy.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Dummy_Links.class));
    gb.registerTypeAdapter(Run.class, AutoParcelAdapter.create(AutoParcel_Dummy_Run.class));
    gb.registerTypeAdapter(Run.Status.class,
        AutoParcelAdapter.create(AutoParcel_Dummy_Run_Status.class));
    gb.registerTypeAdapter(Run.Links.class,
        AutoParcelAdapter.create(AutoParcel_Dummy_Run_Links.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Dummy.class, "dummies"));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Dummy.class, "dummies"));
    gb.registerTypeAdapter(WRAPPED_TYPE_RUN, Wrapped.Adapter.create(Run.class, "run"));

    gb.registerTypeAdapter(Run.Seed.class, new Dummy.Run.Seed.Adapter());
  }

  public static Observable<Run> currentRun(@NonNull String dummyId) {
    return Vinli.curApp().run(dummyId);
  }

  @NonNull public abstract String id();

  @NonNull public abstract String name();

  @NonNull public abstract String caseId();

  @NonNull public abstract String deviceId();

  /*package*/
  abstract Links links();

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();

    public abstract String runs();

    public abstract String device();

    public abstract String messages();

    public abstract String events();

    /*package*/ Links() {
    }
  }

  @AutoParcel public static abstract class Run implements VinliItem {

    @NonNull public abstract String id();

    /*package*/
    @NonNull public abstract Status status();

    /*package*/Run() {

    }

    @AutoParcel
  /*package*/ static abstract class Status implements Parcelable {

      public abstract String routeId();

      public abstract boolean repeat();

      public abstract String state();

      // public abstract String lastLocation();//TODO this is not a string

      @Nullable public abstract String lastSpeed();

      @Nullable public abstract Double lastRPM();

      @Nullable public abstract Integer lastMessageTime();

      @Nullable public abstract Integer totalMessages();

      @Nullable public abstract Integer sentMessages();

      @Nullable public abstract Integer remaningMessages();

      @Nullable public abstract Double remaningSeconds();

      /*package*/ Status() {
      }
    }

    /*package*/
    abstract Links links();

    @AutoParcel
  /*package*/ static abstract class Links {
      public abstract String self();

      /*package*/ Links() {
      }
    }

    @AutoParcel public static abstract class Seed {
      @NonNull public abstract String vin();

      @NonNull public abstract String routeId();

      @Nullable public abstract String repeat();

      /*package*/ Seed() {
      }

      @AutoParcel.Builder public static abstract class Saver {
        public abstract Saver vin(@NonNull String s);

        public abstract Saver routeId(@NonNull String s);

        public abstract Saver repeat(@Nullable String s);

        /*package*/ Saver() {
        }

        /*package*/
        abstract Seed autoBuild();

        public Observable<Run> save(String dummyId) {
          final Seed s = autoBuild();
          return Vinli.curApp()
              .dummies()
              .create(dummyId, s)
              .map(Wrapped.<Run>pluckItem());
        }
      }

      /*package*/ static final class Adapter extends TypeAdapter<Run.Seed> {
        private Gson gson;

        @Override public void write(JsonWriter out, Run.Seed value) throws IOException {
          if (gson == null) {
            gson = Vinli.curApp().gson();
          }

          out.beginObject();
            out.name("run").beginObject();
              out.name("vin").value(value.vin());
              out.name("routeId").value(value.routeId());
              out.name("repeat").value(value.repeat());
            out.endObject();
          out.endObject();
        }

        @Override public Run.Seed read(JsonReader in) throws IOException {
          throw new UnsupportedOperationException("reading a RunSeed is not supported");
        }
      }
    }

    public static final Run.Seed.Saver create() {
      return new AutoParcel_Dummy_Run_Seed.Builder();
    }

    public Observable<Void> delete(String dummyId) {
      return Vinli.curApp().dummies().deleteRun(dummyId);
    }
  }
}
