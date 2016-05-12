package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface Messages {

  @GET("/devices/{deviceId}/messages")
  Observable<TimeSeries<Message>> messages(
    @NonNull @Path("deviceId") String deviceId,
    @Nullable @Query("since") Date since,
    @Nullable @Query("until") Date until,
    @Nullable @Query("limit") Integer limit);

  @GET("/messages/{messageId}")
  Observable<Wrapped<Message>> message(
    @NonNull @Path("messageId") String messageId);
}
