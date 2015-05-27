package li.vin.net;

import com.squareup.okhttp.HttpUrl;

/*package*/ enum Endpoint implements retrofit.Endpoint {
  DIAGNOSTICS("diag"),
  EVENTS("events"),
  PLATFORM("platform"),
  RULES("rules"),
  TELEMETRY("telemetry");

  private static final String DOMAIN = "-dev.vin.li";

  private final HttpUrl mBaseUrl;
  private final HttpUrl mUrl;

  private Endpoint(String subDomain) {
    mBaseUrl = new HttpUrl.Builder()
        .scheme("https")
        .host(subDomain + DOMAIN)
        .build();

    mUrl = mBaseUrl.newBuilder()
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

  public String getUrlWithoutVersion() {
    return mBaseUrl.toString();
  }

}
