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
public class CollisionsIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetCollisionsByVehicleId(){
    vinliApp.collisions().collisionsForVehicle(TestHelper.getVehicleId(), null, null).toBlocking().subscribe(new Subscriber<Page<Collision>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(Page<Collision> collisionPage) {
        assertTrue(collisionPage.getItems().size() > 0);

        for(Collision collision : collisionPage.getItems()){
          assertTrue(collision.id() != null && collision.id().length() > 0);
          assertTrue(collision.deviceId().length() > 0);
          assertTrue(collision.vehicleId().length() > 0);
          assertTrue(collision.timestamp().length() > 0);
        }
      }
    });
  }

  @Test
  public void testGetCollisionsByDeviceId(){
    vinliApp.collisions().collisionsForDevice(TestHelper.getDeviceId(), null, null).toBlocking().subscribe(new Subscriber<Page<Collision>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(Page<Collision> collisionPage) {
        assertTrue(collisionPage.getItems().size() > 0);

        for(Collision collision : collisionPage.getItems()){
          assertTrue(collision.id() != null && collision.id().length() > 0);
          assertTrue(collision.deviceId().length() > 0);
          assertTrue(collision.vehicleId().length() > 0);
          assertTrue(collision.timestamp().length() > 0);
        }
      }
    });
  }

  @Test
  public void getCollisionById(){
    vinliApp.collisions().collision(TestHelper.getCollisionId()).toBlocking().subscribe(new Subscriber<Wrapped<Collision>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        assertTrue(false);
      }

      @Override
      public void onNext(Wrapped<Collision> collisionWrapped) {
        Collision collision = collisionWrapped.item();

        assertTrue(collision.id() != null && collision.id().length() > 0);
        assertTrue(collision.deviceId().length() > 0);
        assertTrue(collision.vehicleId().length() > 0);
        assertTrue(collision.timestamp().length() > 0);

      }
    });
  }

}
