package li.vin.net;

import android.os.Parcelable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import rx.Observable;

@AutoParcel
public abstract class Dtc implements VinliItem {
  /*package*/ static final Type TIME_SERIES_TYPE = new TypeToken<TimeSeries<Dtc>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Dtc.class, AutoParcelAdapter.create(AutoParcel_Dtc.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Dtc_Links.class));
    gb.registerTypeAdapter(Code.class, AutoParcelAdapter.create(AutoParcel_Dtc_Code.class));
    gb.registerTypeAdapter(Code.TwoByte.class, AutoParcelAdapter.create(AutoParcel_Dtc_Code_TwoByte.class));
    gb.registerTypeAdapter(Code.ThreeByte.class, AutoParcelAdapter.create(AutoParcel_Dtc_Code_ThreeByte.class));
    gb.registerTypeAdapter(Code.WRAPPED_TYPE, Wrapped.Adapter.create(Code.class, "code"));
    gb.registerTypeAdapter(Code.PAGE_TYPE, Page.Adapter.create(Code.PAGE_TYPE, Code.class, "codes"));
    gb.registerTypeAdapter(
        TIME_SERIES_TYPE, TimeSeries.Adapter.create(TIME_SERIES_TYPE, Dtc.class, "codes"));
  }

  public abstract String start();
  public abstract String stop();
  public abstract String number();
  public abstract String vehicleId();
  public abstract String deviceId();
  public abstract String description();

  public Observable<Device> device() {
    return Vinli.curApp().device(deviceId());
  }

  public Observable<Vehicle> vehicle() {
    return Vinli.curApp().vehicle(vehicleId());
  }

  public Observable<Dtc.Code> diagnose() {
    return Vinli.curApp().diagnoseDtcCode(number());
  }

  /*package*/ abstract Links links();

  /*package*/ Dtc() { }

  @AutoParcel
  /*package*/ static abstract class Links implements Parcelable {
    public abstract String self();
    public abstract String device();
    public abstract String vehicle();

    /*package*/ Links() { }
  }

  @AutoParcel
  public static abstract class Code implements VinliItem {
    /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<Code>>() { }.getType();
    /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Code>>() { }.getType();

    public abstract String make();

    /*package*/ Code() { }

    @AutoParcel
    /*package*/ static abstract class TwoByte implements Parcelable {
      public abstract String number();
      public abstract String description();

      /*package*/ TwoByte() { }
    }

    @AutoParcel
    /*package*/ static abstract class ThreeByte implements Parcelable {
      public abstract String number();
      public abstract String ftb();
      public abstract String fault();
      public abstract String description();

      /*package*/ ThreeByte() { }
    }
  }
}

