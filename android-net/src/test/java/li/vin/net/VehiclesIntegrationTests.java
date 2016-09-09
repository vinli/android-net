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
public class VehiclesIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetVehicleByDeviceId(){
    vinliApp.vehicles().vehicles(TestHelper.getDeviceId(), null, null).toBlocking().subscribe(new Subscriber<Page<Vehicle>>() {
      @Override
      public void onCompleted() {}

      @Override
      public void onError(Throwable e) {
        assertTrue(false);
      }

      @Override
      public void onNext(Page<Vehicle> vehiclePage) {
        assertTrue(vehiclePage.getItems().size() > 0);

        for(Vehicle vehicle : vehiclePage.getItems()){
          assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
          assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
        }
      }
    });
  }

  @Test
  public void testGetVehicleById(){
    vinliApp.vehicles().vehicle(TestHelper.getVehicleId()).toBlocking().subscribe(new Subscriber<Wrapped<Vehicle>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        assertTrue(false);
      }

      @Override
      public void onNext(Wrapped<Vehicle> vehicleWrapped) {
        Vehicle vehicle = vehicleWrapped.item();
        assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
        assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
      }
    });
  }

  @Test
  public void testGetVehicleByDeviceAndVehicleId(){
    vinliApp.vehicles().vehicle(TestHelper.getDeviceId(), TestHelper.getVehicleId()).toBlocking().subscribe(new Subscriber<Wrapped<Vehicle>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        System.out.println("Error fetching vehicle by device and vehicle id: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(Wrapped<Vehicle> vehicleWrapped) {
        Vehicle vehicle = vehicleWrapped.item();
        assertTrue(vehicle.id() != null && vehicle.id().length() > 0);
        assertTrue(vehicle.vin() != null && vehicle.vin().length() > 0);
      }
    });
  }
}