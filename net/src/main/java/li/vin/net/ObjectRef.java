package li.vin.net;

import android.os.Parcelable;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ObjectRef implements Parcelable {
  public abstract String id();
  public abstract String type();

  /*package*/ ObjectRef() { }
}
