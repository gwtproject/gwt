source lib-maven-deploy.sh

function finishAndCleanup () {
  if [[ $thereHaveBeenErrors ]]; then
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "WARNING: Errors while deploying files, examine output above."
    echo "Leaving intermediate files at:"
    echo "$RANDOM_DIR"
    for i in dev user servlet
    do
      echo "$jarExpandDir-${i}"
    done
    find $pomDir -name pom.xml
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  else
    # Clean up
    rm -rf $RANDOM_DIR
    for i in dev user servlet
    do
      rm -rf $jarExpandDir-${i}
    done
    # Remove POMs & ASCs, leaving only templates
    find $pomDir -name pom.xml | xargs rm
  fi

}

function die () {
  thereHaveBeenErrors=1
  if [[ "$continueOnErrors" != "y" ]]; then
    read -e -p"Error while deploying, ignore errors? (y/N): " continueOnErrors
    if [[ "$continueOnErrors" != "y" ]]; then
      finishAndCleanup
      exit 1
    fi
  fi
}

function warnJavaDoc () {
  echo "WARNING: Could not deploy JavaDoc for $1. Continuing"
}

# Appends to COMMIT_MESSAGE
function maven-gwt() {
  local gwtMavenVersion=$1
  shift
  local gwtSdkArchive=$1
  shift
  local mavenRepoUrl=$1
  shift
  local mavenRepoId=$1
  shift

  if [[ "$mavenRepoUrl" == "" ]]; then
    echo "ERROR: Incorrect parameters to maven-gwt"
    exit 1
  fi

  if [[ "$mavenRepoId" == "" ]]; then
    if [[ "`expr match $mavenRepoUrl "file://"`" == 0 ]]; then
   	echo "ERROR: maven-gwt: mavenRepoId is not specified, and the mavenRepoUrl is not local (does not start with file://)"
    	exit 1
    fi
   # set a dummy repo id
   mavenRepoId=local
  fi

  set-random-dir
  echo "Unzipping $gwtSdkArchive to $RANDOM_DIR"
  unzip -q $gwtSdkArchive -d $RANDOM_DIR || exit 1

  GWT_EXTRACT_DIR=`ls $RANDOM_DIR | tail -n1`
  GWT_EXTRACT_DIR=$RANDOM_DIR/$GWT_EXTRACT_DIR

  JAVADOC_FILE_PATH=$RANDOM_DIR/gwt-javadoc.jar
  jar cf $JAVADOC_FILE_PATH -C $GWT_EXTRACT_DIR/doc/javadoc .

  jarExpandDir=/tmp/tmp-jar-expand-dir-$RANDOM

  # Generate POMs with correct version
  for template in `find $pomDir -name pom-template.xml`
  do
    dir=`dirname $template`
    pushd $dir > /dev/null
    sed "s|\${gwtVersion}|$gwtMavenVersion|g" pom-template.xml >pom.xml
    popd > /dev/null
  done

  # Remove bundled org/objectweb/asm classes from gwt-dev
  echo "Removing ASM classes from gwt-dev"
  zip -d $GWT_EXTRACT_DIR/gwt-dev.jar "org/objectweb/asm/*"

  # Silently skip Elemental if it doesn't exist
  gwtLibs='dev user servlet codeserver'
  if [ -f $GWT_EXTRACT_DIR/gwt-elemental.jar ]; then
    gwtLibs="${gwtLibs} elemental"
  fi

  for i in $gwtLibs
  do
    CUR_FILE=`ls $GWT_EXTRACT_DIR/gwt-${i}.jar`

    # Get rid of the INDEX.LIST file, since it's going to be out of date
    # once we rename the jar files for Maven
    echo "Removing INDEX.LIST from gwt-${i}"
    zip -d $CUR_FILE META-INF/INDEX.LIST

    SOURCES_FILE=gwt-${i}-sources.jar
    curExpandDir=$jarExpandDir-${i}

    rm -rf $curExpandDir
    mkdir -p $curExpandDir
    unzip -q $CUR_FILE -d $curExpandDir
    chmod -R ugo+rwx $curExpandDir
    pushd $curExpandDir > /dev/null

    rm -rf javafilelist
    find . -name "*.java" -print  > javafilelist
    if [ -s javafilelist ]; then
      jar cf $SOURCES_FILE @javafilelist
    fi
    popd > /dev/null
  done

  # push parent poms
  maven-deploy-file $mavenRepoUrl $mavenRepoId $pomDir/gwt/pom.xml $pomDir/gwt/pom.xml

  for i in $gwtLibs
  do
    CUR_FILE=`ls $GWT_EXTRACT_DIR/gwt-${i}.jar`
    gwtPomFile=$pomDir/gwt/gwt-$i/pom.xml
    SOURCES_FILE=gwt-${i}-sources.jar
    SOURCES_PATH_FILE=$jarExpandDir-${i}/$SOURCES_FILE
    # If there are no sources, use gwt-user sources.
    # This is a bit hacky but Sonatype requires a
    # source jar for Central, and lack of sources
    # should only happen for gwt-servlet which is
    # basically a subset of gwt-user.
    if [ ! -f $SOURCES_PATH_FILE ]; then
      SOURCES_PATH_FILE=$jarExpandDir-user/gwt-user-sources.jar
    fi

    maven-deploy-file $mavenRepoUrl $mavenRepoId "$CUR_FILE" $gwtPomFile "$JAVADOC_FILE_PATH" "$SOURCES_PATH_FILE" || die
  done

  # Deploy RequestFactory jars
  maven-deploy-file $mavenRepoUrl $mavenRepoId $pomDir/requestfactory/pom.xml $pomDir/requestfactory/pom.xml || die

  for i in client server apt
  do
    maven-deploy-file $mavenRepoUrl $mavenRepoId $GWT_EXTRACT_DIR/requestfactory-${i}.jar $pomDir/requestfactory/${i}/pom.xml \
        $JAVADOC_FILE_PATH $GWT_EXTRACT_DIR/requestfactory-${i}-src.jar \
         || die
  done

  finishAndCleanup
}

