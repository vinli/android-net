package li.vin.net;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import auto.parcel.AutoParcel;
import rx.Observable;

@AutoParcel
public abstract class Device implements VinliItem {
  /*package*/ static final void registerGson(GsonBuilder gb, VinliApp app) {
    final DeviceAdapter adapter = DeviceAdapter.create(app);

    gb.registerTypeAdapter(Device.class, WrappedJsonAdapter.create(Device.class, adapter));

    final Type type = new TypeToken<Page<Device>>() { }.getType();
    gb.registerTypeAdapter(type, PageAdapter.create(adapter, type, app, Device.class));
  }

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
    return app().rules().forDevice(this.id(), null, null);
  }

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();
    public abstract String rules();
    public abstract String vehicles();
    public abstract String latestVehicle();

    /*package*/ Links() { }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);

    Builder app(VinliApp app);
    Builder links(Links l);

    Device build();
  }

  private static final class DeviceAdapter extends TypeAdapter<Device> {

    public static final DeviceAdapter create(VinliApp app) {
      return new DeviceAdapter(app);
    }

    private final VinliApp mApp;

    private DeviceAdapter(VinliApp app) {
      mApp = app;
    }

    @Override public void write(JsonWriter out, Device value) throws IOException {
      out.beginObject();
        out.name("id").value(value.id());
      out.endObject();
    }

    @Override public Device read(JsonReader in) throws IOException {
      final Device.Builder b = Device.builder();
      b.app(mApp);

      in.beginObject();
        while (in.hasNext()) {
          final String name = in.nextName();

          switch (name) {
            case "id": b.id(in.nextString()); break;
            case "links": b.links(mApp.gson().<Device.Links>fromJson(in, AutoParcel_Device_Links.class)); break;
            default: throw new IOException("unknown device key " + name);
          }
        }
      in.endObject();

      return b.build();
    }
  }
}
