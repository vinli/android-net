package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class NotificationsIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void getNoficationsFromEvent(){
    assertTrue(TestHelper.getEventId() != null);

    vinliApp.events().event(TestHelper.getEventId())
        .map(Wrapped.<Event>pluckItem())
        .flatMap(new Func1<Event, Observable<Page<Notification>>>() {
          @Override
          public Observable<Page<Notification>> call(Event event) {
            return event.notifications();
          }
        })
        .toBlocking()
        .subscribe(new Subscriber<Page<Notification>>() {
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
          public void onNext(Page<Notification> notificationPage) {
            assertTrue(notificationPage.getItems().size() > 0);

            for (Notification notification : notificationPage.getItems()) {
              assertTrue(notification.id() != null && notification.id().length() > 0);
              assertTrue(notification.eventTimestamp() != null && notification.eventTimestamp().length() > 0);
              assertTrue(notification.eventType() != null && notification.eventType().length() > 0);
              assertTrue(notification.eventId() != null && notification.eventId().length() > 0);
              assertTrue(notification.subscriptionId() != null && notification.subscriptionId().length() > 0);
            }
          }
        });
  }
}
