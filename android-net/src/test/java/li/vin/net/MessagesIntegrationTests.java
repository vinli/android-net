package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static junit.framework.Assert.assertTrue;
import static li.vin.net.TestHelper.getDeviceId;
import static li.vin.net.TestHelper.getVehicleId;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class MessagesIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void getPagedMessagesByDeviceId() {
    assertTrue(getDeviceId() != null);

    Message.messagesWithDeviceId(getDeviceId(), (Long) null, null, 1, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Message>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
            for (Message message : messageTimeSeries.getItems()) {
              assertTrue(message.id() != null && message.id().length() > 0);
              assertTrue(message.timestamp != null && message.timestamp.length() > 0);
            }

            if (messageTimeSeries.hasPrior()) {
              messageTimeSeries.loadPrior().toBlocking()
                  .subscribe(new Subscriber<TimeSeries<Message>>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {
                  e.printStackTrace();
                  assertTrue(false);
                }

                @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
                  for (Message message : messageTimeSeries.getItems()) {
                    assertTrue(message.id() != null && message.id().length() > 0);
                    assertTrue(message.timestamp != null && message.timestamp.length() > 0);
                  }
                }
              });
            }
          }
        });
  }
  @Test public void getPagedMessagesByVehicleId() {
    assertTrue(getVehicleId() != null);

    Message.messagesWithVehicleId(getVehicleId(), (Long) null, null, 1, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Message>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
            for (Message message : messageTimeSeries.getItems()) {
              assertTrue(message.id() != null && message.id().length() > 0);
              assertTrue(message.timestamp != null && message.timestamp.length() > 0);
            }

            if (messageTimeSeries.hasPrior()) {
              messageTimeSeries.loadPrior().toBlocking()
                  .subscribe(new Subscriber<TimeSeries<Message>>() {
                    @Override public void onCompleted() {

                    }

                    @Override public void onError(Throwable e) {
                      e.printStackTrace();
                      assertTrue(false);
                    }

                    @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
                      for (Message message : messageTimeSeries.getItems()) {
                        assertTrue(message.id() != null && message.id().length() > 0);
                        assertTrue(message.timestamp != null && message.timestamp.length() > 0);
                      }
                    }
                  });
            }
          }
        });
  }

  @Test public void getMessagesByDeviceId() {
    assertTrue(getDeviceId() != null);

    Message.messagesWithDeviceId(getDeviceId(), (Long) null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Message>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
            assertTrue(messageTimeSeries.getItems().size() > 0);

            for (Message message : messageTimeSeries.getItems()) {
              assertTrue(message.id() != null && message.id().length() > 0);
              assertTrue(message.timestamp != null && message.timestamp.length() > 0);
            }
          }
        });
  }
  @Test public void getMessagesByVehicleId() {
    assertTrue(getVehicleId() != null);

    Message.messagesWithVehicleId(getVehicleId(), null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Message>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
            assertTrue(messageTimeSeries.getItems().size() > 0);

            for (Message message : messageTimeSeries.getItems()) {
              assertTrue(message.id() != null && message.id().length() > 0);
              assertTrue(message.timestamp != null && message.timestamp.length() > 0);
            }
          }
        });
  }

  @Test public void getMessagesWithSinceUntilLimitByDeviceId() {
    assertTrue(getDeviceId() != null);

    vinliApp.messages()
        .messages(getDeviceId(), 0L, System.currentTimeMillis(), 5, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Message>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
            assertTrue(messageTimeSeries.getItems().size() > 0);
            assertTrue(messageTimeSeries.getItems().size() <= 5);

            for (Message message : messageTimeSeries.getItems()) {
              assertTrue(message.id() != null && message.id().length() > 0);
              assertTrue(message.timestamp != null && message.timestamp.length() > 0);
            }
          }
        });
  }

  @Test public void getMessageById() {
    assertTrue(TestHelper.getMessageId() != null);

    Message.messageWithId(TestHelper.getMessageId())
        .toBlocking()
        .subscribe(new Subscriber<Message>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Message message) {
            assertTrue(message.id() != null && message.id().length() > 0);
            assertTrue(message.timestamp != null && message.timestamp.length() > 0);
          }
        });
  }

  @Test public void getMessagesByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.messages()
        .messagesForUrl(String.format("%sdevices/%s/messages", Endpoint.TELEMETRY.getUrl(),
            TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<Message>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<Message> messageTimeSeries) {
                assertTrue(messageTimeSeries.getItems().size() > 0);

                for (Message message : messageTimeSeries.getItems()) {
                  assertTrue(message.id() != null && message.id().length() > 0);
                  assertTrue(message.timestamp != null && message.timestamp.length() > 0);
                }
              }
            });
  }

}
