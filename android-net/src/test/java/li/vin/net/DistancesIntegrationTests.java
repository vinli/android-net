package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import li.vin.net.DistanceList.Distance;
import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class DistancesIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetDistancesByVehicleId(){
    vinliApp.distances().distances(TestHelper.getVehicleId(), null, null, null).toBlocking().subscribe(new Subscriber<DistanceList>() {
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
      public void onNext(DistanceList distanceList) {
        assertTrue(distanceList.distances().size() > 0);

        for(Distance distance : distanceList.distances()){
          assertTrue(distance.confidenceMin() != null);
          assertTrue(distance.confidenceMax() != null);
          assertTrue(distance.value() != null);
        }
      }
    });
  }

  @Test
  public void getBestDistanceByVehicleId(){
    vinliApp.distances().bestDistance(TestHelper.getVehicleId(), null).toBlocking().subscribe(new Subscriber<Wrapped<Distance>>() {
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
      public void onNext(Wrapped<Distance> distanceWrapped) {
        Distance distance = distanceWrapped.item();

        assertTrue(distance.confidenceMin() != null);
        assertTrue(distance.confidenceMax() != null);
        assertTrue(distance.value() != null);
      }
    });
  }

  @Test
  public void getOdometerReportsByVehicleId(){
    vinliApp.distances().odometerReports(TestHelper.getVehicleId(), null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Odometer>>() {
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
      public void onNext(TimeSeries<Odometer> odometerTimeSeries) {
        assertTrue(odometerTimeSeries.getItems().size() > 0);

        for(Odometer odometer : odometerTimeSeries.getItems()){
          assertTrue(odometer.id() != null && odometer.id().length() > 0);
          assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
          assertTrue(odometer.date() != null && odometer.date().length() > 0);
          assertTrue(odometer.reading() != null);
        }
      }
    });
  }

  @Test
  public void getOdometerById(){
    vinliApp.distances().odometerReport(TestHelper.getOdometerId()).toBlocking().subscribe(new Subscriber<Wrapped<Odometer>>() {
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
      public void onNext(Wrapped<Odometer> odometerWrapped) {
        Odometer odometer = odometerWrapped.item();

        assertTrue(odometer.id() != null && odometer.id().length() > 0);
        assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
        assertTrue(odometer.date() != null && odometer.date().length() > 0);
        assertTrue(odometer.reading() != null);

      }
    });
  }

  @Test
  public void getOdometerTriggersByVehicleId(){
    vinliApp.distances().odometerTriggers(TestHelper.getVehicleId(), null, null).toBlocking().subscribe(new Subscriber<TimeSeries<OdometerTrigger>>() {
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
      public void onNext(TimeSeries<OdometerTrigger> odometerTriggerTimeSeries) {
        assertTrue(odometerTriggerTimeSeries.getItems().size() > 0);

        for(OdometerTrigger odometerTrigger : odometerTriggerTimeSeries.getItems()){
          assertTrue(odometerTrigger.id() != null && odometerTrigger.id().length() > 0);
          assertTrue(odometerTrigger.vehicleId() != null && odometerTrigger.vehicleId().length() > 0);
          assertTrue(odometerTrigger.type() != null);
          assertTrue(odometerTrigger.threshold() != null);
        }
      }
    });
  }

  @Test
  public void getOdometerTriggerById(){
    vinliApp.distances().odometerTrigger(TestHelper.getOdometerTriggerId()).toBlocking().subscribe(new Subscriber<Wrapped<OdometerTrigger>>() {
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
      public void onNext(Wrapped<OdometerTrigger> odometerTriggerWrapped) {
        OdometerTrigger odometerTrigger = odometerTriggerWrapped.item();

        assertTrue(odometerTrigger.id() != null && odometerTrigger.id().length() > 0);
        assertTrue(odometerTrigger.vehicleId() != null && odometerTrigger.vehicleId().length() > 0);
        assertTrue(odometerTrigger.type() != null);
        assertTrue(odometerTrigger.threshold() != null);
      }
    });
  }
}