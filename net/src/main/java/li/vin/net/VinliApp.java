package li.vin.net;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observable;

public final class VinliApp implements Devices, Diagnostics, Vehicles {
  private final Devices mDevices;
  private final Diagnostics mDiagnostics;
  private final Vehicles mVehicles;
  private final LinkLoader mLinkLoader;

  /*protected*/ VinliApp(@NonNull String accessToken) {
    final GsonBuilder gsonB = new GsonBuilder()
        .registerTypeAdapter(Device.class, WrappedJsonConverter.create(Device.class))
        .registerTypeAdapter(Vehicle.class, WrappedJsonConverter.create(Vehicle.class))
        .registerTypeAdapter(Dtc.class, WrappedJsonConverter.create(Dtc.class))
        .registerTypeAdapter(Group.class, WrappedJsonConverter.create(Group.class));

    final Client client = new OkClient();
    final RestAdapter.Log logger = new AndroidLog("VinliNet");
    final LinkLoader.GsonFactory gf = new LinkLoader.GsonFactory();
    mLinkLoader = new LinkLoader(gf, client);

    registerPageAdapter(gsonB, Device.PAGE_TYPE, Device.class, mLinkLoader);
    registerPageAdapter(gsonB, Vehicle.PAGE_TYPE, Vehicle.class, mLinkLoader);

    final GsonConverter gson = new GsonConverter(gsonB.create());
    gf.setGson(gson);

    final RequestInterceptor oauthInterceptor = new OauthInterceptor(accessToken);

    final RestAdapter platformAdapter = new RestAdapter.Builder()
        .setEndpoint(Endpoint.PLATFORM)
        .setLog(logger)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build();

    final RestAdapter diagnosticsAdapter = new RestAdapter.Builder()
        .setEndpoint(Endpoint.DIAGNOSTICS)
        .setLog(logger)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build();

    mDevices = platformAdapter.create(Devices.class);
    mDiagnostics = diagnosticsAdapter.create(Diagnostics.class);
    mVehicles = platformAdapter.create(Vehicles.class);
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

  public Observable<Device> registerDevice(String chipId, String caseId) {
    return mDevices.registerDevice(Device.builder()
        .caseId(caseId)
        .chipId(chipId)
        .build());
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

  @Override public Observable<Page<Vehicle>> getVehicles() {
    return mVehicles.getVehicles();
  }

  @Override public Observable<Page<Vehicle>> getVehicles(Integer limit, Integer offset) {
    return mVehicles.getVehicles(limit, offset);
  }

  @Override public Observable<Vehicle> getVehicle(String id) {
    return mVehicles.getVehicle(id);
  }

  @Override public Observable<Vehicle> getLatestDeviceVehicle(String id) {
    return mVehicles.getLatestDeviceVehicle(id);
  }

  /*package*/ LinkLoader getLinkLoader() {
    return mLinkLoader;
  }

  private static <T> void registerPageAdapter(GsonBuilder b, Type t, Class<T> cls, LinkLoader loader) {
    b.registerTypeAdapter(t, PageConverter.create(cls, t, loader));
  }

  public static final class WrappedJsonConverter<T> extends TypeAdapter<T> {

    public static <T> WrappedJsonConverter<T> create(Class<T> cls) {
      return new WrappedJsonConverter<T>(cls, cls.getSimpleName().toLowerCase(Locale.US));
    }

    private final String mName;
    private final Class<T> mCls;
    private final Gson mGson = new Gson();

    private WrappedJsonConverter(Class<T> cls, String name) {
      mCls = cls;
      mName = name;
    }

    @Override public void write(JsonWriter out, T value) throws IOException {
      out.beginObject();
        out.name(mName);
        mGson.toJson(value, mCls, out);
      out.endObject();
    }

    @Override public T read(JsonReader in) throws IOException {
      in.beginObject();

        final String name = in.nextName();
        if (!mName.equals(name)) {
          throw new IOException(name + " does not match expected name " + mName);
        }
        final T item = mGson.fromJson(in, mCls);

      in.endObject();

      return item;
    }

  }

  private static final class PageConverter<T> extends TypeAdapter<Page<T>> {
    private static final String META = "meta";

    public static <T> PageConverter<T> create(Class<T> cls, Type pageType, LinkLoader loader) {
      return new PageConverter<T>(cls, pageType, cls.getSimpleName().toLowerCase(Locale.US) + 's', loader);
    }

    private final String mName;
    private final Class<T> mCls;
    private final Type mPageType;
    private final LinkLoader mLoader;
    private final Gson mGson = new Gson();

    private PageConverter(Class<T> cls, Type pageType, String name, LinkLoader loader) {
      mCls = cls;
      mPageType = pageType;
      mName = name;
      mLoader = loader;
    }

    @Override public void write(JsonWriter out, Page<T> value) throws IOException {
      throw new UnsupportedOperationException("writing a page is not supported");
    }

    @Override public Page<T> read(JsonReader in) throws IOException {
      List<T> items = null;
      Page.Meta meta = null;

      in.beginObject();
      while (in.hasNext()) {
        final String name = in.nextName();
        if (META.equals(name)) {
          meta = mGson.fromJson(in, Page.Meta.class);
        } else if (mName.equals(name)) {
          items = new ArrayList<T>();
          in.beginArray();
          while (in.hasNext()) {
            items.add(mGson.<T>fromJson(in, mCls));
          }
          in.endArray();
        } else {
          throw new IOException("unrecognized key '" + name + "' while parsing " + mName);
        }
      }
      in.endObject();

      if (items == null) {
        throw new IOException("no items found while parsing " + mName);
      }
      if (meta == null) {
        throw new IOException("no meta found while parsing " + mName);
      }

      return Page.create(items, meta, mLoader, mPageType);
    }
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
