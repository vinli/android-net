package li.vin.net;

import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Subscription implements VinliItem {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Subscription>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(Subscription.class, AutoParcelAdapter.create(AutoParcel_Subscription.class));
    gb.registerTypeAdapter(Links.class, AutoParcelAdapter.create(AutoParcel_Subscription_Links.class));
    gb.registerTypeAdapter(PAGE_TYPE, Page.Adapter.create(PAGE_TYPE, Subscription.class));
  }

  /*package*/ static final Builder builder() {
    return new AutoParcel_Subscription.Builder();
  }

  public abstract String deviceId();
  public abstract String eventType();
  public abstract String url();
  @Nullable public abstract ObjectRef object();
  public abstract String appData();
  public abstract String createdAt();
  public abstract String updatedAt();

  /*package*/ abstract VinliApp app();
  /*package*/ abstract Links links();

  /*package*/ Subscription() { }

  @AutoParcel
  /*package*/ static abstract class Links {
    public abstract String self();
    public abstract String notifications();

    /*package*/ Links() { }

    @AutoParcel.Builder
    interface Builder {
      Builder self(String s);
      Builder notifications(String s);

      Links build();
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder deviceId(String s);
    Builder eventType(String s);
    Builder url(String s);
    Builder object(ObjectRef o);
    Builder appData(String s);
    Builder createdAt(String s);
    Builder updatedAt(String s);

    Builder app(VinliApp app);
    Builder links(Links l);

    Subscription build();
  }
}
