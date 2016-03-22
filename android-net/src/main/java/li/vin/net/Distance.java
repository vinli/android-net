package li.vin.net;

import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Created by tbrown on 3/22/16.
 */

@AutoParcel
public abstract class Distance implements VinliItem{
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Distance>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Distance.class, AutoParcelAdapter.create(AutoParcel_Distance.class));
    gb.registerTypeAdapter(TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Distance.class));
  }

  public abstract Double confidenceMin();
  public abstract Double confidenceMax();
  public abstract Double value();
  public abstract String lastOdometerDate();
}
