# Hello

A simple GWT application, showing how to compile a simple application to JS and run it without a dedicated server.
This example serves to show that an application server isn't always necessary to quickly get a simple example going.

### Development mode
To run the application in development mode, we can run the DevMode server to serve the static app:
```shell
mvn war:exploded gwt:devmode
```
Then, navigate in a web browser to http://localhost:8888/Hello.html

### Production
Inasmuch as this application can be considered usable in production, it can be built for production mode simply by running
```shell
mvn verify
```
A war file will be built, and can then be deployed to any servlet container - or unpacked and run with any static
file server. For example, to run it with Python's simple HTTP server:
```shell
python -m http.server 8000 -d target/hello-1.0-SNAPSHOT
```
Then navigate in a web browser to http://localhost:8000/Hello.html

An alternative way to build this example is to specify the "stackmode-strip" profile, to compile a different module that
sets the compiler's stackMode to "strip", slightly reducing the compiled size.
```shell
mvn verify -Pstackmode-strip
```
Only about 1KB is saved in doing this, but it may be useful in some constrained environments - or simply as an example
of how to use modules to vary compiler options.
