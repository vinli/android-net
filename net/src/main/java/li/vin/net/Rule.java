package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

import auto.parcel.AutoParcel;
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


    gb.registerTypeAdapter(PolygonBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_PolygonBoundary.class));

    gb.registerTypeAdapter(RuleSeed.class, new RuleSeed.Adapter());
  }

  public static final Rule.RuleCreator create() {
    return new Rule.RuleCreator();
  }

  public abstract String name();
  public abstract boolean evaluated();
  @Nullable public abstract Boolean covered();
  @Nullable public abstract String createdAt();
  @Nullable public abstract String deviceId();
  @Nullable public abstract PolygonBoundary polygonBoundary();
  @Nullable public abstract RadiusBoundary radiusBoundary();
  @NonNull public abstract List<ParametricBoundary> parametricBoundaries();

  /*package*/ abstract Links links();

  /*package*/ Rule() { }

  public Observable<TimeSeries<Event>> events() {
    final String events = links().events();
    if (events == null) {
      return Observable.error(new IOException("no events link"));
    }

    return Vinli.curApp().linkLoader().read(events, Event.TIME_SERIES_TYPE);
  }

  public Observable<Page<Subscription>> subscriptions() {
    final String subscriptions = links().subscriptions();
    if (subscriptions == null) {
      return Observable.error(new IOException("no subscriptions link"));
    }

    return Vinli.curApp().linkLoader().read(subscriptions, Subscription.PAGE_TYPE);
  }

  public Observable<Rule> fill() {
    return Vinli.curApp().linkLoader()
        .<Wrapped<Rule>>read(links().self(), Rule.WRAPPED_TYPE)
        .map(Wrapped.<Rule>pluckItem());
  }

  public Observable<Void> delete() {
    return Vinli.curApp().linkLoader().delete(links().self());
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

      @AutoParcel.Builder
      public interface Builder {
        Builder parameter(@NonNull String s);
        Builder min(@Nullable Float f);
        Builder max(@Nullable Float f);

        Seed build();
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
      public interface Builder {
        Builder radius(float f);
        Builder lon(float f);
        Builder lat(float f);

        Seed build();
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
      public interface Builder {
        Builder coordinates(List<List<Coordinate>> l);

        Seed build();
      }
    }

    /*package*/ PolygonBoundary() { }
  }

  public static final class RuleCreator {
    private String name;
    private Device device;
    private String deviceId;
    private Rule.PolygonBoundary.Seed polygonBoundary;
    private Rule.RadiusBoundary.Seed radiusBoundary;
    private List<Rule.ParametricBoundary.Seed> parametricBoundaries;

    /*package*/ RuleCreator() { }

    public RuleCreator name(String name) {
      this.name = name;
      return this;
    }

    public RuleCreator device(Device device) {
      this.device = device;
      return this;
    }

    public RuleCreator deviceId(String deviceId) {
      this.deviceId = deviceId;
      return this;
    }

    public RuleCreator polygonBoundary(Rule.PolygonBoundary.Seed polygonBoundary) {
      this.polygonBoundary = polygonBoundary;
      return this;
    }

    public RuleCreator radiusBoundary(Rule.RadiusBoundary.Seed radiusBoundary) {
      this.radiusBoundary = radiusBoundary;
      return this;
    }

    public RuleCreator parametricBoundaries(List<Rule.ParametricBoundary.Seed> parametricBoundaries) {
      this.parametricBoundaries = parametricBoundaries;
      return this;
    }

    public Observable<Rule> create() {
      final StringBuilder missing = new StringBuilder();
      if (name == null) {
        missing.append(" name");
      }

      if (deviceId == null) {
        if (device == null) {
          missing.append(" device||deviceId");
        } else {
          deviceId = device.id();
        }
      }

      if (missing.length() > 0) {
        // TODO: should this be Observable.error() instead?
        throw new IllegalStateException("Missing required properties:" + missing);
      }

      final Rule.RuleSeed ruleSeed =
          new Rule.RuleSeed(name, polygonBoundary, radiusBoundary, parametricBoundaries);

      return Vinli.curApp().rules()
          .create(deviceId, ruleSeed)
          .map(Wrapped.<Rule>pluckItem());
    }
  }

  /*package*/ static final class RuleSeed {
    @NonNull public final String name;
    @Nullable public final PolygonBoundary.Seed polygonBoundary;
    @Nullable public final RadiusBoundary.Seed radiusBoundary;
    @Nullable public final List<ParametricBoundary.Seed> parametricBoundaries;

    public RuleSeed(
        String name,
        PolygonBoundary.Seed polygonBoundary,
        RadiusBoundary.Seed radiusBoundary,
        List<ParametricBoundary.Seed> parametricBoundaries) {
      this.name = name;
      this.polygonBoundary = polygonBoundary;
      this.radiusBoundary = radiusBoundary;
      this.parametricBoundaries = parametricBoundaries;
    }

    public static final class Adapter extends TypeAdapter<RuleSeed> {
      private Gson gson;

      @Override public void write(JsonWriter out, RuleSeed value) throws IOException {
        if (gson == null) {
          gson = Vinli.curApp().gson();
        }

        out.beginObject();
          out.name("rule").beginObject();
            out.name("name").value(value.name);
            out.name("boundaries").beginArray();
              final PolygonBoundary.Seed polyBoundary = value.polygonBoundary;
              if (polyBoundary != null) {
                gson.toJson(polyBoundary, PolygonBoundary.Seed.class, out);
              }

              final RadiusBoundary.Seed radiusBoundary = value.radiusBoundary;
              if (radiusBoundary != null) {
                gson.toJson(radiusBoundary, RadiusBoundary.Seed.class, out);
              }

              if (value.parametricBoundaries != null) {
                for (final ParametricBoundary.Seed pb : value.parametricBoundaries) {
                  gson.toJson(pb, ParametricBoundary.Seed.class, out);
                }
              }
            out.endArray();
          out.endObject();
        out.endObject();
      }

      @Override public RuleSeed read(JsonReader in) throws IOException {
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
          case "deviceId": b.createdAt(in.nextString()); break;
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
          default: throw new JsonParseException("unknown rule key " + name);
        }
      }
      in.endObject();

      return b.build();
    }
  }
}
