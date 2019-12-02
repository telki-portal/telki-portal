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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static bme.tmit.telki.TelkiPortalApplication.LOG;

/**
 * Singleton class
 * Handles database connection, queries.
 */
public class InfluxDBConnection {

    private static InfluxDBConnection connectionProvider = null;
    private static InfluxDB connection;
    private static InfluxDBResultMapper resultMapper;

    private static final String url = "http://127.0.0.1:8086";
    private static final String username = "root";
    private static final String password = "root";
    private static final String database_name = "testdb";
    public static final String measurement_name = "traveltime";

    private InfluxDBConnection() {
        resultMapper = new InfluxDBResultMapper();
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
                .tag("origin", entry.getOrigin())
                .tag("destination", entry.getDest())
                .addField("timeintraffic", entry.getInTraffic())
                .tag("dayofweek", String.valueOf(entry.getDayOfWeek()))
                .tag("hourofday", String.valueOf(entry.getHourOfDay()))
                .build();
        LOG.debug("[QUERY] INSERT " + point.lineProtocol());
        getConnection().write(point);
    }

    public static List<TrafficInfoEntry> getEntries() {
        Query q = new Query("SELECT * FROM " + measurement_name, database_name);
        LOG.debug("[QUERY] " + q.getCommand());
        QueryResult queryResult = getConnection().query(q);
        return resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
    }

    public static List<TrafficInfoEntry> getRouteInfo(DistanceMatrixClient.place from, DistanceMatrixClient.place to) {
        String queryString = "SELECT * " +
                "FROM " + measurement_name + " " +
                "WHERE \"origin\" = '" + from.name() + "' " +
                "AND \"destination\" = '" + to.name() + "'";

        Query q = new Query(queryString, database_name);
        LOG.debug("[QUERY] " + q.getCommand());
        QueryResult queryResult = getConnection().query(q);
        return resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
    }

    public static List<TrafficInfoEntry> getByInterval(DistanceMatrixClient.place from, DistanceMatrixClient.place to) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        String queryString = "SELECT MEAN(timeintraffic) AS timeintraffic " +
                "FROM " + measurement_name + " " +
                "WHERE \"origin\" = '" + from.name() + "' " +
                "AND \"destination\" = '" + to.name() + "' " +
                "AND time >= '" + Timestamp.valueOf(today) + "' " +
                "GROUP BY time(10m) fill(none)";

        Query todaysGroup = new Query(queryString, database_name);
        LOG.debug("[QUERY] " + todaysGroup.getCommand());
        QueryResult queryResult = getConnection().query(todaysGroup);

        List<TrafficInfoEntry> trafficInfoEntries = resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
        trafficInfoEntries.forEach(e -> {
            e.setOrigin(from.name());
            e.setDest(to.name());
        });

        return trafficInfoEntries;
    }

    public static List<TrafficInfoEntry> getWeekHourly(DistanceMatrixClient.place from, DistanceMatrixClient.place to) {
        List<TrafficInfoEntry> trafficInfoEntries=new ArrayList<>();
        for (int i = 1; i<=7; i++) {

            String queryString = "SELECT MEAN(timeintraffic) AS timeintraffic " +
                    "FROM " + measurement_name + " " +
                    "WHERE origin = '" + from.name() + "' " +
                    "AND destination = '" + to.name() + "' " +
                    "AND dayofweek = '" + i + "' " +
                    "GROUP BY hourofday fill(none)";

            Query todaysGroup = new Query(queryString, database_name);
            LOG.debug("[QUERY] " + todaysGroup.getCommand());
            QueryResult queryResult = getConnection().query(todaysGroup);

            List<TrafficInfoEntry> tmpEntries = resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
            for (TrafficInfoEntry tie: tmpEntries) {
                tie.setOrigin(from.name());
                tie.setDest(to.name());
                tie.setDayOfWeek(String.valueOf(i));    //todo loop iterator
                trafficInfoEntries.add(tie);
            }
        }
        //trafficInfoEntries.forEach(e->System.out.println(e.toString()));
        return trafficInfoEntries;
    }

    //select mean(timeintraffic) from (select * from traveltime group by dayofweek) group by hourofday
    // SELECT destination, min(timeintraffic) AS fasetest_route FROM traveltime WHERE "origin"='telki_center' GROUP BY time(10m) fill(none)
    // select mean(timeintraffic) as timeintraffic from traveltime where origin='telki_center' group by time(10m) fill(none)
    // insert traveltime,origin="telki",destination="szell" timeintraffic=21i

    ////SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse("2016-06-20T11:59:37.136244Z") = {Date@7679} "Mon Jun 20 12:01:53 BST 2016"
}