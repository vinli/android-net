package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class SubscriptionsIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void getSubscriptionByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    Subscription.subscriptionsWithDeviceId(TestHelper.getDeviceId(), null, null, null, null)
        .subscribe(new Subscriber<Page<Subscription>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Page<Subscription> subscriptionPage) {
            assertTrue(subscriptionPage.getItems().size() > 0);

            for (Subscription subscription : subscriptionPage.getItems()) {
              assertTrue(subscription.id() != null && subscription.id().length() > 0);
              assertTrue(subscription.deviceId() != null && subscription.deviceId().length() > 0);
              assertTrue(subscription.url() != null && subscription.url().length() > 0);
            }
          }
        });
  }

  @Test public void getSubscriptionWithLimitOffsetByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.subscriptions()
        .subscriptions(TestHelper.getDeviceId(), 5, 1, null, null)
        .subscribe(new Subscriber<Page<Subscription>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Page<Subscription> subscriptionPage) {
            assertTrue(subscriptionPage.getItems().size() <= 5);

            for (Subscription subscription : subscriptionPage.getItems()) {
              assertTrue(subscription.id() != null && subscription.id().length() > 0);
              assertTrue(subscription.deviceId() != null && subscription.deviceId().length() > 0);
              assertTrue(subscription.url() != null && subscription.url().length() > 0);
            }
          }
        });
  }

  @Test public void getSubscriptionById() {
    assertTrue(TestHelper.getSubscriptionId() != null);

    Subscription.subscriptionWithId(TestHelper.getSubscriptionId())
        .toBlocking()
        .subscribe(new Subscriber<Subscription>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            assertTrue(false);
          }

          @Override public void onNext(Subscription subscription) {
            assertTrue(subscription.id() != null && subscription.id().length() > 0);
            assertTrue(subscription.deviceId() != null && subscription.deviceId().length() > 0);
            assertTrue(subscription.url() != null && subscription.url().length() > 0);
          }
        });
  }

  @Test public void getSubscriptionsByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.subscriptions()
        .subscriptionsForUrl(String.format("%sdevices/%s/subscriptions", Endpoint.EVENTS.getUrl(),
            TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<Page<Subscription>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(Page<Subscription> subscriptionPage) {
                assertTrue(subscriptionPage.getItems().size() > 0);

                for (Subscription subscription : subscriptionPage.getItems()) {
                  assertTrue(subscription.id() != null && subscription.id().length() > 0);
                  assertTrue(subscription.deviceId() != null && subscription.deviceId().length() > 0);
                  assertTrue(subscription.url() != null && subscription.url().length() > 0);
                }
              }
            });
  }
}
