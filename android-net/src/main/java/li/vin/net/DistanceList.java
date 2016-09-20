package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import rx.Observable;

@AutoParcel
public abstract class DistanceList implements VinliItem{

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(DistanceList.class, AutoParcelAdapter.create(AutoParcel_DistanceList.class));

    Distance.registerGson(gb);
  }

  public abstract List<Distance> distances();

  public static Observable<DistanceList> distancesWithVehicleId(@NonNull String vehicleId) {
    return distancesWithVehicleId(vehicleId, null, null, null);
  }

  public static Observable<DistanceList> distancesWithVehicleId(@NonNull String vehicleId,
      @Nullable Date since, @Nullable Date until, @Nullable DistanceUnit unit) {
    Long sinceMs = since == null ? null : since.getTime();
    Long untilMs = until == null ? null : until.getTime();
    return Vinli.curApp()
        .distances()
        .distances(vehicleId, sinceMs, untilMs, (unit == null) ? null : unit.getDistanceUnitStr());
  }

  @AutoParcel
  public static abstract class Distance implements Parcelable{
    /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Distance>>() { }.getType();

    /*package*/ static final void registerGson(GsonBuilder gb) {
      gb.registerTypeAdapter(Distance.class, AutoParcelAdapter.create(AutoParcel_DistanceList_Distance.class));
      gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(Distance.class));
    }

    public abstract Double confidenceMin();
    public abstract Double confidenceMax();
    public abstract Double value();
    public abstract String lastOdometerDate();

    public static Observable<Distance> bestDistanceWithVehicleId(@NonNull String vehicleId) {
      return bestDistanceWithVehicleId(vehicleId, null);
    }

    public static Observable<Distance> bestDistanceWithVehicleId(@NonNull String vehicleId,
        @Nullable DistanceUnit unit) {
      return Vinli.curApp()
          .distances()
          .bestDistance(vehicleId, (unit == null) ? null : unit.getDistanceUnitStr())
          .map(Wrapped.<Distance>pluckItem());
    }
  }

}
