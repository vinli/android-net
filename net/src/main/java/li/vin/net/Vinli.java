package li.vin.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Vinli {
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

  public static final @Nullable VinliApp getApp(@NonNull Intent intent) {
    final Bundle extras = intent.getExtras();
    if (extras == null) {
      return null;
    }

    final String accessToken = extras.getString(ACCESS_TOKEN);
    if (accessToken == null) {
      return null;
    }

    return new VinliApp(accessToken);
  }

  private Vinli() { }
}
