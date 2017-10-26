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
public class DevicesIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void getPagedDevices() {
    vinliApp.devices(1, 0)
        .toBlocking()
        .subscribe(new Subscriber<Page<Device>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Page<Device> devicePage) {
            assertTrue(devicePage.getItems().size() > 0);

            for(Device device : devicePage.getItems()){
              assertTrue(device.id() != null && device.id().length() > 0);
              assertTrue(device.name() != null && device.name().length() > 0);
            }

            if (devicePage.hasNextPage()) {
              devicePage.loadNextPage().toBlocking()
                  .subscribe(new Subscriber<Page<Device>>() {
                    @Override public void onCompleted() {

                    }

                    @Override public void onError(Throwable e) {
                      e.printStackTrace();
                      assertTrue(false);
                    }

                    @Override public void onNext(Page<Device> devicePage) {
                      assertTrue(devicePage.getItems().size() > 0);

                      for(Device device : devicePage.getItems()){
                        assertTrue(device.id() != null && device.id().length() > 0);
                        assertTrue(device.name() != null && device.name().length() > 0);
                      }
                    }
                  });
            }
          }
        });
  }

  @Test
  public void testGetDevices(){
    vinliApp.devices().toBlocking().subscribe(new Subscriber<Page<Device>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(Page<Device> devicePage) {
        assertTrue(devicePage.getItems().size() > 0);

        for(Device device : devicePage.getItems()){
          assertTrue(device.id() != null && device.id().length() > 0);
          assertTrue(device.name() != null && device.name().length() > 0);
        }
      }
    });
  }

  @Test
  public void testGetDevicesByUrl(){
      vinliApp.devicesSvc().devicesForUrl(String.format("%sdevices", VinliEndpoint.PLATFORM.getUrl()))
        .toBlocking().subscribe(new Subscriber<Page<Device>>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(Page<Device> devicePage) {
        assertTrue(devicePage.getItems().size() > 0);

        for(Device device : devicePage.getItems()){
          assertTrue(device.id() != null && device.id().length() > 0);
          assertTrue(device.name() != null && device.name().length() > 0);
        }
      }
    });
  }

  @Test
  public void testGetDeviceById(){
    assertTrue(TestHelper.getDeviceId() != null);

    Device.deviceWithId(TestHelper.getDeviceId()).toBlocking().subscribe(new Subscriber<Device>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {
        e.printStackTrace();
        assertTrue(false);
      }

      @Override
      public void onNext(Device device) {
        assertTrue(device.id() != null && device.id().length() > 0);
        assertTrue(device.name() != null && device.name().length() > 0);
      }
    });
  }

}
