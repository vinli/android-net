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

  /*package*/ static DistanceUnit parse(String str) {
    if ("km".equals(str)) return KILOMETERS;
    if ("m".equals(str)) return METERS;
    if ("mi".equals(str)) return MILES;
    return null;
  }

}
