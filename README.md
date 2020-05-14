# telki-portal
Forgalmi adatok Google DistanceMatrix használatával.

## Fejlesztőkörnyezet indítása
IntelliJ IDEA: projekt mappa megnyitása Gradle projektkét. 

Java 1.8 kell<br>
![import](res/1.JPG?raw=true "Import")

Ha nem automatikusan nem buildel, akkor kézzel indítani egy gradle build-et.<br>
![build](res/2.JPG?raw=true "Build")

Ez után a springboot alkalmazás a run gombbal indul<br>
![run](res/3.JPG?raw=true "Run")

## API kulcs
A projekt gyökér mappájában létre kell hozni egy **secret.config** nevű fájlt ami tartalmazza a kulcsot ilyen formábban:<br>
(a **sample-secret.config** fájl mintájára)
> key:AIzaGoogleApiKeyHere

## Adatbázis indítása
Lokálisan futtatott [InfluxDB (win64x)](https://dl.influxdata.com/influxdb/releases/influxdb-1.7.9_windows_amd64.zip) szükséges (más verziók: https://portal.influxdata.com/downloads/ )<br>
A projekt gyökérben található influxdb-1.7.-1 mappában az **influxd.exe**-t futtatva elindul az adatbázis deamon.<br>
![run](res/influxd.JPG?raw=true "Run")


Az **influx.exe** az influxdb szöveges interfésze, SQL parancsokat itt lehet közvetlenül adni.<br>
Az alkalmazásnak szüksége van egy felhasználóra: username:root password:root<br>
> CREATE USER root WITH PASSWORD 'root' WITH ALL PRIVILEGES

![run](res/createuser.JPG?raw=true "Run")<br>
Ezután a spring app létre tud hozni magának táblát és menti bele az adatokat.<br>
> SHOW DATABASES<br>
USE test1db (USE "databse_name")<br>
SELECT * FROM traveltime<br>
AUTH<br>

## Felület
http://localhost:8080/traffic

## Futtatás parancssorból
1. Gradle-el:
    > gradle bootRun OR gradlew bootRun
2. Buildelt .jar fájlból:<br>
Az api kulcsot tartalmazó secret.config fájlt a futtatott jar fájl mellé is be kell tenni.
    > gradle build OR gradlew build OR gradlew bootJar<br>
    java -jar build\libs\telki-0.0.1-SNAPSHOT.jar

