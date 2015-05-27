package li.vin.net;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PageAdapter<T extends VinliItem> extends TypeAdapter<Page<T>> {
  public static <T extends VinliItem> PageAdapter<T> create(
      TypeAdapter<T> itemAdapter,
      Type pageType,
      VinliApp app,
      Class<T> cls) {
    return create(itemAdapter, pageType, app, cls.getSimpleName().toLowerCase(Locale.US) + 's');
  }

  public static <T extends VinliItem> PageAdapter<T> create(
      TypeAdapter<T> itemAdapter,
      Type pageType,
      VinliApp app,
      String name) {
    return new PageAdapter<>(itemAdapter, pageType, app, name);
  }

  private final String mName;
  private final Type mPageType;
  private final VinliApp mApp;
  private final TypeAdapter<T> mItemAdapter;

  private PageAdapter(TypeAdapter<T> itemAdapter, Type pageType, VinliApp app, String name) {
    mItemAdapter = itemAdapter;
    mPageType = pageType;
    mApp = app;
    mName = name;
  }

  @Override public void write(JsonWriter out, Page<T> value) throws IOException {
    throw new UnsupportedOperationException("writing a page is not supported");
  }

  @Override public Page<T> read(JsonReader in) throws IOException {
    final Page.Builder<T> b = Page.<T>builder()
        .linkLoader(mApp.getLinkLoader())
        .type(mPageType);

    in.beginObject();
    while (in.hasNext()) {
      final String name = in.nextName();

      if ("meta".equals(name)) {
        b.meta(mApp.gson().<Page.Meta>fromJson(in, Page.Meta.class));
      } else if (mName.equals(name)) {
        final List<T> items = new ArrayList<>();

        in.beginArray();
          while (in.hasNext()) {
            items.add(mItemAdapter.read(in));
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
