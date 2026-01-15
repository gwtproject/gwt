# DynaTable

A simple GWT application, demonstrating how to define widgets, compile source, share code with the server, 
and communicate using GWT-RPC.

The application's structure and build is generated with the help of the `modular-requestfactory` archetype from
https://github.com/tbroyer/gwt-maven-archetypes/.

### Development mode
To run the application in development mode, first run the GWT codeserver to set up the bootstrap JS file:

```shell
mvn gwt:codeserver -pl dynatable-client -am 
```
Then, in a separate terminal, run the server:

```shell
mvn jetty:run -pl dynatable-server -am -Denv=dev
```

### Production
The application can be built for production mode simply by running

```shell
mvn install
```

Then either run the server in production mode through the maven plugin

```shell
mvn jetty:run-war -pl dynatable-server -am
```
or deploy the generated WAR file found in `dynatable-server/target/dynatable-server-1.0-SNAPSHOT.war` to
any Java EE9+ servlet container.