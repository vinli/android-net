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

public final class VinliApp implements Devices, Diagnostics {
  private final Devices mDevices;
  private final Diagnostics mDiagnostics;
  private final Rules mRules;
  private final Gson mGson;
  private final LinkLoader mLinkLoader;

  /*protected*/ VinliApp(@NonNull String accessToken) {
    final GsonBuilder gsonB = new GsonBuilder();

    final Client client = new OkClient();
    final RestAdapter.Log logger = new AndroidLog("VinliNet");

    Device.registerGson(gsonB, this);
    Rule.registerGson(gsonB, this);

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
  }

  @Override public Observable<Page<Device>> getDevices() {
    return mDevices.getDevices();
  }

  /**
   * Pass null for default
   */
  @Override public Observable<Page<Device>> getDevices(Integer limit, Integer offset) {
    return mDevices.getDevices(limit, offset);
  }

  @Override public Observable<Device> getDevice(String deviceId) {
    return mDevices.getDevice(deviceId);
  }

  /** <p><b>Parameters:</b></p>  <Ul>deviceId - Api device id for Vinli devices.</Ul>
   * <p><b>Returns:</b></p> <Ul>Device observable stream</Ul>
   * <p><b>Notice:</b></p> <Ul>This function was orignally intended for debugging purposes.
   * It will be gone soon.</Ul>
   */
  public Observable<Device> registerDevice(String deviceID) {
    return  mDevices.registerDevice(Device.builder().id(deviceID).build());
  }

  @Override public Observable<Device> registerDevice(Device device) {
    return mDevices.registerDevice(device);
  }

  @Override public Observable<Dtc> diagnoseDtcCode(String dtcCode) {
    return mDiagnostics.diagnoseDtcCode(dtcCode);
  }

  /*package*/ Rules rules() {
    return mRules;
  }

  /*package*/ LinkLoader getLinkLoader() {
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
