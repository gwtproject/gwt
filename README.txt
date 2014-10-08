** How to build GWT:

 - You need 'java' and 'ant' installed in your system.

 - Optional: if you want to compile elemental you need
   'python' and 'g++'

 - You need the 'gwt-tools' downloaded, and by default they
   should be in the folder '../tools' unless you define
   the environment variable GWT_TOOLS

 - To create the SDK distribution files run:
   $ ant clean elemental dist-dev
   or if you dont have phyton and g++
   $ ant clean dist-dev

   Then you will get all .jar files in the folder build/lib and
   the redistributable file 'build/dist/gwt-0.0.0.zip'

   Tip: if you want to specify a different version number run:
   $ export GWT_VERSION=x.x.x
   $ ant elemental clean dist-dev
   Note that if you are running windows instead of 'export' you should
   use the 'set' command. A good alternative is to install cygwin in
   windows and use its shell instead of classic CMD.

 - To compile everything including examples you have to run
   $ ant clean elemental dist

** How to verify that any change in sources fits the GWT code
   conventions:

 - With this command you will compile everything including tests
   and check APIs and code style.
   $ ant compile.tests apicheck checkstyle -Dprecompile.disable=true

** How to run GWT tests

 - To run all test suites just run the following command, but be
   prepared because it could take hours, and probably will fail because
   timeouts etc.
   $ export TZ=America/Los_Angeles ANT_OPTS=-Dfile.encoding=UTF8
   $ ant test

   Note: we need some variables to run tests always in the same
   conditions.

 - But you might want to run only certain tests so as you can
   focus on checking the modifications you are working on.
   GWT build scripts use specific ant tasks and a bunch of System
   properties to spefify which tests to run and how.

   Some modules are divided in tasks to run specific tests
   or to select the environment to run them:
    Test tasks in 'user'
      test.nongwt
      test.dev.htmlunit
      test.web.htmlunit
      test.dev.selenium
      test.emma.selenium
      test.web.selenium
      test.draft.selenium
      test.nometa.selenium
      test.emma.htmlunit
      test.draft.htmlunit
      test.nometa.htmlunit
      test.coverage.htmlunit
    Test tasks in 'elemental'
      test.jre
      test.dev.htmlunit
      test.web.htmlunit
    The rest of modules just come with the 'test' task.

   Properties to disable the execution of certain tests:
    Skip dev/core/tests
      test.dev.disable
    Skip dev/codeserver/javatests/
      test.codeserver.disable
    Skip tools/*/test
      test.tools.disable
    Skip request factory related test in user/test
      test.requestfactory.disable
    Skip user/test
      test.user.disable
    Additional variables to disable specific executions
    in 'user/test'
      test.coverage.htmlunit.disable
      test.dev.htmlunit.disable
      test.dev.selenium.disable
      test.draft.htmlunit.disable
      test.draft.selenium.disable
      test.emma.htmlunit.disable
      test.emma.selenium.disable
      test.nometa.htmlunit.disable
      test.nometa.selenium.disable
      test.nongwt.disable
      test.web.htmlunit.disable
      test.web.selenium.disable

   Properties to filter what tests to run:
    Include or exclude tests in 'dev/core/tests'
      gwt.junit.testcase.dev.core.includes
      gwt.junit.testcase.dev.core.excludes

    Include or exclude tests in 'user/test'
      gwt.junit.testcase.includes
      gwt.junit.testcase.web.includes
      gwt.junit.testcase.web.excludes
      gwt.junit.testcase.dev.includes
      gwt.junit.testcase.dev.excludes
      gwt.tck.testcase.dev.includes
      gwt.tck.testcase.dev.excludes
      gwt.nongwt.testcase.includes
      gwt.nongwt.testcase.excludes

** Examples

 - Run all tests in dev
      $ ant dev -Dtarget=test

 - Run all tests in codeserver
      $ ant codeserver -Dtarget=test -Dtest.dev.disable=true

      Note: that we disable dev tests because code server depends on dev
      and we don't want to run its tests.

 - Run all tests in elemental. There are two options:
      $ ( cd elemental && ant test.jre )
      or
      $ ant elemental -Dtarget=test -Dtest.dev.disable=true -Dtest.user.disable=true

      Note: that we have to disable dev and user tests because elemental
      depends on both, or we can use this command to do the same:

 - Run all tests in tools
      $ ant tools -Dtarget=test -Dtest.dev.disable=true -Dtest.user.disable=true

 - Run only the JsniRefTest in dev
      $ ant dev -Dtarget=test \
          -Dgwt.junit.testcase.dev.core.includes=**/JsniRefTest.class

 - Run a couple of tests in dev
      $ ant dev -Dtarget=test \
          -Dgwt.junit.testcase.dev.core.includes=,**/JsniRefTest.class,**/JsParserTest.class

      Note: that you use regular expressions separated by comma to
      select the test classes to execute.

 - Run all Jre tests in user, they should take not more than 3min.
   We have also two ways to run them, although the second case is plenty
   of options.
      $ ( cd user && ant test.nongwt )
      or
      $ ant user -Dtarget=test -Dtest.dev.disable=true \
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

        Note: that we have to set all disable variables but
        'test.nongwt.disable'

 - Run certain Jre tests in user
      $ ( cd user && ant test.nongwt -Dgwt.nongwt.testcase.includes=**/I18NJreSuite.class )

 - Run all GWT tests in user using dev mode.
      $ ( cd user && ant test.dev.htmlunit )

