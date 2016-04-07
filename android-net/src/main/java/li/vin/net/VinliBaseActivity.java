package li.vin.net;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

public abstract class VinliBaseActivity extends AppCompatActivity{


  private VinliSignInHandler signInHandler;
  public VinliApp vinliApp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    signInHandler = new VinliSignInHandler();
  }

  @Override
  protected void onResume(){
    super.onResume();
    vinliApp = signInHandler.handleOnResume(this, getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent){
    super.onNewIntent(intent);
    vinliApp = signInHandler.handleOnNewIntent(this, intent);
  }

  @Override
  protected void onDestroy(){
    super.onDestroy();
  }

  public void signIn(@NonNull String clientId, @NonNull String redirectURI, @NonNull PendingIntent pendingIntent){
    signInHandler.signIn(this, clientId, redirectURI, pendingIntent);
  }

  public boolean signedIn(){
    return signInHandler.signedIn();
  }

}
