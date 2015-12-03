-- Option A: Import your project into Eclipse (recommended) --

Configure Eclipse following the instructions at
http://gwt-plugins.github.io/documentation/gwt-eclipse-plugin/servers/AppEngine.html

In Eclipse, go to the File menu and choose:

  File -> Import... -> Existing Maven Projects into Workspace


Now, you need to enable m2Eclipse's annotation processing functionality.
Under project properties, select Maven > Annotation Processing > Enable Project-Specific Settings,
and choose the "Automatically configure JDT APT". Click "Finish", and then right-click on the project,
and select click Maven > Update project.

To launch the web app in GWT super development mode.

  Verify the App Engine web server has been created in Eclipse Preferences > Server > Server Runtimes
	
  Goto the debug view and to the servers tab. Once there add the imported project to the server. 
  Right click on the server to add the project.
  
  Once the project is added to the server, right click on the server and click start. 
  The Super Dev Mode code server will automatically start after the web server starts. 

  Then goto http://localhost:8080.


-- Option B: Build from the command line with Maven --

If you prefer to work from the command line, you can use Maven to
build your project (http://maven.apache.org/). You will also need Java
1.6 JDK. Maven uses the supplied 'pom.xml' file which describes
exactly how to build your project. This file has been tested to work
against Maven 2.2.1. The following assumes 'mvn' is on your command
line path. 

To run super development mode, use these commands.

  mvn clean package 
  
  mvn appengine:devserver_start
  
  mvn gwt:run-codeserver
   
  goto http://localhost:8080
   
  mvn appengine:devserver_stop

To compile your project for deployment, just type 'mvn package'.

For a full listing of other goals, visit:
https://cloud.google.com/appengine/docs/java/tools/maven

