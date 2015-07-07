package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import auto.parcel.AutoParcel;
import rx.Observable;
import rx.functions.Func1;
import rx.internal.operators.OnSubscribeFromIterable;

@AutoParcel
public abstract class Page<T extends VinliItem> implements Parcelable {
  /*package*/ static final Func1 EXTRACT_ITEMS = new Func1<Page<?>, Observable<?>>() {
    @Override public Observable<?> call(Page<?> tPage) {
      return tPage.observeItems();
    }
  };

  @SuppressWarnings("unchecked")
  public static final <T extends VinliItem> Func1<Page<T>, Observable<T>> extractItems() {
    return (Func1<Page<T>, Observable<T>>) EXTRACT_ITEMS;
  }

  /*package*/ static final Func1 ALL_ITEMS = new Func1<Page<? extends VinliItem>, Observable<? extends VinliItem>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Observable<? extends VinliItem> call(Page<? extends VinliItem> tPage) {
      if (tPage.hasNextPage()) {
        return tPage.observeItems().concatWith(tPage.loadNextPage().flatMap(ALL_ITEMS));
      }

      return tPage.observeItems();
    }
  };

  @SuppressWarnings("unchecked")
  public static final <T extends VinliItem> Func1<Page<T>, Observable<T>> allItems() {
    return (Func1<Page<T>, Observable<T>>) ALL_ITEMS;
  }

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(
        Meta.class,
        AutoParcelAdapter.create(AutoParcel_Page_Meta.class));

    gb.registerTypeAdapter(
        Meta.Pagination.class,
        AutoParcelAdapter.create(AutoParcel_Page_Meta_Pagination.class));

    gb.registerTypeAdapter(
        Meta.Pagination.Links.class,
        AutoParcelAdapter.create(AutoParcel_Page_Meta_Pagination_Links.class));
  }

  /*package*/ abstract List<T> items();
  /*package*/ abstract Meta meta();
  /*package*/ abstract Type type();

  public int size() {
    return items().size();
  }

  public int total() {
    return meta().pagination().total();
  }

  public List<T> getItems() {
    return Collections.unmodifiableList(items());
  }

  public Observable<T> observeItems() {
    return Observable.create(new OnSubscribeFromIterable<>(items()));
  }

  public boolean hasNextPage() {
    final Meta.Pagination.Links links = meta().pagination().links();
    return links != null && links.next() != null;
  }

  public Observable<Page<T>> loadPrevPage() {
    final Meta.Pagination.Links links = meta().pagination().links();
    if (links == null) {
      return Observable.error(new IOException("no links"));
    }

    final String link = links.prev();
    if (link == null) {
      return Observable.error(new IOException("no prev link"));
    }

    return Vinli.curApp().linkLoader().read(link, type());
  }

  public Observable<Page<T>> loadNextPage() {
    final Meta.Pagination.Links links = meta().pagination().links();
    if (links == null) {
      return Observable.error(new IOException("no links"));
    }

    final String link = links.next();
    if (link == null) {
      return Observable.error(new IOException("no next link"));
    }

    return Vinli.curApp().linkLoader().read(link, type());
  }

  public Observable<Page<T>> loadFirstPage() {
    final Meta.Pagination.Links links = meta().pagination().links();
    if (links == null) {
      return Observable.error(new IOException("no links"));
    }

    return Vinli.curApp().linkLoader().read(links.first(), type());
  }

  public Observable<Page<T>> loadLastPage() {
    final Meta.Pagination.Links links = meta().pagination().links();
    if (links == null) {
      return Observable.error(new IOException("no links"));
    }

    return Vinli.curApp().linkLoader().read(links.last(), type());
  }

  /*package*/ Page() { }

  @AutoParcel
  /*package*/ static abstract class Meta implements Parcelable {
    public abstract Pagination pagination();

    @AutoParcel
    public static abstract class Pagination implements Parcelable {
      public abstract int total();
      public abstract int limit();
      public abstract int offset();
      @Nullable public abstract Links links();

      @AutoParcel
      public static abstract class Links implements Parcelable {
        public abstract String first();
        public abstract String last();
        @Nullable public abstract String next();
        @Nullable public abstract String prev();
      }
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder<T extends VinliItem> {
    Builder<T> items(List<T> l);
    Builder<T> meta(Meta m);
    Builder<T> type(Type t);

    Page<T> build();
  }

  /*package*/ static final class Adapter<T extends VinliItem> extends TypeAdapter<Page<T>> {
    public static final <T extends VinliItem> Adapter<T> create(Type pageType, Class<T> itemCls) {
      return create(pageType, itemCls, itemCls.getSimpleName().toLowerCase(Locale.US) + 's');
    }

    public static final <T extends VinliItem> Adapter<T> create(Type pageType, Class<T> itemCls, String collectionName) {
      return new Adapter<>(pageType, itemCls, collectionName);
    }

    private final Type pageType;
    private final Class<T> itemCls;
    private final String collectionName;

    private Gson gson;

    private Adapter(Type pageType, Class<T> itemCls, String collectionName) {
      this.pageType = pageType;
      this.itemCls = itemCls;
      this.collectionName = collectionName;
    }

    @Override public void write(JsonWriter out, Page<T> value) throws IOException {
      throw new UnsupportedOperationException("writing a page is not supported");
    }

    @Override public Page<T> read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      final Page.Builder<T> b = new AutoParcel_Page.Builder<T>()
          .type(pageType);

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        if ("meta".equals(name)) {
          b.meta(gson.<Page.Meta>fromJson(in, Page.Meta.class));
        } else if (collectionName.equals(name)) {
          final List<T> items = new ArrayList<>();

          in.beginArray();
          while (in.hasNext()) {
            items.add(gson.<T>fromJson(in, itemCls));
          }
          in.endArray();

          b.items(items);
        } else {
          throw new IOException("unrecognized key '" + name + "' while parsing " + collectionName);
        }
      }
      in.endObject();

      return b.build();
    }
  }
}
