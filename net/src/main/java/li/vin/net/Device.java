package li.vin.net;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;

import rx.Observable;

public final class Device implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Device>>() { }.getType();

  private final String id;
  private final String chipId;
  private final String caseId;
  private final Links links;

  /*package*/ Device(String id, String chipId, String caseId, Links links) {
    this.id = id;
    this.chipId = chipId;
    this.caseId = caseId;
    this.links = links;
  }

  private Device(Parcel in) {
    id = in.readString();
    chipId = in.readString();
    caseId = in.readString();
    links = new Links(in.readString(), in.readString(), in.readString(), in.readString());
  }

  @Override public String getId() {
    return id;
  }

  public String getChipId() {
    return chipId;
  }

  public String getCaseId() {
    return caseId;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeString(id);
    out.writeString(chipId);
    out.writeString(caseId);
    out.writeString(links.self);
    out.writeString(links.groups);
    out.writeString(links.vehicles);
    out.writeString(links.latestVehicle);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Device)) {
      return false;
    }

    final Device d = (Device) o;

    return d.id.equals(this.id)
       && d.chipId.equals(this.chipId)
       && d.caseId.equals(this.caseId);
  }

  @Override public String toString() {
    return super.toString() + '{'
       + "id=" + id
       + " chipdId=" + chipId
       + " caseId=" + caseId
    + '}';
  }

  @Override public int hashCode() {
    return Arrays.hashCode(new String[] {id, chipId, caseId});
  }

  public Observable<Page<Vehicle>> loadVehicles(VinliApp app) {
    return app.getLinkLoader().loadPage(links.vehicles, Vehicle.PAGE_TYPE);
  }

  public Observable<Vehicle> loadLatestVehicle(VinliApp app) {
    return app.getLinkLoader().loadItem(links.latestVehicle, Vehicle.class);
  }

  /*package*/ static final class Links {
    public final String self, groups, vehicles, latestVehicle;

    public Links(String self, String groups, String vehicles, String latestVehicle) {
      this.self = self;
      this.groups = groups;
      this.vehicles = vehicles;
      this.latestVehicle = latestVehicle;
    }
  }

  public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
    @Override public Device createFromParcel(Parcel in) {
      return new Device(in);
    }

    @Override public Device[] newArray(int size) {
      return new Device[size];
    }
  };
}
