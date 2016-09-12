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
public class EventsIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetEventsByDeviceId(){
    vinliApp.events().events(TestHelper.getDeviceId(), null, null, null, null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Event>>() {

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
      public void onNext(TimeSeries<Event> eventTimeSeries) {
        assertTrue(eventTimeSeries.getItems().size() > 0);

        for(Event event : eventTimeSeries.getItems()){
          assertTrue(event.id() != null && event.id().length() > 0);
          assertTrue(event.timestamp() != null && event.timestamp().length() > 0);
          assertTrue(event.deviceId() != null && event.deviceId().length() > 0);
          assertTrue(event.eventType() != null && event.eventType().length() > 0);
        }
      }
    });
  }

  @Test
  public void testGetEventById(){
    vinliApp.events().event(TestHelper.getEventId())
        .map(Wrapped.<Event>pluckItem())
        .toBlocking()
        .subscribe(new Subscriber<Event>() {
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
          public void onNext(Event event) {
            assertTrue(event.id() != null && event.id().length() > 0);
            assertTrue(event.timestamp() != null && event.timestamp().length() > 0);
            assertTrue(event.deviceId() != null && event.deviceId().length() > 0);
            assertTrue(event.eventType() != null && event.eventType().length() > 0);
          }
        });
  }
}
