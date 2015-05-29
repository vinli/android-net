package li.vin.net;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import auto.parcel.AutoParcel;
import rx.Observable;
import rx.functions.Func1;
import rx.internal.operators.OnSubscribeFromIterable;

@AutoParcel
public abstract class Page<T extends VinliItem> {
  public static final Func1 EXTRACT_ITEMS = new Func1<Page<?>, Observable<?>>() {
    @Override public Observable<?> call(Page<?> tPage) {
      return tPage.observeItems();
    }
  };

  @SuppressWarnings("unchecked")
  public static final <T extends VinliItem> Func1<Page<T>, Observable<T>> extractItems() {
    return EXTRACT_ITEMS;
  }

  public static final Func1 ALL_ITEMS = new Func1<Page<? extends VinliItem>, Observable<? extends VinliItem>>() {
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
    return ALL_ITEMS;
  }

  /*package*/ static final <T extends VinliItem> Builder<T> builder() {
    return new AutoParcel_Page.Builder<>();
  }

  /*package*/ abstract List<T> items();
  /*package*/ abstract Meta meta();
  /*package*/ abstract LinkLoader linkLoader();
  /*package*/ abstract Type type();

  public int size() {
    return items().size();
  }

  public List<T> getItems() {
    return Collections.unmodifiableList(items());
  }

  public Observable<T> observeItems() {
    return Observable.create(new OnSubscribeFromIterable<>(items()));
  }

  public boolean hasNextPage() {
    final Meta meta = meta();
    return meta.pagination != null &&
        meta.pagination.links != null &&
        meta.pagination.links.next != null;
  }

  public Observable<Page<T>> loadPrevPage() {
    return linkLoader().loadPage(meta().pagination.links.prev, type());
  }

  public Observable<Page<T>> loadNextPage() {
    return linkLoader().loadPage(meta().pagination.links.next, type());
  }

  public Observable<Page<T>> loadFirstPage() {
    return linkLoader().loadPage(meta().pagination.links.first, type());
  }

  public Observable<Page<T>> loadLastPage() {
    return linkLoader().loadPage(meta().pagination.links.last, type());
  }

  /*package*/ Page() { }

  /*package*/ static final class Meta {
    // CHECKSTYLE.OFF: VisibilityModifier
    public final Pagination pagination;
    // CHECKSTYLE.ON

    public Meta(Pagination pagination) {
      this.pagination = pagination;
    }

    public static final class Pagination {
      public final int total, limit, offset;
      // CHECKSTYLE.OFF: VisibilityModifier
      public final Links links;
      // CHECKSTYLE.ON

      public Pagination(int total, int limit, int offset, Links links) {
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.links = links;
      }

      public static final class Links {
        public final String first, last, next, prev;

        public Links(String first, String last, String next, String prev) {
          this.first = first;
          this.last = last;
          this.next = next;
          this.prev = prev;
        }
      }
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder<T extends VinliItem> {
    Builder<T> items(List<T> l);
    Builder<T> meta(Meta m);
    Builder<T> linkLoader(LinkLoader ll);
    Builder<T> type(Type t);

    Page<T> build();
  }
}
