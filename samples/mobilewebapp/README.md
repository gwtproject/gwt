# MobileWebApp

This is a sample mobile webapp demonstrating a variety of features:
 * Google App Engine for persistence and authentication features
 * Local storage
 * Manual DI
 * `<audio>` and `<video>` tags
 * Device formfactor detection resulting in different views through rebind rules
 * Cache manifest feature through the use of a linker - no longer supported in modern browsers (replaced largely by service workers)

It also serves at this point as an example of a legacy application that cannot run on modern Java - it is limited to Java
11 without additional JVM args to allow reflection on JDK internals. 

### Development mode
First, build the whole application
```shell
mvn clean package
```
Start the App Engine dev server in the background:
```shell
mvn appengine:devserver_start
```
To stop this, run:
```shell
mvn appengine:devserver_stop
```

You can also start the devserver in the foreground in a separate shell - this approach lets you tail the logs
to look for errors:
```shell
mvn appengine:devserver
```

Next, run the GWT codeserver:
```shell
mvn gwt:codeserver
```

And connect your browser to http://localhost:8080/ to view the application.

## Mobile device testing
To test on a mobile device, first ensure that your development machine and mobile device
are on the same network and can reach each other - some corporate networks may prevent this.

Next, the `appengine-maven-plugin` defaults to only exposing the app server on localhost - to
change this, edit the configuration to comment in the `<address>` block to declare the address
to bind to (using `0.0.0.0` will bind to all interfaces).
`<configuration>` section of the plugin:
```
      <!-- Uncomment the snippet below to bind to all IPs instead of just localhost -->
      <address>0.0.0.0</address>
```

The GWT codeserver process has the same security feature, but also is configurable in the `gwt-maven-plugin`
declaration:
```
    <codeserverArgs>
      <!-- Uncomment the snippet below to bind to all IPs instead of just localhost -->
      <codeserverArg>-bindAddress</codeserverArg>
      <codeserverArg>0.0.0.0</codeserverArg>
    <codeserverArgs>
```

Now you can navigate to `http://<your-computer-ip>:8080/` on your mobile device to access the application.
