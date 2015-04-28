package li.vin.net;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created by ottowagner on 6/27/14.
 */
public final class Vehicle implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Vehicle>>() { }.getType();

  private final String id;
  private final String make;
  private final String model;
  private final String year;
  private final String trim;
  private final String vin;

  /*package*/ Vehicle(String id, String make, String model, String year, String trim, String vin) {
    this.id = id;
    this.make = make;
    this.model = model;
    this.year = year;
    this.trim = trim;
    this.vin = vin;
  }

  private Vehicle(Parcel in) {
    id = in.readString();
    make = in.readString();
    model = in.readString();
    year = in.readString();
    trim = in.readString();
    vin = in.readString();
  }

  @Override public String getId() {
    return id;
  }

  public String getModel() {
    return model;
  }

  public String getMake() {
    return make;
  }

  public String getYear() {
    return year;
  }

  public String getTrim() {
    return trim;
  }

  public String getVin() {
    return vin;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeString(id);
    out.writeString(make);
    out.writeString(model);
    out.writeString(year);
    out.writeString(trim);
    out.writeString(vin);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Vehicle)) {
      return false;
    }

    final Vehicle v = (Vehicle) o;

    return v.id.equals(this.id)
       && v.make.equals(this.make)
       && v.model.equals(this.model)
       && v.year.equals(this.year)
       && v.trim.equals(this.trim)
       && v.vin.equals(this.vin);
  }

  @Override public String toString() {
    return super.toString() + '{'
       + "id=" + id
       + " make=" + make
       + " model=" + model
       + " year=" + year
       + " trim=" + trim
       + " vin=" + vin
       + '}';
  }

  @Override public int hashCode() {
    return Arrays.hashCode(new String[]{id, make, model, year, trim, vin});
  }

  public static final Parcelable.Creator<Vehicle> CREATOR = new Parcelable.Creator<Vehicle>() {
    @Override public Vehicle createFromParcel(Parcel in) {
      return new Vehicle(in);
    }

    @Override public Vehicle[] newArray(int size) {
      return new Vehicle[size];
    }
  };
}
