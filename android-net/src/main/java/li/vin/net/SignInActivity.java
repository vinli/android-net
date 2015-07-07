package li.vin.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.okhttp.HttpUrl;


public class SignInActivity extends Activity {
  private static final String TAG = SignInActivity.class.getSimpleName();

  private static final String CLIENT_ID = "li.vin.net.SignInActivity#CLIENT_ID";
  private static final String REDIRECT_URI = "li.vin.net.SignInActivity#REDIRECT_URI";

  private static final String ACTION_ERROR = "li.vin.net.signIn.ERROR";
  private static final String ACTION_APPROVED = "li.vin.net.signIn.APPROVED";

  private static final HttpUrl OAUTH_ENPOINT = new HttpUrl.Builder()
      .scheme("https")
      .host("my-dev.vin.li")
      .addPathSegment("oauth")
      .addPathSegment("authorization")
      .addPathSegment("new")
      .addQueryParameter("response_type", "token")
      .build();

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
        Log.d(TAG, "shouldOverrideUrlLoading: " + url);

        if (url.startsWith(redirectUri)) {
          final HttpUrl uri = HttpUrl.parse(url);
          final String[] fragmentPieces = uri.fragment().split("&");

          String error = null;
          String accessToken = null;
          for (String piece : fragmentPieces) {
            if (piece.startsWith("access_token=")) {
              accessToken = piece.substring("access_token=".length());
              break;
            } else if (piece.startsWith("error=")) {
              error = piece.substring("error=".length());
              break;
            }
          }

          if (error == null) {
            if (accessToken == null) {
              final Intent errorIntent = new Intent(ACTION_ERROR);
              errorIntent.putExtra(Vinli.SIGN_IN_ERROR, "missing access_token");
              startActivity(errorIntent);
            } else {
              Log.d(TAG, "oauth accessToken: " + accessToken);
              final Intent approvedIntent = new Intent(ACTION_APPROVED);
              approvedIntent.putExtra(Vinli.ACCESS_TOKEN, accessToken);
              startActivity(approvedIntent);
            }
          } else {
            Log.d(TAG, "oauth error: " + error);
            final Intent errorIntent = new Intent(ACTION_ERROR);
            errorIntent.putExtra(Vinli.SIGN_IN_ERROR, error);
            startActivity(errorIntent);
          }

          return true;
        }

        return false;
      }
    });

    final String url = OAUTH_ENPOINT.newBuilder()
        .addQueryParameter("client_id", clientId)
        .addQueryParameter("redirect_uri", redirectUri)
        .toString();

    Log.d("SignInActivity", "loading url: " + url);

    wv.loadUrl(url);
  }

}
