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
    Collision.collisionsWithVehicleId(TestHelper.getVehicleId(), (Long) null, null, null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Collision>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(TimeSeries<Collision> collisionTimeSeries) {
        assertTrue(collisionTimeSeries.getItems().size() > 0);

        for(Collision collision : collisionTimeSeries.getItems()){
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
    Collision.collisionsWithDeviceId(TestHelper.getDeviceId(), (Long) null, null, null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Collision>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(TimeSeries<Collision> collisionTimeSeries) {
        assertTrue(collisionTimeSeries.getItems().size() > 0);

        for(Collision collision : collisionTimeSeries.getItems()){
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
    Collision.collisionWithId(TestHelper.getCollisionId()).toBlocking().subscribe(
        new Subscriber<Collision>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            assertTrue(false);
          }

          @Override public void onNext(Collision collision) {
            assertTrue(collision.id() != null && collision.id().length() > 0);
            assertTrue(collision.deviceId().length() > 0);
            assertTrue(collision.vehicleId().length() > 0);
            assertTrue(collision.timestamp().length() > 0);
          }
        });
  }

  @Test public void getCollisionsByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.collisions()
        .collisionsForUrl(String.format("%sdevices/%s/collisions", Endpoint.SAFETY.getUrl(),
            TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Collision>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<Collision> collisionTimeSeries) {
                assertTrue(collisionTimeSeries.getItems().size() > 0);

                for(Collision collision : collisionTimeSeries.getItems()){
                  assertTrue(collision.id() != null && collision.id().length() > 0);
                  assertTrue(collision.deviceId().length() > 0);
                  assertTrue(collision.vehicleId().length() > 0);
                  assertTrue(collision.timestamp().length() > 0);
                }
              }
            });
  }

}
