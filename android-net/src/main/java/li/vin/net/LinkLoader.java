package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import auto.parcel.AutoParcel;
import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedOutput;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/*package*/ class LinkLoader {
  private static final String GET = "GET";
  private static final String DELETE = "DELETE";
  static final String THREAD_PREFIX = "Vinli-";
  static final String IDLE_THREAD_NAME = THREAD_PREFIX + "Idle";

  private final Executor executor = Executors.newCachedThreadPool(new ThreadFactory() {
    @Override public Thread newThread(final Runnable r) {
      return new Thread(new Runnable() {
        @Override public void run() {
          android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
          r.run();
        }
      }, IDLE_THREAD_NAME);
    }
  });

  private final GsonConverter mGson;
  private final Client mClient;
  private final List<Header> mHeaders;

  public LinkLoader(Client client, String accessToken, GsonConverter gson) {
    mClient = client;
    mHeaders = Collections.singletonList(new Header("Authorization", "Bearer " + accessToken));
    mGson = gson;
  }

  public <T> Observable<T> create(@NonNull final String link, @NonNull final Type type, T body) {
    final Observable.OnSubscribe<T> onSubscribe = RequestSubscriber.<T>builder()
        .gson(mGson)
        .client(mClient)
        .headers(mHeaders)
        .verb("POST")
        .link(link)
        .respType(type)
        .body(body)
        .build();

    return Observable.create(onSubscribe).subscribeOn(Schedulers.io());
  }

  public <T> Observable<T> read(@NonNull final String link, @NonNull final Type type) {
    final Observable.OnSubscribe<T> onSubscribe = RequestSubscriber.<T>builder()
        .gson(mGson)
        .client(mClient)
        .headers(mHeaders)
        .verb("GET")
        .link(link)
        .respType(type)
        .build();

    return Observable.create(onSubscribe).subscribeOn(Schedulers.io());
  }

  public <T> Observable<T> update(@NonNull final String link, @NonNull final Type type, T body) {
    final Observable.OnSubscribe<T> onSubscribe = RequestSubscriber.<T>builder()
        .gson(mGson)
        .client(mClient)
        .headers(mHeaders)
        .verb("PUT")
        .link(link)
        .respType(type)
        .body(body)
        .build();

    return Observable.create(onSubscribe).subscribeOn(Schedulers.io());
  }

  public Observable<Void> delete(@NonNull final String link) {
    final Observable.OnSubscribe<Void> onSubscribe = RequestSubscriber.<Void>builder()
        .gson(mGson)
        .client(mClient)
        .headers(mHeaders)
        .verb("DELETE")
        .link(link)
        .build();

    return Observable.create(onSubscribe).subscribeOn(Schedulers.io());
  }

  @AutoParcel
  /*package*/ static abstract class RequestSubscriber<T> implements Observable.OnSubscribe<T> {
    public static final <T> Builder<T> builder() {
      return new AutoParcel_LinkLoader_RequestSubscriber.Builder<>();
    }

    protected abstract String verb();
    protected abstract Client client();
    protected abstract String link();
    protected abstract List<Header> headers();
    protected abstract GsonConverter gson();
    @Nullable protected abstract Object body();
    @Nullable protected abstract Type respType();

    @AutoParcel.Builder
    public interface Builder<T> {
      Builder<T> verb(String s);
      Builder<T> client(Client c);
      Builder<T> link(String s);
      Builder<T> headers(List<Header> l);
      Builder<T> gson(GsonConverter g);
      Builder<T> body(@Nullable Object o);
      Builder<T> respType(@Nullable Type t);

      RequestSubscriber<T> build();
    }

    /*package*/ RequestSubscriber() { }

    @Override public final void call(Subscriber<? super T> subscriber) {
      try {
        final TypedOutput reqBody = body() == null
            ? null
            : gson().toBody(body());

        // NOTE: this approach works, but we lose all of Retrofit's nice logging.
        // Need to look into alternatives.
        final Request request = new Request(verb(), link(), headers(), reqBody);
        final Response resp = client().execute(request);
        if (subscriber.isUnsubscribed()) {
          return;
        }

        final int statusCode = resp.getStatus();
        if (statusCode >= 200 && statusCode < 300) { // 2XX == successful request
          if (respType() != null) {
            @SuppressWarnings("unchecked")
            final T parsedResponse = (T) gson().fromBody(resp.getBody(), respType());
            subscriber.onNext(parsedResponse);
          }

          subscriber.onCompleted();
        } else {
          final VinliError.ServerError err =
              (VinliError.ServerError) gson().fromBody(resp.getBody(), VinliError.ServerError.class);
          subscriber.onError(VinliError.serverError(err));
        }
      } catch (IOException | ConversionException e) {
        subscriber.onError(e);
      }
    }

  }


}
