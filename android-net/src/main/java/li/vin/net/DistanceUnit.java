package li.vin.net;

/**
 * Created by tbrown on 3/22/16.
 */
public enum DistanceUnit {
  KILOMETERS("km"),
  METERS("m"),
  MILES("mi");

  private String unitStr;

  private DistanceUnit(String unit){
    this.unitStr = unit;
  }

  public String getDistanceUnitStr(){
    return this.unitStr;
  }

}
