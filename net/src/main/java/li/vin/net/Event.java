package li.vin.net;

import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Event implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Event>>() { }.getType();

  public abstract String eventType();


  /*package*/ abstract VinliApp app();
  /*package*/ abstract Links links();

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();
    public abstract String rules();
    public abstract String vehicles();
    public abstract String latestVehicle();

    /*package*/ Links() { }

    @AutoParcel.Builder
    interface Builder {
      Builder self(String s);
      Builder rules(String s);
      Builder vehicles(String s);
      Builder latestVehicle(String s);

      Links build();
    }
  }
}
