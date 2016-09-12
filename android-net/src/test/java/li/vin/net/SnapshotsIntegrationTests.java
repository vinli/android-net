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
public class SnapshotsIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void getSnapshotsByDeviceId(){
    vinliApp.snapshots().snapshots(TestHelper.getDeviceId(), "vehicleSpeed", null, null, null, null)
        .toBlocking().subscribe(new Subscriber<TimeSeries<Snapshot>>() {
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
      public void onNext(TimeSeries<Snapshot> snapshotTimeSeries) {
        assertTrue(snapshotTimeSeries.getItems().size() > 0);

        for(Snapshot snapshot : snapshotTimeSeries.getItems()){
          assertTrue(snapshot.timestamp() != null && snapshot.timestamp().length() > 0);
          assertTrue(snapshot.doubleVal("vehicleSpeed", Double.MIN_VALUE) != Double.MIN_VALUE);
        }
      }
    });
  }

}
