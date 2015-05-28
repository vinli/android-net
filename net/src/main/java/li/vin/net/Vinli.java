package li.vin.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Vinli {
  /*protected*/ static final String VINLI_PREFS = "li.vin.net.Vinli";
  /*protected*/ static final String SIGN_IN_ERROR = "li.vin.net.Vinli#SIGN_IN_ERROR";
  /*protected*/ static final String ACCESS_TOKEN = "li.vin.net.Vinli#ACCESS_TOKEN";

  public static final void signIn(@NonNull Activity context, @NonNull String clientId,
      @NonNull String redirectUri) {
    context.startActivity(SignInActivity.newIntent(context, clientId, redirectUri));
  }

  public static final @Nullable String getSignInError(@NonNull Intent intent) {
    final Bundle extras = intent.getExtras();
    if (extras == null) {
      return null;
    }

    return extras.getString(SIGN_IN_ERROR);
  }

  private static VinliApp sApp = null;

  public static void clearApp() {
    sApp = null;
  }

  public static final @Nullable VinliApp initApp(Context context, @NonNull Intent intent) {
    if (sApp == null) {
      final Bundle extras = intent.getExtras();
      if (extras != null) {
        final String accessToken = extras.getString(ACCESS_TOKEN);
        if (accessToken != null) {
          context
              .getSharedPreferences(VINLI_PREFS, Context.MODE_PRIVATE)
              .edit()
              .putString(ACCESS_TOKEN, accessToken)
              .apply();

          sApp = new VinliApp(accessToken);
        }
      }
    }

    return loadApp(context);
  }

  public static final @Nullable VinliApp loadApp(@NonNull Context context) {
    if (sApp == null) {
      final String accessToken = context
          .getSharedPreferences(VINLI_PREFS, Context.MODE_PRIVATE)
          .getString(ACCESS_TOKEN, null);

      if (accessToken != null) {
        sApp = new VinliApp(accessToken);
      }
    }

    return sApp;
  }

  private Vinli() { }
}
