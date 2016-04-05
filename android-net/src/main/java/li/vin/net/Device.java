package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import auto.parcel.AutoParcel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import okio.Buffer;
import okio.BufferedSource;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

@AutoParcel
public abstract class Device implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Device>>() {
  }.getType();
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Device>>() {
  }.getType();

  /*package*/
  static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Device.class, AutoParcelAdapter.create(AutoParcel_Device.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Device_Links.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Device.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Device.class));
  }

  @SuppressWarnings("unused")
  private static String toListJson(@NonNull List<Device> devices) {
    return Vinli.curApp().gson().toJson(devices, new TypeToken<List<Device>>() {
    }.getType());
  }

  @SuppressWarnings("unused")
  private static List<Device> fromListJson(@NonNull String devices) {
    return Vinli.curApp().gson().fromJson(devices, new TypeToken<List<Device>>() {
    }.getType());
  }

  @SuppressWarnings("unused")
  private static String toJson(@NonNull Device device) {
    return Vinli.curApp().gson().toJson(device);
  }

  @SuppressWarnings("unused")
  private static Device fromJson(@NonNull String device) {
    return Vinli.curApp().gson().fromJson(device, Device.class);
  }

  @SuppressWarnings("unused")
  private static Device createDevice(final String id, final String name, final String chipId,
      final String icon) {
    return new AutoParcel_Device(id, new AutoParcel_Device_Links("", "", "", ""), name, chipId,
        icon);
  }

  /*package*/
  abstract Links links();

  public abstract String name();

  public abstract String chipId();

  public abstract String icon();

  /*package*/ Device() {
  }

  public Observable<StreamMessage> stream(){
    return stream(null, null);
  }

  public Observable<StreamMessage> stream(List<StreamMessage.ParametricFilter> parametricFilters, StreamMessage.GeometricFilter geometricFilter) {
    return Observable.create(new Observable.OnSubscribe<StreamMessage>() {
      @Override
      public void call(final Subscriber<? super StreamMessage> subscriber) {
        if (subscriber.isUnsubscribed()) return;

        final String token;
        try {
          token = Vinli.curApp().getAccessToken();
          if (token == null || TextUtils.getTrimmedLength(token) == 0) {
            throw new RuntimeException("no access token!");
          }
        } catch (Exception e) {
          subscriber.onError(e);
          return;
        }

        String deviceId = id();
        if (deviceId == null || TextUtils.getTrimmedLength(deviceId) == 0) {
          subscriber.onError(new RuntimeException("no device id!"));
          return;
        }
        final String message = String.format( //
            "{\"type\":\"sub\",\"subject\":{\"type\":\"device\",\"id\":\"%s\"}}", deviceId);

        final Gson gson = new Gson();

        final Charset UTF8 = Charset.forName("UTF-8");

        final AtomicReference<WebSocket> webSocketRef = new AtomicReference<>();

        final Runnable cleanup = new Runnable() {
          @Override
          public void run() {
            try {
              webSocketRef.get().close(1000, "CLOSE_NORMAL");
            } catch (Exception ignored) {
            }
          }
        };

        subscriber.add(rx.subscriptions.Subscriptions.create(new Action0() {
          @Override
          public void call() {
            cleanup.run();
          }
        }));

        WebSocketCall call = WebSocketCall.create(new OkHttpClient(), new Request.Builder() //
            .url(String.format("wss://stream.vin.li/api/v1/messages?token=%s", token)) //
            .addHeader("Accept", "application/json") //
            .addHeader("Content-Type", "application/json") //
            .build());

        call.enqueue(new WebSocketListener() {
          @Override
          public void onOpen(WebSocket webSocket, Response response) {
            webSocketRef.set(webSocket);

            if (subscriber.isUnsubscribed()) {
              cleanup.run();
              return;
            }

            Buffer buffer = new Buffer();
            buffer.writeString(message, UTF8);
            try {
              webSocket.sendMessage(WebSocket.PayloadType.TEXT, buffer);
            } catch (IOException ioe) {
              cleanup.run();
              if (!subscriber.isUnsubscribed()) {
                subscriber.onError(ioe);
              }
            }
          }

          @Override
          public void onFailure(IOException ioe, Response response) {
            cleanup.run();
            if (!subscriber.isUnsubscribed()) {
              subscriber.onError(ioe);
            }
          }

          @Override
          public void onMessage(BufferedSource payload, WebSocket.PayloadType type)
              throws IOException {
            try {
              if (subscriber.isUnsubscribed()) {
                cleanup.run();
                return;
              }

              if (type == WebSocket.PayloadType.TEXT) {
                try {
                  subscriber.onNext(gson.fromJson(payload.readString(UTF8), StreamMessage.class));
                } catch (IOException ioe) {
                  throw ioe;
                } catch (Exception ignored) {
                }
              }
            } finally {
              payload.close();
            }
          }

          @Override
          public void onPong(Buffer payload) {
            if (subscriber.isUnsubscribed()) {
              cleanup.run();
            }
          }

          @Override
          public void onClose(int code, String reason) {
            cleanup.run();
            if (!subscriber.isUnsubscribed()) {
              subscriber.onCompleted();
            }
          }
        });
      }
    });
  }

  public Observable<Page<Vehicle>> vehicles() {
    return vehicles(null, null);
  }

  public Observable<Page<Vehicle>> vehicles(@Nullable Integer limit, @Nullable Integer offset) {
    return Vinli.curApp().vehicles().vehicles(id(), limit, offset);
  }

  public Observable<Vehicle> vehicle(@NonNull String vehicleId) {
    return Vinli.curApp().vehicles().vehicle(id(), vehicleId).map(Wrapped.<Vehicle>pluckItem());
  }

  public Observable<Vehicle> latestVehicle() {
    return vehicle("_latest");
  }

  public Observable<Page<Rule>> rules() {
    return rules(null, null);
  }

  public Observable<Page<Rule>> rules(@Nullable Integer limit, @Nullable Integer offset) {
    return Vinli.curApp().rules().rules(id(), limit, offset);
  }

  public Observable<Rule> rule(@NonNull String ruleId) {
    return Vinli.curApp().rules().rule(id(), ruleId).map(Wrapped.<Rule>pluckItem());
  }

  public Observable<TimeSeries<Event>> events() {
    return events(null, null, null, null, null);
  }

  public Observable<TimeSeries<Event>> events(@Nullable String type, @Nullable String objectId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit) {
    return Vinli.curApp().events().events(id(), type, objectId, since, until, limit);
  }

  public Observable<TimeSeries<Location>> locations() {
    return locations(null, null, null, null, null);
  }

  public Observable<TimeSeries<Location>> locations(@Nullable String fields, @Nullable Date until,
      @Nullable Date since, @Nullable Integer limit, @Nullable String sortDir) {
    return Vinli.curApp().locations().locations(id(), fields, until, since, limit, sortDir);
  }

  public Observable<Location> latestlocation() {
    return locations(null, null, null, 1, null).flatMap(TimeSeries.<Location>extractItems())
        .firstOrDefault(null);
  }

  public Observable<TimeSeries<Snapshot>> snapshots() {
    return snapshots(null, null, null, null, null);
  }

  public Observable<TimeSeries<Snapshot>> snapshots(@Nullable String fields, @Nullable Date until,
      @Nullable Date since, @Nullable Integer limit, @Nullable String sortDir) {
    return Vinli.curApp().snapshots().snapshots(id(), fields, until, since, limit, sortDir);
  }

  public Observable<Page<Subscription>> subscriptions() {
    return subscriptions(null, null, null, null);
  }

  public Observable<Page<Subscription>> subscriptions(@Nullable Integer limit,
      @Nullable Integer offset, @Nullable String objectId, @Nullable String objectType) {
    return Vinli.curApp().subscriptions().subscriptions(id(), limit, offset, objectId, objectType);
  }

  /** Use {@link VinliApp#subscription(String)} instead. */
  @Deprecated
  public Observable<Subscription> subscription(@NonNull String subscriptionId) {
    return Vinli.curApp()
        .subscriptions()
        .subscription(subscriptionId)
        .map(Wrapped.<Subscription>pluckItem());
  }

  public Observable<Page<Trip>> trips() {
    return Vinli.curApp().trips().trips(id(), null, null);
  }

  public Observable<Page<Trip>> trips(@Nullable Integer limit, @Nullable Integer offset) {
    return Vinli.curApp().trips().trips(id(), limit, offset);
  }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String self();

    public abstract String rules();

    public abstract String vehicles();

    public abstract String latestVehicle();

    /*package*/ Links() {
    }
  }
}
