package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Locale;

import auto.parcel.AutoParcel;
import rx.functions.Func1;

@AutoParcel
/*package*/ abstract class Wrapped<T extends VinliItem> implements Parcelable {
  public static final Func1 PLUCK_ITEM = new Func1<Wrapped<?>, Object>() {
    @Override public Object call(Wrapped<?> tWrapped) {
      return tWrapped.item();
    }
  };

  @SuppressWarnings("unchecked")
  public static final <T extends VinliItem> Func1<Wrapped<T>, T> pluckItem() {
    return PLUCK_ITEM;
  }

  @Nullable public abstract T item();

  /*package*/ static final class Adapter<T extends VinliItem> extends TypeAdapter<Wrapped<T>> {
    public static final <T extends VinliItem> Adapter<T> create(Class<T> itemCls) {
      return create(itemCls, itemCls.getSimpleName().toLowerCase(Locale.US));
    }

    public static final <T extends VinliItem> Adapter<T> create(Class<T> itemCls, String itemName) {
      return new Adapter<>(itemCls, itemName);
    }

    private final String itemName;
    private final Class<T> wrappedCls;

    private Gson gson;

    private Adapter(Class<T> wrappedCls, String itemName) {
      this.itemName = itemName;
      this.wrappedCls = wrappedCls;
    }

    @Override public void write(JsonWriter out, Wrapped<T> value) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      out.beginObject();
        out.name(itemName); gson.toJson(value.item(), wrappedCls, out);
      out.endObject();
    }

    @Override public Wrapped<T> read(JsonReader in) throws IOException {
      if (gson == null) {
        gson = Vinli.curApp().gson();
      }

      in.beginObject();

      final String name = in.nextName();
      if (!itemName.equals(name)) {
        throw new IOException(name + " does not match expected name " + itemName);
      }

      final T item = gson.fromJson(in, wrappedCls);

      in.endObject();

      return new AutoParcel_Wrapped<>(item);
    }
  }
}
