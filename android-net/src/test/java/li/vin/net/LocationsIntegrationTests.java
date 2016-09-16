package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static java.lang.System.currentTimeMillis;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class LocationsIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void testGetLocationsByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.locations()
        .locations(TestHelper.getDeviceId(), null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Location>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Location> locationTimeSeries) {
            assertTrue(locationTimeSeries.getItems().size() > 0);

            for (Location location : locationTimeSeries.getItems()) {
              assertTrue(location.coordinate() != null);
              assertTrue(Math.abs(location.coordinate().lat()) <= 180.0);
              assertTrue(Math.abs(location.coordinate().lon()) <= 180.0);
              assertTrue(location.timestamp() != null && location.timestamp().length() > 0);
            }
          }
        });
  }

  @Test public void testGetLatestLocation(){
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.device(TestHelper.getDeviceId()).flatMap(new Func1<Device, Observable<Location>>() {
      @Override public Observable<Location> call(Device device) {
        return device.latestlocation();
      }
    }).toBlocking().subscribe(new Subscriber<Location>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(Location location) {
        assertTrue(location.coordinate() != null);
        assertTrue(Math.abs(location.coordinate().lat()) <= 180.0);
        assertTrue(Math.abs(location.coordinate().lon()) <= 180.0);
        assertTrue(location.timestamp() != null && location.timestamp().length() > 0);
      }
    });
  }

  @Test public void testGetLocationsWithSinceUntilLimitByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.locations()
        .locations(TestHelper.getDeviceId(), 0L, currentTimeMillis(), 5, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Location>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Location> locationTimeSeries) {
            assertTrue(locationTimeSeries.getItems().size() > 0);
            assertTrue(locationTimeSeries.getItems().size() <= 5);

            for (Location location : locationTimeSeries.getItems()) {
              assertTrue(location.coordinate() != null);
              assertTrue(Math.abs(location.coordinate().lat()) <= 180.0);
              assertTrue(Math.abs(location.coordinate().lon()) <= 180.0);
              assertTrue(location.timestamp() != null && location.timestamp().length() > 0);
            }
          }
        });
  }
}
