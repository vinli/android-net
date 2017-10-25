package li.vin.net;

import android.support.annotation.NonNull;

import okhttp3.HttpUrl;

enum Endpoint {
    AUTH("auth"),
    DIAGNOSTICS("diagnostic"),
    EVENTS("events"),
    PLATFORM("platform"),
    RULES("rules"),
    TELEMETRY("telemetry"),
    TRIPS("trips"),
    SAFETY("safety"),
    BEHAVIORAL("behavioral"),
    DISTANCE("distance"),
    DUMMY("dummies");

    static final String DOMAIN_QA = "-qa.";
    static final String DOMAIN_DEV = "-dev.";
    static final String DOMAIN_DEMO = "-demo.";
    static final String DOMAIN_PROD = ".";

    static private String host = "vin.li";
    static private String domain = DOMAIN_PROD;

    static synchronized String host() {
        return host;
    }

    static synchronized String domain() {
        return domain;
    }

    static synchronized void setHost(@NonNull String host) {
        Endpoint.host = host;
    }

    static synchronized void setDomain(@NonNull String domain) {
        Endpoint.domain = domain;
    }

    private final HttpUrl mUrl;
    private final String subDomain;

    Endpoint(String subDomain) {
        this.subDomain = subDomain;
        mUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(subDomain + domain() + host())
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("")
                .build();
    }

    public String getName() {
        return this.name();
    }

    public String getUrl() {
        return mUrl.newBuilder().host(subDomain + domain()).toString();
    }

}
