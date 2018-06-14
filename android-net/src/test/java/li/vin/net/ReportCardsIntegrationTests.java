package li.vin.net;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 22)
public class ReportCardsIntegrationTests {

  public VinliApp vinliApp;

  @Before public void setup() {
    assertTrue(TestHelper.getAccessToken() != null);

    vinliApp = TestHelper.getVinliApp();
  }

  @Test public void testGetReportCardById() {
    assertTrue(TestHelper.getReportCardId() != null);

    ReportCard.reportCardWithId(TestHelper.getReportCardId())
        .toBlocking()
        .subscribe(new Subscriber<ReportCard>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(ReportCard reportCard) {
            assertTrue(reportCard.id() != null && reportCard.id().length() > 0);
            assertTrue(reportCard.deviceId() != null && reportCard.deviceId().length() > 0);
            assertTrue(reportCard.vehicleId() != null && reportCard.vehicleId().length() > 0);
            assertTrue(reportCard.tripId() != null && reportCard.tripId().length() > 0);
            assertTrue(reportCard.grade() != null && reportCard.grade().length() > 0);
          }
        });
  }

  @Test public void testGetReportCardsForVehicle() {
    assertTrue(TestHelper.getVehicleId() != null);

    ReportCard.reportCardsWithVehicleId(TestHelper.getVehicleId(), null, null, null, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<ReportCard>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<ReportCard> reportCardTimeSeries) {
            assertTrue(reportCardTimeSeries.getItems().size() > 0);

            for (ReportCard reportCard : reportCardTimeSeries.getItems()) {
              assertTrue(reportCard.id() != null && reportCard.id().length() > 0);
              assertTrue(reportCard.deviceId() != null && reportCard.deviceId().length() > 0);
              assertTrue(reportCard.vehicleId() != null && reportCard.vehicleId().length() > 0);
              assertTrue(reportCard.tripId() != null && reportCard.tripId().length() > 0);
              assertTrue(reportCard.grade() != null && reportCard.grade().length() > 0);
            }
          }
        });
  }

  @Test public void testGetReportCardsForDevice() {
    assertTrue(TestHelper.getDeviceId() != null);

    ReportCard.reportCardsWithDeviceId(TestHelper.getDeviceId(), (Long) null, null, 1, null)
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<ReportCard>>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(TimeSeries<ReportCard> reportCardTimeSeries) {
            assertTrue(reportCardTimeSeries.getItems().size() > 0);

            for (ReportCard reportCard : reportCardTimeSeries.getItems()) {
              assertTrue(reportCard.id() != null && reportCard.id().length() > 0);
              assertTrue(reportCard.deviceId() != null && reportCard.deviceId().length() > 0);
              assertTrue(reportCard.vehicleId() != null && reportCard.vehicleId().length() > 0);
              assertTrue(reportCard.tripId() != null && reportCard.tripId().length() > 0);
              assertTrue(reportCard.grade() != null && reportCard.grade().length() > 0);
            }

            if (reportCardTimeSeries.hasPrior()) {
              reportCardTimeSeries.loadPrior()
                  .toBlocking().subscribe(new Subscriber<TimeSeries<ReportCard>>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {
                  e.printStackTrace();
                  assertTrue(false);
                }

                @Override public void onNext(TimeSeries<ReportCard> reportCardTimeSeries) {
                  assertTrue(reportCardTimeSeries.getItems().size() > 0);

                  for (ReportCard reportCard : reportCardTimeSeries.getItems()) {
                    assertTrue(reportCard.id() != null && reportCard.id().length() > 0);
                    assertTrue(reportCard.deviceId() != null && reportCard.deviceId().length() > 0);
                    assertTrue(reportCard.vehicleId() != null && reportCard.vehicleId().length() > 0);
                    assertTrue(reportCard.tripId() != null && reportCard.tripId().length() > 0);
                    assertTrue(reportCard.grade() != null && reportCard.grade().length() > 0);
                  }
                }
              });
            }
          }
        });
  }

  @Test public void testGetReportCardForTrip() {
    assertTrue(TestHelper.getTripId() != null);

    ReportCard.reportCardWithTripId(TestHelper.getTripId())
        .toBlocking()
        .subscribe(new Subscriber<ReportCard>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(ReportCard reportCard) {
            assertTrue(reportCard.id() != null && reportCard.id().length() > 0);
            assertTrue(reportCard.deviceId() != null && reportCard.deviceId().length() > 0);
            assertTrue(reportCard.vehicleId() != null && reportCard.vehicleId().length() > 0);
            assertTrue(reportCard.tripId() != null && reportCard.tripId().length() > 0);
            assertTrue(reportCard.grade() != null && reportCard.grade().length() > 0);
          }
        });
  }

  @Test public void testOverallReportCard() {
    assertTrue(TestHelper.getDeviceId() != null);

    ReportCard.OverallReportCard.overallReportCardForDevice(TestHelper.getDeviceId())
        .toBlocking()
        .subscribe(new Subscriber<ReportCard.OverallReportCard>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
            assertTrue(false);
          }

          @Override public void onNext(ReportCard.OverallReportCard overallReportCard) {
            assertTrue(overallReportCard.overallGrade() != null
                && overallReportCard.overallGrade().length() > 0);
            assertTrue(overallReportCard.tripSampleSize() != null);
            assertTrue(overallReportCard.gradeCount().keySet().size() > 0);
          }
        });
  }

  @Test public void getReportCardsWithUrl() {
    assertTrue(TestHelper.getDeviceId() != null);

    vinliApp.reportCards()
            .reportCardsForUrl(String.format("%sdevices/%s/report_cards", VinliEndpoint.BEHAVIORAL.getUrl(),
            TestHelper.getDeviceId()))
        .toBlocking()
        .subscribe(new Subscriber<TimeSeries<ReportCard>>() {
              @Override public void onCompleted() {

              }

              @Override public void onError(Throwable e) {
                e.printStackTrace();
                assertTrue(false);
              }

              @Override public void onNext(TimeSeries<ReportCard> reportCardTimeSeries) {
                assertTrue(reportCardTimeSeries.getItems().size() > 0);

                for (ReportCard reportCard : reportCardTimeSeries.getItems()) {
                  assertTrue(reportCard.id() != null && reportCard.id().length() > 0);
                  assertTrue(reportCard.deviceId() != null && reportCard.deviceId().length() > 0);
                  assertTrue(reportCard.vehicleId() != null && reportCard.vehicleId().length() > 0);
                  assertTrue(reportCard.tripId() != null && reportCard.tripId().length() > 0);
                  assertTrue(reportCard.grade() != null && reportCard.grade().length() > 0);
                }
              }
            });
  }
}
