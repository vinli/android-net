package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import li.vin.net.DistanceList.Distance;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class DistancesIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetDistancesByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    DistanceList.distancesWithVehicleId(TestHelper.getVehicleId(), null, null, null).toBlocking().subscribe(new Subscriber<DistanceList>() {
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
    assertTrue(TestHelper.getVehicleId() != null);

    Distance.bestDistanceWithVehicleId(TestHelper.getVehicleId(), null).toBlocking().subscribe(new Subscriber<Distance>() {
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
      public void onNext(Distance distance) {
        assertTrue(distance.confidenceMin() != null);
        assertTrue(distance.confidenceMax() != null);
        assertTrue(distance.value() != null);
      }
    });
  }

  @Test
  public void getOdometerReportsByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    Odometer.odometersWithVehicleId(TestHelper.getVehicleId(), null, null, null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Odometer>>() {
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
    assertTrue(TestHelper.getOdometerId() != null);

    Odometer.odometerWithId(TestHelper.getOdometerId()).toBlocking().subscribe(
        new Subscriber<Odometer>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Odometer odometer) {
            assertTrue(odometer.id() != null && odometer.id().length() > 0);
            assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
            assertTrue(odometer.date() != null && odometer.date().length() > 0);
            assertTrue(odometer.reading() != null);
          }
        });
  }

  @Test public void getOdometersByUrl() {
    assertTrue(TestHelper.getVehicleId() != null);

    vinliApp.distances()
        .odometerReportsForUrl(String.format("%svehicles/%s/odometers", Endpoint.DISTANCE.getUrl(),
            TestHelper.getVehicleId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Odometer>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Odometer> odometerTimeSeries) {
            assertTrue(odometerTimeSeries.getItems().size() > 0);

            for (Odometer odometer : odometerTimeSeries.getItems()) {
              assertTrue(odometer.id() != null && odometer.id().length() > 0);
              assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
              assertTrue(odometer.date() != null && odometer.date().length() > 0);
              assertTrue(odometer.reading() != null);
            }
          }
        });
  }

  @Test
  public void getOdometerTriggersByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    OdometerTrigger.odometerTriggersWithVehicleId(TestHelper.getVehicleId(), null, null, null, null).toBlocking().subscribe(
        new Subscriber<TimeSeries<OdometerTrigger>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<OdometerTrigger> odometerTriggerTimeSeries) {
            assertTrue(odometerTriggerTimeSeries.getItems().size() > 0);

            for (OdometerTrigger odometerTrigger : odometerTriggerTimeSeries.getItems()) {
              assertTrue(odometerTrigger.id() != null && odometerTrigger.id().length() > 0);
              assertTrue(
                  odometerTrigger.vehicleId() != null && odometerTrigger.vehicleId().length() > 0);
              assertTrue(odometerTrigger.type() != null);
              assertTrue(odometerTrigger.threshold() != null);
            }
          }
        });
  }

  @Test
  public void getOdometerTriggerById(){
    assertTrue(TestHelper.getOdometerTriggerId() != null);

    OdometerTrigger.odometerTriggerWithId(TestHelper.getOdometerTriggerId()).toBlocking().subscribe(
        new Subscriber<OdometerTrigger>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(OdometerTrigger odometerTrigger) {
            assertTrue(odometerTrigger.id() != null && odometerTrigger.id().length() > 0);
            assertTrue(
                odometerTrigger.vehicleId() != null && odometerTrigger.vehicleId().length() > 0);
            assertTrue(odometerTrigger.type() != null);
            assertTrue(odometerTrigger.threshold() != null);
          }
        });
  }

  @Test public void getOdometerTriggersByUrl() {
    assertTrue(TestHelper.getVehicleId() != null);

    vinliApp.distances()
        .odometerTriggersForUrl(
            String.format("%svehicles/%s/odometer_triggers", Endpoint.DISTANCE.getUrl(),
                TestHelper.getVehicleId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<OdometerTrigger>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<OdometerTrigger> odometerTriggerTimeSeries) {
                assertTrue(odometerTriggerTimeSeries.getItems().size() > 0);

                for (OdometerTrigger odometerTrigger : odometerTriggerTimeSeries.getItems()) {
                  assertTrue(odometerTrigger.id() != null && odometerTrigger.id().length() > 0);
                  assertTrue(
                      odometerTrigger.vehicleId() != null && odometerTrigger.vehicleId().length() > 0);
                  assertTrue(odometerTrigger.type() != null);
                  assertTrue(odometerTrigger.threshold() != null);
                }
              }
            });
  }
}
