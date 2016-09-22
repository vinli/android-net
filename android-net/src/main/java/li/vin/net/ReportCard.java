package li.vin.net;

import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import java.util.Date;
import rx.Observable;

@AutoParcel public abstract class ReportCard implements VinliItem {
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<ReportCard>>() {
  }.getType();
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<ReportCard>>() {
  }.getType();

  /*package*/
  static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(ReportCard.class, AutoParcelAdapter.create(AutoParcel_ReportCard.class));
    gb.registerTypeAdapter(OverallReportCard.class,
        AutoParcelAdapter.create(AutoParcel_ReportCard_OverallReportCard.class));
    gb.registerTypeAdapter(OverallReportCard.InnerReportCard.class,
        AutoParcelAdapter.create(AutoParcel_ReportCard_OverallReportCard_InnerReportCard.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE,
        TimeSeries.Adapter.create(TIME_SERIES_TYPE, ReportCard.class, "reportCards"));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(ReportCard.class, "reportCard"));
  }

  @NonNull public abstract String deviceId();

  @NonNull public abstract String vehicleId();

  @NonNull public abstract String tripId();

  @NonNull public abstract String grade();

  public static Observable<ReportCard> reportCardWithId(@NonNull String reportCardId) {
    return Vinli.curApp().reportCard(reportCardId);
  }

  public static Observable<TimeSeries<ReportCard>> reportCardsWithDeviceId(
      @NonNull String deviceId) {
    return reportCardsWithDeviceId(deviceId, (Long) null, null, null, null);
  }

  public static Observable<TimeSeries<ReportCard>> reportCardsWithDeviceId(@NonNull String deviceId,
      @Nullable Long sinceMs, @Nullable Long untilMs, @Nullable Integer limit,
      @Nullable String sortDir) {
    return Vinli.curApp()
        .reportCards()
        .reportCardsForDevice(deviceId, sinceMs, untilMs, limit, sortDir);
  }

  @Deprecated
  public static Observable<TimeSeries<ReportCard>> reportCardsWithDeviceId(@NonNull String deviceId,
      @Nullable Date since, @Nullable Date until, @Nullable Integer limit,
      @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp()
        .reportCards()
        .reportCardsForDevice(deviceId, sinceMs, untilMs, limit, sortDir);
  }

  public static Observable<TimeSeries<ReportCard>> reportCardsWithVehicleId(
      @NonNull String vehicleId) {
    return reportCardsWithVehicleId(vehicleId, null, null, null, null);
  }

  public static Observable<TimeSeries<ReportCard>> reportCardsWithVehicleId(
      @NonNull String vehicleId, @Nullable Long sinceMs, @Nullable Long untilMs,
      @Nullable Integer limit, @Nullable String sortDir) {
    return Vinli.curApp()
        .reportCards()
        .reportCardsForVehicle(vehicleId, sinceMs, untilMs, limit, sortDir);
  }

  @Deprecated
  public static Observable<TimeSeries<ReportCard>> reportCardWithVehicleId(
      @NonNull String vehicleId, @Nullable Date since, @Nullable Date until,
      @Nullable Integer limit, @Nullable String sortDir) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp()
        .reportCards()
        .reportCardsForVehicle(vehicleId, sinceMs, untilMs, limit, sortDir);
  }

  public static Observable<ReportCard> reportCardWithTripId(@NonNull String tripId) {
    return Vinli.curApp()
        .reportCards()
        .reportCardForTrip(tripId)
        .map(Wrapped.<ReportCard>pluckItem());
  }

  @AutoParcel public static abstract class OverallReportCard {

    @NonNull public abstract Integer tripSampleSize();

    @Nullable public abstract LinkedTreeMap<String, String> gradeCount();

    @NonNull /*package*/ abstract InnerReportCard reportCard();

    public String overallGrade() {
      return reportCard().overallGrade();
    }

    @AutoParcel
    /*package*/ static abstract class InnerReportCard {
      @NonNull public abstract String overallGrade();

      /*package*/ InnerReportCard() {
      }
    }

    public static Observable<OverallReportCard> overallReportCardForDevice(
        @NonNull String deviceId) {
      return Vinli.curApp().reportCards().overallReportCardForDevice(deviceId);
    }
  }
}
