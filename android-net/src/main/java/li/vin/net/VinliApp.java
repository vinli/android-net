package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public final class VinliApp {
  private final Devices mDevices;
  private final Diagnostics mDiagnostics;
  private final Rules mRules;
  private final Events mEvents;
  private final Locations mLocations;
  private final Snapshots mSnapshots;
  private final Vehicles mVehicles;
  private final Subscriptions mSubscriptions;
  private final Users mUsers;
  private final Trips mTrips;
  private final Distances mDistances;
  private final Messages mMessages;
  private final Collisions mCollisions;
  private final ReportCards mReportCards;
  private final Notifications mNotifications;

  private final Gson mGson;

  private final String mAccessToken;

  private OkHttpClient client;

  public GsonBuilder gsonBuilder() {
    final GsonBuilder gsonB = new GsonBuilder();

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
    Snapshot.registerGson(gsonB);
    Notification.registerGson(gsonB);
    User.registerGson(gsonB);
    Trip.registerGson(gsonB);
    DistanceList.registerGson(gsonB);
    Odometer.registerGson(gsonB);
    OdometerTrigger.registerGson(gsonB);
    Dtc.registerGson(gsonB);
    StreamMessage.ParametricFilter.registerGson(gsonB);
    StreamMessage.GeometryFilter.registerGson(gsonB);
    Message.registerGson(gsonB);
    Collision.registerGson(gsonB);
    ReportCard.registerGson(gsonB);
    BatteryStatus.registerGson(gsonB);

    return gsonB;
  }

  /*package*/ Observable<? extends Page<? extends VinliItem>> pagingPageObservable(
      Class itemClz, String link) {
    // TODO - add every Retrofit interface and class to this
    if (Device.class.equals(itemClz)) {
       return mDevices.devicesForUrl(link.replaceFirst(Endpoint.PLATFORM.getUrl(), ""));
    }
    if (Dtc.Code.class.equals(itemClz)) {
       return mDiagnostics.rawCodesForUrl(link.replaceFirst(Endpoint.DIAGNOSTICS.getUrl(), ""));
    }
    if (Rule.class.equals(itemClz)) {
       return mRules.rulesForUrl(link.replaceFirst(Endpoint.RULES.getUrl(), ""));
    }
    if (Subscription.class.equals(itemClz)) {
       return mSubscriptions.subscriptionsForUrl(link.replaceFirst(Endpoint.EVENTS.getUrl(), ""));
    }
    throw new RuntimeException(String.format(
        "no paging observable for %s : %s", link, itemClz.getSimpleName()));
  }

  /*package*/ Observable<? extends TimeSeries<? extends VinliItem>> pagingTsObservable(
      Class itemClz, String link) {
    // TODO - add every Retrofit interface and class to this
    if (Message.class.equals(itemClz)) {
      return mMessages.messagesForUrl(link.replaceFirst(Endpoint.TELEMETRY.getUrl(), ""));
    }
    if (Snapshot.class.equals(itemClz)) {
      return mSnapshots.snapshotsForUrl(link.replaceFirst(Endpoint.TELEMETRY.getUrl(), ""));
    }
    if (Location.LocationTimeSeriesAdapter.class.equals(itemClz)) {
      return mLocations.locationsForUrl(link.replaceFirst(Endpoint.TELEMETRY.getUrl(), ""));
    }
    if (Collision.class.equals(itemClz)) {
      return mCollisions.collisionsForUrl(link.replaceFirst(Endpoint.SAFETY.getUrl(), ""));
    }
    if (Dtc.class.equals(itemClz)) {
      return mDiagnostics.codesForUrl(link.replaceFirst(Endpoint.DIAGNOSTICS.getUrl(), ""));
    }
    if (Odometer.class.equals(itemClz)) {
      return mDistances.odometerReportsForUrl(link.replaceFirst(Endpoint.DISTANCE.getUrl(), ""));
    }
    if (OdometerTrigger.class.equals(itemClz)) {
      return mDistances.odometerTriggersForUrl(link.replaceFirst(Endpoint.DISTANCE.getUrl(), ""));
    }
    if (Event.class.equals(itemClz)) {
      return mEvents.eventsForUrl(link.replaceFirst(Endpoint.EVENTS.getUrl(), ""));
    }
    if (Notification.class.equals(itemClz)) {
      return mNotifications.notificationsForUrl(link.replaceFirst(Endpoint.EVENTS.getUrl(), ""));
    }
    if (ReportCard.class.equals(itemClz)) {
      return mReportCards.reportCardsForUrl(link.replaceFirst(Endpoint.BEHAVIORAL.getUrl(), ""));
    }
    throw new RuntimeException(String.format(
        "no paging observable for %s : %s", link, itemClz.getSimpleName()));
  }

  /*package*/ static OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

  /*protected*/ VinliApp(@NonNull String accessToken) {
    mAccessToken = accessToken;

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
      @Override public void log(String message) {
        Log.d("VinliNet", message);
      }
    });
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

    client = clientBuilder.build().newBuilder()
        .addInterceptor(new OauthInterceptor(accessToken))
        .addInterceptor(loggingInterceptor)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build();

    mGson = gsonBuilder().create();
    final GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(mGson);
    RxJavaCallAdapterFactory rxJavaCallAdapterFactory =
        RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

    final Retrofit platformAdapter = new Retrofit.Builder().baseUrl(Endpoint.PLATFORM.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build();

    mDevices = platformAdapter.create(Devices.class);
    mVehicles = platformAdapter.create(Vehicles.class);

    mDiagnostics = new Retrofit.Builder().baseUrl(Endpoint.DIAGNOSTICS.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build()
        .create(Diagnostics.class);

    mRules = new Retrofit.Builder().baseUrl(Endpoint.RULES.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build()
        .create(Rules.class);

    final Retrofit eventsAdapter = new Retrofit.Builder().baseUrl(Endpoint.EVENTS.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build();

    mEvents = eventsAdapter.create(Events.class);
    mSubscriptions = eventsAdapter.create(Subscriptions.class);
    mNotifications = eventsAdapter.create(Notifications.class);

    final Retrofit telemAdapter = new Retrofit.Builder().baseUrl(Endpoint.TELEMETRY.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build();

    mLocations = telemAdapter.create(Locations.class);
    mSnapshots = telemAdapter.create(Snapshots.class);
    mMessages = telemAdapter.create(Messages.class);

    mUsers = new Retrofit.Builder().baseUrl(Endpoint.AUTH.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build()
        .create(Users.class);

    mTrips = new Retrofit.Builder().baseUrl(Endpoint.TRIPS.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build()
        .create(Trips.class);

    mDistances = new Retrofit.Builder().baseUrl(Endpoint.DISTANCE.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build()
        .create(Distances.class);

    mCollisions = new Retrofit.Builder()
        .baseUrl(Endpoint.SAFETY.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build()
        .create(Collisions.class);

    mReportCards = new Retrofit.Builder()
        .baseUrl(Endpoint.BEHAVIORAL.getUrl())
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build()
        .create(ReportCards.class);
  }

  public String getAccessToken() {
    return mAccessToken;
  }

  public Observable<Page<Device>> devices() {
    return mDevices.devices(null, null);
  }

  /**
   * Pass null for default
   */
  public Observable<Page<Device>> devices(@Nullable Integer limit, @Nullable Integer offset) {
    return mDevices.devices(limit, offset);
  }

  public Observable<Device> device(@NonNull String deviceId) {
    return mDevices.device(deviceId).map(Wrapped.<Device>pluckItem());
  }

  public Observable<Vehicle> vehicle(@NonNull String vehicleId) {
    return mVehicles.vehicle(vehicleId).map(Wrapped.<Vehicle>pluckItem());
  }

  public Observable<Dtc.Code> diagnoseDtcCode(String number) {
    return mDiagnostics.diagnose(number).flatMap(Page.<Dtc.Code>allItems());
  }

  public Observable<User> currentUser() {
    return mUsers.currentUser().map(Wrapped.<User>pluckItem());
  }

  public Observable<Trip> trip(@NonNull String tripId) {
    return mTrips.trip(tripId).map(Wrapped.<Trip>pluckItem());
  }

  public Observable<Rule> rule(@NonNull String ruleId) {
    return mRules.rule(ruleId).map(Wrapped.<Rule>pluckItem());
  }

  public Observable<Collision> collision(@NonNull String collisionId){
    return mCollisions.collision(collisionId).map(Wrapped.<Collision>pluckItem());
  }

  public Observable<Subscription> subscription(@NonNull String subscriptionId) {
    return mSubscriptions.subscription(subscriptionId).map(Wrapped.<Subscription>pluckItem());
  }

  public Observable<Odometer> odometerReport(@NonNull String odometerId){
    return mDistances.odometerReport(odometerId).map(Wrapped.<Odometer>pluckItem());
  }

  public Observable<OdometerTrigger> odometerTrigger(@NonNull String odometerTriggerId) {
    return mDistances.odometerTrigger(odometerTriggerId).map(Wrapped.<OdometerTrigger>pluckItem());
  }

  public Observable<ReportCard> reportCard(@NonNull String reportCardId){
    return mReportCards.reportCard(reportCardId).map(Wrapped.<ReportCard>pluckItem());
  }

  public Observable<Message> message(@NonNull String messageId){
    return mMessages.message(messageId).map(Wrapped.<Message>pluckItem());
  }

  public Observable<Notification> notification(@NonNull String notificationId){
    return mNotifications.notification(notificationId).map(Wrapped.<Notification>pluckItem());
  }

  public Observable<Event> event(@NonNull String eventId){
    return mEvents.event(eventId).map(Wrapped.<Event>pluckItem());
  }

  /*package*/ Devices devicesSvc() {
    return mDevices;
  }

  /*package*/ Vehicles vehicles() {
    return mVehicles;
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

  /*package*/ Snapshots snapshots() {
    return mSnapshots;
  }

  /*package*/ Subscriptions subscriptions() {
    return mSubscriptions;
  }

  /*package*/ Trips trips() {
    return mTrips;
  }

  /*package*/ Distances distances() {
      return mDistances;
  }

  /*package*/ Messages messages(){
    return mMessages;
  }

  /*package*/ Diagnostics diagnostics(){
    return mDiagnostics;
  }

  /*package*/ Collisions collisions(){
    return mCollisions;
  }

  /*package*/ ReportCards reportCards(){
    return mReportCards;
  }

  /*package*/ Notifications notifications(){
    return mNotifications;
  }

  /*package*/ Gson gson() {
    return mGson;
  }

  private static final class OauthInterceptor implements Interceptor {
    private static final String AUTH = "Authorization";
    private final String mBearer;

    public OauthInterceptor(String accessToken) {
      mBearer = "Bearer " + accessToken;
    }

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
      Request original = chain.request();

      Request request = original.newBuilder().header(AUTH, mBearer).build();

      return chain.proceed(request);
    }
  }
}
