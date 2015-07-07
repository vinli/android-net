package li.vin.net;

import com.squareup.okhttp.HttpUrl;

/*package*/ enum Endpoint implements retrofit.Endpoint {
  AUTH("auth"),
  DIAGNOSTICS("diag"),
  EVENTS("events"),
  PLATFORM("platform"),
  RULES("rules"),
  TELEMETRY("telemetry");

  private static final String DOMAIN = "-dev.vin.li";

  private final HttpUrl mUrl;

  private Endpoint(String subDomain) {
    mUrl = new HttpUrl.Builder()
        .scheme("https")
        .host(subDomain + DOMAIN)
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
