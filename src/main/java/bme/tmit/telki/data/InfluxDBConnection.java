package bme.tmit.telki.data;

import bme.tmit.telki.distance_matrix.DistanceMatrixClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        Query q = new Query(
                "SELECT * " +
                        "FROM " + measurement_name + " " +
                        "WHERE \"origin\" = '" + from.name() + "' " +
                            "AND \"destination\" = '" + to.name() + "'"
                ,database_name);
        LOG.debug("[QUERY] " + q.getCommand());
        QueryResult queryResult = getConnection()
                .query(q);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
    }

    public static List<TrafficInfoEntry> getByInterval(DistanceMatrixClient.place from, DistanceMatrixClient.place to) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        Query todaysGroup = new Query(
                "SELECT MEAN(timeintraffic) AS timeintraffic " +
                "FROM " + measurement_name + " " +
                "WHERE \"origin\" = '" + from.name() + "' " +
                        "AND \"destination\" = '" + to.name() + "' " +
                        "AND time >= '" + Timestamp.valueOf(today) + "' " +
                "GROUP BY time(10m) fill(none)"
                ,database_name);

        LOG.debug("[QUERY] " + todaysGroup.getCommand());
        QueryResult queryResult = getConnection()
                .query(todaysGroup);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<TrafficInfoEntry> trafficInfoEntries = resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
        trafficInfoEntries.forEach(e -> {
            e.setOrigin(from.name());
            e.setDest(to.name());
        });

        return trafficInfoEntries;
    }

    // SELECT destination, min(timeintraffic) AS fasetest_route FROM traveltime WHERE "origin"='telki_center' GROUP BY time(10m) fill(none)
    // select mean(timeintraffic) as timeintraffic from traveltime where origin='telki_center' group by time(10m) fill(none)
    // insert traveltime,origin="telki",destination="szell" timeintraffic=21i

    ////SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse("2016-06-20T11:59:37.136244Z") = {Date@7679} "Mon Jun 20 12:01:53 BST 2016"
}