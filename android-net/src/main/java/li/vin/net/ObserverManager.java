package li.vin.net;

import android.app.Activity;
import android.support.annotation.NonNull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.internal.Assertions;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by christophercasey on 8/3/15.
 *
 * Wraps Observable subscriptions in simple managed observables that can automatically bind to
 * Activities or Fragments and observe on the UI thread.
 */
/*package*/ final class ObserverManager {

  /** Register an observer. Binding is optional, if nonnull is assumed to be an Activity or
   * Fragment. Always observes on the UI thread. Will throw an unchecked exception if observer is
   * already registered. */
  /*package*/ static <T> void registerObserver(@NonNull Observer<T> cb,
      @NonNull Observable<T> observable, Object binding) {
    Assertions.assertUiThread();
    Subscription sub;
    if (binding instanceof Activity) {
      sub = AppObservable.bindActivity((Activity) binding, observable).subscribe(cb);
    } else if (binding != null) {
      sub = AppObservable.bindFragment(binding, observable).subscribe(cb);
    } else {
      sub = observable.observeOn(AndroidSchedulers.mainThread()).subscribe(cb);
    }
    if (!callbacks().add(new CallbackSubscriptionTuple(cb, sub))) {
      throw new IllegalStateException("callback already registered.");
    }
  }

  /** Permissively attempt to unregister an already-registered observer. */
  /*package*/ static void unregisterObserver(@NonNull Observer<?> cb) {
    Assertions.assertUiThread();
    for (Iterator<CallbackSubscriptionTuple> i=callbacks().iterator(); i.hasNext(); ) {
      CallbackSubscriptionTuple cbSub = i.next();
      if (cbSub.cb.equals(cb)) {
        cbSub.sub.unsubscribe();
        i.remove();
        return;
      }
    }
  }

  private static final class InitOnDemandHolder {
    private static final Set<CallbackSubscriptionTuple> sCallbacks = new HashSet<>();
  }

  private static Set<CallbackSubscriptionTuple> callbacks() {
    return InitOnDemandHolder.sCallbacks;
  }

  private static final class CallbackSubscriptionTuple {
    final Observer<?> cb;
    final rx.Subscription sub;

    CallbackSubscriptionTuple(Observer<?> cb, rx.Subscription sub) {
      this.cb = cb;
      this.sub = sub;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CallbackSubscriptionTuple that = (CallbackSubscriptionTuple) o;

      return cb.equals(that.cb);
    }

    @Override public int hashCode() {
      return cb.hashCode();
    }
  }

  private ObserverManager() { }
}
