package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;

@AutoParcel
public abstract class Rule implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Rule>>() { }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Rule>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Rule.class, new RuleAdapter());
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Rule.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Rule.class));

    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Rule_Links.class));

    gb.registerTypeAdapter(ParametricBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_ParametricBoundary.class));
    gb.registerTypeAdapter(ParametricBoundary.Seed.class,
        AutoParcelAdapter.create(AutoParcel_Rule_ParametricBoundary_Seed.class));

    gb.registerTypeAdapter(RadiusBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_RadiusBoundary.class));
    gb.registerTypeAdapter(RadiusBoundary.Seed.class,
        AutoParcelAdapter.create(AutoParcel_Rule_RadiusBoundary_Seed.class));

    gb.registerTypeAdapter(PolygonBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_PolygonBoundary.class));
    gb.registerTypeAdapter(PolygonBoundary.Seed.class,
        AutoParcelAdapter.create(AutoParcel_Rule_PolygonBoundary_Seed.class));

    gb.registerTypeAdapter(Rule.Seed.class, new Seed.Adapter());
  }

  public static Observable<Rule> ruleWithId(@NonNull String ruleId) {
    return Vinli.curApp().rule(ruleId);
  }

  public static Observable<Page<Rule>> rulesWithDeviceId(@NonNull String deviceId) {
    return rulesWithDeviceId(deviceId, null, null);
  }

  public static Observable<Page<Rule>> rulesWithDeviceId(@NonNull String deviceId,
      @Nullable Integer limit, @Nullable Integer offset) {
    return Vinli.curApp().rules().rules(deviceId, limit, offset);
  }

  public static final Seed.Saver create() {
    return new AutoParcel_Rule_Seed.Builder();
  }

  public abstract String name();
  public abstract boolean evaluated();
  @Nullable public abstract Boolean covered();
  @Nullable public abstract String createdAt();
  @Nullable public abstract String deviceId();
  @Nullable public abstract PolygonBoundary polygonBoundary();
  @Nullable public abstract RadiusBoundary radiusBoundary();
  @NonNull public abstract List<ParametricBoundary> parametricBoundaries();
  @NonNull public abstract ObjectRef object();

  /*package*/ abstract Links links();

  /*package*/ Rule() { }

  public Observable<TimeSeries<Event>> events() {
    return Event.eventsWithDeviceId(this.deviceId(), "rule-*", this.id(), (Long) null, null, null, null);
  }

  public Observable<Page<Subscription>> subscriptions() {
    return Subscription.subscriptionsWithDeviceId(this.deviceId(), null, null, this.id(), "rule");
  }

  public Observable<Rule> fill() {
    return Rule.ruleWithId(this.id());
  }

  public Observable<Void> delete() {
    return Vinli.curApp().rules().delete(this.id());
  }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String self();
    @Nullable public abstract String events();
    @Nullable public abstract String subscriptions();

    /*package*/ Links() { }
  }

  @AutoParcel.Builder
  /*package*/ static abstract class Builder {
    public abstract Builder id(String s);
    public abstract Builder name(String s);
    public abstract Builder evaluated(boolean b);
    public abstract Builder covered(@Nullable Boolean b);
    public abstract Builder createdAt(@Nullable String s);
    public abstract Builder deviceId(@Nullable String s);
    public abstract Builder polygonBoundary(@Nullable PolygonBoundary pb);
    public abstract Builder radiusBoundary(@Nullable RadiusBoundary rb);
    public abstract Builder parametricBoundaries(List<ParametricBoundary> l);
    public abstract Builder links(Links l);
    public abstract Builder object(ObjectRef objectRef);

    public abstract Rule build();
  }

  @AutoParcel
  public static abstract class ParametricBoundary implements VinliItem {
    /*package*/ static final String TYPE = "parametric";

    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_ParametricBoundary.Builder();
    }

    public static final Seed.Builder create() {
      return new AutoParcel_Rule_ParametricBoundary_Seed.Builder();
    }

    public abstract String parameter();
    @Nullable public abstract Float min();
    @Nullable public abstract Float max();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder id(String s);
      Builder parameter(String s);
      Builder min(Float f);
      Builder max(Float f);

      ParametricBoundary build();
    }

    @AutoParcel
    public static abstract class Seed {
      private final String type = ParametricBoundary.TYPE; // needed for GSON serialization

      @NonNull public abstract String parameter();
      @Nullable public abstract Float min();
      @Nullable public abstract Float max();

      /*package*/ Seed() { }

      @AutoParcel.Builder
      public static abstract class Builder {
        public abstract Builder parameter(@NonNull String s);
        public abstract Builder min(@Nullable Float f);
        public abstract Builder max(@Nullable Float f);

        public abstract Seed build();

        /*package*/ Builder() { }
      }
    }

    /*package*/ ParametricBoundary() { }
  }

  @AutoParcel
  public static abstract class RadiusBoundary implements VinliItem {
    /*package*/ static final String TYPE = "radius";

    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_RadiusBoundary.Builder();
    }

    public static final Seed.Builder create() {
      return new AutoParcel_Rule_RadiusBoundary_Seed.Builder();
    }

    public abstract float radius();
    public abstract float lon();
    public abstract float lat();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder id(String s);
      Builder radius(float f);
      Builder lon(float f);
      Builder lat(float f);

      RadiusBoundary build();
    }

    @AutoParcel
    public static abstract class Seed {
      private final String type = RadiusBoundary.TYPE; // needed for GSON serialization

      public abstract float radius();
      public abstract float lon();
      public abstract float lat();

      @AutoParcel.Builder
      public static abstract class Builder {
        public abstract Builder radius(float f);
        public abstract Builder lon(float f);
        public abstract Builder lat(float f);

        public abstract Seed build();

        /*package*/ Builder() { }
      }
    }

    /*package*/ RadiusBoundary() { }
  }

  @AutoParcel
  public static abstract class PolygonBoundary implements VinliItem {
    /*package*/ static final String TYPE = "polygon";

    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_PolygonBoundary.Builder();
    }

    public static final Seed.Builder create() {
      return new AutoParcel_Rule_PolygonBoundary_Seed.Builder();
    }

    public abstract List<List<Coordinate>> coordinates();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder id(String s);
      Builder coordinates(List<List<Coordinate>> l);

      PolygonBoundary build();
    }

    @AutoParcel
    public static abstract class Seed {
      private final String type = PolygonBoundary.TYPE; // needed for GSON serialization

      public abstract List<List<Coordinate>> coordinates();

      @AutoParcel.Builder
      public static abstract class Builder {
        public abstract Builder coordinates(List<List<Coordinate>> l);

        public abstract Seed build();

        /*package*/ Builder() { }
      }
    }

    /*package*/ PolygonBoundary() { }
  }

  @AutoParcel
  public static abstract class Seed {
    @NonNull public abstract String name();
    @Nullable public abstract PolygonBoundary.Seed polygonBoundary();
    @Nullable public abstract RadiusBoundary.Seed radiusBoundary();
    @Nullable public abstract List<ParametricBoundary.Seed> parametricBoundaries();
    @NonNull public abstract String deviceId();

    /*package*/ Seed() { }

    @AutoParcel.Builder
    public static abstract class Saver {
      public abstract Saver name(@NonNull String s);
      public abstract Saver polygonBoundary(@Nullable PolygonBoundary.Seed s);
      public abstract Saver radiusBoundary(@Nullable RadiusBoundary.Seed s);
      public abstract Saver parametricBoundaries(@Nullable List<ParametricBoundary.Seed> l);
      public abstract Saver deviceId(@NonNull String s);

      /*package*/ Saver() { }

      /*package*/ abstract Seed autoBuild();

      public Observable<Rule> save() {
        final Seed s = autoBuild();

        return Vinli.curApp().rules().create(s.deviceId(), s)
            .map(Wrapped.<Rule>pluckItem());
      }
    }

    /*package*/ static final class Adapter extends TypeAdapter<Seed> {
      private Gson gson;

      @Override public void write(JsonWriter out, Seed value) throws IOException {
        if (gson == null) {
          gson = Vinli.curApp().gson();
        }

        out.beginObject();
          out.name("rule").beginObject();
            out.name("name").value(value.name());
            out.name("boundaries").beginArray();
              final PolygonBoundary.Seed polyBoundary = value.polygonBoundary();
              if (polyBoundary != null) {
                gson.toJson(polyBoundary, PolygonBoundary.Seed.class, out);
              }

              final RadiusBoundary.Seed radiusBoundary = value.radiusBoundary();
              if (radiusBoundary != null) {
                gson.toJson(radiusBoundary, RadiusBoundary.Seed.class, out);
              }

              final List<ParametricBoundary.Seed> parametricBoundaries = value.parametricBoundaries();
              if (parametricBoundaries != null) {
                for (final ParametricBoundary.Seed pb : parametricBoundaries) {
                  gson.toJson(pb, ParametricBoundary.Seed.class, out);
                }
              }
            out.endArray();
          out.endObject();
        out.endObject();
      }

      @Override public Seed read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("reading a RuleSeed is not supported");
      }
    }
  }

  private static final class RuleAdapter extends TypeAdapter<Rule> {
    private Gson gson;

    @Override public void write(JsonWriter out, Rule value) throws IOException {
      throw new UnsupportedOperationException("writing a Rule is not supported");
    }

    @Override public Rule read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      final Rule.Builder b = new AutoParcel_Rule.Builder()
          .parametricBoundaries(Collections.<ParametricBoundary>emptyList());

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        switch (name) {
          case "id": b.id(in.nextString()); break;
          case "name": b.name(in.nextString()); break;
          case "evaluated": b.evaluated(in.nextBoolean()); break;
          case "covered":
            if (in.peek() == JsonToken.NULL) {
              in.nextNull();
            } else {
              b.covered(in.nextBoolean());
            }

            break;
          case "createdAt":
            if (in.peek() == JsonToken.NULL) {
              in.nextNull();
            } else {
              b.createdAt(in.nextString());
            }

            break;
          case "deviceId": b.deviceId(in.nextString()); break;
          case "boundaries":
            final List<ParametricBoundary> parametricBoundaries = new ArrayList<>();

            in.beginArray();

            while (in.hasNext()) {
              final JsonObject boundary = (JsonObject) TypeAdapters.JSON_ELEMENT.read(in);
              final String type = boundary.getAsJsonPrimitive("type").getAsString();

              switch (type) {
                case ParametricBoundary.TYPE:
                  parametricBoundaries.add(gson.fromJson(boundary, ParametricBoundary.class));
                  break;
                case PolygonBoundary.TYPE:
                  b.polygonBoundary(gson.fromJson(boundary, PolygonBoundary.class));
                  break;
                case RadiusBoundary.TYPE:
                  b.radiusBoundary(gson.fromJson(boundary, RadiusBoundary.class));
                  break;
                default:
                  throw new JsonParseException("unknown boundary type " + type);
              }
            }

            in.endArray();

            b.parametricBoundaries(parametricBoundaries);
            break;
          case "links": b.links(gson.<Rule.Links>fromJson(in, Links.class)); break;
          case "object":
            ObjectRef.Builder bld = new AutoParcel_ObjectRef.Builder();
            in.beginObject();
            while (in.hasNext()) {
              switch (in.nextName()) {
                case "id": bld = bld.id(in.nextString()); break;
                case "type": bld = bld.type(in.nextString()); break;
                default: break;
              }
            }
            in.endObject();
            b.object(bld.build());
            break;
          default: throw new JsonParseException("unknown rule key " + name);
        }
      }
      in.endObject();

      return b.build();
    }
  }
}
