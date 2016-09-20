package li.vin.net;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface Distances {

  @GET("/vehicles/{vehicleId}/distances")
  Observable<DistanceList> distances(
      @Path("vehicleId") String vehicleId,
      @Query("since") Long since,
      @Query("until") Long until,
      @Header("x-vinli-unit") String unit);

  @GET("/vehicles/{vehicleId}/distances/_best")
  Observable<Wrapped<DistanceList.Distance>> bestDistance(
      @Path("vehicleId") String vehicleId,
      @Header("x-vinli-unit") String unit);

  @POST("/vehicles/{vehicleId}/odometers")
  Observable<Wrapped<Odometer>> createOdometerReport(
      @Path("vehicleId") String vehicleId,
      @Body Odometer.Seed odometerSeed);

  @GET("/vehicles/{vehicleId}/odometers")
  Observable<TimeSeries<Odometer>> odometerReports(
      @Path("vehicleId") String vehicleId,
      @Query("since") Long since,
      @Query("until") Long until,
      @Query("limit") Integer limit,
      @Query("sortDir") String sortDir);

  @GET("/odometers/{odometerId}")
  Observable<Wrapped<Odometer>> odometerReport(
      @Path("odometerId") String odometerId);

  @DELETE("/odometers/{odometerId}")
  Observable<Void> deleteOdometerReport(
      @Path("odometerId") String odometerId);

  @POST("/vehicles/{vehicleId}/odometer_triggers")
  Observable<Wrapped<OdometerTrigger>> createOdometerTrigger(
      @Path("vehicleId") String vehicleId,
      @Body OdometerTrigger.Seed odometerTrigger);

  @GET("/odometer_triggers/{odometerTriggerId}")
  Observable<Wrapped<OdometerTrigger>> odometerTrigger(
      @Path("odometerTriggerId") String odometerTriggerId);

  @DELETE("/odometer_triggers/{odometerTriggerId}")
  Observable<Void> deleteOdometerTrigger(
      @Path("odometerTriggerId") String odometerTriggerId);

  @GET("/vehicles/{vehicleId}/odometer_triggers")
  Observable<TimeSeries<OdometerTrigger>> odometerTriggers(
      @Path("vehicleId") String vehicleId,
      @Query("since") Long since,
      @Query("until") Long until,
      @Query("limit") Integer limit,
      @Query("sortDir") String sortDir);
}
