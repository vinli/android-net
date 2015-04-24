package li.vin.net.utils;

import android.util.Log;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import li.vin.net.Page;
import li.vin.net.VinliItem;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by kyle on 7/8/14.
 */
public abstract class PageAdapter<T extends VinliItem> extends BaseAdapter implements Observer<Page<T>> {
  private final List<Page<T>> mPages = new ArrayList<Page<T>>();

  private int mCount = 0;

  protected final String mTag;

  public PageAdapter() {
    mTag = ((Object) this).getClass().getSimpleName();
  }

  @Override public int getCount() {
    return mCount;
  }

  @Override public T getItem(int position) {
    if (position < 0 || position >= mCount) {
      throw new IllegalArgumentException(position + " is outside of the acceptable range 0.." + mCount);
    }

    for (int i = 0, il = mPages.size(), pos = 0; i < il; ++i) {
      final Page<T> page = mPages.get(i);
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
    return getItem(position).getId().hashCode();
  }

  @Override public void onCompleted() {
  }

  @Override public void onError(Throwable e) {
    Log.e(mTag, "failed to load a page", e);
  }

  @Override public void onNext(Page<T> page) {
    mPages.add(page);
    mCount += page.size();
    notifyDataSetChanged();
  }

  public boolean hasNext() {
    if (mPages.isEmpty()) {
      return true;
    }

    return mPages.get(mPages.size() - 1).hasNextPage();
  }

  public Subscription loadNext() {
    if (hasNext()) {
      return mPages.get(mPages.size() - 1).loadNextPage()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this);
    } else {
      return Subscriptions.empty();
    }
  }
}
