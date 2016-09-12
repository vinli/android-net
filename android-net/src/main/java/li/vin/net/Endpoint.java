package li.vin.net;

import android.support.annotation.NonNull;
import com.squareup.okhttp.HttpUrl;

/*package*/ enum Endpoint implements retrofit.Endpoint {
  AUTH("auth"),
  DIAGNOSTICS("diagnostics"),
  EVENTS("events"),
  PLATFORM("platform"),
  RULES("rules"),
  TELEMETRY("telemetry"),
  TRIPS("trips"),
  DISTANCE("distance");

  /*package*/ static final String DOMAIN_DEMO = "-demo.vin.li";
  /*package*/ static final String DOMAIN_DEV = "-dev.vin.li";
  /*package*/ static final String DOMAIN_PROD = ".vin.li";
  /*package*/ static final String DOMAIN_QA = "-qa.vin.li";

  private static String domain = DOMAIN_PROD;

  /*package*/ static synchronized String domain() {
    return domain;
  }

  /*package*/ static synchronized void setDomain(@NonNull String domain) {
    Endpoint.domain = domain;
  }

  private final HttpUrl mUrl;
  private final String subDomain;

  private Endpoint(String subDomain) {
    this.subDomain = subDomain;
    mUrl = new HttpUrl.Builder()
        .scheme("https")
        .host(subDomain + domain())
        .addPathSegment("api")
        .addPathSegment("v1")
        .build();
  }

  @Override public String getName() {
    return this.name();
  }

  @Override public String getUrl() {
    return mUrl.newBuilder().host(subDomain + domain()).toString();
  }

}
