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

  /*package*/ abstract VinliApp app();
  /*package*/ abstract Links links();

  /*package*/ Device() { }

  public Observable<Page<Vehicle>> vehicles() {
    return app().getLinkLoader().loadPage(links().vehicles(), Vehicle.PAGE_TYPE);
  }

  public Observable<Vehicle> latestVehicle() {
    return app().getLinkLoader().loadItem(links().latestVehicle(), Vehicle.class);
  }

  public Observable<Page<Rule>> rules() {
    return app().getLinkLoader().loadPage(links().rules(), Rule.PAGE_TYPE);
  }

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

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder app(VinliApp app);
    Builder id(String s);
    Builder links(Links l);

    Device build();
  }
}
