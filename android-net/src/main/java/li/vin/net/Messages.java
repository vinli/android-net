package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Messages {

  @GET("devices/{deviceId}/messages")
  Observable<TimeSeries<Message>> messages(
    @NonNull @Path("deviceId") String deviceId,
    @Nullable @Query("since") Long since,
    @Nullable @Query("until") Long until,
    @Nullable @Query("limit") Integer limit,
    @Nullable @Query("sortDir") String sortDir);


  @GET("vehicles/{vehicleId}/messages")
  Observable<TimeSeries<Message>> vehicleMessages(
      @NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("messages/{messageId}")
  Observable<Wrapped<Message>> message(
    @NonNull @Path("messageId") String messageId);

  @GET Observable<TimeSeries<Message>> messagesForUrl(@NonNull @Url String url);
}
