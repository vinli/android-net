package li.vin.net;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import okhttp3.HttpUrl;
//import com.squareup.okhttp.HttpUrl;

public class SignInActivity extends Activity {
  private static final String TAG = SignInActivity.class.getSimpleName();

  private static final String CLIENT_ID = "li.vin.net.SignInActivity#CLIENT_ID";
  private static final String REDIRECT_URI = "li.vin.net.SignInActivity#REDIRECT_URI";
  private static final String PENDING_INTENT = "li.vin.net.SignInActivity#PENDING_INTENT";

  private static final String ACTION_ERROR = "li.vin.net.signIn.ERROR";
  private static final String ACTION_APPROVED = "li.vin.net.signIn.APPROVED";

  private static final HttpUrl OAUTH_ENPOINT = new HttpUrl.Builder().scheme("https")
      .host("auth" + Endpoint.domain())
      .addPathSegment("oauth")
      .addPathSegment("authorization")
      .addPathSegment("new")
      .addQueryParameter("response_type", "token")
      .build();

  /*protected*/
  static final Intent newIntent(@NonNull Context context, @NonNull String clientId,
      @NonNull String redirectUri, @NonNull PendingIntent pendingIntent) {
    final Intent signInIntent = new Intent(context, SignInActivity.class);

    signInIntent.putExtra(CLIENT_ID, clientId);
    signInIntent.putExtra(REDIRECT_URI, redirectUri);
    signInIntent.putExtra(PENDING_INTENT, pendingIntent);

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

    final PendingIntent pendingIntent = extras.getParcelable(PENDING_INTENT);
    if (pendingIntent == null) {
      throw new AssertionError("missing pending intent");
    }

    setContentView(R.layout.activity_vinli_sign_in);

    final WebView wv = (WebView) this.findViewById(li.vin.net.R.id.sign_in);

    wv.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading: " + url);

        if (url.startsWith(redirectUri)) {

          String error = null;
          String accessToken = null;

          try {
            final HttpUrl uri = HttpUrl.parse(url);
            final String[] fragmentPieces = uri.fragment().split("&");
            for (String piece : fragmentPieces) {
              if (piece.startsWith("access_token=")) {
                accessToken = piece.substring("access_token=".length());
                break;
              } else if (piece.startsWith("error=")) {
                error = piece.substring("error=".length());
                break;
              }
            }
          } catch (Exception e) {
            error = "redirect parse error: " + e;
          }

          Intent resultIntent;

          if (error == null) {
            if (accessToken == null) {
              resultIntent = new Intent(ACTION_ERROR);
              resultIntent.putExtra(Vinli.SIGN_IN_ERROR, "missing access_token");
            } else {
              Log.d(TAG, "oauth accessToken: " + accessToken);
              resultIntent = new Intent(ACTION_APPROVED);
              resultIntent.putExtra(Vinli.ACCESS_TOKEN, accessToken);
            }
          } else {
            Log.d(TAG, "oauth error: " + error);
            resultIntent = new Intent(ACTION_ERROR);
            resultIntent.putExtra(Vinli.SIGN_IN_ERROR, error);
          }

          try {
            pendingIntent.send(SignInActivity.this, 0, resultIntent);
            finish();
          } catch (Exception e) {
            Log.d(TAG, "pending intent send error: " + e);
          }
          return true;
        }

        if (url.toLowerCase().contains("sign-up")) {
          try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
          } catch (Exception e) {
            Log.d(TAG, "failed to launch sign up url", e);
          }
          return true;
        }
        return false;
      }
    });

    final String url = OAUTH_ENPOINT.newBuilder()
        .host("auth" + Endpoint.domain())
        .setQueryParameter("client_id", clientId)
        .setQueryParameter("redirect_uri", redirectUri)
        .toString();

    Log.d("SignInActivity", "loading url: " + url);
    wv.loadUrl(url);
  }
}
