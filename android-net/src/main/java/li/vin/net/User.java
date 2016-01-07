package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class User implements VinliItem {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<User>>() { }.getType();

  /*package*/ static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(User.class, AutoParcelAdapter.create(AutoParcel_User.class));
    gb.registerTypeAdapter(Settings.class, AutoParcelAdapter.create(AutoParcel_User_Settings.class));
    gb.registerTypeAdapter(WRAPPED_TYPE, Wrapped.Adapter.create(User.class));
  }

  @NonNull public abstract String firstName();
  @NonNull public abstract String lastName();
  @NonNull public abstract String email();
  @Nullable public abstract String image();
  @NonNull public abstract String phone();
  @NonNull public abstract String createdAt();
  @Nullable public abstract Settings settings();

  /*package*/ User() { }

  @AutoParcel
  public static abstract class Settings implements Parcelable {
    @Nullable public abstract String unit();

    /*package*/ Settings() { }
  }
}
