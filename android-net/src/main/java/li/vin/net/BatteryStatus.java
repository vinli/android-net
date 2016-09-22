package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import rx.Observable;

@AutoParcel public abstract class BatteryStatus implements Parcelable {
  /*package*/ static final Type WRAPPED_TYPE = new TypeToken<Wrapped<BatteryStatus>>() {
  }.getType();

  /*package*/
  static final void registerGson(GsonBuilder gb) {
    gb.registerTypeAdapter(BatteryStatus.class,
        AutoParcelAdapter.create(AutoParcel_BatteryStatus.class));
    gb.registerTypeAdapter(WRAPPED_TYPE,
        Wrapped.Adapter.create(BatteryStatus.class, "batteryStatus"));
  }

  @Nullable public abstract BatteryStatusColor status();

  @NonNull public abstract String timestamp();

  /*package*/ BatteryStatus() {
  }

  public static Observable<BatteryStatus> currentBatteryStatusForVehicle(
      @NonNull String vehicleId) {
    return Vinli.curApp()
        .diagnostics()
        .currentBatteryStatus(vehicleId)
        .map(Wrapped.<BatteryStatus>pluckItem());
  }

  public enum BatteryStatusColor {
    @SerializedName("green")GREEN, // indicates a battery voltage reading greater than 11.75 volts
    @SerializedName("yellow")YELLOW, // indicates a battery voltage reading less than 11.75 volts and greater than 11.31 volts
    @SerializedName("red")RED // indicates a batter voltage reading less than or equal to 11.31 volts
  }
}
