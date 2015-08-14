package li.vin.net.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public abstract class RxAdapter<T> extends BaseAdapter {
  private Observable<T> observable;
  private rx.Subscription curSubscription;
  private List<T> items = new ArrayList<>();

  private final Subscriber<T> subscriber = new Subscriber<T>() {
    @Override public void onCompleted() {
      RxAdapter.this.onComplete();
    }

    @Override public void onError(Throwable e) {
      RxAdapter.this.onError(e);
    }

    @Override public void onNext(T t) {
      RxAdapter.this.onNext(t);
      items.add(t);
      RxAdapter.this.notifyDataSetChanged();
    }
  };

  @Override public int getCount() {
    return items.size();
  }

  @Override public T getItem(int position) {
    return items.get(position);
  }

  protected void onComplete() {
    Log.e(this.getClass().getSimpleName(), "onComplete");
  }

  protected void onError(Throwable e) {
    Log.e(this.getClass().getSimpleName(), "onError", e);
  }

  protected void onNext(T t) { }

  public rx.Subscription subscribe(@NonNull Observable<T> observable) {
    return subscribe(null, observable);
  }

  /** Use {@link #subscribe(Observable)} instead. */
  @Deprecated
  public rx.Subscription subscribe(Object context, @NonNull Observable<T> observable) {
    if (curSubscription != null && !curSubscription.isUnsubscribed()) {
      curSubscription.unsubscribe();
    }

    this.observable = bind(observable);

    curSubscription = bind(observable).subscribe(subscriber);

    if (!items.isEmpty()) {
      items.clear();
      this.notifyDataSetChanged();
    }

    return curSubscription;
  }

  private static final <T> Observable<T> bind(@NonNull Observable<T> observable) {
    return observable.observeOn(AndroidSchedulers.mainThread());
  }
}
