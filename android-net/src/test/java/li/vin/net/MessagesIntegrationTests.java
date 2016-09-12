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
public class MessagesIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void getMessagesByDeviceId(){
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.messages().messages(TestHelper.getDeviceId(), null, null, null, null).toBlocking().subscribe(new Subscriber<TimeSeries<Message>>() {
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
      public void onNext(TimeSeries<Message> messageTimeSeries) {
        assertTrue(messageTimeSeries.getItems().size() > 0);

        for(Message message : messageTimeSeries.getItems()){
          assertTrue(message.id() != null && message.id().length() > 0);
          assertTrue(message.timestamp != null && message.timestamp.length() > 0);
        }
      }
    });
  }

  @Test
  public void getMessageById(){
    assertTrue(TestHelper.getMessageId() != null);

    vinliApp.messages().message(TestHelper.getMessageId()).toBlocking().subscribe(new Subscriber<Wrapped<Message>>() {
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
      public void onNext(Wrapped<Message> messageWrapped) {
        Message message = messageWrapped.item();

        assertTrue(message.id() != null && message.id().length() > 0);
        assertTrue(message.timestamp != null && message.timestamp.length() > 0);
      }
    });
  }
}
