Steps to process CLDR data from command-line:

- You need at least java 1.7 installed in your system and ant

- First you have to get latest CLDR data available locally on your system
  and compile it:
  $ svn co http://unicode.org/repos/cldr/tags/release-25 <cldrdir>
  $ cd <cldrdir>/tools/java
  $ ant clean jar

- Second you have to apropriatelly patch cldr data with modifications
  maintained in GWT (replace GWT_ROOT with your gwt source folder)
  $ cd <cldrdir>
  $ patch -p1 -i GWT_ROOT/tools/cldr-import/patches/cldr25.patch

- Now you can run cldr-import tests:
  $ cd GWT_ROOT/tools/cldr-import
  $ ant clean test

- To generate files for certain locales in a tmp folder run:
  $ CLDR_ROOT=<cldrdir> LOCALES=es,en CLDR_TEMP=/tmp/cldr ant gen.temp
  Files will be created in /tmp/cldr-import

- To generate all locales in gwt folders run:
  $ CLDR_ROOT=<cldrdir> ant gen
