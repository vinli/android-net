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
  TRIPS("trips");

  /*package*/ static final String DOMAIN_DEMO = "-demo.vin.li";
  /*package*/ static final String DOMAIN_DEV = "-dev.vin.li";
  /*package*/ static final String DOMAIN_PROD = ".vin.li";

  private static String domain = DOMAIN_PROD;

  /*package*/ static synchronized String domain() {
    return domain;
  }

  /*package*/ static synchronized void setDomain(@NonNull String domain) {
    Endpoint.domain = domain;
  }

  private final HttpUrl mUrl;

  private Endpoint(String subDomain) {
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
    return mUrl.toString();
  }

}
