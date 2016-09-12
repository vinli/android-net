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
public class TripsIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void getTripsByDeviceId(){
    vinliApp.trips().trips(TestHelper.getDeviceId(), null, null, null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Trip>>() {
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
      public void onNext(TimeSeries<Trip> tripTimeSeries) {
        assertTrue(tripTimeSeries.getItems().size() > 0);

        for(Trip trip : tripTimeSeries.getItems()){
          assertTrue(trip.id() != null && trip.id().length() > 0);
          assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
          assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
          assertTrue(trip.start() != null && trip.start().length() > 0);
          assertTrue(trip.stop() != null && trip.stop().length() > 0);
        }
      }
    });
  }

  @Test
  public void getTripsByVehicleId(){
    vinliApp.trips().vehicleTrips(TestHelper.getVehicleId(), null, null, null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Trip>>() {
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
      public void onNext(TimeSeries<Trip> tripTimeSeries) {
        assertTrue(tripTimeSeries.getItems().size() > 0);

        for(Trip trip : tripTimeSeries.getItems()){
          assertTrue(trip.id() != null && trip.id().length() > 0);
          assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
          assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
          assertTrue(trip.start() != null && trip.start().length() > 0);
          assertTrue(trip.stop() != null && trip.stop().length() > 0);
        }
      }
    });
  }

  @Test
  public void getTripById(){
    vinliApp.trips().trip(TestHelper.getTripId()).toBlocking().subscribe(new Subscriber<Wrapped<Trip>>() {
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
      public void onNext(Wrapped<Trip> tripWrapped) {
        Trip trip = tripWrapped.item();

        assertTrue(trip.id() != null && trip.id().length() > 0);
        assertTrue(trip.deviceId() != null && trip.deviceId().length() > 0);
        assertTrue(trip.vehicleId() != null && trip.vehicleId().length() > 0);
        assertTrue(trip.start() != null && trip.start().length() > 0);
        assertTrue(trip.stop() != null && trip.stop().length() > 0);
      }
    });
  }
}
