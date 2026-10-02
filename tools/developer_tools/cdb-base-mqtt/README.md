# CDB Base MQTT

Shared Java 8 MQTT event base class, publishing API, and Payara JCA/JNDI transport used by ComponentDB and BELY.

`gov.anl.aps.cdb.base.mqtt.MqttEvent` owns the event timestamp and requires applications to supply a string topic. Application-specific payload fields and topic definitions remain in each portal.

## Build and test

A JDK and Maven are required.

```sh
mvn clean verify
sha256sum -c SHA256SUMS
```

The versioned thin JAR is written to `target/cdb-base-mqtt-1.0.0.jar`. Jackson is provided by each consuming portal and is intentionally not bundled.

## Release artifact

The checked-in `SHA256SUMS` records the released JAR checksum and verifies the artifact under `target/`. After changing this library:

1. Increment the version in `pom.xml`.
2. Run `mvn clean verify`.
3. Update `SHA256SUMS` with the checksum of the generated JAR.
4. Copy that exact JAR into each portal's checked-in `lib/` directory.
5. Register the versioned JAR in each portal's NetBeans Ant classpath.
6. Verify each copied JAR with `sha256sum` before building the portals.
