package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static java.lang.System.currentTimeMillis;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class SnapshotsIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void getPagedSnapshots() {
    assertTrue(TestHelper.getDeviceId() != null);

    Snapshot.snapshotsWithDeviceId(TestHelper.getDeviceId(), "location,vehicleSpeed", (Long) null, null, 1, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Snapshot>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Snapshot> snapshotTimeSeries) {
            assertTrue(snapshotTimeSeries.getItems().size() > 0);

            for (Snapshot snapshot : snapshotTimeSeries.getItems()) {
              assertTrue(snapshot.coord() != null);
              assertTrue(snapshot.timestamp() != null && snapshot.timestamp().length() > 0);
              assertTrue(snapshot.doubleVal("vehicleSpeed", Double.MIN_VALUE) != Double.MIN_VALUE);
            }

            if (snapshotTimeSeries.hasPrior()) {
              snapshotTimeSeries.loadPrior()
                  .toBlocking().subscribe(new Subscriber<TimeSeries<Snapshot>>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {
                  System.out.println("Error: " + e.getMessage());
                  e.printStackTrace();
                  assertTrue(false);
                }

                @Override public void onNext(TimeSeries<Snapshot> snapshotTimeSeries) {
                  assertTrue(snapshotTimeSeries.getItems().size() > 0);

                  for (Snapshot snapshot : snapshotTimeSeries.getItems()) {
                    assertTrue(snapshot.coord() != null);
                    assertTrue(snapshot.timestamp() != null && snapshot.timestamp().length() > 0);
                    assertTrue(snapshot.doubleVal("vehicleSpeed", Double.MIN_VALUE) != Double.MIN_VALUE);
                  }
                }
              });
            }
          }
        });
  }

  @Test public void getSnapshotsByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    Snapshot.snapshotsWithDeviceId(TestHelper.getDeviceId(), "location,vehicleSpeed", (Long) null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Snapshot>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Snapshot> snapshotTimeSeries) {
            assertTrue(snapshotTimeSeries.getItems().size() > 0);

            for (Snapshot snapshot : snapshotTimeSeries.getItems()) {
              assertTrue(snapshot.coord() != null);
              assertTrue(snapshot.timestamp() != null && snapshot.timestamp().length() > 0);
              assertTrue(snapshot.doubleVal("vehicleSpeed", Double.MIN_VALUE) != Double.MIN_VALUE);
            }
          }
        });
  }

  @Test public void getSnapshotsWithSinceUntilLimitByDeviceId() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.snapshots()
        .snapshots(TestHelper.getDeviceId(), "vehicleSpeed", 0L, currentTimeMillis(), 5, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Snapshot>>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Snapshot> snapshotTimeSeries) {
            assertTrue(snapshotTimeSeries.getItems().size() > 0);
            assertTrue(snapshotTimeSeries.getItems().size() <= 5);

            for (Snapshot snapshot : snapshotTimeSeries.getItems()) {
              assertTrue(snapshot.timestamp() != null && snapshot.timestamp().length() > 0);
              assertTrue(snapshot.doubleVal("vehicleSpeed", Double.MIN_VALUE) != Double.MIN_VALUE);
            }
          }
        });
  }

  @Test public void getSnapshotsByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.snapshots()
        .snapshotsForUrl(
            String.format("%sdevices/%s/snapshots?fields=vehicleSpeed", Endpoint.TELEMETRY.getUrl(),
                TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Snapshot>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<Snapshot> snapshotTimeSeries) {
                assertTrue(snapshotTimeSeries.getItems().size() > 0);

                for (Snapshot snapshot : snapshotTimeSeries.getItems()) {
                  assertTrue(snapshot.timestamp() != null && snapshot.timestamp().length() > 0);
                  assertTrue(snapshot.doubleVal("vehicleSpeed", Double.MIN_VALUE) != Double.MIN_VALUE);
                }
              }
            });
  }
}
