package li.vin.net;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Locale;

/*package*/ class WrappedJsonAdapter<T extends VinliItem> extends TypeAdapter<T> {

  public static <T extends VinliItem> WrappedJsonAdapter<T> create(Class<T> cls, TypeAdapter<T> adapter) {
    return new WrappedJsonAdapter<>(adapter, null, cls, cls.getSimpleName().toLowerCase(Locale.US));
  }

  public static <T extends VinliItem> WrappedJsonAdapter<T> create(Class<T> cls, VinliApp app) {
    return new WrappedJsonAdapter<>(null, app, cls, cls.getSimpleName().toLowerCase(Locale.US));
  }

  public static <T extends VinliItem> WrappedJsonAdapter<T> create(Class<T> cls, VinliApp app, String name) {
    return new WrappedJsonAdapter<>(null, app, cls, name);
  }

  private final String mName;
  private final TypeAdapter<T> mAdapter;
  private final VinliApp mApp;
  private final Class<T> mClass;

  private WrappedJsonAdapter(TypeAdapter<T> adapter, VinliApp app, Class<T> cls, String name) {
    mAdapter = adapter;
    mApp = app;
    mName = name;
    mClass = cls;
  }

  @Override public void write(JsonWriter out, T value) throws IOException {
    out.beginObject();
      out.name(mName); mAdapter.write(out, value);
    out.endObject();
  }

  @Override public T read(JsonReader in) throws IOException {
    in.beginObject();

    final String name = in.nextName();
    if (!mName.equals(name)) {
      throw new IOException(name + " does not match expected name " + mName);
    }

    final T item = mAdapter == null ?
        mApp.gson().<T>fromJson(in, mClass) :
        mAdapter.read(in);

    in.endObject();

    return item;
  }

}
