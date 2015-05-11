package li.vin.net;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;

/*package*/ class LinkLoader {
  private static final Observable EMPTY_OBSERVABLE = Observable.create(new Observable.OnSubscribe<Object>() {
    @Override public void call(Subscriber<? super Object> subscriber) {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onCompleted();
      }
    }
  });

  private static final String GET = "GET";

  private final GsonFactory mGson;
  private final Client mClient;

  public LinkLoader(GsonFactory gson, Client client) {
    mGson = gson;
    mClient = client;
  }

  @SuppressWarnings("unchecked")
  public <T> Observable<T> loadItem(String link, Class<T> cls) {
    if (link == null) {
      return EMPTY_OBSERVABLE;
    }

    return load(link, cls);
  }

  @SuppressWarnings("unchecked")
  public <T> Observable<Page<T>> loadPage(String link, Type type) {
    if (link == null) {
      return EMPTY_OBSERVABLE;
    }

    return load(link, type);
  }

  private Observable load(String link, final Type type) {
    final String fullUrl = Endpoint.PLATFORM.getUrlWithoutVersion() + link;

    return Observable.create(new Observable.OnSubscribe<Object>() {
      @Override public void call(Subscriber<? super Object> subscriber) {
        final Request request = new Request(GET, fullUrl, null, null);
        try {
          final Response resp = mClient.execute(request);
          if (subscriber.isUnsubscribed()) {
            return;
          }

          final int statusCode = resp.getStatus();
          if (statusCode >= 200 && statusCode < 300) { // 2XX == successful request
            final Object parsedResponse = mGson.getGson().fromBody(resp.getBody(), type);
            subscriber.onNext(parsedResponse);
            subscriber.onCompleted();
          } else {
            final VinliError.ServerError err =
               (VinliError.ServerError) mGson.getGson().fromBody(resp.getBody(), VinliError.ServerError.class);
            subscriber.onError(VinliError.serverError(err));
          }
        } catch (IOException e) {
          subscriber.onError(e);
        } catch (ConversionException e) {
          subscriber.onError(e);
        }
      }
    });
  }

  public static final class GsonFactory {
    private GsonConverter mGson;

    public void setGson(GsonConverter gson) {
      mGson = gson;
    }

    public GsonConverter getGson() {
      return mGson;
    }
  }

}
