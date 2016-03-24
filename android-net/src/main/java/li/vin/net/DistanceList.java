package li.vin.net;

import android.os.Parcelable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import java.util.List;

/**
 * Created by tbrown on 3/22/16.
 */

@AutoParcel
public abstract class DistanceList implements VinliItem{

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(DistanceList.class, AutoParcelAdapter.create(AutoParcel_DistanceList.class));
    gb.registerTypeAdapter(Distance.class, AutoParcelAdapter.create(AutoParcel_DistanceList_Distance.class));
  }

  public abstract List<Distance> distances();

  @AutoParcel
  public static abstract class Distance implements Parcelable{
    public abstract Double confidenceMin();
    public abstract Double confidenceMax();
    public abstract Double value();
    public abstract String lastOdometerDate();
  }

}
