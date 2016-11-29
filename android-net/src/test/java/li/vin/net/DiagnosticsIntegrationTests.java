package li.vin.net;

import java.util.concurrent.atomic.AtomicBoolean;
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
  public void testGetPagedCodes(){
    assertTrue(TestHelper.getVehicleId() != null);

    vinliApp.diagnostics().codes(TestHelper.getVehicleId(), null, null, 1, null)
        .toBlocking().subscribe(new Subscriber<TimeSeries<Dtc>>() {
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
      public void onNext(TimeSeries<Dtc> dtcTimeSeries) {
        assertTrue(dtcTimeSeries.getItems().size() > 0);

        for(Dtc dtc : dtcTimeSeries.getItems()){
          assertTrue(dtc.start() != null && dtc.start().length() > 0);
          assertTrue(dtc.vehicleId() != null && dtc.vehicleId().length() > 0);
          assertTrue(dtc.deviceId() != null && dtc.deviceId().length() > 0);
          assertTrue(dtc.number() != null && dtc.number().length() > 0);
          assertTrue(dtc.description() != null && dtc.description().length() > 0);
        }

        if (dtcTimeSeries.hasPrior()) {
          dtcTimeSeries.loadPrior().toBlocking().subscribe(new Subscriber<TimeSeries<Dtc>>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {
              System.out.println("Error: " + e.getMessage());
              e.printStackTrace();
              assertTrue(false);
            }

            @Override public void onNext(TimeSeries<Dtc> dtcTimeSeries) {
              // TODO - uncomment this when platform bug is resolved or explained
              //assertTrue(dtcTimeSeries.getItems().size() > 0);

              for(Dtc dtc : dtcTimeSeries.getItems()){
                assertTrue(dtc.start() != null && dtc.start().length() > 0);
                assertTrue(dtc.vehicleId() != null && dtc.vehicleId().length() > 0);
                assertTrue(dtc.deviceId() != null && dtc.deviceId().length() > 0);
                assertTrue(dtc.number() != null && dtc.number().length() > 0);
                assertTrue(dtc.description() != null && dtc.description().length() > 0);
              }
            }
          });
        }
      }
    });
  }

  @Test
  public void testGetCodesByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    vinliApp.diagnostics().codes(TestHelper.getVehicleId(), null, null, null, null)
        .toBlocking().subscribe(new Subscriber<TimeSeries<Dtc>>() {
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
      public void onNext(TimeSeries<Dtc> dtcPage) {
        assertTrue(dtcPage.getItems().size() > 0);

        for(Dtc dtc : dtcPage.getItems()){
          assertTrue(dtc.start() != null && dtc.start().length() > 0);
          assertTrue(dtc.vehicleId() != null && dtc.vehicleId().length() > 0);
          assertTrue(dtc.deviceId() != null && dtc.deviceId().length() > 0);
          assertTrue(dtc.number() != null && dtc.number().length() > 0);
          assertTrue(dtc.description() != null && dtc.description().length() > 0);
        }
      }
    });
  }

  @Test
  public void testDiagnoseCode(){
    final AtomicBoolean codeFound = new AtomicBoolean(false);
    vinliApp.diagnostics().diagnose("P0301")
        .flatMap(Page.<Dtc.Code>allItems())
        .toBlocking().subscribe(new Subscriber<Dtc.Code>() {
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
      public void onNext(Dtc.Code code) {
        codeFound.set(true);
        assertTrue(code.make() != null && code.make().length() > 0);
      }
    });
    assertTrue(codeFound.get());
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
