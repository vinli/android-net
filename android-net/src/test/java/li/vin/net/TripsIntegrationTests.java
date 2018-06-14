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
public class TripsIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void getTripsByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);
    Trip.tripsWithDeviceId(TestHelper.getDeviceId(), (Long) null, null, 1, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Trip>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Trip> tripTimeSeries) {
            assertTrue(tripTimeSeries.getItems().size() > 0);

            for (Trip trip : tripTimeSeries.getItems()) {
              assertTrue(trip.id() != null && trip.id().length() > 0);
              assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
              assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
              assertTrue(trip.start() != null && trip.start().length() > 0);
              assertTrue(trip.stop() != null && trip.stop().length() > 0);
            }

            if (tripTimeSeries.hasPrior()) {
              tripTimeSeries.loadPrior()
                  .toBlocking().subscribe(new Subscriber<TimeSeries<Trip>>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {
                  System.out.println("Error: " + e.getMessage());
                  e.printStackTrace();
                  assertTrue(false);
                }

                @Override public void onNext(TimeSeries<Trip> tripTimeSeries) {
                  assertTrue(tripTimeSeries.getItems().size() > 0);

                  for (Trip trip : tripTimeSeries.getItems()) {
                    assertTrue(trip.id() != null && trip.id().length() > 0);
                    assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
                    assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
                    assertTrue(trip.start() != null && trip.start().length() > 0);
                    assertTrue(trip.stop() != null && trip.stop().length() > 0);
                  }
                }
              });
            }
          }
        });
  }

  @Test public void getTripsWithSinceUntilLimitByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);
    vinliApp.trips()
        .trips(TestHelper.getDeviceId(), 0L, currentTimeMillis(), 5, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Trip>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Trip> tripTimeSeries) {
            assertTrue(tripTimeSeries.getItems().size() > 0);
            assertTrue(tripTimeSeries.getItems().size() <= 5);

            for (Trip trip : tripTimeSeries.getItems()) {
              assertTrue(trip.id() != null && trip.id().length() > 0);
              assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
              assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
              assertTrue(trip.start() != null && trip.start().length() > 0);
              assertTrue(trip.stop() != null && trip.stop().length() > 0);
            }
          }
        });
  }

  @Test public void getTripsByVehicleId() {
    assertTrue(TestHelper.getVehicleId() != null);

    Trip.tripsWithVehicleId(TestHelper.getVehicleId(), (Long) null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Trip>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Trip> tripTimeSeries) {
            assertTrue(tripTimeSeries.getItems().size() > 0);

            for (Trip trip : tripTimeSeries.getItems()) {
              assertTrue(trip.id() != null && trip.id().length() > 0);
              assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
              assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
              assertTrue(trip.start() != null && trip.start().length() > 0);
              assertTrue(trip.stop() != null && trip.stop().length() > 0);
            }
          }
        });
  }

  @Test public void getTripsWithSinceUntilLimitByVehicleId() {
    assertTrue(TestHelper.getVehicleId() != null);
    vinliApp.trips()
        .vehicleTrips(TestHelper.getVehicleId(), 0L, currentTimeMillis(), 5, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Trip>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Trip> tripTimeSeries) {
            assertTrue(tripTimeSeries.getItems().size() > 0);
            assertTrue(tripTimeSeries.getItems().size() <= 5);

            for (Trip trip : tripTimeSeries.getItems()) {
              assertTrue(trip.id() != null && trip.id().length() > 0);
              assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
              assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
              assertTrue(trip.start() != null && trip.start().length() > 0);
              assertTrue(trip.stop() != null && trip.stop().length() > 0);
            }
          }
        });
  }

  @Test public void getTripById() {
    assertTrue(TestHelper.getTripId() != null);

    Trip.tripWithId(TestHelper.getTripId())
        .toBlocking()
        .subscribe(new Subscriber<Trip>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Trip trip) {
            assertTrue(trip.id() != null && trip.id().length() > 0);
            assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
            assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
            assertTrue(trip.start() != null && trip.start().length() > 0);
            assertTrue(trip.stop() != null && trip.stop().length() > 0);
          }
        });
  }

  @Test public void getTripsByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.trips()
        .tripsForUrl(
                String.format("%sdevices/%s/trips", VinliEndpoint.TRIPS.getUrl(), TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Trip>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<Trip> tripTimeSeries) {
                assertTrue(tripTimeSeries.getItems().size() > 0);

                for (Trip trip : tripTimeSeries.getItems()) {
                  assertTrue(trip.id() != null && trip.id().length() > 0);
                  assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
                  assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
                  assertTrue(trip.start() != null && trip.start().length() > 0);
                  assertTrue(trip.stop() != null && trip.stop().length() > 0);
                }
              }
            });
  }
}
