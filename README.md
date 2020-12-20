## GWT

  [![nightly](https://img.shields.io/jenkins/s/http/build.gwtproject.org/gwt.svg?label=nightly)](http://build.gwtproject.org/job/gwt/)
  [![gitter](https://img.shields.io/badge/gitter.im-Join%20Chat-green.svg)](https://gitter.im/gwtproject/gwt/)
  [![irc](https://img.shields.io/badge/irc:%20chat.freenode.net-%23%23gwt-green.svg)](https://webchat.freenode.net/)

  GWT is the official open source project for GWT releases 2.5 and onwards.

  In this document you have some quick instructions to build the SDK from
  source code and to run its tests.

  For a more detailed documentation visit our [web site](http://gwtproject.org).
  If you are interested in contributing with the project, please read the
  [Making GWT better](http://gwtproject.org/makinggwtbetter.html)
  section.

### Building the GWT SDK:

 - In order to build GWT, `java` and `ant` are required in your system.

 - Optional: if you want to compile elemental you need
   `python` and `g++` installed.

 - You need the [GWT tools repository](https://github.com/gwtproject/tools/)
   checked out and up-to-date. By default it is expected to be found at `../tools`.
   You can override the default location using the GWT_TOOLS environment variable
   or passing `-Dgwt.tools=` argument to ant.

 - To create the SDK distribution files run:

   `$ ant clean elemental dist-dev`

   or if you don't have `python` and `g++` just run

   `$ ant clean dist-dev`

   Then you will get all `.jar` files in the folder `build/lib` and
   the redistributable file will be: `build/dist/gwt-0.0.0.zip`

   if you want to specify a different version number run:

   `$ ant clean elemental dist-dev -Dgwt.version=x.x.x`

 - To compile everything including examples you have to run

   `$ ant clean elemental dist`

### How to verify GWT code conventions:

 - In GWT we have some conventions so as all code written
   by contributors look similar being easier to review.

 - After you make any modification, run this command to compile
   everything including tests, to check APIs, and to verify code style.
   It shouldn't take longer than 3-4 minutes.

   `$ ant compile.tests apicheck checkstyle

### How to run GWT tests

 - Previously to run any test you have to set some environment variables
   to guarantee that they are run in the same conditions for all
   developers.

   In a _Unix_ like platform you can use the `export` command:

   `$ export TZ=America/Los_Angeles ANT_OPTS=-Dfile.encoding=UTF-8`

   But in _Windows™_ you have to set the time-zone in your control panel, and
   the environment variables using the command `set`.

 - Finally you can run all test suites with the following command, but be
   prepared because it could take hours, and probably it would fail because
   of timeouts, etc.

   `$ ant test`

 - Thus, you might want to run only certain tests so as you can
   focus on checking the modifications you are working on.

   GWT build scripts use specific ant tasks and a bunch of system
   properties listed in the following table to specify which tests
   to run and how.

   For instance to run the task `test` in the module `user` you
   have to change to the `user` folder and run `ant` with the task
   as argument, adding any other property with the `-D` flag:

   `$ ( cd user && ant test -Dtest.emma.htmlunit.disable=true ; cd .. )`

    Module         | Task                   | Property to skip               | Description
    -------------- | ---------------------- | ------------------------------ | ----------------------
    dev            | test                   | test.dev.disable               | GWT compiler & dev libraries
    codeserver     | test                   | test.codeserver.disable        | SuperDevMode server
    user           | test                   | test.user.disable              | GWT user API and JRE emulation
    user           | test.nongwt            | test.nongwt.disable            | Run tests that not require GWTTestCase
    user           | test.dev.htmlunit      | test.dev.htmlunit.disable      | Run dev-mode tests with HtmlUnit
    user           | test.web.htmlunit      | test.web.htmlunit.disable      | Run web-mode tests with HtmlUnit
    user           | test.draft.htmlunit    | test.draft.htmlunit.disable    | Run draft compiled HtmlUnit tests
    user           | test.nometa.htmlunit   | test.nometa.htmlunit.disable   | Run -XdisableClassMetadata tests with HtmlUnit
    user           | test.emma.htmlunit     | test.emma.htmlunit.disable     | Run emma tests with HtmlUnit
    user           | test.coverage.htmlunit | test.coverage.htmlunit.disable | Run tests for coverage support
    user           | test.dev.selenium      | test.dev.selenium.disable      | Run dev-mode tests using Selenium RC servers
    user           | test.web.selenium      | test.web.selenium.disable      | Run web tests using Selenium RC servers
    user           | test.draft.selenium    | test.draft.selenium.disable    | Run draft compiled tests using Selenium RC servers
    user           | test.nometa.selenium   | test.nometa.selenium.disable   | Run -XdisableClassMetadata tests using Selenium RC servers
    user           | test.emma.selenium     | test.emma.selenium.disable     | Run emma tests with Selenium RC servers
    requestfactory | test                   | test.requestfactory.disable    | Request Factory library
    elemental      | test                   | test.elemental.disable         | Elemental library
    elemental      | test.nongwt            | test.nongwt.disable            | Run elemental tests that not require GWTTestCase
    elemental      | test.dev.htmlunit      | test.dev.htmlunit.disable      | Run elemental dev-mode tests with HtmlUnit
    elemental      | test.web.htmlunit      | test.web.htmlunit.disable      | Run elemental web-mode tests with HtmlUnit
    tools          | test                   | test.tools.disable             | Some tools used in GWT development

   Additionally you can utilize some variables to filter which test to run in each task:

    Module         | Task                                  | Properties                           | Default
    ---------------|---------------------------------------|--------------------------------------|-------------------
    dev/core       | test                                  | gwt.junit.testcase.dev.core.includes | `**/com/google/**/*Test.class`
                   |                                       | gwt.junit.testcase.dev.core.excludes |
    user           | test                                  | gwt.junit.testcase.includes          | `**/*Suite.class`
    user           | test.nongwt                           | gwt.nongwt.testcase.includes         | `**/*JreSuite.class`
                   |                                       | gwt.nongwt.testcase.excludes         |
    user           | test.web.* test.draft.* test.nometa.* | gwt.junit.testcase.web.includes      | `**/*Suite.class`
                   |                                       | gwt.junit.testcase.web.excludes      | `**/*JsInteropSuite.class,**/*JreSuite.class,***/OptimizedOnly*`
    user           | test.dev.* test.emma.*                | gwt.junit.testcase.dev.includes      | `**/*Suite.class`
                   |                                       | gwt.junit.testcase.dev.excludes      | `**/*JsInteropSuite.class,**/*JreSuite.class,***/OptimizedOnly*`

Running GWT Tests in Eclipse

You should be able to run most of your GWT tests from within Eclipse using the following steps.

Right-click on a test that extends GWTTestCase and go to Run As > JUnit Test. It's likely you will see the error message below.


Invalid launch configuration: -XstartOnFirstThread not specified.

On Mac OS X, GWT requires that the Java virtual machine be invoked with the
-XstartOnFirstThread VM argument.

Example:
  java -XstartOnFirstThread -cp gwt-dev-mac.jar com.google.gwt.dev.GWTShell

To fix this error, go to Run > Open Run Dialog. Click on the Arguments tab and add the following values. The 2nd value is to increase the amount of memory available to the test and avoid an OOM error.

-XstartOnFirstThread -Xmx512M
When you re-run the test, you will probably see the following error:

com.google.gwt.junit.JUnitFatalLaunchException: The test class 'org.richresume.client.home.HomeControllerGwtTest' 
was not found in module 'org.richresume.client.Application'; no compilation unit for that type was seen
  at com.google.gwt.junit.JUnitShell.checkTestClassInCurrentModule(JUnitShell.java:193)
  at com.google.gwt.junit.JUnitShell.runTestImpl(JUnitShell.java:628)
  at com.google.gwt.junit.JUnitShell.runTest(JUnitShell.java:150)
  at com.google.gwt.junit.client.GWTTestCase.runTest(GWTTestCase.java:219)
To fix this, open the Run Dialog again, click on the Classpath tab and click on User Entries. Click on the Advanced button and select Add Folders. In the Folder Selection dialog, select your source and test directories (e.g. src/main/java and src/test/java).
Run the test again and you should see a green bar in your JUnit tab.
To create a JUnit configuration that runs all tests, duplicate the previously mentioned run configuration. Then change the name to "All Tests" and select the 2nd radio button to run all tests in the project.
Click Run to execute all the tests in the project.
### Examples

 - Run all tests in dev

   `$ ( cd dev && ant test ; cd .. )`

    _Note: that the last `cd ..' is only needed in Windows._

 - There is another option to do the same but without changing to the
   module folder. We have to specify the module as the ant task, and
   the task as a target argument.

   `$ ant dev -Dtarget=test`

 - Run all tests in codeserver

   `$ ( cd dev/codeserver && ant test )`

    or

   `$ ant codeserver -Dtarget=test -Dtest.dev.disable=true`

    _Note: that we disable dev tests because code server depends on dev
    and we don`t want to run its tests._

 - Run all tests in elemental:

   `$ ( cd elemental && ant test.nongwt )`

    or

   `$ ant elemental -Dtarget=test -Dtest.dev.disable=true -Dtest.user.disable=true`

    _Note: that we have to disable dev and user tests because elemental
    depends on both._

 - Run all tests in tools

   `$ ant tools -Dtarget=test -Dtest.dev.disable=true -Dtest.user.disable=true`

 - Run only the JsniRefTest in dev

   ```
   $ ant dev -Dtarget=test \
       -Dgwt.junit.testcase.dev.core.includes="**/JsniRefTest.class"
   ```

 - Run a couple of tests in dev

   ```
   $ ant dev -Dtarget=test \
       -Dgwt.junit.testcase.dev.core.includes="**/JsniRefTest.class,**/JsParserTest.class"
   ```

   _Note: that you have to use regular expressions separated by comma to
   select the test classes to execute._

 - Run all Jre tests in user, they should take not longer than 3min.
   We have two ways to run them. Although the second case is more
   complex it is here to know how disable properties work.

   `$ ( cd user && ant test.nongwt )`

      or

   ```
   $ ant user -Dtarget=test
          -Dtest.dev.disable=true \
          -Dtest.codeserver.disable=true \
          -Dtest.requestfactory.disable=true \
          -Dtest.elemental.disable=true \
          -Dtest.tools.disable=true \
          -Dtest.dev.htmlunit.disable=true \
          -Dtest.web.htmlunit.disable=true \
          -Dtest.coverage.htmlunit.disable=true \
          -Dtest.dev.selenium.disable=true \
          -Dtest.draft.htmlunit.disable=true \
          -Dtest.draft.selenium.disable=true \
          -Dtest.emma.htmlunit.disable=true \
          -Dtest.emma.selenium.disable=true \
          -Dtest.nometa.htmlunit.disable=true \
          -Dtest.nometa.selenium.disable=true \
          -Dtest.web.selenium.disable=true
   ```

    _Note: that we have to set all disable variables but `test.nongwt.disable`_

 - Run certain Jre tests in the user module.

   `$ ( cd user && ant test.nongwt -Dgwt.nongwt.testcase.includes="**/I18NJreSuite.class" )`

 - Run all GWT tests in user using htmlunit in dev mode.

   `$ ( cd user && ant test.dev.htmlunit )`

