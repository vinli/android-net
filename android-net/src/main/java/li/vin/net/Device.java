package li.vin.net;

import android.os.Looper;
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
import java.io.InterruptedIOException;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import okio.Buffer;
import okio.BufferedSource;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
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
    final AtomicLong lastStreamData = new AtomicLong(System.currentTimeMillis());
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

        final AtomicBoolean suspend = new AtomicBoolean(false);

        final Func1<WebSocket, Boolean> handleSuspend = new Func1<WebSocket, Boolean>() {
          @Override
          public Boolean call(WebSocket webSocket) {
            if (suspend.get()) {
              try {
                webSocket.close(1000, "CLOSE_NORMAL");
              } catch (Exception ignored) {
              }
              return true;
            }
            return false;
          }
        };

        subscriber.add(rx.subscriptions.Subscriptions.create(new Action0() {
          @Override
          public void call() {
            suspend.set(false);
            cleanup.run();
          }
        }));

        final Runnable recordActivity = new Runnable() {
          @Override
          public void run() {
            lastStreamData.set(System.currentTimeMillis());
          }
        };

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
                recordActivity.run();

                if (handleSuspend.call(webSocket)) return;

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
                if (handleSuspend.call(webSocketRef.get())) return;

                cleanup.run();
                if (!subscriber.isUnsubscribed()) {
                  subscriber.onError(ioe);
                }
              }

              @Override
              public void onMessage(BufferedSource payload, WebSocket.PayloadType type)
                  throws IOException {
                try {
                  recordActivity.run();

                  if (handleSuspend.call(webSocketRef.get())) return;

                  if (subscriber.isUnsubscribed()) {
                    cleanup.run();
                    return;
                  }

                  if (type == WebSocket.PayloadType.TEXT) {
                    try {
                      String payloadStr = payload.readString(UTF8);
                      if (payloadStr != null && !payloadStr.isEmpty()) {
                        subscriber.onNext(gson.fromJson(payloadStr, StreamMessage.class));
                      }
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
                recordActivity.run();

                if (handleSuspend.call(webSocketRef.get())) return;

                if (subscriber.isUnsubscribed()) {
                  cleanup.run();
                }
              }

              @Override
              public void onClose(int code, String reason) {
                if (handleSuspend.call(webSocketRef.get())) return;

                cleanup.run();
                if (!subscriber.isUnsubscribed()) {
                  subscriber.onCompleted();
                }
              }
            });
          }
        };

        String chipId = chipId();
        if (chipId == null || (chipId = chipId.trim()).length() == 0) {
          // If we don't have a valid chip ID, start the websocket and return.
          startup.run();
          return;
        }

        // If we do have a valid chip id, let's try to find a UDP stream as an alternative to the
        // websocket streaming - faster, more battery efficient, better info available.

        try {
          // Acquire Duktape to keep its static instance refcounted to the whole stream instead
          // of each UDP session to avoid the need to excessively create and close Duktape
          // instances. Also, if for any reason Duktape cannot be acquired, this lets us know
          // that UDP streaming won't work out, so we can go ahead and proceed with the websocket
          // approach and bail out early on UDP.
          acquireDuktape();
          subscriber.add(rx.subscriptions.Subscriptions.create(new Action0() {
            @Override
            public void call() {
              releaseDuktape();
            }
          }));
        } catch (Exception any) {
          // If Duktape can't be acquired, start the websocket and return.
          startup.run();
          return;
        }

        try {
          // We can afford to wait 2 seconds before starting the websocket to see if we can get
          // an immediate response over UDP. If so, it's more efficient to never start the web-
          // socket than to start it then immediately suspend.
          final Scheduler.Worker worker = Schedulers.io().createWorker();
          subscriber.add(worker);
          worker.schedule(new Action0() {
            @Override
            public void call() {
              if (!worker.isUnsubscribed()) worker.unsubscribe();
              if (!subscriber.isUnsubscribed() && !suspend.get()) {
                startup.run();
              }
            }
          }, 2, TimeUnit.SECONDS);
        } catch (Exception any) {
          // Shouldn't happen, but if it does, we can start the websocket right away and it will be
          // suspended later if the UDP streaming kicks in.
          startup.run();
        }

        final AtomicInteger consectiveUdpTimeouts = new AtomicInteger();

        subscriber.add(makeUdpStream(chipId) //
            // time without UDP stream data before starting or restarting websocket
            .timeout(8, TimeUnit.SECONDS) //
            .doOnNext(new Action1<StreamMessage>() {
              @Override
              public void call(StreamMessage message) {
                // suspend the websocket if we have a UDP stream running
                suspend.set(true);
                // send the UDP data to the main stream subscriber
                consectiveUdpTimeouts.set(0); // valid data resets consecutive timeouts
                recordActivity.run(); // valid stream data
                if (!subscriber.isUnsubscribed()) subscriber.onNext(message);
              }
            }) //
            .doOnError(new Action1<Throwable>() {
              @Override
              public void call(Throwable throwable) {
                // timed out, or something else went wrong:
                // restart the websocket if it had been previously suspended
                if (suspend.compareAndSet(true, false)) startup.run();
              }
            }) //
            .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
              @Override
              public Observable<?> call(Observable<? extends Throwable> errs) {
                // exponential backoff on retries getting response over UDP. Each time we timeout
                // with no data over UDP, wait a little longer before warming up a new UDP socket
                // and trying again.
                return errs //
                    .flatMap(new Func1<Throwable, Observable<?>>() {
                      @Override
                      public Observable<?> call(Throwable throwable) {
                        if (throwable instanceof TimeoutException) {
                          consectiveUdpTimeouts.incrementAndGet();
                          return Observable.just(null);
                        }
                        // don't retry if it's not a timeout exception - something unknown went
                        // wrong, and we should have acounted for everything, so it's safer not
                        // to retry at all.
                        return Observable.error(throwable);
                      }
                    }) //
                    .flatMap(new Func1<Object, Observable<?>>() {
                      @Override
                      public Observable<?> call(Object o) {
                        // wait #timeouts since last data ^ 3, capped at 64 secs, before retrying
                        long wait = (long) Math.pow(Math.min(consectiveUdpTimeouts.get(), 4), 3);
                        return Observable.timer(wait, TimeUnit.SECONDS);
                      }
                    });
              }
            }) //
            .onErrorReturn(new Func1<Throwable, StreamMessage>() {
              @Override
              public StreamMessage call(Throwable throwable) {
                // ignore errors so we can safely subscribe
                return null;
              }
            }) //
            .subscribe());
      }
    }).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
      @Override
      public Observable<?> call(Observable<? extends Throwable> errs) {
        return errs.flatMap(new Func1<Object, Observable<?>>() {
          @Override
          public Observable<?> call(Object o) {
            long msSinceLastStreamData = System.currentTimeMillis() - lastStreamData.get();
            long wait = Math.min(30, msSinceLastStreamData / 1000);
            return Observable.timer(wait, TimeUnit.SECONDS);
          }
        });
      }
    });
  }

  // --- singleton Duktape JS interpreter inst with threadsafe refcounting semantics

  private static final Object duktapeRefLock = new Object();
  private static int duktapeRefCtr = 0;
  private static Duktaper duktape;

  private static void releaseDuktape() {
    synchronized (duktapeRefLock) {
      duktapeRefCtr--;
      if (duktapeRefCtr < 0) duktapeRefCtr = 0;
      if (duktapeRefCtr == 0) {
        if (duktape == null) throw new IllegalStateException("try to release null duktape");
        try {
          duktape.close();
        } catch (Exception ignored) {
        }
        duktape = null;
      }
    }
  }

  @NonNull
  private static Duktaper acquireDuktape() {
    synchronized (duktapeRefLock) {
      duktapeRefCtr++;
      if (duktapeRefCtr == 1) {
        try {
          if (duktape != null) throw new IllegalStateException("try to create w/ nonnull duktape");
          duktape = Duktaper.create();
          duktape.evaluate(ObdJsLib.lib());
          return duktape;
        } catch (Exception e) {
          duktapeRefCtr = 0;
          throw e;
        }
      }
      if (duktape == null) {
        duktapeRefCtr = 0;
        throw new IllegalStateException("try to acquire null duktape");
      }
      return duktape;
    }
  }

  // ---

  /** Stream all the UDP things. */
  private static Observable<StreamMessage> makeUdpStream(@NonNull final String chipId) {
    return Observable.create(new Observable.OnSubscribe<StreamMessage>() {
      @Override
      public void call(Subscriber<? super StreamMessage> subscriber) {
        if (subscriber.isUnsubscribed()) return;

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
          subscriber.onError(new IllegalThreadStateException( //
              "Cannot stream UDP on main thread."));
          return;
        }

        String connId = UUID.randomUUID().toString();

        AtomicBoolean chipIdFound = new AtomicBoolean(false);

        while (true) {
          // ---

          byte[] read = new byte[256];
          DatagramSocket udpSocket = null;
          InetAddress hostAddr = null;
          Duktaper duktape = null;

          try {
            udpSocket = new DatagramSocket(); // choose any available port
            udpSocket.setSoTimeout(1000);
            hostAddr = InetAddress.getByName("192.168.1.1");
            duktape = acquireDuktape();

            for (int i = 0; ; i++) {
              // ---
              if (i % 20 == 0) {
                byte[] msg = String.format("vvv%s", connId).getBytes();
                try {
                  udpSocket.send(new DatagramPacket(msg, msg.length, hostAddr, 54321));
                } catch (Exception e) {
                  throw Exceptions.propagate(e);
                }
              }

              String result = null;
              try {
                DatagramPacket recv = new DatagramPacket(read, read.length);
                udpSocket.receive(recv);
                result = new String(recv.getData(), recv.getOffset(), recv.getLength()).trim();
              } catch (Exception e) {
                if (!(e instanceof InterruptedIOException)) {
                  throw Exceptions.propagate(e);
                }
              }

              if (result != null && !result.isEmpty()) {
                int idIndex = result.indexOf('#');
                String id = idIndex != -1
                    ? result.substring(0, idIndex)
                    : null;

                if (!chipIdFound.get() && chipId.equals(id)) {
                  chipIdFound.set(true);
                }

                if (idIndex != -1) {
                  if (idIndex == result.length() - 1) {
                    result = null;
                  } else {
                    result = result.substring(idIndex + 1);
                  }
                }

                if (result != null && chipIdFound.get()) {
                  if (subscriber.isUnsubscribed()) return;
                  StreamMessage.processRawLine(result, duktape, subscriber);
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
                udpSocket.disconnect();
              } catch (Exception ignored) {
              }
              try {
                udpSocket.close();
              } catch (Exception ignored) {
              }
            }

            if (duktape != null) {
              try {
                releaseDuktape();
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
