package li.vin.net;

/*package*/ enum Endpoint implements retrofit.Endpoint {
  PLATFORM("platform"),
  EVENTS("events"),
  DIAGNOSTICS("diag"),
  TELEMETRY("telemetry");

  private static final String PROTOCOL = "https://";
  private static final String DOMAIN_END = ".vin.li";
  private static final String API_V1 = "/api/v1";

  private final String mDomain;

  private Endpoint(String domain) {
    mDomain = domain;
  }

  @Override public String getName() {
    return this.name();
  }

  @Override public String getUrl() {
    return innerGetUrl().append(API_V1).toString();
  }

  public String getUrlWithoutVersion() {
    return innerGetUrl().toString();
  }

  private StringBuilder innerGetUrl() {
    final StringBuilder sb = new StringBuilder().append(PROTOCOL).append(mDomain);

    final String env = getEnv();
    if (env != null) {
      sb.append('-').append(env);
    }

    return sb.append(DOMAIN_END);
  }

  private static final Object ENVIRONMENT_LOCK = new Object();
  private static Environment sEnvironment = Environment.TEST;

  public static void setEnvironment(Environment env) {
    synchronized (ENVIRONMENT_LOCK) {
      sEnvironment = env;
    }
  }

  private static String getEnv() {
    synchronized (ENVIRONMENT_LOCK) {
      return sEnvironment.getUrl();
    }
  }

  private static enum Environment {
    PRODUCTION(null),
    STAGING("staging"),
    TEST("test");

    private final String mUrl;

    private Environment(String url) {
      mUrl = url;
    }

    public String getUrl() {
      return mUrl;
    }
  }

}
