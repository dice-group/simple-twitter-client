package upb.dice.twitter;

import twitter4j.GeoQuery;
import twitter4j.Query;

import java.io.IOException;

public class LocationQuery extends QueryGenerator {
    GeoQuery geoQuery;
    double radius;
    public LocationQuery(GeoQuery geoQuery, double radius) {
        this.geoQuery = geoQuery;
        this.radius= radius;
    }
    @Override
    public Query generateQuery() throws IOException {
        query.setGeoCode(geoQuery.getLocation(), radius, Query.KILOMETERS);
        return getModifiedQuery(query);
    }
}
