package li.vin.net;

import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import okio.Buffer;
import okio.BufferedSource;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

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

  @Nullable
  public abstract String name();

  public abstract String chipId();

  @Nullable
  public abstract String icon();

  /*package*/ Device() {
  }

  public Observable<StreamMessage> stream() {
    return stream(null, null);
  }

  public Observable<StreamMessage> stream(
      @Nullable final List<StreamMessage.ParametricFilter.Seed> parametricFilters,
      @Nullable final StreamMessage.GeometryFilter.Seed geometryFilter) {
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

        final Gson gson = Vinli.curApp().gson();

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

        final Runnable startup = new Runnable() {
          @Override
          public void run() {
            if (subscriber.isUnsubscribed()) return;

            cleanup.run();

            WebSocketCall call = WebSocketCall.create(new OkHttpClient(), new Request.Builder() //
                .url(String.format("wss://stream%s/api/v1/messages?token=%s", Endpoint.domain(),
                    token)) //
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

                  if (geometryFilter != null) {
                    buffer.clear();
                    buffer.writeString(
                        gson.toJson(geometryFilter, StreamMessage.GeometryFilter.Seed.class), UTF8);
                    webSocket.sendMessage(WebSocket.PayloadType.TEXT, buffer);
                  }

                  if (parametricFilters != null && parametricFilters.size() > 0) {
                    for (StreamMessage.ParametricFilter.Seed filter : parametricFilters) {
                      buffer.clear();
                      buffer.writeString(
                          gson.toJson(filter, StreamMessage.ParametricFilter.Seed.class), UTF8);
                      webSocket.sendMessage(WebSocket.PayloadType.TEXT, buffer);
                    }
                  }
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
                      subscriber.onNext(
                          gson.fromJson(payload.readString(UTF8), StreamMessage.class));
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
        };

        startup.run();

        String chipId = chipId();
        if (chipId != null && (chipId = chipId.trim()).length() != 0) {
          final AtomicInteger udpNextCounter = new AtomicInteger(0);
          subscriber.add(makeUdpStream(chipId) //
              .timeout(15, TimeUnit.SECONDS) //
              .doOnNext(new Action1<StreamMessage>() {
                @Override
                public void call(StreamMessage message) {
                  if (udpNextCounter.incrementAndGet() >= 5) {
                    // stop the websocket if we have a hotspot to fall back on
                    udpNextCounter.set(Integer.MIN_VALUE);
                    cleanup.run();
                  }
                }
              }) //
              .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                  if (udpNextCounter.get() != 0) {
                    // restart the websocket in case it had been previously stopped
                    udpNextCounter.set(0);
                    startup.run();
                  }
                }
              }) //
              .retry(new Func2<Integer, Throwable, Boolean>() {
                @Override
                public Boolean call(Integer integer, Throwable throwable) {
                  // always retry the UDP stream if it was just a timeout.
                  // otherwise, something unexpected happen, abort completely.
                  return (throwable instanceof TimeoutException);
                }
              }) //
              .subscribe());
        }
      }
    });
  }

  private static Observable<StreamMessage> makeUdpStream(@NonNull final String chipId) {
    final String connId = UUID.randomUUID().toString();
    return Observable.create(new Observable.OnSubscribe<StreamMessage>() {
      @Override
      public void call(Subscriber<? super StreamMessage> subscriber) {
        if (subscriber.isUnsubscribed()) return;

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
          subscriber.onError(new IllegalThreadStateException( //
              "Cannot stream UDP on main thread."));
          return;
        }

        AtomicBoolean chipIdFound = new AtomicBoolean(false);

        while (true) {
          // ---
          byte[] read = new byte[256];
          DatagramSocket udpSocket = null;
          InetAddress hostAddr = null;

          try {
            udpSocket = new DatagramSocket(54321);
            udpSocket.setSoTimeout(1000);
            hostAddr = InetAddress.getByName("192.168.1.1");

            for (int i = 0; ; i++) {
              // ---
              if (i % 10 == 0) {
                byte[] msg = String.format("vvv%s", connId).getBytes();
                try {
                  udpSocket.send(new DatagramPacket(msg, msg.length, hostAddr, 54321));
                } catch (Exception ignored) {
                }
              }

              String result = null;
              try {
                DatagramPacket recv = new DatagramPacket(read, read.length);
                udpSocket.receive(recv);
                result = new String(recv.getData(), recv.getOffset(), recv.getLength()).trim();
              } catch (Exception ignored) {
              }

              if (result != null && !result.isEmpty()) {
                if (!chipIdFound.get()) {
                  if (result.startsWith("I:") && result.substring(2).equals(chipId)) {
                    chipIdFound.set(true);
                  }
                }
                if (chipIdFound.get()) {
                  // TODO parse the line - just log it and onNext an empty result for now.
                  // need a solution that turns raw lines from the OBD into StreamMessage -
                  // probably dropping a chunk of the bluetooth SDK into here for parsing.
                  Log.e("VVV", result); // remove this when parsing exists
                  if (subscriber.isUnsubscribed()) return;
                  subscriber.onNext(new StreamMessage());
                }
              }

              if (subscriber.isUnsubscribed()) return;
              // ---
            }
          } catch (Exception ignored) {
            // no-op
          } finally {
            if (udpSocket != null && hostAddr != null) {
              try {
                byte[] msg = String.format("vvk%s", connId).getBytes();
                udpSocket.send(new DatagramPacket(msg, msg.length, hostAddr, 54321));
              } catch (Exception ignored) {
              }
            }

            try {
              Thread.sleep(500);
            } catch (Exception ignored) {
            }

            if (udpSocket != null) {
              try {
                udpSocket.close();
              } catch (Exception ignored) {
              }
            }
          }

          if (subscriber.isUnsubscribed()) return;
          // ---
        }
      }
    }).subscribeOn(Schedulers.io());
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

  public Observable<TimeSeries<Trip>> trips() {
    return Vinli.curApp().trips().trips(id(), null, null, null, null);
  }

  public Observable<TimeSeries<Trip>> trips(@Nullable Date since, @Nullable Date until,
      @Nullable Integer limit, @Nullable String sortDir) {
    return Vinli.curApp().trips().trips(id(), since, until, limit, sortDir);
  }

  public Observable<TimeSeries<Message>> messages() {
    return Vinli.curApp().messages().messages(id(), null, null, null, null);
  }

  public Observable<TimeSeries<Message>> messages(@Nullable Date since, @Nullable Date until,
      @Nullable Integer limit, @Nullable String sortDir) {
    return Vinli.curApp().messages().messages(id(), since, until, limit, sortDir);
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
