package li.vin.net;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PageAdapter<T extends VinliItem> extends TypeAdapter<Page<T>> {
  private static final String META = "meta";
  private static final TypeAdapter<Page.Meta> META_ADAPTER = new Gson().getAdapter(Page.Meta.class);

  public static <T extends VinliItem> PageAdapter<T> create(Class<T> cls, TypeAdapter<T> adapter, Type pageType, LinkLoader loader) {
    return new PageAdapter<T>(adapter, pageType, cls.getSimpleName().toLowerCase(Locale.US) + 's', loader);
  }

  private final String mName;
  private final Type mPageType;
  private final LinkLoader mLoader;
  private final TypeAdapter<T> mAdapter;

  private PageAdapter( TypeAdapter<T> adapter, Type pageType, String name, LinkLoader loader) {
    mAdapter = adapter;
    mPageType = pageType;
    mName = name;
    mLoader = loader;
  }

  @Override public void write(JsonWriter out, Page<T> value) throws IOException {
    throw new UnsupportedOperationException("writing a page is not supported");
  }

  @Override public Page<T> read(JsonReader in) throws IOException {
    final Page.Builder<T> b = Page.<T>builder()
        .linkLoader(mLoader)
        .type(mPageType);

    in.beginObject();
    while (in.hasNext()) {
      final String name = in.nextName();

      if (META.equals(name)) {
        b.meta(META_ADAPTER.read(in));
      } else if (mName.equals(name)) {
        final List<T> items = new ArrayList<>();

        in.beginArray();
          while (in.hasNext()) {
            items.add(mAdapter.read(in));
          }
        in.endArray();

        b.items(items);
      } else {
        throw new IOException("unrecognized key '" + name + "' while parsing " + mName);
      }
    }
    in.endObject();

    return b.build();
  }
}
