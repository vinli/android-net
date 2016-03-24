package li.vin.net;

public enum DistanceUnit {
  KILOMETERS("km"),
  METERS("m"),
  MILES("mi");

  private String unitStr;

  private DistanceUnit(String unit){
    this.unitStr = unit;
  }

  /*package*/ String getDistanceUnitStr(){
    return this.unitStr;
  }

}
