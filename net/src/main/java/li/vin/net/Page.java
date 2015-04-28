package li.vin.net;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.internal.operators.OnSubscribeFromIterable;

/**
 * Created by kyle on 7/7/14.
 */
public final class Page<T> {
  /*package*/ static <T> Page<T> create(List<T> items, Meta meta, LinkLoader linkLoader, Type type) {
    if (items == null) {
      throw new IllegalArgumentException("items == null");
    }
    if (meta == null) {
      throw new IllegalArgumentException("meta == null");
    }
    if (linkLoader == null) {
      throw new IllegalArgumentException("nextLoader == null");
    }
    if (type == null) {
      throw new IllegalArgumentException("type == null");
    }
    return new Page<T>(items, meta, linkLoader, type);
  }

  private final List<T> mItems;
  private final Meta mMeta;
  private final LinkLoader mLinkLoader;
  private final Type mType;

  private Page(List<T> items, Meta meta, LinkLoader linkLoader, Type type) {
    mItems = Collections.unmodifiableList(items);
    mMeta = meta;
    mLinkLoader = linkLoader;
    mType = type;
  }

  public int size() {
    return mItems.size();
  }

  public List<T> getItems() {
    return mItems;
  }

  public Observable<T> observeItems() {
    return Observable.create(new OnSubscribeFromIterable<T>(mItems));
  }

  public boolean hasNextPage() {
    return mMeta.pagination.links.next != null;
  }

  public Observable<Page<T>> loadPrevPage() {
    return mLinkLoader.loadPage(mMeta.pagination.links.prev, mType);
  }

  public Observable<Page<T>> loadNextPage() {
    return mLinkLoader.loadPage(mMeta.pagination.links.next, mType);
  }

  public Observable<Page<T>> loadFirstPage() {
    return mLinkLoader.loadPage(mMeta.pagination.links.first, mType);
  }

  public Observable<Page<T>> loadLastPage() {
    return mLinkLoader.loadPage(mMeta.pagination.links.last, mType);
  }

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
}
