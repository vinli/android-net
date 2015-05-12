package li.vin.net;

import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Device implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Device>>() { }.getType();

  /*package*/ static final Builder builder() {
    return new AutoParcel_Device.Builder();
  }

  public abstract String chipId();
  public abstract String caseId();
  /*package*/ abstract Links links();

  /*package*/ Device() { }

  public Observable<Page<Vehicle>> loadVehicles(VinliApp app) {
    return app.getLinkLoader().loadPage(links().vehicles(), Vehicle.PAGE_TYPE);
  }

  public Observable<Vehicle> loadLatestVehicle(VinliApp app) {
    return app.getLinkLoader().loadItem(links().latestVehicle(), Vehicle.class);
  }

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();
    public abstract String groups();
    public abstract String vehicles();
    public abstract String latestVehicle();

    /*package*/ Links() { }

    @AutoParcel.Builder
    interface Builder {
      Builder self(String s);
      Builder groups(String s);
      Builder vehicles(String s);
      Builder latestVehicle(String s);
      Links build();
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder chipId(String s);
    Builder caseId(String s);
    Builder links(Links l);
    Device build();
  }
}
