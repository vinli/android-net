package li.vin.net;

import android.os.Parcelable;

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
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Coordinate.Builder();
  }

  public abstract float lon();
  public abstract float lat();

  @AutoParcel.Builder
    /*package*/ interface Builder {
    Builder lon(float f);
    Builder lat(float f);

    Coordinate build();
  }

  /*package*/ Coordinate() { }

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
