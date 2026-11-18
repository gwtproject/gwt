# MobileWebApp

This is a sample mobile webapp demonstrating a variety of features:
 * Google App Engine for persistence and authentication features
 * Local storage
 * Manual DI
 * `<audio>` and `<video>` tags
 * Device formfactor detection resulting in different views through rebind rules
 * Cache manifest feature through the use of a linker - no longer supported in modern browsers (replaced largely by service workers)
 * Custom RequestFactory transport for authentication headers

It also serves at this point as an example of a legacy application that cannot run on modern Java - as presently implemented,
the App Engine plugin seems to be limited to being built and run on **Java 11**.

The sample is built to use latest GWT from the snapshot server. To change to a specific GWT version, adjust
the `gwt.version` property to your desired release, and optionally remove the snapshot repositories from the pom.xml.

### Development mode
To run the application in development mode, first we need to build once - the appengine-maven-plugin wants to run on
every module in the project, so when we start it, it needs to be specifically on the server module. Build and install 
the project:
```shell
mvn install
```

Next run the GWT codeserver to set up the bootstrap JS file:
```shell
mvn gwt:codeserver -pl mobilewebapp-client -am
```

Then, in a separate terminal, run the App Engine dev server:
```shell
mvn appengine:devserver -pl mobilewebapp-server -Denv=dev 
```
Note that this will not build the shared project (as `-am` is omitted to only run this on the server), but that should
be built by the first step.

Alternatively, you can start the devserver in the background, once the codeserver has created its basic bootstrap file:
```shell
mvn appengine:devserver_start -pl mobilewebapp-server -Denv=dev

```
To stop this, run:
```shell
mvn appengine:devserver_stop -pl mobilewebapp-server -Denv=dev
```

Connect your browser to http://localhost:8080/ to view the application.

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
