package upb.dice.twitter;

import twitter4j.GeoLocation;

/**
 * Location defined by latitude, longitude and radius of coverage
 */
public class GeoPosition {
    private Double latitude;
    private Double longitude;
    private Double radius;

    /**
     * Constructor for the Position
     * @param latitude
     * @param longitude
     * @param radius
     */
    public GeoPosition(Double latitude, Double longitude, Double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public GeoLocation getLocation(){
        return new GeoLocation(latitude, longitude);
    }

    @Override
    public String toString(){
        return (" Position is: "+"Latitude: "+ latitude +".Longitude: "+longitude + ". Radius: "+radius);
    }
}
