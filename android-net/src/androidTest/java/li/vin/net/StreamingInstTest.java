package li.vin.net;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static junit.framework.Assert.assertTrue;

/**
 * Created by JoshBeridon on 12/2/16.
 */

@RunWith(AndroidJUnit4.class) @SmallTest public class StreamingInstTest {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(InstTestHelper.getAccessToken() != null);

    vinliApp = InstTestHelper.getVinliApp();
  }

  @Test public void streamTest() {
    final AtomicInteger messages = new AtomicInteger(0);
    final String deviceId;
    deviceId =
        vinliApp.dummies().dummies(null, null).toBlocking().first().getItems().get(0).deviceId();
    System.out.println(deviceId);
    vinliApp.dummies()
        .currentRun(InstTestHelper.getDummyId())
        .toBlocking()
        .subscribe(new Subscriber<Wrapped<Dummy.Run>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Wrapped<Dummy.Run> runWrapped) {
            if (runWrapped.item() != null) {
              vinliApp.dummies()
                  .deleteRun(InstTestHelper.getDummyId())
                  .toBlocking()
                  .subscribe(new Subscriber<Void>() {
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
          }
        });

    Dummy.Run.create()
        .routeId(InstTestHelper.getRouteId())
        .vin(InstTestHelper.getVIN())
        .save(InstTestHelper.getDummyId())
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
            System.out.println(run.status().state());
          }
        });

    vinliApp.device(deviceId).toBlocking().subscribe(new Subscriber<Device>() {
      @Override public void onCompleted() {
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
                if (messages.get() > 5) {
                  unsubscribe();
                }
                messages.addAndGet(1);
                System.out.println(streamMessage.toString());
                assertTrue(streamMessage.getType() != null);
              }
            });
      }
    });
  }
}
