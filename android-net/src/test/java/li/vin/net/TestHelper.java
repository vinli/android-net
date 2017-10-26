package li.vin.net;


import org.robolectric.shadows.httpclient.FakeHttp;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;


public class TestHelper {

  private static VinliApp vinliApp;
  private static VinliApp vehicleVinliApp;

  public static VinliApp getVinliApp(){
    if(vinliApp == null){
      VinliApp.clientBuilder = generateUnsafeBuilder();

        VinliEndpoint.setDomain(VinliEndpoint.DOMAIN_DEV);
      FakeHttp.getFakeHttpLayer().interceptHttpRequests(false);

      vinliApp = new VinliApp(getAccessToken());
      Vinli.setCurrentApp(vinliApp);
    }

    return vinliApp;
  }

  public static VinliApp getVehicleVinliApp(){
    if(vehicleVinliApp == null){
      VinliApp.clientBuilder = generateUnsafeBuilder();

        VinliEndpoint.setDomain(VinliEndpoint.DOMAIN_DEV);
      FakeHttp.getFakeHttpLayer().interceptHttpRequests(false);

      vehicleVinliApp = new VinliApp(getVehicleAccessToken());
    }

    return vehicleVinliApp;
  }

  public static String getAccessToken(){
    String accessToken = BuildConfig.ACCESS_TOKEN;
    return accessToken.equals("DEFAULT_ACCESS_TOKEN") ? null : accessToken;
  }

  public static String getVehicleAccessToken(){
    String accessToken = BuildConfig.VEHICULARIZATION_ACCESS_TOKEN;
    return accessToken.equals("DEFAULT_ACCESS_TOKEN") ? null : accessToken;
  }

  public static String getDeviceId(){
    String deviceId = BuildConfig.DEVICE_ID;
    return deviceId.equals("DEFAULT_DEVICE_ID") ? null : deviceId;
  }

  public static String getVehicleId(){
    String vehicleId = BuildConfig.VEHICLE_ID;
    return vehicleId.equals("DEFAULT_VEHICLE_ID") ? null : vehicleId;
  }

  public static String getSecondVehicleId(){
    String secondVehicleId = BuildConfig.SECOND_VEHICLE_ID;
    return secondVehicleId.equals("DEFAULT_SECOND_VEHICLE_ID") ? null : secondVehicleId;
  }

  public static String getTripId(){
    String tripId = BuildConfig.TRIP_ID;
    return tripId.equals("DEFAULT_TRIP_ID") ? null : tripId;
  }

  public static String getMessageId(){
    String messageId = BuildConfig.MESSAGE_ID;
    return messageId.equals("DEFAULT_MESSAGE_ID") ? null : messageId;
  }

  public static String getRuleId(){
    String ruleId = BuildConfig.RULE_ID;
    return ruleId.equals("DEFAULT_RULE_ID") ? null : ruleId;
  }

  public static String getVehicleRuleId(){
    String vehicleRuleId = BuildConfig.VEHICLE_RULE_ID;
    return vehicleRuleId.equals("DEFAULT_VEHICLE_RULE_ID") ? null : vehicleRuleId;
  }

  public static String getSubscriptionId(){
    String subscriptionId = BuildConfig.SUBSCRIPTION_ID;
    return subscriptionId.equals("DEFAULT_SUBSCRIPTION_ID") ? null : subscriptionId;
  }

  public static String getOdometerId(){
    String odometerId = BuildConfig.ODO_ID;
    return odometerId.equals("DEFAULT_ODO_ID") ? null : odometerId;
  }

  public static String getOdometerTriggerId(){
    String odometerTriggerId = BuildConfig.ODO_TRIGGER_ID;
    return odometerTriggerId.equals("DEFAULT_ODO_TRIGGER_ID") ? null : odometerTriggerId;
  }

  public static String getEventId(){
    String eventId = BuildConfig.EVENT_ID;
    return eventId.equals("DEFAULT_EVENT_ID") ? null : eventId;
  }

  public static String getCollisionId(){
    String collisionId = BuildConfig.COLLISION_ID;
    return collisionId.equals("DEFAULT_COLLISION_ID") ? null : collisionId;
  }

  public static String getReportCardId(){
    String reportCardId = BuildConfig.REPORT_CARD_ID;
    return reportCardId.equals("DEFAULT_REPORT_CARD_ID") ? null : reportCardId;
  }

  public static String getNotificationId(){
    String notificationId = BuildConfig.NOTIFICATION_ID;
    return notificationId.equals("DEFAULT_NOTIFICATION_ID") ? null : notificationId;
  }

  public static String getDummyId(){
    String dummyId = BuildConfig.DUMMY_ID;
    return dummyId.equals("DEFAULT_DUMMY_ID") ? null : dummyId;
  }

  public static String getRouteId(){
    String routeId = BuildConfig.ROUTE_ID;
    return routeId.equals("DEFAULT_ROUTE_ID") ? null : routeId;
  }

  public static String getVIN(){
    String vin = BuildConfig.VIN;
    return vin.equals("DEFAULT_VIN") ? null : vin;
  }

  //public static OkHttpClient.Builder configureClient(OkHttpClient.Builder clientBuilder) {
  //  final TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
  //
  //    @Override
  //    public X509Certificate[] getAcceptedIssuers() {
  //      return null;
  //    }
  //
  //    @Override
  //    public void checkServerTrusted(final X509Certificate[] chain,
  //                                   final String authType) throws CertificateException {
  //    }
  //
  //    @Override
  //    public void checkClientTrusted(final X509Certificate[] chain,
  //                                   final String authType) throws CertificateException {
  //    }
  //  }};
  //
  //  SSLContext ctx = null;
  //  try {
  //    ctx = SSLContext.getInstance("TLS");
  //    ctx.init(null, certs, new SecureRandom());
  //  } catch (final java.security.GeneralSecurityException ex) {
  //  }
  //
  //  try {
  //    final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
  //      @Override
  //      public boolean verify(final String hostname,
  //                            final SSLSession session) {
  //        return true;
  //      }
  //    };
  //
  //    client.setHostnameVerifier(hostnameVerifier);
  //    client.setSslSocketFactory(ctx.getSocketFactory());
  //  } catch (final Exception e) {
  //  }
  //
  //  return client;
  //}

  public static OkHttpClient.Builder generateUnsafeBuilder(){
    try {
      // Create a trust manager that does not validate certificate chains

      final X509TrustManager x509TrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return new java.security.cert.X509Certificate[] {};
        }
      };

      final TrustManager[] trustAllCerts = new TrustManager[] {
          x509TrustManager
      };

      // Install the all-trusting trust manager
      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      // Create an ssl socket factory with our all-trusting manager
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(final String hostname,
            final SSLSession session) {
          return true;
        }
      };

      return new OkHttpClient.Builder()
          .sslSocketFactory(sslSocketFactory, x509TrustManager)
          .hostnameVerifier(hostnameVerifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
