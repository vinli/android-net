package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class NotificationsIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void testGetNotificationsFromEvent() {
    assertTrue(TestHelper.getEventId() != null);

    Notification.notificationsWithEventId(TestHelper.getEventId(), (Long) null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Notification>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Notification> notificationTimeSeries) {
            assertTrue(notificationTimeSeries.getItems().size() > 0);

            for (Notification notification : notificationTimeSeries.getItems()) {
              assertTrue(notification.id() != null && notification.id().length() > 0);
              assertTrue(notification.eventTimestamp() != null
                  && notification.eventTimestamp().length() > 0);
              assertTrue(notification.eventType() != null && notification.eventType().length() > 0);
              assertTrue(notification.eventId() != null && notification.eventId().length() > 0);
              assertTrue(notification.subscriptionId() != null
                  && notification.subscriptionId().length() > 0);
            }
          }
        });
  }

  @Test public void testGetNotificationsForSubscription() {
    assertTrue(TestHelper.getSubscriptionId() != null);

    Notification
        .notificationsWithSubscriptionId(TestHelper.getSubscriptionId(), (Long) null, null, 1, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Notification>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Notification> notificationTimeSeries) {
            assertTrue(notificationTimeSeries.getItems().size() > 0);

            for (Notification notification : notificationTimeSeries.getItems()) {
              assertTrue(notification.id() != null && notification.id().length() > 0);
              assertTrue(notification.eventTimestamp() != null
                  && notification.eventTimestamp().length() > 0);
              assertTrue(notification.eventType() != null && notification.eventType().length() > 0);
              assertTrue(notification.eventId() != null && notification.eventId().length() > 0);
              assertTrue(notification.subscriptionId() != null
                  && notification.subscriptionId().length() > 0);
            }

            if (notificationTimeSeries.hasPrior()) {
              notificationTimeSeries.loadPrior()
                  .toBlocking().subscribe(new Subscriber<TimeSeries<Notification>>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {
                  e.printStackTrace();
                  assertTrue(false);
                }

                @Override public void onNext(TimeSeries<Notification> notificationTimeSeries) {
                  assertTrue(notificationTimeSeries.getItems().size() > 0);

                  for (Notification notification : notificationTimeSeries.getItems()) {
                    assertTrue(notification.id() != null && notification.id().length() > 0);
                    assertTrue(notification.eventTimestamp() != null
                        && notification.eventTimestamp().length() > 0);
                    assertTrue(notification.eventType() != null && notification.eventType().length() > 0);
                    assertTrue(notification.eventId() != null && notification.eventId().length() > 0);
                    assertTrue(notification.subscriptionId() != null
                        && notification.subscriptionId().length() > 0);
                  }
                }
              });
            }
          }
        });
  }

  @Test public void testGetNotification() {
    assertTrue(TestHelper.getNotificationId() != null);

    Notification.notificationWithId(TestHelper.getNotificationId())
        .toBlocking()
        .subscribe(new Subscriber<Notification>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Notification notification) {
            assertTrue(notification.id() != null && notification.id().length() > 0);
            assertTrue(notification.eventTimestamp() != null
                && notification.eventTimestamp().length() > 0);
            assertTrue(notification.eventType() != null && notification.eventType().length() > 0);
            assertTrue(notification.eventId() != null && notification.eventId().length() > 0);
            assertTrue(notification.subscriptionId() != null
                && notification.subscriptionId().length() > 0);
          }
        });
  }

  @Test public void getNotificationsByUrl() {
    assertTrue(TestHelper.getEventId() != null);

    vinliApp.notifications()
            .notificationsForUrl(String.format("%sevents/%s/notifications", VinliEndpoint.EVENTS.getUrl(),
            TestHelper.getEventId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Notification>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<Notification> notificationTimeSeries) {
                assertTrue(notificationTimeSeries.getItems().size() > 0);

                for (Notification notification : notificationTimeSeries.getItems()) {
                  assertTrue(notification.id() != null && notification.id().length() > 0);
                  assertTrue(notification.eventTimestamp() != null
                      && notification.eventTimestamp().length() > 0);
                  assertTrue(notification.eventType() != null && notification.eventType().length() > 0);
                  assertTrue(notification.eventId() != null && notification.eventId().length() > 0);
                  assertTrue(notification.subscriptionId() != null
                      && notification.subscriptionId().length() > 0);
                }
              }
            });
  }
}
