package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

/**
 * Created by JoshBeridon on 11/18/16.
 */

public class StreamingIntegrationTests {
  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }
  @Test public void getSubscriptionById() {
    assertTrue(TestHelper.getSubscriptionId() != null);
    vinliApp.dummies().dummies().toBlocking().subscribe(new Subscriber<Page<Dummy>>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
      assertTrue(false);
      }

      @Override public void onNext(Page<Dummy> dummyPage) {

      }
    });
  }

}
