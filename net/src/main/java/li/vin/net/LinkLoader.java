package li.vin.net;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import retrofit.client.Client;
import retrofit.client.Header;
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

  private GsonConverter mGson;
  private final Client mClient;
  private final List<Header> mHeaders;

  public LinkLoader(Client client, String accessToken) {
    mClient = client;
    mHeaders = Collections.singletonList(new Header("Authorization", "Bearer " + accessToken));
  }

  public void setGson(GsonConverter gson) {
    mGson = gson;
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
    if (mGson == null) {
      throw new AssertionError("mGson not set");
    }

    final String fullUrl = link;

    return Observable.create(new Observable.OnSubscribe<Object>() {
      @Override public void call(Subscriber<? super Object> subscriber) {
        final Request request = new Request(GET, fullUrl, mHeaders, null);
        try {
          final Response resp = mClient.execute(request);
          if (subscriber.isUnsubscribed()) {
            return;
          }

          final int statusCode = resp.getStatus();
          if (statusCode >= 200 && statusCode < 300) { // 2XX == successful request
            final Object parsedResponse = mGson.fromBody(resp.getBody(), type);
            subscriber.onNext(parsedResponse);
            subscriber.onCompleted();
          } else {
            final VinliError.ServerError err =
               (VinliError.ServerError) mGson.fromBody(resp.getBody(), VinliError.ServerError.class);
            subscriber.onError(VinliError.serverError(err));
          }
        } catch (IOException | ConversionException e) {
          subscriber.onError(e);
        }
      }
    });
  }

}
