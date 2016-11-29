package li.vin.net;

import android.os.Parcel;

/**
 * Created by JoshBeridon on 11/18/16.
 */
public class Dummy implements VinliItem {

  @Override public String id() {
    return null;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {

  }


}
