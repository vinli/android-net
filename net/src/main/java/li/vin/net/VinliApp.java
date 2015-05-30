package li.vin.net;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observable;

public final class VinliApp implements Diagnostics {
  private final Devices mDevices;
  private final Diagnostics mDiagnostics;
  private final Rules mRules;
  private final Events mEvents;
  private final Locations mLocations;

  private final Gson mGson;
  private final LinkLoader mLinkLoader;

  /*protected*/ VinliApp(@NonNull String accessToken) {
    final GsonBuilder gsonB = new GsonBuilder();

    final Client client = new OkClient();
    final RestAdapter.Log logger = new AndroidLog("VinliNet");

    Device.registerGson(gsonB);
    Rule.registerGson(gsonB);
    Event.registerGson(gsonB);
    Subscription.registerGson(gsonB);
    Vehicle.registerGson(gsonB);
    Message.registerGson(gsonB);
    Page.registerGson(gsonB);
    TimeSeries.registerGson(gsonB);
    ObjectRef.registerGson(gsonB);
    Location.registerGson(gsonB);
    Coordinate.registerGson(gsonB);

    mGson = gsonB.create();

    final GsonConverter gson = new GsonConverter(mGson);

    mLinkLoader = new LinkLoader(client, accessToken, gson);

    final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;

    final RequestInterceptor oauthInterceptor = new OauthInterceptor(accessToken);

    final RestAdapter platformAdapter = new RestAdapter.Builder()
        .setEndpoint(Endpoint.PLATFORM)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build();

    mDevices = platformAdapter.create(Devices.class);

    mDiagnostics = new RestAdapter.Builder()
        .setEndpoint(Endpoint.DIAGNOSTICS)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Diagnostics.class);

    mRules = new RestAdapter.Builder()
        .setEndpoint(Endpoint.RULES)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Rules.class);

    mEvents = new RestAdapter.Builder()
        .setEndpoint(Endpoint.EVENTS)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Events.class);

    mLocations = new RestAdapter.Builder()
        .setEndpoint(Endpoint.TELEMETRY)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Locations.class);
  }

  public Observable<Page<Device>> devices() {
    return mDevices.devices(null, null);
  }

  /**
   * Pass null for default
   */
  public Observable<Page<Device>> devices(Integer limit, Integer offset) {
    return mDevices.devices(limit, offset);
  }

  public Observable<Device> device(String deviceId) {
    return mDevices.device(deviceId).map(Wrapped.<Device>pluckItem());
  }

  @Override public Observable<Dtc> diagnoseDtcCode(String dtcCode) {
    return mDiagnostics.diagnoseDtcCode(dtcCode);
  }

  /*package*/ Rules rules() {
    return mRules;
  }

  /*package*/ Events events() {
    return mEvents;
  }

  /*package*/ Locations locations() {
    return mLocations;
  }

  /*package*/ LinkLoader linkLoader() {
    return mLinkLoader;
  }

  /*package*/ Gson gson() {
    return mGson;
  }

  private static final class OauthInterceptor implements RequestInterceptor {
    private static final String AUTH = "Authorization";
    private final String mBearer;

    public OauthInterceptor(String accessToken) {
      mBearer = "Bearer " + accessToken;
    }

    @Override public void intercept(RequestFacade request) {
      request.addHeader(AUTH, mBearer);
    }
  }
}
