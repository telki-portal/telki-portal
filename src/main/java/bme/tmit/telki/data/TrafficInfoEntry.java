package bme.tmit.telki.data;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

import static bme.tmit.telki.data.InfluxDBConnection.measurement_name;

@Measurement(name = measurement_name)
public class TrafficInfoEntry {
    @Column(name = "time")
    private Instant time;

    @Column(name = "origin")
    private String origin;

    @Column(name = "destination")
    private String dest;

    @Column(name = "timeintraffic")
    private Long inTraffic;

    public TrafficInfoEntry() { } //InfluxDBResultMapper needs the default constructor

    public TrafficInfoEntry(Instant time, String origin, String dest, Long inTraffic) {
        this.time = time;
        this.origin = origin;
        this.dest = dest;
        this.inTraffic = inTraffic;
    }

    public Instant getTime() {
        return time;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDest() { return dest; }

    public Long getInTraffic() {
        return inTraffic;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setInTraffic(Long inTraffic) {
        this.inTraffic = inTraffic;
    }

    @Override
    public String toString() {
        return time.toString() + " " + origin + " " + dest + " " + inTraffic.toString();
    }
}
