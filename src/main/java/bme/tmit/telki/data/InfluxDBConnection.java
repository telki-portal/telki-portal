package bme.tmit.telki.data;

import bme.tmit.telki.distance_matrix.DistanceMatrixClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static bme.tmit.telki.TelkiPortalApplication.LOG;

/**
 * Singleton class
 * Handles database connection.
 */
public class InfluxDBConnection {

    private static InfluxDBConnection connectionProvider = null;
    private static InfluxDB connection;

    private static final String url = "http://127.0.0.1:8086";
    private static final String username = "root";
    private static final String password = "root";
    private static final String database_name = "testdb";
    public static final String measurement_name = "traveltime";

    private InfluxDBConnection() {
        connection = InfluxDBFactory.connect(url, username, password);

        //checking connection
        Pong response = connection.ping();
        if (response.getVersion().equalsIgnoreCase("unknown")) {
            LOG.error("Error pinging server.");
            //todo exception?
            return;
        }
        LOG.info("Successfully connected to database as " + username);

        //create / use database
        if (!connection.databaseExists(database_name)) {
            LOG.debug("Creating database " + database_name);
            connection.createDatabase(database_name);
            //connection.createRetentionPolicy("defaultPolicy", database_name, "30d", 1, true); //todo
        }
        connection.setDatabase(database_name);
    }

    private static InfluxDB getConnection() {
        if (connection == null)
            connectionProvider = new InfluxDBConnection();
        return connection;
    }

    public static void saveEntry(TrafficInfoEntry entry) {
        Point point = Point.measurement(measurement_name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("origin", entry.getOrigin())
                .addField("destination", entry.getDest())
                .addField("timeintraffic", entry.getInTraffic())
                .build();
        LOG.debug("[QUERY] INSERT " + point.lineProtocol());
        getConnection().write(point);
    }

    public static List<TrafficInfoEntry> getEntries() {
        Query q = new Query("SELECT * FROM " + measurement_name, database_name);
        LOG.debug("[QUERY] " + q.getCommand());
        QueryResult queryResult = getConnection()
                .query(q);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
    }

    public static List<TrafficInfoEntry> getRouteInfo(DistanceMatrixClient.place from, DistanceMatrixClient.place to) {
        Query q = new Query("SELECT * FROM " + measurement_name + " WHERE \"origin\" = '" + from.name() + "' AND \"destination\" = '" + to.name() + "'", database_name);
        LOG.debug("[QUERY] " + q.getCommand());
        QueryResult queryResult = getConnection()
                .query(q);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
    }

    //SELECT destination, min(timeintraffic) AS fasetest_route FROM traveltime WHERE "origin"='telki_center' GROUP BY time(10m) fill(none)
    // insert traveltime,origin="telki",destination="szell" timeintraffic=21i
}