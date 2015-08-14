package li.vin.net.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;
import li.vin.net.Page;
import li.vin.net.VinliItem;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

public abstract class PageAdapter<T extends VinliItem> extends BaseAdapter {
  private final List<Page<T>> pages = new ArrayList<>();
  private final Subscriber<Page<T>> subscriber = new Subscriber<Page<T>>() {
    @Override public void onCompleted() { }

    @Override public void onError(Throwable e) {
      PageAdapter.this.onPageError(e);
    }

    @Override public void onNext(Page<T> page) {
      PageAdapter.this.onPageLoaded(page);
      pages.add(page);
      count += page.size();
      PageAdapter.this.notifyDataSetChanged();
    }
  };

  private int count = 0;

  protected void onPageLoaded(Page<T> page) { }

  protected void onPageError(Throwable e) {
    Log.e(this.getClass().getSimpleName(), "onPageError", e);
  }

  @Override public int getCount() {
    return count;
  }

  @Override public T getItem(int position) {
    if (position < 0 || position >= count) {
      throw new IllegalArgumentException(position + " is outside of the acceptable range 0.." + count);
    }

    for (int i = 0, il = pages.size(), pos = 0; i < il; ++i) {
      final Page<T> page = pages.get(i);
      final int nextPos = pos + page.size();
      if (nextPos <= position) {
        pos = nextPos;
        continue;
      }

      final int itemPos = position - pos;
      return page.getItems().get(itemPos);
    }

    throw new AssertionError("should never get here");
  }

  @Override public long getItemId(int position) {
    return getItem(position).id().hashCode();
  }

  public boolean hasNext() {
    return pages.isEmpty() || pages.get(pages.size() - 1).hasNextPage();
  }

  public Subscription subscribe(@NonNull Observable<Page<T>> observable) {
    return subscribe(null, observable);
  }

  /** Use {@link #subscribe(Observable)} instead. */
  @Deprecated
  public Subscription subscribe(Object context, @NonNull Observable<Page<T>> observable) {
    if (!pages.isEmpty()) {
      pages.clear();
      count = 0;
      this.notifyDataSetChanged();
    }

    return bind(observable).subscribe(subscriber);
  }

  public Subscription loadNext() {
    return loadNext(null);
  }

  /** Use {@link #loadNext()} instead. */
  @Deprecated
  public Subscription loadNext(Object context) {
    if (hasNext()) {
      return bind(pages.get(pages.size() - 1).loadNextPage()).subscribe(subscriber);
    } else {
      return Subscriptions.empty();
    }
  }

  private static <T extends VinliItem> Observable<Page<T>> bind(@NonNull Observable<Page<T>> observable) {
    return observable.observeOn(AndroidSchedulers.mainThread());
  }
}
