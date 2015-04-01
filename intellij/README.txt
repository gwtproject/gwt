IntelliJ instructions
=====================



--- Configure path variables ---

Open your IntelliJ preferences and navigate to "Path Variables". In IntelliJ 14+ this can be found
under the "Build, Execution, Deployment" section.

Create two path variables:

1.) GWT_ROOT   : must point to the root of your GWT git clone, e.g. /gwt/trunk
2.) GWT_TOOLS  : must point to the root of the "tools" project, e.g. /gwt/tools

If you haven't checked out the tools project yet, you can do so by running

svn checkout http://google-web-toolkit.googlecode.com/svn/tools/ tools




--- Open project ---

With path variables configured you can now open the IntelliJ project. To do so choose
"Open" / "File" -> "Open" and select the "intellij" folder in the root of your GWT git clone.

The opened project should contain three pre-configured modules

gwt-codeserver, gwt-dev and gwt-user

that should successfully compile out of the box and allow running GWTTestCases right inside the IDE.
(NOTICE: Java SDK might need to be configured. See section "Verify Java SDK" below)

In addition the project is pre-configured to:

- know about GWT's ANT file and pre-configure TZ and ANT_OPTS env variables
(see View -> Tool Windows -> Ant Build)

- know about the git repository
(see View -> Tool Windows -> Version Control)

- add Apache 2.0 file headers for newly created files
(IntelliJ's Copyright plugin must be active)

- use official Java code style from Google
(see https://code.google.com/p/google-styleguide/)




--- Verify Java SDK ---

After opening the project you should verify the Java SDK because by default the IntelliJ project
assumes a Java SDK named "1.7" for Java 1.7.

To verify it go to File -> Project Structure... -> Project. If it shows "1.7 [invalid]" then your
Java SDK is named differently. In that case you have two options:

- rename your SDK to 1.7 and reselect it (preferred)
- select your SDK
  (but make sure to not commit the now changed file: <gwt clone>/intellij/.idea/misc.xml)




--- Running GWTTestCases for RequestFactory ---

Certain tests of RequestFactory require the RfValidation annotation processor to be executed.
In order to run the annotation processor you must first build the requestfactory-apt.jar library
by executing the "requestfactory" target of GWT's ANT script.
This can directly be done inside IntelliJ (see View -> Tool Windows -> Ant Build)

The gwt-user module is already preconfigured to use the produced requestfactory-apt.jar, so after
you have build it you only need to rebuild the IntelliJ project (Build -> Rebuild Project) which
should cause the annotation processor to run.

Once the project has been rebuild the RequestFactory GWTTestCases can be executed.




--- Running ANT tasks within IntelliJ ---

By default the ANT script of GWT assumes that the "tools" project is checked out next to the
GWT git clone and is named "tools", e.g.

~/gwtproject/trunk
~/gwtproject/tools

If your "tools" checkout is named differently or is not at its default location you must configure
the GWT_TOOLS environment variable used by the ANT script. This can be done inside IntelliJ by
opening the Ant Build view (View -> Tool Windows -> Ant Build), opening the ANT properties pane
through the toolbar and add the property "env.GWT_TOOLS" pointing to your "tools" project checkout.

When doing so please make sure to not commit the now changed file <gwt clone>/intellij/.idea/ant.xml

Alternatively move the "tools" project to its default location and rename it accordingly (preferred).

NOTICE: The ANT targets shown in IntelliJ are not all ANT tasks available. To run test tasks
through ANT in a more detailed fashion please refer to instructions in <gwt clone>/README.md.
