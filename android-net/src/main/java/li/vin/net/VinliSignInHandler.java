package li.vin.net;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class VinliSignInHandler {

  private boolean signInRequested;
  private VinliApp vinliApp;

  public VinliApp handleOnResume(@NonNull Activity context, @Nullable Intent intent){
    loadApp(context, intent);
    return vinliApp;
  }

  public VinliApp handleOnNewIntent(@NonNull Activity context, @Nullable Intent intent){
    loadApp(context, intent);
    return vinliApp;
  }

  public void handleOnDestroy(Activity context){

  }

  private void loadApp(@NonNull Activity context, @Nullable Intent intent){
    if (vinliApp == null) {
      vinliApp = intent == null
          ? Vinli.loadApp(context)
          : Vinli.initApp(context, intent);
      if (vinliApp == null) {
        if (signInRequested) {
          // If a sign in was already requested, it failed or was canceled - finish.
          context.finish();
        }
      }
    }
  }

  public void signIn(@NonNull Activity context, @NonNull String clientId, @NonNull String redirectURI, @NonNull PendingIntent pendingIntent){
    signInRequested = true;
    context.setIntent(new Intent());
    Vinli.clearApp(context);
    vinliApp = null;
    Vinli.signIn(context, clientId, redirectURI, pendingIntent);
  }

  public VinliApp getVinliApp(){
    return vinliApp;
  }

  public boolean signedIn(){
    return (vinliApp != null);
  }

}
