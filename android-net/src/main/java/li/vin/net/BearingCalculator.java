package li.vin.net;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import li.vin.net.Coordinate;

/**
 * Created by tommy on 6/7/16.
 */
/*package*/ class BearingCalculator {

  private static final double DISTANCE_THRESHOLD = 0.00025;

  private BearingFilter bearingFilter;
  private Coordinate previousLatLng;

  public BearingCalculator(){
    bearingFilter = new BearingFilter();
    previousLatLng = null;
  }

  public void addCoordinate(Coordinate coordinate, String timestamp){
    if(previousLatLng == null){
      previousLatLng = coordinate;
      return;
    }

    double distance = Math.sqrt(Math.pow(coordinate.lat() - previousLatLng.lat(), 2) + Math.pow(coordinate.lon()- previousLatLng.lon(), 2));
    if(distance > DISTANCE_THRESHOLD){
      calcBearing(timestamp, coordinate, previousLatLng);
      previousLatLng = coordinate;
    }
  }

  private void calcBearing(String newTimestamp, Coordinate newCoord, Coordinate prevCoord){
    double dLat = newCoord.lat() - prevCoord.lat();
    double dLon = newCoord.lon() - prevCoord.lon();

    double bearing = Math.atan2(Math.abs(dLat), Math.abs(dLon));
    bearing = Math.toDegrees(bearing);

    if(dLat > 0 && dLon == 0.0){
      bearing = 0.0;
    }else if(dLat == 0.0 && dLon > 0.0){
      bearing = 90.0;
    }else if(dLat < 0.0 && dLon == 0){
      bearing = 180;
    }else if(dLat == 0.0 && dLon < 0.0){
      bearing = 270;
    }else if(dLat > 0.0 && dLon > 0.0){
      bearing = 90.0 - bearing;
    }else if(dLat > 0.0 && dLon < 0.0){
      bearing = bearing + 270;
    }else if(dLat < 0.0 && dLon > 0.0){
      bearing = bearing + 90;
    }else if(dLat < 0.0 && dLon < 0.0){
      bearing = 180 + (90 - bearing);
    }

    bearingFilter.addBearing(bearing, posixFromISO(newTimestamp));
  }

  public double currentBearing(){
    return bearingFilter.getFilteredBearing();
  }

  private static long posixFromISO(String isoDate){
    DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    Date date = null;

    try {
      date = fromFormat.parse(isoDate);
    } catch (ParseException e) {
      e.printStackTrace();
      return 0;
    }

    return date.getTime();
  }

  private static class BearingFilter {

    private static final int SIZE = 32;
    private static final long SHORTENED_TIME = 4000;

    private LinkedList<Bearing> bearingList;

    public BearingFilter(){
      bearingList = new LinkedList<>();
    }

    public void addBearing(double bearing, long posixTimestamp){
      bearingList.addLast(new Bearing(bearing, posixTimestamp));
      if(bearingList.size() > SIZE){
        bearingList.removeFirst();
      }
    }

    public double getFilteredBearing(){
      if(bearingList.size() == 0){
        return 0.0;
      }

      LinkedList<Bearing> recentBearings = new LinkedList<>();
      Bearing latestBearing = bearingList.getLast();
      for(Bearing bearing : bearingList){
        if((latestBearing.timestamp - bearing.timestamp) <= SHORTENED_TIME){
          recentBearings.addLast(bearing);
        }
      }

      double x = 0;
      double y = 0;
      Bearing previous = null;

      for(Bearing bearing : recentBearings){
        long timestampDiff = bearing.timestamp - recentBearings.getFirst().timestamp;
        if(timestampDiff == 0){
          timestampDiff = 1;
        }

        x += Math.cos(Math.toRadians(bearing.bearing)) * ((previous == null) ? 1 : timestampDiff);
        y += Math.sin(Math.toRadians(bearing.bearing)) * ((previous == null) ? 1 : timestampDiff);

        previous = bearing;
      }

      return Math.toDegrees(Math.atan2(y, x));
    }

    public static class Bearing{
      public double bearing;
      public long timestamp;

      public Bearing(double bearing, long timestamp){
        this.bearing = bearing;
        this.timestamp = timestamp;
      }
    }

  }

}