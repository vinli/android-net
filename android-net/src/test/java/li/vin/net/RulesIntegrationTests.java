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
public class RulesIntegrationTests {

  public VinliApp vinliApp;

  @Before
  public void setup(){
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test
  public void testGetRulesByDeviceId(){
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.rules().rules(TestHelper.getDeviceId(), null, null).toBlocking().subscribe(new Subscriber<Page<Rule>>() {
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
      public void onNext(Page<Rule> rulePage) {
        assertTrue(rulePage.getItems().size() > 0);

        for(Rule rule : rulePage.getItems()){
          assertTrue(rule.id() != null && rule.id().length() > 0);
          assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
        }
      }
    });
  }

  @Test
  public void getRuleById(){
    assertTrue(TestHelper.getRuleId() != null);

    vinliApp.rules().rule(TestHelper.getRuleId()).toBlocking().subscribe(new Subscriber<Wrapped<Rule>>() {
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
      public void onNext(Wrapped<Rule> ruleWrapped) {
        Rule rule = ruleWrapped.item();

        assertTrue(rule.id() != null && rule.id().length() > 0);
        assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
      }
    });
  }
}
