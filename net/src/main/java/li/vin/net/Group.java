package li.vin.net;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;

import rx.Observable;

/**
 * Created by kyle on 7/10/14.
 */
public final class Group implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Group>>() { }.getType();

  private final String id, name;
  private final Links links;

  /*package*/ Group(String id, String name, Links links) {
    this.id = id;
    this.name = name;
    this.links = links;
  }

  private Group(Parcel in) {
    id = in.readString();
    name = in.readString();
    links = new Links(in.readString());
  }

  @Override public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    out.writeString(id);
    out.writeString(name);
    out.writeString(links.devices);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Group)) {
      return false;
    }

    final Group g = (Group) o;

    return g.id.equals(this.id)
      && g.name.equals(this.name);
  }

  @Override public String toString() {
    return super.toString() + '{'
      + "id=" + id
      + " name=" + name
      + '}';
  }

  @Override public int hashCode() {
    return Arrays.hashCode(new String[]{id, name});
  }

  public Observable<Page<Device>> loadDevices(VinliApp app) {
    return app.getLinkLoader().loadPage(links.devices, Device.PAGE_TYPE);
  }

  /*package*/ static final class Links {
    public final String devices;

    public Links(String devices) {
      this.devices = devices;
    }
  }

  public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
    @Override public Group createFromParcel(Parcel in) {
      return new Group(in);
    }

    @Override public Group[] newArray(int size) {
      return new Group[size];
    }
  };
}
