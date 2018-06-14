package li.vin.net;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by JoshBeridon on 12/2/16.
 */

public class InstTestHelper {
  private static VinliApp vinliApp;

  public static VinliApp getVinliApp() {

    if (vinliApp == null) {
      VinliApp.clientBuilder = generateUnsafeBuilder();

        VinliEndpoint.setDomain(VinliEndpoint.DOMAIN_DEV);

      vinliApp = new VinliApp(getAccessToken());
      Vinli.setCurrentApp(vinliApp);
    }

    return vinliApp;
  }

  public static String getAccessToken() {
    String accessToken = BuildConfig.ACCESS_TOKEN;
    return accessToken.equals("DEFAULT_ACCESS_TOKEN") ? null : accessToken;
  }

  public static String getDummyId() {
    String dummyId = BuildConfig.DUMMY_ID;
    return dummyId.equals("DEFAULT_DUMMY_ID") ? null : dummyId;
  }

  public static String getRouteId() {
    String routeId = BuildConfig.ROUTE_ID;
    return routeId.equals("DEFAULT_ROUTE_ID") ? null : routeId;
  }

  public static String getVIN() {
    String vin = BuildConfig.VIN;
    return vin.equals("DEFAULT_VIN") ? null : vin;
  }

  public static OkHttpClient.Builder generateUnsafeBuilder() {
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
        @Override public boolean verify(final String hostname, final SSLSession session) {
          return true;
        }
      };

      return new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, x509TrustManager)
          .hostnameVerifier(hostnameVerifier);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
