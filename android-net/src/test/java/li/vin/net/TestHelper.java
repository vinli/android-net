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

  private static String getAccessToken(){
    return "P_yGeltXI_TvY7yg3EQ7jX_ZBMB2jamKbNZ9K7Ke5EuMYwyyRRvgbQJjsoVJ7b_k";
  }

  public static String getDeviceId(){
    return "82ea8053-aff0-4f51-b075-bd90fbec9d41";
  }

  public static String getVehicleId(){
    return "78659a96-3b9f-4279-9c88-f965b8faa999";
  }

  public static String getTripId(){
    return "ab2d692a-5361-45af-b791-1071541616b5";
  }

  public static String getMessageId(){
    return "6b2739a7-5fce-4448-9d69-8faa2f8d3ae0";
  }

  public static String getRuleId(){
    return "02d4e5c7-befe-4293-bcfb-ca4376ab8237";
  }

  public static String getSubscriptionId(){
    return "25903ca5-d4dc-40f9-b506-d4870c51107a";
  }

  public static String getOdometerId(){
    return "08d9e181-6928-453c-a9a6-295fbc69d7aa";
  }

  public static String getOdometerTriggerId(){
    return "9da5f762-f715-4a49-9269-0a3935c96186";
  }

  public static String getEventId(){
    return "b91fe5f7-d2ab-483d-a38c-d20b859c4be7";
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
