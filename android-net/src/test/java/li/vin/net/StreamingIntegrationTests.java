package li.vin.net;

import li.vin.net.Dummy.Run;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static junit.framework.Assert.assertTrue;

/**
 * Created by JoshBeridon on 11/18/16.
 */
@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class StreamingIntegrationTests {
  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void getDummies() {
    //assertTrue(TestHelper.getSubscriptionId() != null);

    vinliApp.dummies().dummies(1, 0).toBlocking().subscribe(new Subscriber<Page<Dummy>>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(Page<Dummy> dummyPage) {
        assertTrue(dummyPage.getItems().size() > 0);
        System.out.println(dummyPage.toString());
        for (Dummy dummy : dummyPage.getItems()) {
          assertTrue(dummy.id() != null && dummy.id().length() > 0);
          assertTrue(dummy.name() != null && dummy.id().length() > 0);
          assertTrue(dummy.deviceId() != null && dummy.id().length() > 0);
          assertTrue(dummy.caseId() != null && dummy.id().length() > 0);
        }
      }
    });
  }

  @Test public void getRun() {
    assertTrue(TestHelper.getSubscriptionId() != null);
    Run.create()
        .vin(TestHelper.getVIN())
        .routeId(TestHelper.getRouteId())
        .save(TestHelper.getDummyId())
        .toBlocking()
        .subscribe(new Subscriber<Dummy.Run>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Dummy.Run run) {
            assertTrue(run.id() != null && run.id().length() > 0);
            assertTrue(run.status() != null);
          }
        });
    Dummy.currentRun(TestHelper.getDummyId()).toBlocking().subscribe(new Subscriber<Run>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(Run run) {
        assertTrue(run.id() != null && run.id().length() > 0);
        assertTrue(run.status() != null);
        assertTrue(run.status().state() != null && run.status().state().length() > 0);
        assertTrue(run.status().state() != null && run.status().state().length() > 0);
        assertTrue(run.status().routeId() != null && run.status().routeId().length() > 0);

        run.delete(TestHelper.getDummyId()).toBlocking().subscribe(new Subscriber<Void>() {
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

  @Test public void streamTest() {
    final String deviceId;
    deviceId =
        vinliApp.dummies().dummies(null, null).toBlocking().first().getItems().get(0).deviceId();
    System.out.println(deviceId);
    vinliApp.dummies()
        .currentRun(TestHelper.getDummyId())
        .toBlocking()
        .subscribe(new Subscriber<Wrapped<Run>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Wrapped<Run> runWrapped) {
            System.out.println("On next for deleting");

            if (runWrapped.item() != null) {
              System.out.println(runWrapped.item().id());
              System.out.println(runWrapped.item().status());
              System.out.println("run is null");
              vinliApp.dummies()
                  .deleteRun(TestHelper.getDummyId())
                  .toBlocking()
                  .subscribe(new Subscriber<Void>() {
                    @Override public void onCompleted() {
                      System.out.println("Run was deleted");
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
          }
        });

    Run.create()
        .routeId(TestHelper.getRouteId())
        .vin(TestHelper.getVIN())
        .save(TestHelper.getDummyId())
        .toBlocking()
        .subscribe(new Subscriber<Run>() {
          @Override public void onCompleted() {
            System.out.println("Created a run!");
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Run run) {
            System.out.println(run.status().state());
          }
        });

    vinliApp.device(deviceId)

        .toBlocking()
        .subscribe(new Subscriber<Device>() {
          @Override public void onCompleted() {
          System.out.println("Completed looking for device");
          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Device device) {
            System.out.println(device.id());
            device.stream()
                .observeOn(AndroidSchedulers.mainThread())
                .toBlocking()
                .subscribe(new Subscriber<StreamMessage>() {
                  @Override public void onCompleted() {

                  }

                  @Override public void onError(Throwable e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                    assertTrue(false);
                  }

                  @Override public void onNext(StreamMessage streamMessage) {
                    System.out.println(streamMessage);
                  }
                });
          }
        });

    System.out.println("End of Test");
  }
}
