source lib-maven-deploy.sh

function finishAndCleanup () {
  if [[ $thereHaveBeenErrors ]]; then
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "WARNING: Errors while deploying files, examine output above."
    echo "Leaving intermediate files at:"
    echo "$RANDOM_DIR"
    find $pomDir -name pom.xml -o -name pom.xml.asc
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  else
    # Clean up
    rm -rf $RANDOM_DIR
    # Remove POMs & ASCs, leaving only templates
    find $pomDir -name pom.xml -o -name pom.xml.asc -delete
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
  [ -d $GWT_EXTRACT_DIR/doc/javadoc ] && jar cf $JAVADOC_FILE_PATH -C $GWT_EXTRACT_DIR/doc/javadoc .

  # Generate POMs with correct version
  for template in `find $pomDir -name pom-template.xml`
  do
    dir=`dirname $template`
    pushd $dir > /dev/null
    sed -e "s|\${gwtVersion}|$gwtMavenVersion|g" pom-template.xml >pom.xml
    popd > /dev/null
  done

  gwtLibs='dev user servlet servlet-jakarta codeserver'

  echo "Removing bundled third-parties from gwt-dev"
  zip -q $GWT_EXTRACT_DIR/gwt-dev.jar --copy --out $GWT_EXTRACT_DIR/gwt-dev-trimmed.jar \
      "com/google/gwt/*"
  mv $GWT_EXTRACT_DIR/gwt-dev-trimmed.jar $GWT_EXTRACT_DIR/gwt-dev.jar
  echo "Removing bundled third-parties from gwt-user"
  zip -q $GWT_EXTRACT_DIR/gwt-user.jar --copy --out $GWT_EXTRACT_DIR/gwt-user-trimmed.jar \
      "com/google/gwt/*" "com/google/web/bindery/*" "javaemul/*" \
      "javax/validation/*" "org/hibernate/validator/*" \
      "org/w3c/flute/*"
  mv $GWT_EXTRACT_DIR/gwt-user-trimmed.jar $GWT_EXTRACT_DIR/gwt-user.jar

  for i in $gwtLibs
  do
    CUR_FILE=`ls $GWT_EXTRACT_DIR/gwt-${i}.jar`

    # Get rid of the INDEX.LIST file, since it's going to be out of date
    # once we rename the jar files for Maven
    if unzip -l $CUR_FILE META-INF/INDEX.LIST >/dev/null; then
      echo "Removing INDEX.LIST from gwt-${i}"
      zip -d $CUR_FILE META-INF/INDEX.LIST
    fi

    SOURCES_FILE=$GWT_EXTRACT_DIR/gwt-${i}-sources.jar
    if unzip -l $CUR_FILE '*.java' >/dev/null; then
      zip -q $CUR_FILE --copy --out $SOURCES_FILE "*.java"
    fi
  done

  # push parent poms
  maven-deploy-file $mavenRepoUrl $mavenRepoId $pomDir/gwt/pom.xml $pomDir/gwt/pom.xml

  for i in $gwtLibs
  do
    CUR_FILE=`ls $GWT_EXTRACT_DIR/gwt-${i}.jar`
    gwtPomFile=$pomDir/gwt/gwt-$i/pom.xml
    SOURCES_FILE=gwt-${i}-sources.jar
    SOURCES_PATH_FILE=$GWT_EXTRACT_DIR/$SOURCES_FILE
    # If there are no sources, fail, this is a requirement of maven central
    if [ ! -f $SOURCES_PATH_FILE ]; then
      echo "ERROR: sources jar not found for $i"
      exit 1
    fi

    maven-deploy-file $mavenRepoUrl $mavenRepoId "$CUR_FILE" $gwtPomFile "$JAVADOC_FILE_PATH" "$SOURCES_PATH_FILE" || die
  done

  # Deploy RequestFactory jars
  maven-deploy-file $mavenRepoUrl $mavenRepoId $pomDir/requestfactory/pom.xml $pomDir/requestfactory/pom.xml || die

  for i in client server apt server-jakarta
  do
    maven-deploy-file $mavenRepoUrl $mavenRepoId $GWT_EXTRACT_DIR/requestfactory-${i}.jar $pomDir/requestfactory/${i}/pom.xml \
        $JAVADOC_FILE_PATH $GWT_EXTRACT_DIR/requestfactory-${i}-src.jar \
         || die
  done

  finishAndCleanup
}
