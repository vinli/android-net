package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Coordinate implements Parcelable {
  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(
        Coordinate.class,
        new CoordinateAdapter());

    gb.registerTypeAdapter(AutoParcel_Coordinate.Seed.class, new Seed.Adapter());
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Coordinate.Builder();
  }

  public abstract float lon();
  public abstract float lat();

  public static final Seed.Builder create(){
    return new AutoParcel_Coordinate_Seed.Builder();
  }

  @AutoParcel.Builder
    /*package*/ interface Builder {
    Builder lon(float f);
    Builder lat(float f);

    Coordinate build();
  }

  /*package*/ Coordinate() { }

  @AutoParcel
  public static abstract class Seed{
    @NonNull public abstract float lon();
    @NonNull public abstract float lat();

    /*package*/ Seed() {}

    @AutoParcel.Builder
    public static abstract class Builder{
      public abstract Builder lat(@NonNull float latitude);
      public abstract Builder lon(@NonNull float longitude);

      public abstract Seed build();

      /*package*/ Builder(){}
    }

    /*package */ static final class Adapter extends TypeAdapter<Coordinate.Seed>{

      @Override
      public void write(com.google.gson.stream.JsonWriter out, Seed value) throws IOException {
        out.beginArray();
          out.value(value.lon());
          out.value(value.lat());
        out.endArray();
      }

      @Override public Coordinate.Seed read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("reading a Coordinate.Seed is not supported");
      }
    }
  }

  private static final class CoordinateAdapter extends TypeAdapter<Coordinate> {

    @Override public void write(JsonWriter out, Coordinate value) throws IOException {
      out.beginArray();
        out.value(value.lon());
        out.value(value.lat());
      out.endArray();
    }

    @Override public Coordinate read(JsonReader in) throws IOException {
      final Coordinate.Builder b = Coordinate.builder();

      in.beginArray();
        b.lon((float) in.nextDouble());
        b.lat((float) in.nextDouble());
      in.endArray();

      return b.build();
    }
  }
}
