package li.vin.net;

import android.os.Parcelable;
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
import java.util.List;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Rule implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Rule>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    final RuleAdapter adapter = new RuleAdapter();

    gb.registerTypeAdapter(Rule.class, adapter);
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Rule.class));

    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Rule_Links.class));
    gb.registerTypeAdapter(ParametricBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_ParametricBoundary.class));
    gb.registerTypeAdapter(RadiusBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_RadiusBoundary.class));
    gb.registerTypeAdapter(PolygonBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_PolygonBoundary.class));
    gb.registerTypeAdapter(PolygonBoundary.class, AutoParcelAdapter.create(AutoParcel_Rule_PolygonBoundary.class));
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Rule.Builder();
  }

  public abstract String name();
  public abstract boolean evaluated();
  @Nullable public abstract Boolean covered();
  @Nullable public abstract String createdAt();
  @Nullable public abstract String deviceId();
  @Nullable public abstract PolygonBoundary polygonBoundary();
  @Nullable public abstract RadiusBoundary radiusBoundary();
  @Nullable public abstract List<ParametricBoundary> parametricBoundaries();

  /*package*/ abstract Links links();

  /*package*/ Rule() { }

  public Observable<TimeSeries<Event>> events() {
    return Vinli.curApp().linkLoader().loadTimeSeries(links().events(), Event.TIME_SERIES_TYPE);
  }

  public Observable<Page<Subscription>> subscriptions() {
    return Vinli.curApp().linkLoader().loadPage(links().subscriptions(), Subscription.PAGE_TYPE);
  }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String self();
    public abstract String events();
    public abstract String subscriptions();

    /*package*/ Links() { }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder name(String s);
    Builder evaluated(boolean b);
    Builder covered(@Nullable Boolean b);
    Builder createdAt(@Nullable String s);
    Builder deviceId(@Nullable String s);
    Builder polygonBoundary(@Nullable PolygonBoundary pb);
    Builder radiusBoundary(@Nullable RadiusBoundary rb);
    Builder parametricBoundaries(@Nullable List<ParametricBoundary> l);
    Builder links(Links l);

    Rule build();
  }

  @AutoParcel
  public static abstract class ParametricBoundary implements VinliItem {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_ParametricBoundary.Builder();
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

    /*package*/ ParametricBoundary() { }
  }

  @AutoParcel
  public static abstract class RadiusBoundary implements VinliItem {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_RadiusBoundary.Builder();
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

    /*package*/ RadiusBoundary() { }
  }

  @AutoParcel
  public static abstract class PolygonBoundary implements VinliItem {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_PolygonBoundary.Builder();
    }

    public abstract List<Coordinate> coordinates();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder id(String s);
      Builder coordinates(List<Coordinate> l);

      PolygonBoundary build();
    }

    /*package*/ PolygonBoundary() { }
  }

  private static final class RuleAdapter extends TypeAdapter<Rule> {
    private Gson gson;

    @Override public void write(JsonWriter out, Rule value) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      out.beginObject();
        out.name("id").value(value.id());
        out.name("name").value(value.name());
        out.name("evaluated").value(value.evaluated());
        out.name("covered").value(value.covered());
        out.name("createdAt").value(value.createdAt());
        out.name("deviceId").value(value.deviceId());
        out.name("boundaries").beginArray();
          final PolygonBoundary polyBoundary = value.polygonBoundary();
          if (polyBoundary != null) {
            gson.toJson(polyBoundary, PolygonBoundary.class, out);
          }

          final RadiusBoundary radiusBoundary = value.radiusBoundary();
          if (radiusBoundary != null) {
            gson.toJson(radiusBoundary, RadiusBoundary.class, out);
          }

          for (final ParametricBoundary pb : value.parametricBoundaries()) {
            gson.toJson(pb, ParametricBoundary.class, out);
          }
        out.endArray();
      out.endObject();
    }

    @Override public Rule read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      final Rule.Builder b = Rule.builder();

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
                case "parametric":
                  parametricBoundaries.add(gson.fromJson(boundary, ParametricBoundary.class));
                  break;
                case "polygon":
                  b.polygonBoundary(gson.fromJson(boundary, PolygonBoundary.class));
                  break;
                case "radius":
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
