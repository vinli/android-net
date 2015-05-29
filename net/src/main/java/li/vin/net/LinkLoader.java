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
  private static final String GET = "GET";

  private final GsonConverter mGson;
  private final Client mClient;
  private final List<Header> mHeaders;

  public LinkLoader(Client client, String accessToken, GsonConverter gson) {
    mClient = client;
    mHeaders = Collections.singletonList(new Header("Authorization", "Bearer " + accessToken));
    mGson = gson;
  }

  @SuppressWarnings("unchecked")
  public <T extends VinliItem> Observable<Wrapped<T>> loadItem(String link, Type type) {
    if (link == null) {
      return Observable.empty();
    }

    return load(link, type);
  }

  @SuppressWarnings("unchecked")
  public <T extends VinliItem> Observable<Page<T>> loadPage(String link, Type type) {
    if (link == null) {
      return Observable.empty();
    }

    return load(link, type);
  }

  @SuppressWarnings("unchecked")
  public <T extends VinliItem> Observable<TimeSeries<T>> loadTimeSeries(String link, Type type) {
    if (link == null) {
      return Observable.empty();
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
