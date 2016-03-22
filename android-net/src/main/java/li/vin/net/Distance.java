package li.vin.net;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by tbrown on 3/22/16.
 */
public interface Distance {

  @GET("/vehicles/{vehicleId}/distances")
  Observable<Object> distances(@Field("vehicleId") String vehicleId);

  @POST("/vehicles/{vehicleId}/odometers")
  Observable<Object> createOdometerReport(@Field("vehicleId") String vehicleId, @Body Object odometer);

  @GET("/vehicles/{vehicleId}/odometers")
  Observable<Object> odometerReports(@Field("vehicleId") String vehicleId);

  @GET("/odometers/{odometerId}")
  Observable<Object> odometerReport(@Field("odometerId") String odometerId);

  @DELETE("/odometers/{odometerId}")
  Observable<Object> deleteOdometerReport(@Field("odometerId") String odometerId);

  @POST("/vehicles/{vehicleId}/odometer_triggers")
  Observable<Object> createOdometerTrigger(@Field("vehicleId") String vehicleId, @Body Object odometerTrigger);

  @GET("/odometer_triggers/{odometerTriggerId}")
  Observable<Object> odometerTrigger(@Field("odometerTriggerId") String odometerTriggerId);

  @DELETE("/odometer_triggers/{odometerTriggerId}")
  Observable<Object> deleteOdometerTrigger(@Field("odometerTriggerId") String odometerTriggerId);

  @GET("/vehicles/{vehicleId}/odometer_triggers")
  Observable<Object> odometerTriggers(@Field("vehicleId") String vehicleId);
}
