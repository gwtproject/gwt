# Validation

A slightly more complex GWT application, demonstrating primarily how to use Bean Validation (JSR 380) on shared
client/server types.

The application's structure and build is generated with the help of the `modular-webapp` archetype from
https://github.com/tbroyer/gwt-maven-archetypes/.

The sample is built to use latest GWT from the snapshot server. To change to a specific GWT version, adjust
the `gwt.version` property to your desired release, and optionally remove the snapshot repository from the pom.xml.

**Java 17+** is required to run the server, although GWT compilation can be done with Java 11+. A different
server plugin could be used instead, then the Java version can be reduced to 11.

### Development mode
To run the application in development mode, first run the GWT codeserver to set up the bootstrap JS file:

```shell
mvn gwt:codeserver -pl validation-client -am
```
Then, in a separate terminal, run the server:

```shell
mvn jetty:run -pl validation-server -am -Denv=dev
```

### Production
The application can be built for production mode simply by running

```shell
mvn install
```

Then either run the server in production mode through the maven plugin

```shell
mvn jetty:run-war -pl validation-server -am
```
or deploy the generated WAR file found in `validation-server/target/validation-server-1.0-SNAPSHOT.war` to
any Java EE9+ servlet container.
