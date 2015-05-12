package li.vin.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class SignInActivity extends Activity {
  private static final String CLIENT_ID = "li.vin.net.SignInActivity#CLIENT_ID";
  private static final String REDIRECT_URI = "li.vin.net.SignInActivity#REDIRECT_URI";

  private static final String ACTION_ERROR = "li.vin.net.signIn.ERROR";
  private static final String ACTION_APPROVED = "li.vin.net.signIn.APPROVED";

  private static final Uri OAUTH_ENPOINT =
      Uri.parse("https://auth-dev.vin.li/oauth/authorization/new?response_type=token");

  /*protected*/ static final Intent newIntent(@NonNull Context context, @NonNull String clientId,
      @NonNull String redirectUri) {
    final Intent signInIntent = new Intent(context, SignInActivity.class);

    signInIntent.putExtra(CLIENT_ID, clientId);
    signInIntent.putExtra(REDIRECT_URI, redirectUri);

    return signInIntent;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Bundle extras = getIntent().getExtras();
    if (extras == null) {
      throw new AssertionError("missing app info extras");
    }

    final String clientId = extras.getString(CLIENT_ID);
    if (clientId == null) {
      throw new AssertionError("missing client ID");
    }

    final String redirectUri = extras.getString(REDIRECT_URI);
    if (redirectUri == null) {
      throw new AssertionError("missing redirect URI");
    }

    setContentView(R.layout.activity_vinli_sign_in);

    final WebView wv = (WebView) this.findViewById(li.vin.net.R.id.sign_in);

    wv.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(redirectUri)) {
          final Uri uri = Uri.parse(url);

          final String error = uri.getQueryParameter("error");
          if (error == null) {
            final String accessToken = uri.getQueryParameter("access_token");
            if (accessToken == null) {
              final Intent errorIntent = new Intent(ACTION_ERROR);
              errorIntent.putExtra(Vinli.SIGN_IN_ERROR, "missing access_token");
              startActivity(errorIntent);
            } else {
              final Intent approvedIntent = new Intent(ACTION_APPROVED);
              approvedIntent.putExtra(Vinli.ACCESS_TOKEN, accessToken);
              startActivity(approvedIntent);
            }
          } else {
            final Intent errorIntent = new Intent(ACTION_ERROR);
            errorIntent.putExtra(Vinli.SIGN_IN_ERROR, error);
            startActivity(errorIntent);
          }

          return true;
        }

        return false;
      }
    });

    wv.loadUrl(OAUTH_ENPOINT.buildUpon()
        .appendQueryParameter("client_id", clientId)
        .appendQueryParameter("redirect_uri", redirectUri)
        .toString());
  }

}
