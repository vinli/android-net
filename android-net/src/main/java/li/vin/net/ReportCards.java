package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface ReportCards {

  @GET("report_cards/{reportCardId}") Observable<Wrapped<ReportCard>> reportCard(
      @NonNull @Path("reportCardId") String reportCardId);

  @GET("vehicles/{vehicleId}/report_cards")
  Observable<TimeSeries<ReportCard>> reportCardsForVehicle(
      @NonNull @Path("vehicleId") String vehicleId, @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until, @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("devices/{deviceId}/report_cards") Observable<TimeSeries<ReportCard>> reportCardsForDevice(
      @NonNull @Path("deviceId") String deviceId, @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until, @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("devices/{deviceId}/report_cards/overall")
  Observable<ReportCard.OverallReportCard> overallReportCardForDevice(
      @NonNull @Path("deviceId") String deviceId);

  @GET("trips/{tripId}/report_cards/_current") Observable<Wrapped<ReportCard>> reportCardForTrip(
      @NonNull @Path("tripId") String tripId);

  @GET Observable<TimeSeries<ReportCard>> reportCardsForUrl(@NonNull @Url String url);
}
