package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static java.lang.System.currentTimeMillis;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class EventsIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void testGetEventsByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    Event.eventsWithDeviceId(TestHelper.getDeviceId(), null, null, (Long) null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Event>>() {

          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Event> eventTimeSeries) {
            assertTrue(eventTimeSeries.getItems().size() > 0);

            for (Event event : eventTimeSeries.getItems()) {
              assertTrue(event.id() != null && event.id().length() > 0);
              assertTrue(event.timestamp() != null && event.timestamp().length() > 0);
              assertTrue(event.deviceId() != null && event.deviceId().length() > 0);
              assertTrue(event.eventType() != null && event.eventType().length() > 0);
            }
          }
        });
  }

  @Test public void testGetEventsWithSinceUntilLimitByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.events()
        .events(TestHelper.getDeviceId(), null, null, 0L, currentTimeMillis(), 5, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Event>>() {

          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Event> eventTimeSeries) {
            assertTrue(eventTimeSeries.getItems().size() > 0);
            assertTrue(eventTimeSeries.getItems().size() <= 5);

            for (Event event : eventTimeSeries.getItems()) {
              assertTrue(event.id() != null && event.id().length() > 0);
              assertTrue(event.timestamp() != null && event.timestamp().length() > 0);
              assertTrue(event.deviceId() != null && event.deviceId().length() > 0);
              assertTrue(event.eventType() != null && event.eventType().length() > 0);
            }
          }
        });
  }

  @Test public void testGetEventById() {
    assertTrue(TestHelper.getEventId() != null);

    Event.eventWithId(TestHelper.getEventId())
        .toBlocking()
        .subscribe(new Subscriber<Event>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Event event) {
            assertTrue(event.id() != null && event.id().length() > 0);
            assertTrue(event.timestamp() != null && event.timestamp().length() > 0);
            assertTrue(event.deviceId() != null && event.deviceId().length() > 0);
            assertTrue(event.eventType() != null && event.eventType().length() > 0);
          }
        });
  }

  @Test public void getEventsByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.events()
        .eventsForUrl(String.format("%sdevices/%s/events", Endpoint.EVENTS.getUrl(),
            TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Event>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<Event> eventTimeSeries) {
                assertTrue(eventTimeSeries.getItems().size() > 0);

                for (Event event : eventTimeSeries.getItems()) {
                  assertTrue(event.id() != null && event.id().length() > 0);
                  assertTrue(event.timestamp() != null && event.timestamp().length() > 0);
                  assertTrue(event.deviceId() != null && event.deviceId().length() > 0);
                  assertTrue(event.eventType() != null && event.eventType().length() > 0);
                }
              }
            });
  }
}
