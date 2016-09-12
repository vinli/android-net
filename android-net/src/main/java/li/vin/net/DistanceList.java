package li.vin.net;

import android.os.Parcelable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@AutoParcel
public abstract class DistanceList implements VinliItem{

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(DistanceList.class, AutoParcelAdapter.create(AutoParcel_DistanceList.class));

    Distance.registerGson(gb);
  }

  public abstract List<Distance> distances();

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
  }

}
