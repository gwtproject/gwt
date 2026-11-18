# Mail

A simple GWT application, demonstrating how to define widgets, compile source, and load data from a URL. The server
in this example only hosts the static application, but could be imagined to be more complex as needed.

The application's structure and build is generated with the help of the `modular-webapp` archetype from
https://github.com/tbroyer/gwt-maven-archetypes/.

The sample is built to use latest GWT from the snapshot server. To change to a specific GWT version, adjust
the `gwt.version` property to your desired release, and optionally remove the snapshot repository from the pom.xml.

**Java 17+** is required to run the server, although GWT compilation can be done with Java 11+. A different
server plugin could be used instead, then the Java version can be reduced to 11.

### Development mode
To run the application in development mode, first run the GWT codeserver to set up the bootstrap JS file:

```shell
mvn gwt:codeserver -pl mail-client -am
```
Then, in a separate terminal, run the server:

```shell
mvn jetty:run -pl mail-server -am -Denv=dev
```

### Production
The application can be built for production mode simply by running

```shell
mvn install
```

Then either run the server in production mode through the maven plugin

```shell
mvn jetty:run-war -pl mail-server -am
```
or deploy the generated WAR file found in `mail-server/target/mail-server-1.0-SNAPSHOT.war` to
any Java EE9+ servlet container.
