package li.vin.net;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

public abstract class VinliBaseActivity extends AppCompatActivity{

  private boolean signInRequested;
  public VinliApp vinliApp;

  @Override
  protected void onResume(){
    super.onResume();

    loadApp(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent){
    super.onNewIntent(intent);

    loadApp(intent);
  }

  @Override
  protected void onDestroy(){
    super.onDestroy();
  }

  private void loadApp(Intent intent){
    if (vinliApp == null) {
      vinliApp = intent == null
          ? Vinli.loadApp(this)
          : Vinli.initApp(this, intent);
      if (vinliApp == null) {
        if (signInRequested) {
          // If a sign in was already requested, it failed or was canceled - finish.
          finish();
        }
      }
    }
  }

  public void signIn(@NonNull String clientId, @NonNull String redirectURI, @NonNull PendingIntent pendingIntent){
    signInRequested = true;
    setIntent(new Intent());
    Vinli.clearApp(this);
    vinliApp = null;
    Vinli.signIn(this, clientId, redirectURI, pendingIntent);
  }

  public boolean signedIn(){
    return (vinliApp != null);
  }

}
