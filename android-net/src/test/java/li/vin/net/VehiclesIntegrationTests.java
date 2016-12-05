package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class VehiclesIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void testGetVehicleByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    Vehicle.vehiclesWithDeviceId(TestHelper.getDeviceId(), 1, null)
        .toBlocking()
        .subscribe(new Subscriber<Page<Vehicle>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Page<Vehicle> vehiclePage) {
            assertTrue(vehiclePage.getItems().size() > 0);

            for (Vehicle vehicle : vehiclePage.getItems()) {
              assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
              assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
            }

            if (vehiclePage.hasNextPage()) {
              vehiclePage.loadNextPage()
                  .toBlocking().subscribe(new Subscriber<Page<Vehicle>>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {
                  System.out.println("Error: " + e.getMessage());
                  e.printStackTrace();
                  assertTrue(false);
                }

                @Override public void onNext(Page<Vehicle> vehiclePage) {
                  assertTrue(vehiclePage.getItems().size() > 0);

                  for (Vehicle vehicle : vehiclePage.getItems()) {
                    assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
                    assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
                  }
                }
              });
            }
          }
        });
  }

  @Test public void testGetLatestVehicleByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    Vehicle.latestVehicleWithDeviceId(TestHelper.getDeviceId())
        .toBlocking()
        .subscribe(new Subscriber<Vehicle>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Vehicle vehicle) {
            assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
            assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
          }
        });
  }

  @Test public void testGetVehicleWithLimitOffsetByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.vehicles()
        .vehicles(TestHelper.getDeviceId(), 5, 1)
        .toBlocking()
        .subscribe(new Subscriber<Page<Vehicle>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Page<Vehicle> vehiclePage) {
            assertTrue(vehiclePage.getItems().size() <= 5);

            for (Vehicle vehicle : vehiclePage.getItems()) {
              assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
              assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
            }
          }
        });
  }

  @Test public void testGetVehicleById() {
    assertTrue(TestHelper.getVehicleId() != null);

    Vehicle.vehicleWithId(TestHelper.getVehicleId())
        .toBlocking()
        .subscribe(new Subscriber<Vehicle>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Vehicle vehicle) {
            assertTrue(vehicle.engine()!=null);
            assertTrue(vehicle.engineDisplacement()!=null);
            assertTrue(vehicle.transmission()!=null);
            assertTrue(vehicle.manufacturer()!=null);
            assertTrue(vehicle.categories()!=null);
            assertTrue(vehicle.epaMpg()!=null);
            assertTrue(vehicle.drive()!=null);
            assertTrue(vehicle.numDoors()!=null);
            assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
            assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
          }
        });
  }

  @Test public void getVehiclesByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.vehicles()
        .vehiclesForUrl(String.format("%sdevices/%s/vehicles", Endpoint.PLATFORM.getUrl(),
            TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<Page<Vehicle>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(Page<Vehicle> vehiclePage) {
                assertTrue(vehiclePage.getItems().size() > 0);

                for (Vehicle vehicle : vehiclePage.getItems()) {
                  assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
                  assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
                }
              }
            });
  }


}