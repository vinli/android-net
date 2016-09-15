package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observable;

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

  private final Gson mGson;
  private final LinkLoader mLinkLoader;

  private final String mAccessToken;

  /*package*/ static Client client = new OkClient();

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

    return gsonB;
  }

  /*protected*/ VinliApp(@NonNull String accessToken) {
    mAccessToken = accessToken;

    final RestAdapter.Log logger = new AndroidLog("VinliNet");

    mGson = gsonBuilder().create();

    final GsonConverter gson = new GsonConverter(mGson);

    mLinkLoader = new LinkLoader(client, accessToken, gson);

    final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;

    final RequestInterceptor oauthInterceptor = new OauthInterceptor(accessToken);

    final RestAdapter platformAdapter = new RestAdapter.Builder().setEndpoint(Endpoint.PLATFORM)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build();

    mDevices = platformAdapter.create(Devices.class);
    mVehicles = platformAdapter.create(Vehicles.class);

    mDiagnostics = new RestAdapter.Builder().setEndpoint(Endpoint.DIAGNOSTICS)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Diagnostics.class);

    mRules = new RestAdapter.Builder().setEndpoint(Endpoint.RULES)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Rules.class);

    final RestAdapter eventsAdapter = new RestAdapter.Builder().setEndpoint(Endpoint.EVENTS)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build();

    mEvents = eventsAdapter.create(Events.class);
    mSubscriptions = eventsAdapter.create(Subscriptions.class);

    final RestAdapter telemAdapter = new RestAdapter.Builder().setEndpoint(Endpoint.TELEMETRY)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build();

    mLocations = telemAdapter.create(Locations.class);
    mSnapshots = telemAdapter.create(Snapshots.class);
    mMessages = telemAdapter.create(Messages.class);

    mUsers = new RestAdapter.Builder().setEndpoint(Endpoint.AUTH)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Users.class);

    mTrips = new RestAdapter.Builder().setEndpoint(Endpoint.TRIPS)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Trips.class);

    mDistances = new RestAdapter.Builder()
        .setEndpoint(Endpoint.DISTANCE)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Distances.class);

    mCollisions = new RestAdapter.Builder()
        .setEndpoint(Endpoint.SAFETY)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
        .build()
        .create(Collisions.class);

    mReportCards = new RestAdapter.Builder()
        .setEndpoint(Endpoint.BEHAVIORAL)
        .setLog(logger)
        .setLogLevel(logLevel)
        .setClient(client)
        .setConverter(gson)
        .setRequestInterceptor(oauthInterceptor)
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

    @Override
    public void intercept(RequestFacade request) {
      request.addHeader(AUTH, mBearer);
    }
  }
}
