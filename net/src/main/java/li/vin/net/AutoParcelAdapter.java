package li.vin.net;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/*package*/ class AutoParcelAdapter<T> extends TypeAdapter<T> {
  public static final <T> AutoParcelAdapter<T> create(Class<? extends T> autoParcelClass) {
    return new AutoParcelAdapter<T>(autoParcelClass);
  }

  private final Class<? extends T> autoParcelClass;
  private Gson gson;

  private AutoParcelAdapter(Class<? extends T> autoParcelClass) {
    this.autoParcelClass = autoParcelClass;
  }

  @Override public void write(JsonWriter out, T value) throws IOException {
    if (gson == null) {
      gson = Vinli.curApp().gson();
    }

    gson.toJson(value, autoParcelClass, out);
  }

  @Override public T read(JsonReader in) throws IOException {
    if (gson == null) {
      gson = Vinli.curApp().gson();
    }

    Log.d("AutoParcelAdapter", "parsing " + autoParcelClass.getSimpleName());
    return gson.fromJson(in, autoParcelClass);
  }
}
