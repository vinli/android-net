package li.vin.net;

import java.io.Serializable;

/**
 * Created by kyle on 6/22/14.
 */
public class Dtc implements Serializable {


  private String codeType;
  private String id;
  private String longdescription;
  private String name;


  public Dtc() {
  }

  public Dtc(String codeType, String id, String longdescription, String name) {
    super();
    this.codeType = codeType;
    this.id = id;
    this.longdescription = longdescription;
    this.name = name;
  }


  public String toString() {
    return "DTC [codeType=" + codeType + ", id=" + id + ", long_description=" + longdescription + ", name=" + name + "]";
  }

  public String getCodeType() {
    return codeType;
  }

  public String getId() {
    return id;
  }

  public String getLongdescription() {
    return longdescription;
  }

  public String getName() {
    return name;
  }
}
