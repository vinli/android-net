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
public class DiagnosticsIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetCodesByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    vinliApp.diagnostics().codes(TestHelper.getVehicleId()).toBlocking().subscribe(new Subscriber<Page<Dtc>>() {
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
      public void onNext(Page<Dtc> dtcPage) {
        assertTrue(dtcPage.getItems().size() > 0);

        for(Dtc dtc : dtcPage.getItems()){
          assertTrue(dtc.start() != null && dtc.start().length() > 0);
          assertTrue(dtc.vehicleId() != null && dtc.vehicleId().length() > 0);
          assertTrue(dtc.deviceId() != null && dtc.deviceId().length() > 0);
          assertTrue(dtc.number() != null && dtc.number().length() > 0);
        }
      }
    });
  }

  @Test
  public void testDiagnoseCode(){
    vinliApp.diagnostics().diagnose("P0301").toBlocking().subscribe(new Subscriber<Page<Dtc.Code>>() {
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
      public void onNext(Page<Dtc.Code> codePage) {
        assertTrue(codePage.getItems().size() > 0);

        for(Dtc.Code code : codePage.getItems()){
          assertTrue(code.make() != null && code.make().length() > 0);
        }
      }
    });
  }

  @Test public void getCurrentBatteryStatus() {
    assertTrue(TestHelper.getVehicleId() != null);

    BatteryStatus.currentBatteryStatusForVehicle(TestHelper.getVehicleId())
        .toBlocking()
        .subscribe(new Subscriber<BatteryStatus>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(BatteryStatus batteryStatus) {
            if (batteryStatus != null) {
              assertTrue(batteryStatus.status() != null);
              assertTrue(batteryStatus.timestamp().length() > 0);
            }
          }
        });
  }

}
