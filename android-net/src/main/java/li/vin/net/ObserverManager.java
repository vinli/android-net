package li.vin.net;

import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by christophercasey on 8/3/15.
 *
 * Wraps Observable subscriptions in simple managed observables that can automatically bind to
 * Activities or Fragments and observe on the UI thread.
 */
/*package*/ final class ObserverManager {

  /** Register an observer. Always observes on the UI thread. Will throw an unchecked exception
   * if observer is already registered. */
  /*package*/ static <T> void registerObserver(@NonNull Observer<T> cb,
      @NonNull Observable<T> observable) {
    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      throw new IllegalStateException("Must be called on UI thread.");
    }
    Subscription sub = observable.observeOn(AndroidSchedulers.mainThread()).subscribe(cb);
    if (!callbacks().add(new CallbackSubscriptionTuple(cb, sub))) {
      throw new IllegalStateException("callback already registered.");
    }
  }

  /** Permissively attempt to unregister an already-registered observer. */
  /*package*/ static <T> void unregisterObserver(@NonNull Observer<T> cb) {
    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      throw new IllegalStateException("Must be called on UI thread.");
    }
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
