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
public abstract class TimeSeries<T extends VinliItem> implements Parcelable {
  /*package*/ static final Func1 EXTRACT_ITEMS = new Func1<TimeSeries<?>, Observable<?>>() {
    @Override public Observable<?> call(TimeSeries<?> tTimeSeries) {
      return tTimeSeries.observeItems();
    }
  };

  @SuppressWarnings("unchecked")
  public static final <T extends VinliItem> Func1<TimeSeries<T>, Observable<T>> extractItems() {
    return (Func1<TimeSeries<T>, Observable<T>>) EXTRACT_ITEMS;
  }

  /*package*/ static final Func1 ALL_ITEMS = new Func1<TimeSeries<? extends VinliItem>, Observable<? extends VinliItem>>() {
    @Override
    @SuppressWarnings("unchecked")
    public Observable<? extends VinliItem> call(TimeSeries<? extends VinliItem> tTimeSeries) {
      if (tTimeSeries.hasPrior()) {
        return tTimeSeries.observeItems().concatWith(tTimeSeries.loadPrior().flatMap(ALL_ITEMS));
      }

      return tTimeSeries.observeItems();
    }
  };

  @SuppressWarnings("unchecked")
  public static final <T extends VinliItem> Func1<TimeSeries<T>, Observable<T>> allItems() {
    return (Func1<TimeSeries<T>, Observable<T>>) ALL_ITEMS;
  }

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(
        Meta.class,
        AutoParcelAdapter.create(AutoParcel_TimeSeries_Meta.class));

    gb.registerTypeAdapter(
        Meta.Pagination.class,
        AutoParcelAdapter.create(AutoParcel_TimeSeries_Meta_Pagination.class));

    gb.registerTypeAdapter(
        Meta.Pagination.Links.class,
        AutoParcelAdapter.create(AutoParcel_TimeSeries_Meta_Pagination_Links.class));
  }

  /*package*/ abstract List<T> items();
  /*package*/ abstract Meta meta();
  /*package*/ abstract Type type();

  public int size() {
    return items().size();
  }

  public int total() {
    return size() + meta().pagination().remaining();
  }

  public List<T> getItems() {
    return Collections.unmodifiableList(items());
  }

  public Observable<T> observeItems() {
    return Observable.create(new OnSubscribeFromIterable<>(items()));
  }

  public Observable<TimeSeries<T>> loadPrior() {
    final Meta.Pagination.Links links = meta().pagination().links();
    if (links == null) {
      return Observable.error(new IOException("no links"));
    }

    final String link = links.prior();
    if (link == null) {
      return Observable.error(new IOException("no prior link"));
    }

    return Vinli.curApp().linkLoader().read(link, type());
  }

  public boolean hasPrior() {
    final Meta.Pagination.Links links = meta().pagination().links();
    return links != null && links.prior() != null;
  }

  /*package*/ TimeSeries() { }

  @AutoParcel
  /*package*/ static abstract class Meta implements Parcelable {
    public abstract Pagination pagination();

    @AutoParcel
    public static abstract class Pagination implements Parcelable {
      public abstract int remaining();
      public abstract int limit();
      public abstract String until();
      @Nullable public abstract Links links();

      @AutoParcel
      public static abstract class Links implements Parcelable {
        @Nullable public abstract String prior();
      }
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder<T extends VinliItem> {
    Builder<T> items(List<T> l);
    Builder<T> meta(Meta m);
    Builder<T> type(Type t);

    TimeSeries<T> build();
  }

  /*package*/ static final class Adapter<T extends VinliItem> extends TypeAdapter<TimeSeries<T>> {
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

    @Override public void write(JsonWriter out, TimeSeries<T> value) throws IOException {
      throw new UnsupportedOperationException("writing a time series is not supported");
    }

    @Override public TimeSeries<T> read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      final TimeSeries.Builder<T> b = new AutoParcel_TimeSeries.Builder<T>()
          .type(pageType);

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();

        if ("meta".equals(name)) {
          b.meta(gson.<TimeSeries.Meta>fromJson(in, TimeSeries.Meta.class));
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
