package li.vin.net;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observable;
import rx.Observer;

public final class Vinli {
  /*protected*/ static final String VINLI_PREFS = "li.vin.net.Vinli";
  /*protected*/ static final String SIGN_IN_ERROR = "li.vin.net.Vinli#SIGN_IN_ERROR";
  /*protected*/ static final String ACCESS_TOKEN = "li.vin.net.Vinli#ACCESS_TOKEN";

  public static final void signIn(@NonNull Activity context, @NonNull String clientId,
      @NonNull String redirectUri, @NonNull PendingIntent pendingIntent) {
    context.startActivity(SignInActivity.newIntent(context, clientId, redirectUri, pendingIntent));
  }

  public static final @Nullable String getSignInError(@NonNull Intent intent) {
    final Bundle extras = intent.getExtras();
    if (extras == null) {
      return null;
    }

    return extras.getString(SIGN_IN_ERROR);
  }

  private static VinliApp sApp = null;

  public static void clearApp(@NonNull Context context) {
    context.getSharedPreferences(VINLI_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(ACCESS_TOKEN, null)
        .apply();
    sApp = null;
  }

  public static final @Nullable VinliApp initApp(Context context, @NonNull Intent intent) {
    if (sApp == null) {
      final Bundle extras = intent.getExtras();
      if (extras != null) {
        String accessToken = extras.getString("li.vin.my.access_token");
        if (accessToken == null) {
          // fallback for backwards compat
          accessToken = extras.getString(ACCESS_TOKEN);
        }
        String userId = extras.getString("li.vin.my.user_id");
        if (accessToken != null || userId != null) {
          SharedPreferences pref = context.getSharedPreferences(VINLI_PREFS, Context.MODE_PRIVATE);
          SharedPreferences.Editor edit = pref.edit();
          if (accessToken != null) {
            edit.putString(ACCESS_TOKEN, accessToken);
            sApp = new VinliApp(accessToken);
          }
          if (userId != null) {
            edit.putString("li.vin.my.user_id", userId);
          }
          edit.apply();
        }
      }
    }

    return loadApp(context);
  }

  public static final @Nullable VinliApp initApp(Context context, @NonNull String accessToken) {
    if (sApp == null) {
      context.getSharedPreferences(VINLI_PREFS, Context.MODE_PRIVATE)
          .edit()
          .putString(ACCESS_TOKEN, accessToken)
          .apply();

      sApp = new VinliApp(accessToken);
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

  /*package*/ static final VinliApp curApp() {
    if (sApp == null) {
      throw new IllegalStateException("no current app exists");
    }
    return sApp;
  }

  public static <T> void registerObserver(@NonNull Observer<T> observer,
      @NonNull Observable<T> observable) {
    ObserverManager.registerObserver(observer, observable, null);
  }

  public static <T> void registerObserver(@NonNull Observer<T> observer,
      @NonNull Observable<T> observable,
      @Nullable Object binding) {
    ObserverManager.registerObserver(observer, observable, binding);
  }

  public static void unregisterObserver(@NonNull Observer<?> observer) {
    ObserverManager.unregisterObserver(observer);
  }

  private Vinli() { }
}
