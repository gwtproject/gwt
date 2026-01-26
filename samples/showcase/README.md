# Showcase

A simple GWT application, demonstrating how to define widgets, compile source, and load data from a URL. The server
in this example only hosts the static application, but could be imagined to be more complex as needed.

The application's structure and build is generated with the help of the `modular-webapp` archetype from
https://github.com/tbroyer/gwt-maven-archetypes/.

The sample is built to use latest GWT from the snapshot server. To change to a specific GWT version, adjust
the `gwt.version` property to your desired release, and optionally remove the snapshot repository from the pom.xml.

### Development mode
To run the application in development mode, first run GWT DevMode to set up the bootstrap JS file.

NOTE: This is not the usual gwt:codeserver, there seems to be a classpath issue in loading source files as resources
through dev mode, which is part of how this demo functions. This is not a typical use case, but an issue specific to
the Showcase sample.

```shell
mvn gwt:devmode -pl showcase-client -am
```
Then, in a separate terminal, run the server:

```shell
mvn jetty:run -pl showcase-server -am -Denv=dev
```

### Production
The application can be built for production mode simply by running

```shell
mvn install
```

Then either run the server in production mode through the maven plugin

```shell
mvn jetty:run-war -pl showcase-server -am
```
or deploy the generated WAR file found in `showcase-server/target/showcase-server-1.0-SNAPSHOT.war` to
any Java EE9+ servlet container.
