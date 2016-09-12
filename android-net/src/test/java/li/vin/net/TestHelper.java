package li.vin.net;


import com.squareup.okhttp.OkHttpClient;

import org.robolectric.shadows.httpclient.FakeHttp;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.client.OkClient;

public class TestHelper {

  private static VinliApp vinliApp;

  public static VinliApp getVinliApp(){
    if(vinliApp == null){
      VinliApp.client = new OkClient(configureClient(new OkHttpClient()));

      Endpoint.setDomain(Endpoint.DOMAIN_DEV);
      FakeHttp.getFakeHttpLayer().interceptHttpRequests(false);

      vinliApp = new VinliApp(getAccessToken());
      Vinli.setCurrentApp(vinliApp);
    }

    return vinliApp;
  }

  public static String getAccessToken(){
    String accessToken = BuildConfig.ACCESS_TOKEN;
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

  public static OkHttpClient configureClient(final OkHttpClient client) {
    final TrustManager[] certs = new TrustManager[]{new X509TrustManager() {

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkServerTrusted(final X509Certificate[] chain,
                                     final String authType) throws CertificateException {
      }

      @Override
      public void checkClientTrusted(final X509Certificate[] chain,
                                     final String authType) throws CertificateException {
      }
    }};

    SSLContext ctx = null;
    try {
      ctx = SSLContext.getInstance("TLS");
      ctx.init(null, certs, new SecureRandom());
    } catch (final java.security.GeneralSecurityException ex) {
    }

    try {
      final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(final String hostname,
                              final SSLSession session) {
          return true;
        }
      };
      client.setHostnameVerifier(hostnameVerifier);
      client.setSslSocketFactory(ctx.getSocketFactory());
    } catch (final Exception e) {
    }

    return client;
  }
}
