package li.vin.net;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
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
  public void testCreateAndDeleteRadiusBoundaryRule(){
    assertTrue(TestHelper.getDeviceId() != null);

    Rule.create().deviceId(TestHelper.getDeviceId()).name("testrule").radiusBoundary(
        Rule.RadiusBoundary.create().lat(32.897480f).lon(-97.040443f).radius(100).build()).save()
        .toBlocking().subscribe(new Subscriber<Rule>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Rule rule) {
            assertTrue(rule.id() != null && rule.id().length() > 0);
            assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
            assertTrue(rule.object().type().length() > 0);
            assertTrue(rule.object().id().length() > 0);

            rule.delete().toBlocking().subscribe(new Subscriber<Void>() {
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

  @Test
  public void testCreateAndDeleteParametricBoundaryRule(){
    assertTrue(TestHelper.getDeviceId() != null);

    List<double[]> l = new ArrayList<>();
    l.add(new double[]{32.792492f, -96.823495f});
    l.add(new double[]{32.817846f, -96.670862f});
    l.add(new double[]{32.67926f, -96.771103f});
    l.add(new double[]{32.792492f, -96.823495f});
    List<List<double[]>> ll = new ArrayList<>();
    ll.add(l);

    Rule.create().deviceId(TestHelper.getDeviceId()).name("testrule")
        .parametricBoundaries(Arrays.asList(new Rule.ParametricBoundary.Seed[]{
            Rule.ParametricBoundary.create().parameter("vehicleSpeed").max(32f).min(16f).build(),
            Rule.ParametricBoundary.create().parameter("rpm").max(32f).min(16f).build(),
        }))
        .save()
        .toBlocking().subscribe(new Subscriber<Rule>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Rule rule) {
            assertTrue(rule.id() != null && rule.id().length() > 0);
            assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
            assertTrue(rule.object().type().length() > 0);
            assertTrue(rule.object().id().length() > 0);

            rule.delete().toBlocking().subscribe(new Subscriber<Void>() {
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

  @Test
  public void testCreateAndDeletePolygonBoundaryRule(){
    assertTrue(TestHelper.getDeviceId() != null);

    List<double[]> l = new ArrayList<>();
    l.add(new double[]{32.792492f, -96.823495f});
    l.add(new double[]{32.817846f, -96.670862f});
    l.add(new double[]{32.67926f, -96.771103f});
    l.add(new double[]{32.792492f, -96.823495f});
    List<List<double[]>> ll = new ArrayList<>();
    ll.add(l);

    Rule.create().deviceId(TestHelper.getDeviceId()).name("testrule")
        .polygonBoundary(Rule.PolygonBoundary.create().coordinates(ll).build())
        .save()
        .toBlocking().subscribe(new Subscriber<Rule>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(Rule rule) {
            assertTrue(rule.id() != null && rule.id().length() > 0);
            assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
            assertTrue(rule.object().type().length() > 0);
            assertTrue(rule.object().id().length() > 0);

            rule.delete().toBlocking().subscribe(new Subscriber<Void>() {
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

  @Test
  public void testGetRulesByDeviceId(){
    assertTrue(TestHelper.getDeviceId() != null);

    Rule.rulesWithDeviceId(TestHelper.getDeviceId(), 1, null)
        .toBlocking().subscribe(new Subscriber<Page<Rule>>() {
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
          assertTrue(rule.object().type().length() > 0);
          assertTrue(rule.object().id().length() > 0);
        }

        if (rulePage.hasNextPage()) {
          rulePage.loadNextPage()
              .toBlocking().subscribe(new Subscriber<Page<Rule>>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {
              System.out.println("Error: " + e.getMessage());
              e.printStackTrace();
              assertTrue(false);
            }

            @Override public void onNext(Page<Rule> rulePage) {
              assertTrue(rulePage.getItems().size() > 0);

              for(Rule rule : rulePage.getItems()){
                assertTrue(rule.id() != null && rule.id().length() > 0);
                assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
                assertTrue(rule.object().type().length() > 0);
                assertTrue(rule.object().id().length() > 0);
              }
            }
          });
        }
      }
    });
  }

  @Test
  public void testCreateAndDeletePolygonBoundaryRuleByVehicle(){
    assertTrue(TestHelper.getDeviceId() != null);

    List<double[]> l = new ArrayList<>();
    l.add(new double[]{32.792492f, -96.823495f});
    l.add(new double[]{32.817846f, -96.670862f});
    l.add(new double[]{32.67926f, -96.771103f});
    l.add(new double[]{32.792492f, -96.823495f});
    List<List<double[]>> ll = new ArrayList<>();
    ll.add(l);

    Rule.create().vehicleId(TestHelper.getVehicleId()).name("testrule")
        .polygonBoundary(Rule.PolygonBoundary.create().coordinates(ll).build())
        .vehicleSave()
        .toBlocking().subscribe(new Subscriber<Rule>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(Rule rule) {
        assertTrue(rule.id() != null && rule.id().length() > 0);
        assertTrue(rule.object().type().equals("vehicle") && rule.object().id()!=null);
        assertTrue(rule.object().type().length() > 0);
        assertTrue(rule.object().id().length() > 0);

        rule.delete().toBlocking().subscribe(new Subscriber<Void>() {
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

  @Test
  public void testCreateAndDeleteParametricBoundaryRuleByVehicle(){
    assertTrue(TestHelper.getDeviceId() != null);

    List<double[]> l = new ArrayList<>();
    l.add(new double[]{32.792492f, -96.823495f});
    l.add(new double[]{32.817846f, -96.670862f});
    l.add(new double[]{32.67926f, -96.771103f});
    l.add(new double[]{32.792492f, -96.823495f});
    List<List<double[]>> ll = new ArrayList<>();
    ll.add(l);

    Rule.create().vehicleId(TestHelper.getVehicleId()).name("testrule")
        .parametricBoundaries(Arrays.asList(new Rule.ParametricBoundary.Seed[]{
            Rule.ParametricBoundary.create().parameter("vehicleSpeed").max(32f).min(16f).build(),
            Rule.ParametricBoundary.create().parameter("rpm").max(32f).min(16f).build(),
        }))
        .vehicleSave()
        .toBlocking().subscribe(new Subscriber<Rule>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(Rule rule) {
        assertTrue(rule.id() != null && rule.id().length() > 0);
        assertTrue(rule.object().type().equals("vehicle") && rule.object().id()!=null);
        assertTrue(rule.object().type().length() > 0);
        assertTrue(rule.object().id().length() > 0);

        rule.delete().toBlocking().subscribe(new Subscriber<Void>() {
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

  @Test
  public void testCreateAndDeleteRadiusBoundaryRuleByVehicle(){
    assertTrue(TestHelper.getDeviceId() != null);

    Rule.create().vehicleId(TestHelper.getVehicleId()).name("testrule").radiusBoundary(
        Rule.RadiusBoundary.create().lat(32.897480f).lon(-97.040443f).radius(100).build()).vehicleSave()
        .toBlocking().subscribe(new Subscriber<Rule>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(Rule rule) {
        assertTrue(rule.id() != null && rule.id().length() > 0);
        assertTrue(rule.object().type().equals("vehicle") && rule.object().id()!=null);
        assertTrue(rule.object().type().length() > 0);
        assertTrue(rule.object().id().length() > 0);

        rule.delete().toBlocking().subscribe(new Subscriber<Void>() {
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

  @Test
  public void testGetRulesByVehicleId(){
    assertTrue(TestHelper.getVehicleId() != null);

    Rule.rulesWithVehicleId(TestHelper.getVehicleId(), 1, null)
        .toBlocking().subscribe(new Subscriber<Page<Rule>>() {
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
          assertTrue(rule.object().type().equals("vehicle") && rule.object().id()!=null);
          assertTrue(rule.object().type().length() > 0);
          assertTrue(rule.object().id().length() > 0);
        }

        if (rulePage.hasNextPage()) {
          rulePage.loadNextPage()
              .toBlocking().subscribe(new Subscriber<Page<Rule>>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {
              System.out.println("Error: " + e.getMessage());
              e.printStackTrace();
              assertTrue(false);
            }

            @Override public void onNext(Page<Rule> rulePage) {
              assertTrue(rulePage.getItems().size() > 0);

              for(Rule rule : rulePage.getItems()){
                assertTrue(rule.id() != null && rule.id().length() > 0);
                assertTrue(rule.object().type().equals("vehicle") && rule.object().id()!=null);
                assertTrue(rule.object().type().length() > 0);
                assertTrue(rule.object().id().length() > 0);
              }
            }
          });
        }
      }
    });
  }

  @Test
  public void getRuleById(){
    assertTrue(TestHelper.getRuleId() != null);

    Rule.ruleWithId(TestHelper.getRuleId()).toBlocking().subscribe(new Subscriber<Rule>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
        assertTrue(false);
      }

      @Override public void onNext(Rule rule) {
        assertTrue(rule.id() != null && rule.id().length() > 0);
        assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
        assertTrue(rule.object().type().length() > 0);
        assertTrue(rule.object().id().length() > 0);
      }
    });
  }

  @Test public void getRulesByUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.rules()
        .rulesForUrl(
            String.format("%sdevices/%s/rules", Endpoint.RULES.getUrl(), TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<Page<Rule>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(Page<Rule> rulePage) {
                assertTrue(rulePage.getItems().size() > 0);

                for(Rule rule : rulePage.getItems()){
                  assertTrue(rule.id() != null && rule.id().length() > 0);
                  assertTrue(rule.deviceId() != null && rule.deviceId().length() > 0);
                  assertTrue(rule.object().type().length() > 0);
                  assertTrue(rule.object().id().length() > 0);
                }
              }
            });
  }
}
