package li.vin.net;

import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;

/**
 * Created by tbrown on 3/22/16.
 */

@AutoParcel
public abstract class Distance implements VinliItem{

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Distance.class, AutoParcelAdapter.create(AutoParcel_Distance.class));
  }

  public abstract Double confidenceMin();
  public abstract Double confidenceMax();
  public abstract Double value();
  public abstract String lastOdometerDate();
}
