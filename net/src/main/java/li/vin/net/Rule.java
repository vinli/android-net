package li.vin.net;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class Rule implements VinliItem, Parcelable {
  /*package*/ static final Type PAGE_TYPE = new TypeToken<Page<Rule>>() { }.getType();

  /*package*/ static final Builder builder() {
    return new AutoParcel_Rule.Builder();
  }

  public abstract String name();
  public abstract boolean evaluated();
  public abstract boolean covered();
  public abstract String createdAt();
  public abstract String deviceId();

  /*package*/ abstract Links links();

  /*package*/ Rule() { }

  @AutoParcel
  /*package*/ static abstract class Links {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_Links.Builder();
    }

    public abstract String self();
    public abstract String events();
    public abstract String subscriptions();

    /*package*/ Links() { }

    @AutoParcel.Builder
    interface Builder {
      Builder self(String s);
      Builder events(String s);
      Builder subscriptions(String s);
      Links build();
    }
  }

  @AutoParcel.Builder
  /*package*/ interface Builder {
    Builder id(String s);
    Builder name(String s);
    Builder evaluated(boolean b);
    Builder covered(boolean b);
    Builder createdAt(String s);
    Builder deviceId(String s);
    Builder links(Links l);

    Rule build();
  }

  @AutoParcel
  public static abstract class ParametricBoundary implements VinliItem, Parcelable {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_ParametricBoundary.Builder();
    }

    public abstract String parameter();
    @Nullable public abstract Float min();
    @Nullable public abstract Float max();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder id(String s);
      Builder parameter(String s);
      Builder min(Float f);
      Builder max(Float f);

      ParametricBoundary build();
    }

    /*package*/ ParametricBoundary() { }
  }

  @AutoParcel
  public static abstract class RadiusBoundary implements VinliItem, Parcelable {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_RadiusBoundary.Builder();
    }

    public abstract float radius();
    public abstract float lon();
    public abstract float lat();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder id(String s);
      Builder radius(float f);
      Builder lon(float f);
      Builder lat(float f);

      RadiusBoundary build();
    }

    /*package*/ RadiusBoundary() { }
  }

  @AutoParcel
  public static abstract class Coordinate {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_Coordinate.Builder();
    }

    public abstract float lon();
    public abstract float lat();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder lon(float f);
      Builder lat(float f);

      Coordinate build();
    }

    /*package*/ Coordinate() { }
  }

  @AutoParcel
  public static abstract class PolygonBoundary implements VinliItem, Parcelable {
    /*package*/ static final Builder builder() {
      return new AutoParcel_Rule_PolygonBoundary.Builder();
    }

    public abstract List<Coordinate> coordinates();

    @AutoParcel.Builder
    /*package*/ interface Builder {
      Builder id(String s);
      Builder coordinates(List<Coordinate> l);

      PolygonBoundary build();
    }

    /*package*/ PolygonBoundary() { }
  }
}
