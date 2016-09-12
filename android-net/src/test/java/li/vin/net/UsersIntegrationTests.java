package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class UsersIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetUser(){
    vinliApp.currentUser().toBlocking().subscribe(new Subscriber<User>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(User user) {
        assertTrue(user.id() != null && user.id().length() > 0);
        assertTrue(user.firstName().length() > 0);
        assertTrue(user.lastName().length() > 0);
        assertTrue(user.email().length() > 0);
        assertTrue(user.phone().length() > 0);
      }
    });
  }
}
