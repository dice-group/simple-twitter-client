package upb.dice.twitter;

import twitter4j.GeoQuery;
import twitter4j.Query;
import java.io.IOException;

/**
 * Generates a query based on the location details provided and the
 * by considering the current state of the query
 */
public class LocationQuery extends QueryGenerator {
    private GeoQuery geoQuery;
    private double radius;

    /**
     * Constructor which sets the geeoquer and the radiius
     * @param geoQuery for which the query should be generated
     * @param radius around the given location
     */
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
