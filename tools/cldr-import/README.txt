Steps to process CLDR data from command-line:

- You need at least java 1.7 installed in your system and ant

- First you have to get latest CLDR data available locally on your system
  and compile it:
  $ svn co http://unicode.org/repos/cldr/tags/release-32-0-1 <cldrdir>

- Third, you need to compile cldr tools
  $ cd <cldrdir>/tools/java
  $ ant clean jar

- Now you can run cldr-import tests:
  $ cd GWT_ROOT/tools/cldr-import
  $ ant clean test

- To generate files for certain locales in a tmp folder run:
  $ CLDR_ROOT=<cldrdir> LOCALES=es,en CLDR_TEMP=/tmp/cldr ant gen.temp
  Files will be created in /tmp/cldr-import

- To generate all locales in gwt folders run:
  $ CLDR_ROOT=<cldrdir> ant gen
