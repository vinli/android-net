package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import li.vin.net.DistanceList.Distance;
import rx.Observer;
import rx.Subscriber;

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
  public void testCreateGetAndDeleteOdometer() {
    assertTrue(TestHelper.getVehicleId() != null);

    Odometer.create()
        .reading(200000d)
        .unit(DistanceUnit.MILES)
        .vehicleId(TestHelper.getVehicleId())
        .save().toBlocking().subscribe(new Subscriber<Odometer>() {
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

        Odometer.odometerWithId(odometer.id())
            .toBlocking().subscribe(new Subscriber<Odometer>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(final Odometer odometer) {
            assertTrue(odometer.id() != null && odometer.id().length() > 0);
            assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
            assertTrue(odometer.date() != null && odometer.date().length() > 0);
            assertTrue(odometer.reading() != null);

            odometer.delete().toBlocking().subscribe(new Observer<Void>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(Void aVoid) {

              }
            });
          }
        });
      }
    });
  }

  @Test
  public void testCreateGetByVehicleIdAndDeleteOdometer() {
    assertTrue(TestHelper.getVehicleId() != null);

    Odometer.create()
        .reading(200000d)
        .unit(DistanceUnit.MILES)
        .vehicleId(TestHelper.getVehicleId())
        .save().toBlocking().subscribe(new Subscriber<Odometer>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(final Odometer odometer) {
        assertTrue(odometer.id() != null && odometer.id().length() > 0);
        assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
        assertTrue(odometer.date() != null && odometer.date().length() > 0);
        assertTrue(odometer.reading() != null);

        Odometer.odometersWithVehicleId(TestHelper.getVehicleId())
            .toBlocking().subscribe(new Subscriber<TimeSeries<Odometer>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Odometer> odometerTimeSeries) {
            assertTrue(odometer.id() != null && odometer.id().length() > 0);
            assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
            assertTrue(odometer.date() != null && odometer.date().length() > 0);
            assertTrue(odometer.reading() != null);

            odometer.delete().toBlocking().subscribe(new Observer<Void>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(Void aVoid) {

              }
            });
          }
        });
      }
    });
  }

  @Test
  public void testCreateGetByUrlAndDeleteOdometer() {
    assertTrue(TestHelper.getVehicleId() != null);

    Odometer.create()
        .reading(200000d)
        .unit(DistanceUnit.MILES)
        .vehicleId(TestHelper.getVehicleId())
        .save().toBlocking().subscribe(new Subscriber<Odometer>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(final Odometer odometer) {
        assertTrue(odometer.id() != null && odometer.id().length() > 0);
        assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
        assertTrue(odometer.date() != null && odometer.date().length() > 0);
        assertTrue(odometer.reading() != null);

        vinliApp.distances().odometerReportsForUrl(String.format(
                "%svehicles/%s/odometers", VinliEndpoint.DISTANCE.getUrl(), TestHelper.getVehicleId()))
            .toBlocking().subscribe(new Subscriber<TimeSeries<Odometer>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Odometer> odometerTimeSeries) {
            assertTrue(odometer.id() != null && odometer.id().length() > 0);
            assertTrue(odometer.vehicleId() != null && odometer.vehicleId().length() > 0);
            assertTrue(odometer.date() != null && odometer.date().length() > 0);
            assertTrue(odometer.reading() != null);

            odometer.delete().toBlocking().subscribe(new Observer<Void>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(Void aVoid) {

              }
            });
          }
        });
      }
    });
  }

  @Test
  public void testCreateAndDeleteOdometerTrigger() {
    assertTrue(TestHelper.getVehicleId() != null);

    OdometerTrigger.create()
        .unit(DistanceUnit.METERS)
        .vehicleId(TestHelper.getVehicleId())
        .threshold(300000d)
        .type(OdometerTrigger.TriggerType.SPECIFIC)
        .save().toBlocking().subscribe(new Subscriber<OdometerTrigger>() {
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

        odometerTrigger.delete().toBlocking().subscribe(new Observer<Void>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Void aVoid) {

          }
        });
      }
    });

    DistanceList.distancesWithVehicleId(TestHelper.getVehicleId(), (Long) null, null, null).toBlocking().subscribe(new Subscriber<DistanceList>() {
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
  public void testGetDistancesByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    DistanceList.distancesWithVehicleId(TestHelper.getVehicleId(), (Long) null, null, null).toBlocking().subscribe(new Subscriber<DistanceList>() {
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
  public void getOdometerTriggersByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    OdometerTrigger
        .odometerTriggersWithVehicleId(TestHelper.getVehicleId(), (Long) null, null, 1, null)
        .toBlocking().subscribe(
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

            if (odometerTriggerTimeSeries.hasPrior()) {
              odometerTriggerTimeSeries.loadPrior()
                  .toBlocking().subscribe(new Subscriber<TimeSeries<OdometerTrigger>>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {
                  System.out.println("Error: " + e.getMessage());
                  e.printStackTrace();
                  assertTrue(false);
                }

                @Override
                public void onNext(TimeSeries<OdometerTrigger> odometerTriggerTimeSeries) {
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
                String.format("%svehicles/%s/odometer_triggers", VinliEndpoint.DISTANCE.getUrl(),
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
